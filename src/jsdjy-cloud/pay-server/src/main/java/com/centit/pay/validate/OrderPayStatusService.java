package com.centit.pay.validate;

import com.alibaba.fastjson.JSONObject;
import com.centit.core.model.OrderPayInfo;
import com.centit.core.result.Result;
import com.centit.pay.biz.dao.ShoppingOrderPayDao;
import com.centit.pay.biz.dao.ShoppingOrderPaykeyDao;
import com.centit.pay.biz.dao.ShoppingOrderPaylogDao;
import com.centit.pay.biz.po.ShoppingOrderPay;
import com.centit.pay.biz.po.ShoppingOrderPaykey;
import com.centit.pay.biz.po.ShoppingOrderPaylog;
import com.centit.pay.exp.PaymentException;
import com.centit.pay.feign.FeignThirdService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2025/9/2 11:09
 **/
@Service
@Slf4j
public class OrderPayStatusService {
    @Resource
    private ShoppingOrderPayDao shoppingOrderPayDao;
    @Resource
    private ShoppingOrderPaykeyDao shoppingOrderPaykeyDao;
    @Resource
    private ShoppingOrderPaylogDao shoppingOrderPaylogDao;

    @Resource
    private FeignThirdService feignThirdService;

    // 设置传播行为为REQUIRES_NEW，创建独立事务
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void updateOrderPayStatus(ShoppingOrderPay shoppingOrderPay){
        try {
            int rows = shoppingOrderPayDao.update(shoppingOrderPay);
            if (rows == 0) {
                throw new PaymentException("订单支付状态更新失败，未找到对应记录");
            }
        } catch (Exception e) {
            // 向上抛出异常
            log.error("更新支付状态异常", e);
            throw e; // 将异常传播到父事务
        }
    }

    /**
     * 向麦座确认订单
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void confirmMzOrder(OrderPayInfo orderPayInfo, ShoppingOrderPay shoppingOrderPay) {
        Result<String> result= feignThirdService.addOrder(orderPayInfo);
        ShoppingOrderPaylog shoppingOrderPaylog = new ShoppingOrderPaylog();
        shoppingOrderPaylog.setTradeType("确认麦座订单");
        shoppingOrderPaylog.setLogInfo("确认麦座订单");
        shoppingOrderPaylog.setReqInfo(orderPayInfo.toString());
        shoppingOrderPaylog.setLogContent(result.toString());
        shoppingOrderPaylog.setOfId(orderPayInfo.getOfId());
        if(result.getRetCode().equals("0")){
            shoppingOrderPaylog.setStateInfo("确认麦座订单成功");
            shoppingOrderPaylog.setOutTradeNo(result.getBizData());
            addOrderPayLog(shoppingOrderPaylog);
            //调用麦座接口确认订单成功，则直接将订单的积分、余额和优惠券相应支付状态置为成功
            if (orderPayInfo.getDeductionIntegralPrice().compareTo(BigDecimal.ZERO) > 0) {
                shoppingOrderPay.setIntegralStatus(1);
            }
            if (orderPayInfo.getDeductionBalancePrice().compareTo(BigDecimal.ZERO) > 0) {
                shoppingOrderPay.setBalanceStatus(1);
            }
            if (orderPayInfo.getDeductionCouponPrice().compareTo(BigDecimal.ZERO) > 0) {
                shoppingOrderPay.setCouponStatus(1);
            }
            shoppingOrderPayDao.update(shoppingOrderPay);

        }else{
            shoppingOrderPaylog.setStateInfo("向麦座确认订单失败");
            addOrderPayLog(shoppingOrderPaylog);
            throw new PaymentException("向麦座确认订单失败");
        }
    }

    /**
     * 余额扣除
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void costBalance(OrderPayInfo orderPayInfo, ShoppingOrderPay shoppingOrderPay) {
        //待扣除余额数值大于0
        if(orderPayInfo.getDeductionBalancePrice().compareTo(BigDecimal.ZERO) > 0){
            //余额为待支付或支付失败状态，可以发起支付
            if (shoppingOrderPay.getBalanceStatus() == 0 || shoppingOrderPay.getBalanceStatus() == 2) {
                //如果需要扣除的余额数额超过限额，则需要传递“资产使用业务key”，该key值之前在调用验证码接口时已经存入数据库
                String asset_biz_key = null;
                ShoppingOrderPaykey shoppingOrderPaykey = new ShoppingOrderPaykey();
                shoppingOrderPaykey.setOfId(orderPayInfo.getOfId());
                shoppingOrderPaykey = shoppingOrderPaykeyDao.queryDetail(shoppingOrderPaykey);
                if (null != shoppingOrderPaykey) {
                    if(null !=shoppingOrderPaykey.getAccountMoneyPayKey()&&!"".equals(shoppingOrderPaykey.getAccountMoneyPayKey())){
                        asset_biz_key = shoppingOrderPaykey.getAccountMoneyPayKey().split("_")[0];
                        orderPayInfo.setAssetBizKey(asset_biz_key);
                    }
                }
                //调用麦座接口扣除余额
                ShoppingOrderPaylog shoppingOrderPaylog = new ShoppingOrderPaylog();
                shoppingOrderPaylog.setTradeType("扣除余额");
                shoppingOrderPaylog.setLogInfo("扣除余额");
                shoppingOrderPaylog.setReqInfo(orderPayInfo.toString());
                shoppingOrderPaylog.setOfId(orderPayInfo.getOfId());
                Result<JSONObject> result = feignThirdService.cutMoney(orderPayInfo);
                shoppingOrderPaylog.setLogContent(result.toString());
                if(result.getRetCode().equals("0")){
                    shoppingOrderPay.setBalanceStatus(1);
                    shoppingOrderPayDao.update(shoppingOrderPay);
                }else{
                    shoppingOrderPay.setBalanceStatus(2);
                    shoppingOrderPayDao.update(shoppingOrderPay);
                    log.error("订单{}扣除余额失败：", orderPayInfo.getOrderId());
                    throw new PaymentException("扣除余额失败");
                }
            }
        }else{
            //待扣除余额数值为0时，直接标识余额支付成功
            if (shoppingOrderPay.getBalanceStatus() == 0 || shoppingOrderPay.getBalanceStatus() == 2) {
                shoppingOrderPay.setBalanceStatus(1);
                shoppingOrderPayDao.update(shoppingOrderPay);
            }
        }
    }

    /**
     * 积分扣除
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void costIntegral(OrderPayInfo orderPayInfo, ShoppingOrderPay shoppingOrderPay) {
        if(orderPayInfo.getDeductionIntegral() > 0){
            if (shoppingOrderPay.getIntegralStatus() == 0 || shoppingOrderPay.getIntegralStatus() == 2) {
                //如果积分数额超过限额，则需要传递“资产使用业务key”，该key值之前在调用验证码接口时已经存入数据库
                String asset_biz_key = null;
                ShoppingOrderPaykey shoppingOrderPaykey = new ShoppingOrderPaykey();
                shoppingOrderPaykey.setOfId(orderPayInfo.getOfId());
                shoppingOrderPaykey = shoppingOrderPaykeyDao.queryDetail(shoppingOrderPaykey);
                if (null != shoppingOrderPaykey) {
                    if(null !=shoppingOrderPaykey.getAccountPointPayKey()&&!"".equals(shoppingOrderPaykey.getAccountPointPayKey())){
                        String str = shoppingOrderPaykey.getAccountPointPayKey();
                        //余额扣除在积分扣除之前，如果余额扣除也使用了资产key，则积分扣除这边需要重新再获取一次key
                        if(null !=shoppingOrderPaykey.getAccountMoneyPayKey()&&!"".equals(shoppingOrderPaykey.getAccountMoneyPayKey())){
                            String verifyCode = str.split("_")[1];
                            Result<JSONObject> result =feignThirdService.checkVerifyCode(orderPayInfo.getMzUserId(),verifyCode);
                            asset_biz_key =result.getBizData().getString("asset_biz_key");
                        }else{
                            asset_biz_key = str.split("_")[0];
                        }
                        orderPayInfo.setAssetBizKey(asset_biz_key);
                    }
                }
                //调用麦座接口扣除积分
                Result<JSONObject> result = feignThirdService.cutPoint(orderPayInfo);
                if(result.getRetCode().equals("0")){
                    shoppingOrderPay.setIntegralStatus(1);
                    shoppingOrderPayDao.update(shoppingOrderPay);
                }else{
                    shoppingOrderPay.setIntegralStatus(2);
                    shoppingOrderPayDao.update(shoppingOrderPay);
                    log.error("订单{}扣除积分失败：", orderPayInfo.getOrderId());
                    throw new PaymentException("扣除积分失败");
                }
            }
        }else{
            if (shoppingOrderPay.getIntegralStatus() == 0 || shoppingOrderPay.getIntegralStatus() == 2) {
                shoppingOrderPay.setIntegralStatus(1);
                shoppingOrderPayDao.update(shoppingOrderPay);
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public String costCoupon(OrderPayInfo orderPayInfo, ShoppingOrderPay shoppingOrderPay) {
        if(StringUtils.isNotBlank(orderPayInfo.getCouponId())){
            if (shoppingOrderPay.getCouponStatus() == 0 || shoppingOrderPay.getCouponStatus() == 2) {
                //调用麦座接口扣除优惠券
                Result<String> result = feignThirdService.writeoffCoupon(orderPayInfo.getCouponId());
                if(result.getRetCode().equals("0")){
                    shoppingOrderPay.setCouponStatus(1);
                    shoppingOrderPayDao.update(shoppingOrderPay);
                    return result.getBizData();
                }else{
                    shoppingOrderPay.setCouponStatus(2);
                    shoppingOrderPayDao.update(shoppingOrderPay);
                    log.error("订单{}核销优惠券失败：", orderPayInfo.getOrderId());
                    throw new PaymentException("核销优惠券失败");
                }
            }
        }
        return null;
    }

    @Async("logExecutor")
    public void addOrderPayLog(ShoppingOrderPaylog shoppingOrderPaylog){
        shoppingOrderPaylogDao.insert(shoppingOrderPaylog);
    }
}
