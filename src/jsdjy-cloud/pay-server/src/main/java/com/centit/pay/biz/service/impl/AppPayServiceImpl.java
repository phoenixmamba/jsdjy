package com.centit.pay.biz.service.impl;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.pay.biz.dao.ShoppingOrderPaykeyDao;
import com.centit.pay.biz.dao.ShoppingOrderformDao;
import com.centit.pay.biz.dao.ShoppingPaymentDao;
import com.centit.pay.biz.po.ShoppingOrderPaykey;
import com.centit.pay.biz.po.ShoppingOrderform;
import com.centit.pay.biz.po.ShoppingPayment;
import com.centit.pay.biz.po.ShoppingUser;
import com.centit.pay.biz.service.AppPayService;
import com.centit.pay.common.contst.PayConfig;
import com.centit.pay.common.enums.Const;
import com.centit.pay.utils.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 速停车服务实现类
 * @Date : 2021-01-22
 **/
@Transactional
@Service
public class AppPayServiceImpl implements AppPayService {
    public static final Log log = LogFactory.getLog(AppPayService.class);

    @Resource
    private ShoppingOrderformDao shoppingOrderformDao;

    @Resource
    private ShoppingPaymentDao shoppingPaymentDao;

    @Resource
    private ShoppingOrderPaykeyDao shoppingOrderPaykeyDao;

    @Value("${pay.istest}")
    private Boolean istest;


    /**
     * 吊起支付（微信/支付宝）
     */
    @Override
    public JSONObject InPayment(JSONObject reqJson,HttpServletRequest request) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "服务器内部错误！";
        JSONObject bizDataJson = new JSONObject();
        try {

//            String data = request.getParameter("data");// 参数
//            JSONObject reqJson = JSONObject.parseObject(URLDecoder.decode(data, "utf-8"));
            //userId
            String userId = reqJson.getString("userId");
            //订单id
            String orderId = reqJson.getString("orderId");
            //用户选择的第三方支付方式
            String payment = reqJson.getString("payment");

            ShoppingOrderform orderform = new ShoppingOrderform();
            orderform.setOrderId(orderId);
            orderform = shoppingOrderformDao.queryDetail(orderform);
            int i=orderform.getPayPrice().compareTo(BigDecimal.ZERO);
            if(i<=0){
                retCode = "2001";
                retMsg = "无需调用第三方支付！";
            }else{
                //获取支付配置
                ShoppingPayment shoppingPayment = new ShoppingPayment();
                shoppingPayment.setId(payment);
                shoppingPayment = shoppingPaymentDao.queryDetail(shoppingPayment);

                if (orderform.getOrderStatus() != 10) {
                    retCode = "2002";
                    retMsg = "当前订单不可支付，请重新确认订单状态！";
                }else{
                    //更新订单支付方式
                    orderform.setPaymentId(payment);

                    //微信APP支付
                    if ("wxpay".equals(shoppingPayment.getMark())) {
                        BigDecimal payPrice = orderform.getPayPrice();
                        if(istest){
                            payPrice = new BigDecimal("0.01");   //测试默认支付0.01元
                        }
                        PayConfig config = null;
                        //停车订单需要使用专门的微信账号
                        if(orderform.getOrderId().startsWith(Const.PARK_ORDER_PREFIX)){
                            shoppingPayment.setId(Const.PARK_PAYID_WX);  //停车专用的微信支付方式id
                            shoppingPayment = shoppingPaymentDao.queryDetail(shoppingPayment);
                            config = new PayConfig(orderId, orderform.getAddTime(),payPrice, shoppingPayment.getWeixinAppid(), shoppingPayment.getWeixinPartnerid(), shoppingPayment.getWeixinPartnerkey());
                        }else{
                            config = new PayConfig(orderId, orderform.getAddTime(),payPrice, shoppingPayment.getWeixinAppid(), shoppingPayment.getWeixinPartnerid(), shoppingPayment.getWeixinPartnerkey());

                        }

                        config.setPayTime(PayUtil.formatDate(new Date()));
                        config.setIp("127.0.0.1");
                        config.setType("shoppingOrder");   //回调时原样返回，后续可用于标记不同订单等，目前统一传递shoppingOrder
                        config.setBody("江苏大剧院App商城消费");
                        Map<Object, Object> map = PayService.wxAppPay(config, request);
                        bizDataJson.put("data_wx",map);
                    }else if("alipay".equals(shoppingPayment.getMark())){
                        String price = orderform.getPayPrice().toString();
                        if(istest){
                            price = "0.01";  //测试默认支付0.01元
                        }
                        String map = PayService.alipayAppPay(request, orderId,orderform.getAddTime(), "江苏大剧院App商城消费", "江苏大剧院App商城消费", price, "shoppingOrder");
                        bizDataJson.put("data_ali",map);
                    }

                    retCode = "0";
                    retMsg = "操作成功！";
                }
            }

        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 获取卖座验证码
     */
    @Override
    public JSONObject mzVerifycode(JSONObject reqJson,HttpServletRequest request) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String userId = reqJson.getString("userId");
            ShoppingUser user =CommonUtil.getShoppingUserByUserId(userId);
            String mzUserId = CommonUtil.getMzUserId(userId);
            String mobilePhone = "";
//            if(null==user.getMzuserid()||user.getMobile()==null||user.getMobile().trim().length()!=11){
//                mobilePhone = "13776407246";
//            }else{
//                if(mzUserId.equals("9253315")){
//                    mobilePhone = "13776407246";
//                }else{
//                    mobilePhone = user.getMobile().trim();
//                }
//            }
            mobilePhone = user.getMobile().trim();
            if(MZService.sendVerifyCode(mzUserId,mobilePhone)){
                bizDataJson.put("mobile_phone",mobilePhone);
                retCode = "0";
                retMsg = "操作成功！";
            }
        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 校验卖座验证码（获取资产业务key）
     */
    @Override
    public JSONObject checkVerifycode(JSONObject reqJson,HttpServletRequest request) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String userId = reqJson.getString("userId");
            String verifyCode = reqJson.getString("verifyCode");
            String mzUserId = CommonUtil.getMzUserId(userId);
            JSONObject dataObj= MZService.checkVerifyCode(mzUserId,verifyCode);
            if(null !=dataObj){
                bizDataJson.put("asset_biz_key",dataObj.getString("asset_biz_key"));
                bizDataJson.put("key_expire_time",dataObj.getString("key_expire_time"));

                retCode = "0";
                retMsg = "操作成功！";
            }
        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 获取订单支付是否结束
     */
    @Override
    public JSONObject orderPayStatus(String orderId,HttpServletRequest request) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingOrderform orderform = new ShoppingOrderform();
            orderform.setOrderId(orderId);
            orderform = shoppingOrderformDao.queryDetail(orderform);
            if(orderform.getOrderStatus()==15){
                bizDataJson.put("data",0);
            }else if(orderform.getOrderStatus()==-1||orderform.getOrderStatus()==10){
                bizDataJson.put("data",2);  //支付失败
            }else{
                bizDataJson.put("data",1);  //支付完成
            }

            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 获取麦座积分和余额免密限额值
     */
    @Override
    public JSONObject accountLimit(HttpServletRequest request) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            int point_avoid_limit = 0;   //积分支付免密限额；
            int account_avoid_limit = 0;
            //从卖座实时查询积分和余额免密限额
            JSONObject limitObj = MZService.getAssetRule();
            if (null != limitObj) {
                point_avoid_limit = Integer.valueOf(limitObj.getJSONObject("point_risk_rule").getString("point_avoid_limit"));   //积分支付免密限额；
                account_avoid_limit = Integer.valueOf(limitObj.getJSONObject("account_risk_rule").getString("account_avoid_limit"));
                //接口返回的免密限额的单位是分，需要转换为元
                bizDataJson.put("accountPointLimit", point_avoid_limit);
                bizDataJson.put("accountMoneyLimit", new BigDecimal(account_avoid_limit));
            }

            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 更新订单资产业务key
     */
    @Override
    public JSONObject updateOrderPayKey(JSONObject reqJson,HttpServletRequest request) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String ofId = reqJson.getString("ofId");
            ShoppingOrderPaykey shoppingOrderPaykey = new ShoppingOrderPaykey();
            //积分支付限额验证码
            if (null != reqJson.get("accountPointPayKey") && !"".equals(reqJson.get("accountPointPayKey"))) {
                shoppingOrderPaykey.setAccountPointPayKey(reqJson.getString("accountPointPayKey"));
            }
            //余额支付限额验证码
            if (null != reqJson.get("accountMoneyPayKey") && !"".equals(reqJson.get("accountMoneyPayKey"))) {
                shoppingOrderPaykey.setAccountMoneyPayKey(reqJson.getString("accountMoneyPayKey"));
            }
            shoppingOrderPaykey.setOfId(ofId);
            ShoppingOrderPaykey paykey = shoppingOrderPaykeyDao.queryDetail(shoppingOrderPaykey);
            if(null ==paykey){
                shoppingOrderPaykeyDao.insert(shoppingOrderPaykey);
            }else{
                shoppingOrderPaykeyDao.update(shoppingOrderPaykey);
            }

            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 获取系统支付方式
     */
    @Override
    public JSONObject queryPayments(JSONObject reqJson,HttpServletRequest request) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            //获取当前系统可用支付方式
            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("deleteStatus", 0);
            if(StringUtil.isNotNull(reqJson.get("orderId"))&&reqJson.getString("orderId").startsWith(Const.PARK_ORDER_PREFIX)){
                reqMap.put("id", "21");
            }
            List<ShoppingPayment> payments = shoppingPaymentDao.queryList(reqMap);
            JSONArray payArray = new JSONArray();
            for (ShoppingPayment shoppingPayment : payments) {
                JSONObject obj = new JSONObject();
                obj.put("id", shoppingPayment.getId());
                obj.put("name", shoppingPayment.getName());
                payArray.add(obj);
            }
            bizDataJson.put("payments", payArray);

            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }
}
