package com.centit.pay.schedule;

import com.centit.pay.biz.dao.ShoppingOrderPayDao;
import com.centit.pay.biz.dao.ShoppingOrderformDao;
import com.centit.pay.biz.po.ShoppingOrderPay;
import com.centit.pay.biz.po.ShoppingOrderform;
import com.centit.pay.common.enums.Const;
import com.centit.pay.utils.CommonUtil;
import com.centit.pay.utils.MZService;
import com.centit.pay.utils.StringUtil;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

/**
 * @author ：cui_jian
 * @version ：1.0
 * @date ：Created in 2020/8/25 14:25
 * @description ：关闭超时未支付的订单
 */
@Component
public class CheckAbnormalOrderTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(CheckAbnormalOrderTask.class);

    @Resource
    private ShoppingOrderformDao shoppingOrderformDao;

    @Resource
    private ShoppingOrderPayDao shoppingOrderPayDao;

    @Value("${order.orderState.anomalous}")
    private int orderStateAnomalous;

    @Value("${payment.wxpay}")
    private String paymentWxpay;
    @Value("${payment.wxpay_park}")
    private String paymentParkWxpay;
    @Value("${payment.alipay}")
    private String paymentAlipay;


    @Scheduled(cron = "0 0/5 * * * ?")
    @SchedulerLock(name = "CheckAbnormalOrderTask",
            lockAtMostFor = 10 * 60 * 1000, lockAtLeastFor = 2 * 60 * 1000)
    public void scheduledTask() {
        confimOrder();
    }

    public void confimOrder(){
        LOGGER.info("扫描异常订单-定时任务开始");
        try{
            HashMap<String, Object> reqMap =new HashMap<>();
            reqMap.put("orderStatus", orderStateAnomalous);   //异常订单
            reqMap.put("orderType", 2);
            reqMap.put("deleteStatus","0");
            reqMap.put("startTime",StringUtil.nowTimePlusMinutes(-15));
            reqMap.put("endTime",StringUtil.nowTimePlusMinutes(15));
            List<ShoppingOrderform> orderList =shoppingOrderformDao.queryList(reqMap);
            for(ShoppingOrderform orderform:orderList){
                //确认订单
                //查询订单现金支付是否已完成
                ShoppingOrderPay shoppingOrderPay = new ShoppingOrderPay();
                shoppingOrderPay.setOfId(orderform.getId());
                shoppingOrderPay = shoppingOrderPayDao.queryDetail(shoppingOrderPay);
                if(shoppingOrderPay.getCashStatus()==1||shoppingOrderPay.getCashStatus()==-1){   //现金支付已完成或者订单本身不需要支付现金
                    String paymentId= orderform.getPaymentId();
                    String mzPayId="";
                    if(StringUtil.isNotNull(paymentId)&&paymentId.equals(paymentAlipay)){
                        mzPayId=Const.MZ_PAYID_ALI;
                    }else{
                        mzPayId=Const.MZ_PAYID_WX;
                    }
                    if (MZService.confirmOrder(orderform, mzPayId, shoppingOrderPay.getOutTradeNo(),CommonUtil.getMzUserId(orderform.getUserId()))) {
                        //调用麦座接口确认订单成功，则直接将订单的积分、余额和优惠券相应支付状态置为成功
                        if (orderform.getDeductionIntegralPrice().compareTo(BigDecimal.ZERO) == 1) {
                            shoppingOrderPay.setIntegralStatus(1);
                        }
                        if (orderform.getDeductionBalancePrice().compareTo(BigDecimal.ZERO) == 1) {
                            shoppingOrderPay.setBalanceStatus(1);
                        }
                        if (orderform.getDeductionCouponPrice().compareTo(BigDecimal.ZERO) == 1) {
                            shoppingOrderPay.setCouponStatus(1);
                        }
                        shoppingOrderPayDao.update(shoppingOrderPay);
                        //演出票订单与麦座确认成功，即可直接更新订单状态状态为已完成
                        orderform.setOrderStatus(Const.ORDER_STATE_HASDONE);
                        orderform.setFinishTime(StringUtil.nowTimeString());
                        shoppingOrderformDao.update(orderform);

                    } else {
                        //调用麦座接口确认支付失败，更新订单状态为异常
                        orderform.setMsg("调用麦座订单确认接口失败");
                        orderform.setOrderStatus(Const.ORDER_STATE_ANOMALOUS);
                        shoppingOrderformDao.update(orderform);
                    }
                }
            }
            LOGGER.info("扫描异常订单-定时任务结束");
        }catch (Exception e){
            e.printStackTrace();
            LOGGER.error("扫描异常订单-定时任务出现异常");
        }
    }


//    public static void main(String[] args) throws ParseException {
//        int total = 7011;
//        int num = 100;
//        System.out.println(total / num);
//    }

}