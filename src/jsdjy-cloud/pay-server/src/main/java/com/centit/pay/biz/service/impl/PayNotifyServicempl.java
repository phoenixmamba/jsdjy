package com.centit.pay.biz.service.impl;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.centit.pay.biz.dao.*;
import com.centit.pay.biz.po.*;
import com.centit.pay.biz.service.PayNotifyService;
import com.centit.pay.common.contst.Pay;
import com.centit.pay.common.enums.Const;
import com.centit.pay.utils.*;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p><p>
 *
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 速停车服务实现类
 * @Date : 2021-01-22
 **/
//@Transactional
@Service
public class PayNotifyServicempl implements PayNotifyService {
    public static final Log log = LogFactory.getLog(PayNotifyService.class);

    private static final String url ="http://api.jsmsxx.com:8030/service/httpService/httpInterface.do";


    @Resource
    private ShoppingOrderformDao shoppingOrderformDao;

    @Resource
    private ShoppingOrderLogDao shoppingOrderLogDao;

    @Resource
    private ShoppingPaymentDao shoppingPaymentDao;

    @Resource
    private ShoppingOrderPayDao shoppingOrderPayDao;

    @Resource
    private ParkOrderDao parkOrderDao;

    @Resource
    private ShoppingOrderPaylogDao shoppingOrderPaylogDao;

    @Resource
    private ShoppingOrderPaykeyDao shoppingOrderPaykeyDao;
    @Resource
    private TOnOndemandhistoryDao tOnOndemandhistoryDao;
    @Resource
    private ShoppingRechargeDao shoppingRechargeDao;
    @Resource
    private ShoppingCouponUsertempDao shoppingCouponUsertempDao;
    @Resource
    private ShoppingGoodscartDao shoppingGoodscartDao;

    @Resource
    private ShoppingRechargeActivityDao shoppingRechargeActivityDao;
    @Resource
    private ShoppingRechargeActivityRecordDao shoppingRechargeActivityRecordDao;

    @Resource
    private ShoppingCouponRecordDao shoppingCouponRecordDao;

    @Resource
    private ShoppingGoodsDao shoppingGoodsDao;
    @Resource
    private ShoppingArtactivityDao shoppingArtactivityDao;
    @Resource
    private ShoppingArtplanDao shoppingArtplanDao;

    @Value("${moneyToIntegralScale}")
    private int moneyToIntegralScale;
    @Value("${park.api.getParkingPaymentInfo}")
    private String parkApiGetParkingPaymentInfo;
    @Value("${park.api.payParkingFee}")
    private String parkApiPayParkingFee;
    @Value("${park.api.getOrderStatus}")
    private String parkApiGetOrderStatus;
    @Value("${park.api.getParkingPaymentList}")
    private String parkApiGetParkingPaymentList;
    @Value("${park.appId}")
    private String parkAppID;
    @Value("${park.appSecret}")
    private String parkAppSecret;
    @Value("${park.parkId}")
    private int parkId;

    @Value("${payment.wxpay}")
    private String paymentWxpay;
    @Value("${payment.wxpay_park}")
    private String paymentParkWxpay;
    @Value("${payment.alipay}")
    private String paymentAlipay;

    @Value("${pay.istest}")
    private Boolean istest;

    /**
     * 微信支付回调
     */
    public void wxNotify(HttpServletRequest request, HttpServletResponse response) {
        String resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>" + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
        try {
            //解析微信回调数据
            ServletInputStream in = request.getInputStream();
            int size = request.getContentLength();
            byte[] bdata = new byte[size];
            in.read(bdata);
            String xmlstring = new String(bdata, StringUtil.getCharacterEncoding(request, response));

            //记录微信支付回调信息
            ShoppingOrderPaylog shoppingOrderPaylog = new ShoppingOrderPaylog();
            shoppingOrderPaylog.setTradeType("微信支付");
            shoppingOrderPaylog.setIp(PayUtil.getIpAddr(request));
            shoppingOrderPaylog.setLogInfo("微信回调通知");
            if (!CommUtil.isNull(xmlstring)) {
                shoppingOrderPaylog.setLogContent(xmlstring);
                log.info("【微信回调通知】微信回调报文接收成功");
                //获取支付配置
                ShoppingPayment shoppingPayment = new ShoppingPayment();
                shoppingPayment.setId(paymentWxpay);
                shoppingPayment = shoppingPaymentDao.queryDetail(shoppingPayment);
                //签名校验
                if(!PayUtil.checkNotifySign(xmlstring,shoppingPayment.getWeixinPartnerkey())){
                    shoppingOrderPaylog.setStateInfo("[微信回调通知]支付失败，签名校验失败");
                    shoppingOrderPaylogDao.insert(shoppingOrderPaylog);
                    log.info("【微信回调通知】支付失败，签名校验失败");
                    resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>" + "<return_msg><![CDATA[签名校验失败]]></return_msg>" + "</xml> ";
                }else{
                    Map<String, String> paramMap = PayUtil.doXMLParse(xmlstring);
                    //打印返回结果
                    for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                        log.info(entry.getKey() + ":" + entry.getValue());
                    }
                    String orderid = paramMap.get("out_trade_no");
                    if (!CommUtil.isNull(orderid)){  //大剧院的订单标识
                        ShoppingOrderform orderform = new ShoppingOrderform();
                        orderform.setOrderId(orderid);
                        orderform = shoppingOrderformDao.queryDetail(orderform);
                        if(null !=orderform){
                            resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>" + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";
                            shoppingOrderPaylog.setOfId(orderform.getId());   //记录本次支付关联的订单id
                            if ("SUCCESS".equals(paramMap.get("return_code").toUpperCase()) && "SUCCESS".equals(paramMap.get("result_code").toUpperCase())) {
                                String out_order_id = paramMap.get("transaction_id");
                                int total_fee = Integer.valueOf(paramMap.get("total_fee"));
//                                boolean f = true;
                                //校验订单金额
                                boolean moneyCheck = true;
                                //测试环境由于支付金额统一为0.01，与订单金额不一致，因此不做校验
                                if(!istest){
                                    moneyCheck=checkWxMoney(orderform,total_fee);
                                }
                                if(moneyCheck){
                                    //保存回调日志
                                    shoppingOrderPaylog.setOutTradeNo(out_order_id);
                                    shoppingOrderPaylog.setStateInfo("[微信回调通知]支付成功");
                                    shoppingOrderPaylogDao.insert(shoppingOrderPaylog);

                                    //查询订单状态信息并更新订单现金支付为已完成
                                    ShoppingOrderPay shoppingOrderPay = new ShoppingOrderPay();
                                    shoppingOrderPay.setOfId(orderform.getId());
                                    shoppingOrderPay = shoppingOrderPayDao.queryDetail(shoppingOrderPay);
                                    shoppingOrderPay.setCashStatus(1);   //标识现金支付完成
                                    shoppingOrderPay.setOutTradeNo(out_order_id);   //第三方支付的订单标识
                                    shoppingOrderPayDao.update(shoppingOrderPay);


                                    orderform.setPayTime(StringUtil.nowTimeString());  //订单付款时间
                                    //完成订单剩余的支付并更新订单相应状态
                                    //只有处于待支付状态的订单才进行后续支付处理(演出订单以麦座订单状态为准，只要有支付回调，并且订单不是已完成状态，就需要处理)
                                    if (orderform.getOrderStatus() == Const.ORDER_STATE_TOPAY||(orderform.getOrderId().startsWith(Const.TICKET_ORDER_PREFIX)&&orderform.getOrderStatus() != Const.ORDER_STATE_HASDONE)) {
                                        orderform.setPaymentId(paymentWxpay);  //设置订单支付方式为微信支付
                                        //更新订单状态为支付中,避免多次回调引发数据冲突
                                        orderform.setOrderStatus(Const.ORDER_STATE_INPAY);
                                        shoppingOrderformDao.update(orderform);
////
                                        //校验账户积分或者余额是否足够，如果不够，更新订单状态为异常
                                        //演出票订单不需要校验，因为目前演出票订单在下单时会提前扣除积分和余额
                                        if (!orderform.getOrderId().startsWith(Const.TICKET_ORDER_PREFIX)&&checkAccountStatus(orderform) == 1) {
                                            // 添加订单支付日志
                                            ShoppingOrderLog shoppingOrderLog = new ShoppingOrderLog();
                                            shoppingOrderLog.setStateInfo("[积分或余额]支付失败");
                                            shoppingOrderLog.setLogInfo("账户积分或余额不足");
                                            shoppingOrderLog.setLogUserId(orderform.getUserId());
                                            shoppingOrderLog.setOfId(orderform.getId());
                                            shoppingOrderLogDao.insert(shoppingOrderLog);

                                            orderform.setMsg("账户积分或余额不足");   //记录异常信息
                                            orderform.setOrderStatus(Const.ORDER_STATE_ANOMALOUS);
                                            shoppingOrderformDao.update(orderform);
                                            sendAbnormalMsg();
                                        } else {  //处理余额、积分、优惠券的支付和核销
                                            //如果是演出票订单，需要调用麦座确认订单接口，核销相应的积分、余额、优惠券
                                            if (orderform.getOrderId().startsWith(Const.TICKET_ORDER_PREFIX)) {
                                                if (MZService.confirmOrder(orderform, Const.MZ_PAYID_WX,out_order_id, CommonUtil.getMzUserId(orderform.getUserId()))) {
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

                                                } else {
                                                    //调用麦座接口确认支付失败，更新订单状态为异常
                                                    orderform.setMsg("调用麦座订单确认接口失败");
                                                    orderform.setOrderStatus(Const.ORDER_STATE_ANOMALOUS);
                                                    sendAbnormalMsg();
                                                }
                                            }

                                            //非演出票订单的余额、积分、优惠券的核销，需要本地处理
                                            if (!orderform.getOrderId().startsWith(Const.TICKET_ORDER_PREFIX)) {
                                                //第三方交互，扣除余额
                                                costBalance(orderform, shoppingOrderPay);
                                                //第三方交互，扣除积分
                                                costIntegral(orderform, shoppingOrderPay);
                                                //核销优惠券
                                                costCoupon(orderform, shoppingOrderPay);

                                                //判断订单是否所有支付项均支付成功，如果是，更新相应订单状态
                                                if (checkPayStatus(orderform)) {
                                                    // 更新订单状态为已付款
                                                    orderform.setOrderStatus(Const.ORDER_STATE_HASPAY);
                                                    //给商户发短信
                                                    sendMsg(orderform);

                                                    // 添加订单日志
                                                    ShoppingOrderLog shoppingOrderLog = new ShoppingOrderLog();
                                                    shoppingOrderLog.setLogInfo("订单支付成功");
                                                    shoppingOrderLog.setLogUserId(orderform.getUserId());
                                                    shoppingOrderLog.setOfId(orderform.getId());
                                                    shoppingOrderLogDao.insert(shoppingOrderLog);

                                                    //如果是停车订单，通知第三方，更新订单状态状态为已完成
                                                    if (orderform.getOrderId().startsWith(Const.PARK_ORDER_PREFIX)) {
                                                        //通知第三方速停车订单支付成功
                                                        if(PayParkingFee(orderform)){
                                                            //标识订单已完成
                                                            orderform.setOrderStatus(Const.ORDER_STATE_HASDONE);
                                                            orderform.setFinishTime(StringUtil.nowTimeString());
                                                        }else{
                                                            //通知第三方失败，更新订单状态为异常
                                                            orderform.setMsg("向速停车确认订单支付完成失败");
                                                            orderform.setOrderStatus(Const.ORDER_STATE_ANOMALOUS);
                                                            sendAbnormalMsg();
                                                        }
                                                    }

                                                    //如果是点播订单，更新点播购买记录表响应状态，更新订单状态状态为已完成
                                                    if (orderform.getOrderId().startsWith(Const.VIDEO_ORDER)) {
                                                        //余额、积分、优惠券均核销成功，更新点播购买记录表状态
                                                        if(setOndemandBuyStatus(orderform)){
                                                            orderform.setOrderStatus(Const.ORDER_STATE_HASDONE);
                                                            orderform.setFinishTime(StringUtil.nowTimeString());
                                                        }else{
                                                            //更新点播购买记录表失败，更新订单状态为异常
                                                            orderform.setMsg("更新点播购买记录表失败");
                                                            orderform.setOrderStatus(Const.ORDER_STATE_ANOMALOUS);
                                                            sendAbnormalMsg();
                                                        }
                                                    }

                                                    //如果是充值订单，更新充值记录表状态，更新订单状态状态为已完成
                                                    if (orderform.getOrderId().startsWith(Const.RECHARGE_ORDER)) {
                                                        //调用麦座接口，进行余额充值
                                                        if(addMoney(orderform)){
                                                            orderform.setOrderStatus(Const.ORDER_STATE_HASDONE);
                                                            orderform.setFinishTime(StringUtil.nowTimeString());
                                                            //更新充值记录表状态
                                                            setRechargeStatus(orderform,1);

                                                        }else{
                                                            orderform.setMsg("调用麦座接口充值失败");
                                                            orderform.setOrderStatus(-1);
                                                            //更新充值记录表状态
                                                            setRechargeStatus(orderform,-1);
                                                        }
                                                    }

                                                    //如果是艺教订单，直接更新订单状态状态为已完成
                                                    if (orderform.getOrderId().startsWith(Const.SHOPPING_ACT_ORDER)||orderform.getOrderId().startsWith(Const.SHOPPING_PLAN_ORDER)||orderform.getOrderId().startsWith(Const.SHOPPING_CLASS_ORDER)) {
                                                        orderform.setOrderStatus(Const.ORDER_STATE_HASDONE);
                                                        orderform.setFinishTime(StringUtil.nowTimeString());
                                                    }

                                                    //如果是合并支付订单
                                                    if (orderform.getOrderId().startsWith(Const.SHOPPING_MERGE_ORDER)) {
                                                        HashMap<String, Object> reqMap = new HashMap<>();
                                                        reqMap.put("ofId",orderform.getId());
                                                        reqMap.put("deleteStatus","0");
                                                        List<ShoppingGoodscart> cartGoods = shoppingGoodscartDao.queryList(reqMap);
                                                        orderform.setOrderStatus(Const.ORDER_STATE_HASDONE);
                                                        for(ShoppingGoodscart shoppingGoodscart:cartGoods){
                                                            //如果合并订单中存在文创或者积分订单，则只能将订单状态置为已付款，不能直接置为已完成
                                                            if(shoppingGoodscart.getCartType()!=null&&(shoppingGoodscart.getCartType().equals(Const.SHOPPING_CUL_CART_TYPE)||shoppingGoodscart.getCartType().equals(Const.SHOPPING_INT_CART_TYPE))){
                                                                orderform.setOrderStatus(Const.ORDER_STATE_HASPAY);
                                                                break;
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    //积分、余额或优惠券支付失败，更新订单状态为异常
                                                    orderform.setMsg("积分或余额或优惠券支付失败，请检查订单支付信息");
                                                    orderform.setOrderStatus(Const.ORDER_STATE_ANOMALOUS);
                                                    sendAbnormalMsg();
                                                }
                                            }

                                            shoppingOrderformDao.update(orderform);
                                        }
                                    }
                                    else if(orderform.getOrderStatus() == Const.ORDER_STATE_CALCEL){   //如果该订单已超时取消，则需要转为异常订单

                                        //更新订单状态为异常
                                        orderform.setMsg("微信支付回调成功，但该订单已经超时取消");
                                        orderform.setOrderStatus(-1);
                                        shoppingOrderformDao.update(orderform);
                                        sendAbnormalMsg();
                                    }
                                    else if(StringUtils.isNotBlank(orderform.getPaymentId())&&!orderform.getPaymentId().equals(paymentWxpay)){
                                        //更新订单状态为异常
                                        orderform.setMsg("已使用其他支付方式支付");
                                        orderform.setOrderStatus(-1);
                                        sendAbnormalMsg();
                                        shoppingOrderformDao.update(orderform);
                                    }
                                }else{
                                    shoppingOrderPaylog.setStateInfo("微信回调通知】支付失败，订单金额校验失败");
                                    shoppingOrderPaylogDao.insert(shoppingOrderPaylog);
                                    log.info("【微信回调通知】支付失败，订单金额和appId校验失败");
                                }

                            }
                            else {
                                shoppingOrderPaylog.setStateInfo("微信回调通知】支付失败");
                                shoppingOrderPaylogDao.insert(shoppingOrderPaylog);
                                log.info("【微信回调通知】支付失败，报错信息【" + paramMap.get("return_msg") + "】");
                            }
                        }else{
                            shoppingOrderPaylog.setStateInfo("微信回调通知】支付失败，订单信息不存在");
                            shoppingOrderPaylogDao.insert(shoppingOrderPaylog);
                            log.info("【微信回调通知】支付失败，订单信息不存在");
                            resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>" + "<return_msg><![CDATA[订单信息不存在]]></return_msg>" + "</xml> ";
                        }

                    }else{
                        shoppingOrderPaylog.setStateInfo("微信回调通知】支付失败，订单标识out_trade_no为空");
                        shoppingOrderPaylogDao.insert(shoppingOrderPaylog);
                        log.info("【微信回调通知】支付失败，订单标识out_trade_no为空");
                        resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>" + "<return_msg><![CDATA[out_trade_no为空]]></return_msg>" + "</xml> ";
                    }
                }
            } else {
                shoppingOrderPaylog.setStateInfo("微信回调通知】支付失败，回调报文为空");
                shoppingOrderPaylogDao.insert(shoppingOrderPaylog);
                log.info("【微信回调通知】支付失败，回调报文为空");
                resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>" + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
            }
        } catch (Exception e) {
            log.info("【微信回调通知】内部异常");
            e.printStackTrace();
        } finally {
            PayUtil.sendToCFT(resXml, response);
        }
    }

    /**
     * 停车专用微信支付回调
     */
    public void wxParkNotify(HttpServletRequest request, HttpServletResponse response) {
        String resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>" + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
        try {
            ServletInputStream in = request.getInputStream();
            int size = request.getContentLength();
            byte[] bdata = new byte[size];
            in.read(bdata);
            String xmlstring = new String(bdata, StringUtil.getCharacterEncoding(request, response));

            ShoppingOrderPaylog shoppingOrderPaylog = new ShoppingOrderPaylog();
            shoppingOrderPaylog.setTradeType("微信支付");
            shoppingOrderPaylog.setIp(PayUtil.getIpAddr(request));
            shoppingOrderPaylog.setLogInfo("微信回调通知");
            if (!CommUtil.isNull(xmlstring)) {
                shoppingOrderPaylog.setLogContent(xmlstring);
                log.info("【微信回调通知】微信回调报文接收成功");
                //获取支付配置
                ShoppingPayment shoppingPayment = new ShoppingPayment();
                shoppingPayment.setId(paymentParkWxpay);
                shoppingPayment = shoppingPaymentDao.queryDetail(shoppingPayment);
                //签名校验
                if(!PayUtil.checkNotifySign(xmlstring,shoppingPayment.getWeixinPartnerkey())){
                    shoppingOrderPaylog.setStateInfo("微信回调通知】支付失败，签名校验失败");
                    shoppingOrderPaylogDao.insert(shoppingOrderPaylog);
                    log.info("【微信回调通知】支付失败，签名校验失败");
                    resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>" + "<return_msg><![CDATA[签名校验失败]]></return_msg>" + "</xml> ";
                }else{
                    Map<String, String> paramMap = PayUtil.doXMLParse(xmlstring);
                    //打印返回结果
                    for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                        log.info(entry.getKey() + ":" + entry.getValue());
                    }
                    String orderid = paramMap.get("out_trade_no");
                    if (!CommUtil.isNull(orderid)){

                        ShoppingOrderform orderform = new ShoppingOrderform();
                        orderform.setOrderId(orderid);
                        orderform = shoppingOrderformDao.queryDetail(orderform);
                        if(null !=orderform){
                            resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>" + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";

                            shoppingOrderPaylog.setOfId(orderform.getId());
                            if ("SUCCESS".equals(paramMap.get("return_code").toUpperCase()) && "SUCCESS".equals(paramMap.get("result_code").toUpperCase())) {
                                String out_order_id = paramMap.get("transaction_id");
                                int total_fee = Integer.valueOf(paramMap.get("total_fee"));
                                orderform.setPayTime(StringUtil.nowTimeString());  //付款时间
                                orderform.setPaymentId(paymentParkWxpay); //设置订单支付方式为微信支付

                                ShoppingOrderPay shoppingOrderPay = new ShoppingOrderPay();
                                shoppingOrderPay.setOfId(orderform.getId());
                                shoppingOrderPay = shoppingOrderPayDao.queryDetail(shoppingOrderPay);

                                // 更新现金支付状态为已付款
                                shoppingOrderPay.setCashStatus(1);
                                shoppingOrderPay.setOutTradeNo(out_order_id);
                                shoppingOrderPayDao.update(shoppingOrderPay);

                                //保存回调日志
                                shoppingOrderPaylog.setOutTradeNo(out_order_id);
                                shoppingOrderPaylog.setStateInfo("【微信回调通知】支付成功");
                                shoppingOrderPaylogDao.insert(shoppingOrderPaylog);

                                //完成订单剩余的支付并更新订单相应状态
                                //只有处于待支付状态的订单才进行后续支付处理
                                if (orderform.getOrderStatus() == Const.ORDER_STATE_TOPAY) {
                                    //更新订单状态为支付中
                                    orderform.setOrderStatus(Const.ORDER_STATE_INPAY);
                                    shoppingOrderformDao.update(orderform);

                                    //校验账户积分或者余额是否足够，如果不够，更新订单状态为异常
                                    if (checkAccountStatus(orderform) == 1) {
                                        // 添加订单支付日志
                                        ShoppingOrderLog shoppingOrderLog = new ShoppingOrderLog();
                                        shoppingOrderLog.setStateInfo("[积分或余额]支付失败");
                                        shoppingOrderLog.setLogInfo("账户积分或余额不足");
                                        shoppingOrderLog.setLogUserId(orderform.getUserId());
                                        shoppingOrderLog.setOfId(orderform.getId());
                                        shoppingOrderLogDao.insert(shoppingOrderLog);

                                        orderform.setMsg("账户积分或余额不够");
                                        orderform.setOrderStatus(Const.ORDER_STATE_ANOMALOUS);
                                        shoppingOrderformDao.update(orderform);
                                    } else {
                                        //处理余额、积分、优惠券信息
                                        //第三方交互，扣除余额
                                        costBalance(orderform, shoppingOrderPay);
                                        //第三方交互，扣除积分
                                        costIntegral(orderform, shoppingOrderPay);
                                        //核销优惠券
                                        costCoupon(orderform, shoppingOrderPay);

                                        //判断订单是否所有支付项均支付成功，如果是，进行下一步处理
                                        if (checkPayStatus(orderform)) {
                                            // 更新订单状态为已付款
                                            orderform.setOrderStatus(Const.ORDER_STATE_HASPAY);

                                            // 添加订单日志
                                            ShoppingOrderLog shoppingOrderLog = new ShoppingOrderLog();
                                            shoppingOrderLog.setLogInfo("订单支付成功");
                                            shoppingOrderLog.setLogUserId(orderform.getUserId());
                                            shoppingOrderLog.setOfId(orderform.getId());
                                            shoppingOrderLogDao.insert(shoppingOrderLog);

                                            //通知速停车订单支付成功
                                            if(PayParkingFee(orderform)){
                                                orderform.setOrderStatus(Const.ORDER_STATE_HASDONE);
                                                orderform.setFinishTime(StringUtil.nowTimeString());
                                            }else{
                                                //通知第三方失败，更新订单状态为异常
                                                orderform.setMsg("向速停车确认订单支付完成失败");
                                                orderform.setOrderStatus(Const.ORDER_STATE_ANOMALOUS);
                                                sendAbnormalMsg();
                                            }
                                        } else {
                                            //积分、余额或优惠券支付失败，更新订单状态为异常
                                            if (!orderform.getOrderId().startsWith(Const.TICKET_ORDER_PREFIX)){
                                                orderform.setMsg("积分或余额或优惠券支付失败，请检查订单支付信息");
                                                orderform.setOrderStatus(Const.ORDER_STATE_ANOMALOUS);
                                                sendAbnormalMsg();
                                            }
                                        }
                                        shoppingOrderformDao.update(orderform);
                                    }
                                }else if(orderform.getOrderStatus() == Const.ORDER_STATE_CALCEL){   //如果该订单已超时取消，则需要转为异常订单

                                    //更新订单状态为异常
                                    orderform.setMsg("微信支付回调成功，但该订单已经超时取消");
                                    orderform.setOrderStatus(Const.ORDER_STATE_ANOMALOUS);
                                    shoppingOrderformDao.update(orderform);
                                    sendAbnormalMsg();
                                }
                            }
                            else {
                                shoppingOrderPaylog.setStateInfo("微信回调通知】支付失败");
                                shoppingOrderPaylogDao.insert(shoppingOrderPaylog);
                                log.info("【微信回调通知】支付失败，报错信息【" + paramMap.get("return_msg") + "】");
                            }
                        }else{
                            shoppingOrderPaylog.setStateInfo("微信回调通知】支付失败，订单信息不存在");
                            shoppingOrderPaylogDao.insert(shoppingOrderPaylog);
                            log.info("【微信回调通知】支付失败，订单信息不存在");
                            resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>" + "<return_msg><![CDATA[订单信息不存在]]></return_msg>" + "</xml> ";
                        }

                    }else{
                        shoppingOrderPaylog.setStateInfo("微信回调通知】支付失败，订单标识out_trade_no为空");
                        shoppingOrderPaylogDao.insert(shoppingOrderPaylog);
                        log.info("【微信回调通知】支付失败，订单标识out_trade_no为空");
                        resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>" + "<return_msg><![CDATA[out_trade_no为空]]></return_msg>" + "</xml> ";
                    }
                }
            } else {
                shoppingOrderPaylog.setStateInfo("微信回调通知】支付失败，回调报文为空");
                shoppingOrderPaylogDao.insert(shoppingOrderPaylog);
                log.info("【微信回调通知】支付失败，回调报文为空");
                resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>" + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
            }
        } catch (Exception e) {
            log.info("【微信回调通知】内部异常");
            e.printStackTrace();
        } finally {
            PayUtil.sendToCFT(resXml, response);
        }
    }


    public void aliNotify(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("==============支付宝回调================");
        ShoppingOrderPaylog shoppingOrderPaylog = new ShoppingOrderPaylog();
        shoppingOrderPaylog.setTradeType("支付宝支付");
        shoppingOrderPaylog.setIp(PayUtil.getIpAddr(request));
        shoppingOrderPaylog.setLogInfo("支付宝回调通知");

        try{
            PrintWriter out = response.getWriter();
            try {
//                System.out.println("Pay.ALIPAY_PUBLIC_KEY===="+Pay.ALIPAY_PUBLIC_KEY);
//                System.out.println("Pay.CHARSET===="+Pay.CHARSET);
//                System.out.println("Pay.SIGN_TYPE===="+Pay.SIGN_TYPE);
                Map< String , String > params = new HashMap < String , String > ();
                Map requestParams = request.getParameterMap();
                for(Iterator iter = requestParams.keySet().iterator(); iter.hasNext();){
                    String name = (String)iter.next();
                    String[] values = (String [])requestParams.get(name);
                    String valueStr = "";
                    for(int i = 0;i < values.length;i ++ ){
                        valueStr =  (i==values.length-1)?valueStr + values [i]:valueStr + values[i] + ",";
                    }
                    //乱码解决，这段代码在出现乱码时使用。
                    //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
                    params.put (name,valueStr);
                }
                shoppingOrderPaylog.setLogContent(params.toString());
                //boolean AlipaySignature.rsaCheckV1(Map<String, String> params, String publicKey, String charset, String sign_type)
                //签名校验
                boolean flag = AlipaySignature.rsaCheckV1 (params,Pay.ALIPAY_PUBLIC_KEY, Pay.CHARSET,"RSA2");
                System.out.println("flag===="+flag);
                boolean f = false;
                if(!flag){
                    shoppingOrderPaylog.setStateInfo("支付宝回调通知】支付失败，签名校验失败");
                    shoppingOrderPaylogDao.insert(shoppingOrderPaylog);
                }
                else{
                    if (params != null && ("WAIT_SELLER_SEND_GOODS".equals(params.get("trade_status")) || "TRADE_FINISHED".equals(params.get("trade_status")) || "TRADE_SUCCESS".equals(params.get("trade_status")))) {

                        String orderid = params.get("out_trade_no");
                        String out_order_id = params.get("trade_no");
//                int total_fee = Integer.valueOf(params.get("total_amount"));
                        if (!CommUtil.isNull(orderid)) {   //大剧院订单id
                            ShoppingOrderform orderform = new ShoppingOrderform();
                            orderform.setOrderId(orderid);
                            orderform = shoppingOrderformDao.queryDetail(orderform);
                            if(null !=orderform){
                                //校验订单金额和app_id
                                boolean moneyCheck = true;
                                //测试环境由于支付金额统一为0.01，与订单金额不一致，因此不做校验
                                if(!istest){
                                    moneyCheck=checkAlipayParams(orderform,params);
                                }
                                if(moneyCheck){
                                    f = true;
                                    shoppingOrderPaylog.setOfId(orderform.getId());

                                    orderform.setPayTime(StringUtil.nowTimeString());  //付款时间

                                    shoppingOrderPaylog.setOutTradeNo(out_order_id);
                                    shoppingOrderPaylog.setStateInfo("【支付宝回调通知】支付成功");
                                    shoppingOrderPaylogDao.insert(shoppingOrderPaylog);

                                    ShoppingOrderPay shoppingOrderPay = new ShoppingOrderPay();
                                    shoppingOrderPay.setOfId(orderform.getId());
                                    shoppingOrderPay = shoppingOrderPayDao.queryDetail(shoppingOrderPay);
                                    // 更新现金支付状态为已付款
                                    shoppingOrderPay.setCashStatus(1);
                                    shoppingOrderPay.setOutTradeNo(out_order_id);
                                    shoppingOrderPayDao.update(shoppingOrderPay);
                                    //更新订单状态
                                    if (orderform.getOrderStatus() == Const.ORDER_STATE_TOPAY||(orderform.getOrderId().startsWith(Const.TICKET_ORDER_PREFIX)&&orderform.getOrderStatus() != Const.ORDER_STATE_HASDONE)) {
                                        //设置订单支付方式为zhifubao支付
                                        orderform.setPaymentId(paymentAlipay);
                                        //更新订单状态为支付中
                                        orderform.setOrderStatus(Const.ORDER_STATE_INPAY);
                                        shoppingOrderformDao.update(orderform);

                                        //校验账户积分或者余额是否足够，如果不够，更新订单状态为异常
                                        if (!orderform.getOrderId().startsWith(Const.TICKET_ORDER_PREFIX)&&checkAccountStatus(orderform) == 1) {
                                            // 添加订单支付日志
                                            ShoppingOrderLog shoppingOrderLog = new ShoppingOrderLog();
                                            shoppingOrderLog.setStateInfo("[积分或余额]支付失败");
                                            shoppingOrderLog.setLogInfo("账户积分或余额不足");
                                            shoppingOrderLog.setLogUserId(orderform.getUserId());
                                            shoppingOrderLog.setOfId(orderform.getId());
                                            shoppingOrderLogDao.insert(shoppingOrderLog);

                                            orderform.setMsg("账户积分或余额不够");
                                            orderform.setOrderStatus(Const.ORDER_STATE_ANOMALOUS);
                                            shoppingOrderformDao.update(orderform);
                                            sendAbnormalMsg();
                                        } else {
                                            //处理余额、积分、优惠券信息

                                            //如果是演出票订单，需要调用麦座确认订单接口，核销相应的积分、余额、优惠券
                                            if (orderform.getOrderId().startsWith(Const.TICKET_ORDER_PREFIX)) {
                                                if (MZService.confirmOrder(orderform, Const.MZ_PAYID_ALI,out_order_id,CommonUtil.getMzUserId(orderform.getUserId()))) {
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
//
//                                                // 更新订单状态为已付款
//                                                orderform.setOrderStatus(20);

                                                    // 添加订单日志
                                                    ShoppingOrderLog shoppingOrderLog = new ShoppingOrderLog();
                                                    shoppingOrderLog.setLogInfo("订单支付成功");
                                                    shoppingOrderLog.setLogUserId(orderform.getUserId());
                                                    shoppingOrderLog.setOfId(orderform.getId());
                                                    shoppingOrderLogDao.insert(shoppingOrderLog);

                                                    //演出票订单与麦座确认成功，即可直接更新订单状态状态为已完成
                                                    orderform.setOrderStatus(50);
                                                    orderform.setFinishTime(StringUtil.nowTimeString());

                                                } else {
                                                    //调用麦座接口失败，更新订单状态为异常
                                                    orderform.setMsg("调用麦座订单确认接口失败");
                                                    orderform.setOrderStatus(-1);
                                                    sendAbnormalMsg();
                                                }
                                            }

                                            //演出票订单的余额、积分、优惠券均由麦座核销，不需要本地处理
                                            if (!orderform.getOrderId().startsWith(Const.TICKET_ORDER_PREFIX)) {
                                                //第三方交互，扣除余额
                                                costBalance(orderform, shoppingOrderPay);
                                                //第三方交互，扣除积分
                                                costIntegral(orderform, shoppingOrderPay);
                                                //核销优惠券
                                                costCoupon(orderform, shoppingOrderPay);

                                                //判断订单是否所有支付项均支付成功，如果是，更新相应订单状态
                                                if (checkPayStatus(orderform)) {
                                                    // 更新订单状态为已付款
                                                    orderform.setOrderStatus(Const.ORDER_STATE_HASPAY);
                                                    //给商户发短信
                                                    sendMsg(orderform);

                                                    // 添加订单日志
                                                    ShoppingOrderLog shoppingOrderLog = new ShoppingOrderLog();
                                                    shoppingOrderLog.setLogInfo("订单支付成功");
                                                    shoppingOrderLog.setLogUserId(orderform.getUserId());
                                                    shoppingOrderLog.setOfId(orderform.getId());
                                                    shoppingOrderLogDao.insert(shoppingOrderLog);

                                                    //如果是停车订单，通知第三方，更新订单状态状态为已完成
                                                    if (orderform.getOrderId().startsWith(Const.PARK_ORDER_PREFIX)) {
                                                        //余额、积分、优惠券均核销成功，通知第三方速停车订单支付成功
                                                        if(PayParkingFee(orderform)){
                                                            orderform.setOrderStatus(Const.ORDER_STATE_HASDONE);
                                                            orderform.setFinishTime(StringUtil.nowTimeString());
                                                        }else{
                                                            //通知第三方失败，更新订单状态为异常
                                                            orderform.setMsg("向速停车确认订单支付完成失败");
                                                            orderform.setOrderStatus(Const.ORDER_STATE_ANOMALOUS);
                                                            sendAbnormalMsg();
                                                        }
                                                    }

                                                    //如果是点播订单，更新点播购买记录表响应状态，更新订单状态状态为已完成
                                                    if (orderform.getOrderId().startsWith(Const.VIDEO_ORDER)) {
                                                        //余额、积分、优惠券均核销成功，更新点播购买记录表状态
                                                        if(setOndemandBuyStatus(orderform)){
                                                            orderform.setOrderStatus(Const.ORDER_STATE_HASDONE);
                                                            orderform.setFinishTime(StringUtil.nowTimeString());
                                                        }else{
                                                            //通知第三方失败，更新订单状态为异常
                                                            orderform.setMsg("更新点播购买记录表失败");
                                                            orderform.setOrderStatus(Const.ORDER_STATE_ANOMALOUS);
                                                            sendAbnormalMsg();
                                                        }
                                                    }

                                                    //如果是充值订单，更新充值记录表状态，更新订单状态状态为已完成
                                                    if (orderform.getOrderId().startsWith(Const.RECHARGE_ORDER)) {
                                                        //调用麦座接口，进行余额充值
                                                        if(addMoney(orderform)){
                                                            orderform.setOrderStatus(Const.ORDER_STATE_HASDONE);
                                                            orderform.setFinishTime(StringUtil.nowTimeString());
                                                            //更新充值记录表状态
                                                            setRechargeStatus(orderform,1);
                                                        }else{
                                                            orderform.setMsg("调用麦座接口充值失败");
                                                            orderform.setOrderStatus(Const.ORDER_STATE_ANOMALOUS);
                                                            //更新充值记录表状态
                                                            setRechargeStatus(orderform,-1);
                                                        }
                                                    }

                                                    //如果是艺教订单，更新订单状态状态为已完成
                                                    if (orderform.getOrderId().startsWith(Const.SHOPPING_ACT_ORDER)||orderform.getOrderId().startsWith(Const.SHOPPING_PLAN_ORDER)||orderform.getOrderId().startsWith(Const.SHOPPING_CLASS_ORDER)) {
                                                        orderform.setOrderStatus(Const.ORDER_STATE_HASDONE);
                                                        orderform.setFinishTime(StringUtil.nowTimeString());
                                                    }

                                                    //如果是合并支付订单
                                                    if (orderform.getOrderId().startsWith(Const.SHOPPING_MERGE_ORDER)) {
                                                        HashMap<String, Object> reqMap = new HashMap<>();
                                                        reqMap.put("ofId",orderform.getId());
                                                        reqMap.put("deleteStatus","0");
                                                        List<ShoppingGoodscart> cartGoods = shoppingGoodscartDao.queryList(reqMap);
                                                        orderform.setOrderStatus(Const.ORDER_STATE_HASDONE);
                                                        for(ShoppingGoodscart shoppingGoodscart:cartGoods){
                                                            //如果合并订单中存在文创或者积分订单，则只能将订单状态置为已付款，不能直接置为已完成
                                                            if(shoppingGoodscart.getCartType()!=null&&(shoppingGoodscart.getCartType().equals(Const.SHOPPING_CUL_CART_TYPE)||shoppingGoodscart.getCartType().equals(Const.SHOPPING_INT_CART_TYPE))){
                                                                orderform.setOrderStatus(Const.ORDER_STATE_HASPAY);
                                                                break;
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    //积分、余额或优惠券支付失败，更新订单状态为异常
                                                    if (!orderform.getOrderId().startsWith(Const.TICKET_ORDER_PREFIX)){
                                                        orderform.setMsg("积分或余额或优惠券支付失败，请检查订单支付信息");
                                                        orderform.setOrderStatus(Const.ORDER_STATE_ANOMALOUS);
                                                        sendAbnormalMsg();
                                                    }
                                                }
                                            }




                                            shoppingOrderformDao.update(orderform);
                                        }

                                    }else if(orderform.getOrderStatus() == Const.ORDER_STATE_CALCEL){   //如果该订单已超时取消，则需要转为异常订单


                                        //更新订单状态为异常
                                        orderform.setMsg("微信支付回调成功，但该订单已经超时取消");
                                        orderform.setOrderStatus(-1);
                                        sendAbnormalMsg();
                                        shoppingOrderformDao.update(orderform);
                                    }
                                    else if(StringUtils.isNotBlank(orderform.getPaymentId())&&!orderform.getPaymentId().equals(paymentAlipay)){


                                        //更新订单状态为异常
                                        orderform.setMsg("已使用其他支付方式支付");
                                        orderform.setOrderStatus(-1);
                                        sendAbnormalMsg();
                                        shoppingOrderformDao.update(orderform);
                                    }
                                }else{
                                    shoppingOrderPaylog.setStateInfo("支付宝回调通知】支付失败，订单金额和appId校验失败");
                                    shoppingOrderPaylogDao.insert(shoppingOrderPaylog);
                                    log.info("【支付宝回调通知】支付失败，订单金额和appId校验失败");
                                }
                            }else{
                                shoppingOrderPaylog.setStateInfo("支付宝回调通知】支付失败，订单信息不存在");
                                shoppingOrderPaylogDao.insert(shoppingOrderPaylog);
                                log.info("【支付宝回调通知】支付失败，订单信息不存在");
                            }

                        }else{
                            shoppingOrderPaylog.setStateInfo("支付宝回调通知】支付失败，订单标识out_trade_no为空");
                            shoppingOrderPaylogDao.insert(shoppingOrderPaylog);
                            log.info("【支付宝回调通知】支付失败，订单标识out_trade_no为空");
                        }
                        if (f) {
                            out.println("SUCCESS");
                            out.flush();
                            return;
                        }
                        out.println("FAILURE");
                        out.flush();
                    }else{
                        shoppingOrderPaylog.setStateInfo("支付失败======{}====" + params.toString());
                        shoppingOrderPaylogDao.insert(shoppingOrderPaylog);
                        out.println("SUCCESS");
                        out.flush();
                    }
                }

            } catch (Exception e) {
                log.error("支付宝回调异常======{}====" + CommUtil.getStackTrace(e));
                shoppingOrderPaylog.setStateInfo("支付宝回调异常======{}====" + e.getMessage());
                shoppingOrderPaylogDao.insert(shoppingOrderPaylog);
                e.printStackTrace();
            }

        }catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * 没有现金支付的情况下，由移动端直接调用
     */
    @Override
    public JSONObject appNotify(JSONObject reqJson, HttpServletRequest request) {
        JSONObject retJson = new JSONObject();
        String retCode = "0";
        String retMsg = "操作成功";
        JSONObject bizDataJson = new JSONObject();
        try {
//            String data = request.getParameter("data");// 参数
//            JSONObject reqJson = JSONObject.parseObject(URLDecoder.decode(data, "utf-8"));
            //userId
            String userId = reqJson.getString("userId");
            //订单号
            String orderId = reqJson.getString("orderId");

            ShoppingOrderform orderform = new ShoppingOrderform();
            orderform.setOrderId(orderId);
            orderform = shoppingOrderformDao.queryDetail(orderform);

            ShoppingOrderPay shoppingOrderPay = new ShoppingOrderPay();
            shoppingOrderPay.setOfId(orderform.getId());
            shoppingOrderPay = shoppingOrderPayDao.queryDetail(shoppingOrderPay);

            orderform.setPayTime(StringUtil.nowTimeString());  //付款时间
            //校验账户积分或者余额是否足够，如果不够，更新订单状态为异常
            if (!orderform.getOrderId().startsWith(Const.TICKET_ORDER_PREFIX)&&checkAccountStatus(orderform) == 1) {
                orderform.setMsg("账户积分或余额不足");
                orderform.setOrderStatus(Const.ORDER_STATE_ANOMALOUS);
                shoppingOrderformDao.update(orderform);

                retCode = "-1";
                retMsg = "账户积分或余额不足，该订单自动转为异常订单，请联系客服处理！";
                bizDataJson.put("result", "fail");
            } else {
                //演出票订单的余额、积分、优惠券均由麦座核销，不需要本地处理
                if (!orderform.getOrderId().startsWith(Const.TICKET_ORDER_PREFIX)) {
                    //第三方交互，扣除余额
                    costBalance(orderform, shoppingOrderPay);
                    //第三方交互，扣除积分
                    costIntegral(orderform, shoppingOrderPay);
                    //核销优惠券
                    costCoupon(orderform, shoppingOrderPay);
                }
                //如果是演出票订单，需要调用麦座确认订单接口，核销相应的积分、余额、优惠券
                //20220506更新，无现金支付时，下单成功即代表订单已完成，不需要调用订单确认的接口，直接将订单置为已完成
                if (orderform.getOrderId().startsWith(Const.TICKET_ORDER_PREFIX)) {
//                    if (MZService.confirmOrder(orderform, Const.MZ_PAYID_WX, CommonUtil.getMzUserId(orderform.getUserId()))) {
////                        if (orderform.getDeductionIntegralPrice().compareTo(BigDecimal.ZERO) == 1) {
////                            shoppingOrderPay.setIntegralStatus(1);
////                        }
////                        if (orderform.getDeductionBalancePrice().compareTo(BigDecimal.ZERO) == 1) {
////                            shoppingOrderPay.setBalanceStatus(1);
////                        }
////                        if (orderform.getDeductionCouponPrice().compareTo(BigDecimal.ZERO) == 1) {
////                            shoppingOrderPay.setCouponStatus(1);
////                        }
////                        shoppingOrderPayDao.update(shoppingOrderPay);
////
////
////                        // 添加订单日志
////                        ShoppingOrderLog shoppingOrderLog = new ShoppingOrderLog();
////                        shoppingOrderLog.setLogInfo("订单支付成功");
//////                    shoppingOrderLog.setStateInfo(orderform.getOrderType());
////                        shoppingOrderLog.setLogUserId(orderform.getUserId());
////                        shoppingOrderLog.setOfId(orderform.getId());
////                        shoppingOrderLogDao.insert(shoppingOrderLog);
////
////
////                        orderform.setOrderStatus(50);
////                        orderform.setFinishTime(StringUtil.nowTimeString());
////
////                    } else {
////                        //调用麦座接口失败，更新订单状态为异常
////                        orderform.setMsg("调用麦座订单确认接口失败");
////                        orderform.setOrderStatus(-1);
////                        sendAbnormalMsg();
////                    }

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


                    // 添加订单日志
                    ShoppingOrderLog shoppingOrderLog = new ShoppingOrderLog();
                    shoppingOrderLog.setLogInfo("订单支付成功");
                    shoppingOrderLog.setLogUserId(orderform.getUserId());
                    shoppingOrderLog.setOfId(orderform.getId());
                    shoppingOrderLogDao.insert(shoppingOrderLog);

                    orderform.setOrderStatus(50);
                    orderform.setFinishTime(StringUtil.nowTimeString());
                }

                //判断订单是否所有支付像均支付成功，如果是，更新相应订单状态
                if (!orderform.getOrderId().startsWith(Const.TICKET_ORDER_PREFIX)&&checkPayStatus(orderform)) {
                    // 更新订单状态为已付款
                    orderform.setOrderStatus(20);
                    //给商户发短信
                    sendMsg(orderform);

                    // 添加订单日志
                    ShoppingOrderLog shoppingOrderLog = new ShoppingOrderLog();
                    shoppingOrderLog.setLogInfo("订单支付成功");
                    shoppingOrderLog.setLogUserId(orderform.getUserId());
                    shoppingOrderLog.setOfId(orderform.getId());
                    shoppingOrderLogDao.insert(shoppingOrderLog);


                    //如果是停车订单，通知第三方，更新订单状态状态为已完成
                    if (orderform.getOrderId().startsWith(Const.PARK_ORDER_PREFIX)) {
                        //余额、积分、优惠券均核销成功，通知第三方速停车订单支付成功
                        if(PayParkingFee(orderform)){
                            orderform.setOrderStatus(50);
                            orderform.setFinishTime(StringUtil.nowTimeString());
                        }else{
                            //通知第三方失败，更新订单状态为异常
                            orderform.setMsg("向速停车确认订单支付完成失败");
                            orderform.setOrderStatus(-1);
                            sendAbnormalMsg();
                        }
                    }

                    //如果是点播订单，更新点播购买记录表响应状态，更新订单状态状态为已完成
                    if (orderform.getOrderId().startsWith(Const.VIDEO_ORDER)) {
                        //余额、积分、优惠券均核销成功，更新点播购买记录表状态
                        if(setOndemandBuyStatus(orderform)){
                            orderform.setOrderStatus(50);
                            orderform.setFinishTime(StringUtil.nowTimeString());
                        }else{
                            //通知第三方失败，更新订单状态为异常
                            orderform.setMsg("更新点播购买记录表失败");
                            orderform.setOrderStatus(-1);
                            sendAbnormalMsg();
                        }
                    }

                    //如果是充值订单，更新充值记录表状态，更新订单状态状态为已完成
                    if (orderform.getOrderId().startsWith(Const.RECHARGE_ORDER)) {
                        //调用麦座接口，进行余额充值
                        if(addMoney(orderform)){
                            orderform.setOrderStatus(50);
                            orderform.setFinishTime(StringUtil.nowTimeString());
                            //更新充值记录表状态
                            setRechargeStatus(orderform,1);
                        }else{
                            orderform.setMsg("调用麦座接口充值失败");
                            orderform.setOrderStatus(-1);
                            //更新充值记录表状态
                            setRechargeStatus(orderform,-1);
                        }
                    }

                    //如果是艺教订单，更新订单状态状态为已完成
                    if (orderform.getOrderId().startsWith(Const.SHOPPING_ACT_ORDER)||orderform.getOrderId().startsWith(Const.SHOPPING_PLAN_ORDER)||orderform.getOrderId().startsWith(Const.SHOPPING_CLASS_ORDER)) {
                        orderform.setOrderStatus(50);
                        orderform.setFinishTime(StringUtil.nowTimeString());
                    }

                    //如果是合并支付订单
                    if (orderform.getOrderId().startsWith(Const.SHOPPING_MERGE_ORDER)) {
                        HashMap<String, Object> reqMap = new HashMap<>();
                        reqMap.put("ofId",orderform.getId());
                        reqMap.put("deleteStatus","0");
                        List<ShoppingGoodscart> cartGoods = shoppingGoodscartDao.queryList(reqMap);
                        orderform.setOrderStatus(50);
                        for(ShoppingGoodscart shoppingGoodscart:cartGoods){
                            //如果合并订单中存在文创或者积分订单，则只能将订单状态置为已付款，不能直接置为已完成
                            if(shoppingGoodscart.getCartType()!=null&&(shoppingGoodscart.getCartType().equals(Const.SHOPPING_CUL_CART_TYPE)||shoppingGoodscart.getCartType().equals(Const.SHOPPING_INT_CART_TYPE))){
                                orderform.setOrderStatus(20);
                                break;
                            }
                        }
                    }
                } else {
                    //积分、余额或优惠券支付失败，更新订单状态为异常
                    if (!orderform.getOrderId().startsWith(Const.TICKET_ORDER_PREFIX)){
                        orderform.setMsg("积分或余额或优惠券支付失败，请检查订单支付信息");
                        orderform.setOrderStatus(-1);
                        sendAbnormalMsg();
                        retCode = "-1";
                        retMsg = "支付失败";
                    }
                }
                shoppingOrderformDao.update(orderform);
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    public Boolean checkPayStatus(ShoppingOrderform orderform) {
        ShoppingOrderPay shoppingOrderPay = new ShoppingOrderPay();
        shoppingOrderPay.setOfId(orderform.getId());
        shoppingOrderPay = shoppingOrderPayDao.queryDetail(shoppingOrderPay);
        if (shoppingOrderPay.getPayStatus() == 1) {
            return true;
        } else {
            List<Integer> res = new ArrayList<>();
            res.add(shoppingOrderPay.getCouponStatus());
            res.add(shoppingOrderPay.getIntegralStatus());
            res.add(shoppingOrderPay.getBalanceStatus());
            res.add(shoppingOrderPay.getGiftcardStatus());
            res.add(shoppingOrderPay.getCashStatus());
            if (res.contains(0) || res.contains(2))
                return false;
            else {
                shoppingOrderPay.setPayStatus(1);
                shoppingOrderPayDao.update(shoppingOrderPay);
                return true;
            }
        }
    }

    /**
     * 订单优惠券核销
     */
    public boolean costCoupon(ShoppingOrderform orderform, ShoppingOrderPay shoppingOrderPay) {
        try {
            if (null != orderform.getCiId()) {
                String cid = orderform.getCiId();
                JSONObject couponObj = CRMService.getCouponDtl(cid);
                if(null !=couponObj){
                    orderform.setCouponId(couponObj.getString("right_No"));
                }
                JSONObject resObj = CRMService.writeoffCoupon(cid);
                //优惠券核销成功或失败，均需要修改支付表相应状态  1：成功；2：失败
                if(resObj.get("result").equals("ok")){
                    shoppingOrderPay.setCouponStatus(1);
                    //本地保存优惠券消费记录
                    ShoppingCouponRecord shoppingCouponRecord = new ShoppingCouponRecord();
                    shoppingCouponRecord.setUserId(orderform.getUserId());
                    shoppingCouponRecord.setRightNo(orderform.getCouponId());
                    shoppingCouponRecord.setCouponId(orderform.getCiId());
                    shoppingCouponRecord.setOfId(orderform.getId());
                    shoppingCouponRecordDao.insert(shoppingCouponRecord);
                }else{
                    shoppingOrderPay.setCouponStatus(2);
                }
                shoppingOrderPayDao.update(shoppingOrderPay);
                //删除优惠券锁定表中相应的优惠券信息
                ShoppingCouponUsertemp shoppingCouponUsertemp = new ShoppingCouponUsertemp();
                shoppingCouponUsertemp.setUserId(orderform.getUserId());
                shoppingCouponUsertemp.setCouponId(cid);
                shoppingCouponUsertempDao.delete(shoppingCouponUsertemp);
            }
        } catch (Exception e) {
            log.error("订单" + orderform.getOrderId() + "核销优惠券失败：" + e);
            return false;
        }
        return true;
    }

    /**
     * 校验账户积分和余额是否足够
     */
    public Integer checkAccountStatus(ShoppingOrderform orderform) {
        try {
            if (orderform.getDeductionIntegral() > 0 || orderform.getDeductionBalancePrice().compareTo(BigDecimal.ZERO) == 1){
                //从卖座实时查询账户积分和余额
                JSONObject accountObj = MZService.getAssetinfo(CommonUtil.getMzUserId(orderform.getUserId()), orderform);
                if (null != accountObj) {
                    int account_point = accountObj.get("account_point") == null ? 0 : accountObj.getInteger("account_point");   //会员账户积分剩余点数，单位：点数；
                    int account_money_fen = accountObj.get("account_money_fen") == null ? 0 : accountObj.getInteger("account_money_fen");
                    BigDecimal accountMoney = new BigDecimal(account_money_fen).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);  //分转换为元
                    if (orderform.getDeductionIntegral() > 0 && account_point < orderform.getDeductionIntegral()) {
                        return 1;
                    }
                    if (orderform.getDeductionBalancePrice().compareTo(BigDecimal.ZERO) == 1 && orderform.getDeductionBalancePrice().compareTo(accountMoney) == 1) {
                        return 1;
                    }
                } else {
                    return 2;
                }
            }

        } catch (Exception e) {
            log.error("订单" + orderform.getOrderId() + "查询账户积分和余额信息失败：" + e);
            return 2;
        }
        return 0;
    }

    /**
     * 积分扣除
     */
    public boolean costIntegral(ShoppingOrderform orderform, ShoppingOrderPay shoppingOrderPay) {
        try {
            if(orderform.getDeductionIntegral() > 0){
                if (shoppingOrderPay.getIntegralStatus() == 0 || shoppingOrderPay.getIntegralStatus() == 2) {
                    //如果积分数额超过限额，则需要传递“资产使用业务key”，该key值之前在调用验证码接口时已经存入数据库
                    String asset_biz_key = null;
                    ShoppingOrderPaykey shoppingOrderPaykey = new ShoppingOrderPaykey();
                    shoppingOrderPaykey.setOfId(orderform.getId());
                    shoppingOrderPaykey = shoppingOrderPaykeyDao.queryDetail(shoppingOrderPaykey);
                    if (null != shoppingOrderPaykey) {
                        if(null !=shoppingOrderPaykey.getAccountPointPayKey()&&!"".equals(shoppingOrderPaykey.getAccountPointPayKey())){
                            String str = shoppingOrderPaykey.getAccountPointPayKey();
                            //余额扣除在积分扣除之前，如果余额扣除也使用了资产key，则积分扣除这边需要重新再获取一次key
                            if(null !=shoppingOrderPaykey.getAccountMoneyPayKey()&&!"".equals(shoppingOrderPaykey.getAccountMoneyPayKey())){
                                String verifyCode = str.split("_")[1];
                                String userId = orderform.getUserId();
                                String mzUserId = CommonUtil.getMzUserId(userId);
                                JSONObject dataObj= MZService.checkVerifyCode(mzUserId,verifyCode);
                                if(null !=dataObj){
                                    asset_biz_key =dataObj.getString("asset_biz_key");
                                }
                            }else{
                                asset_biz_key = str.split("_")[0];
                            }
                        }
                    }

                    if (MZService.cutPoint(CommonUtil.getMzUserId(orderform.getUserId()), orderform.getDeductionIntegral(), orderform, asset_biz_key)) {
                        //积分扣除成功或失败，均需要修改支付表相应状态  1：成功；2：失败
                        shoppingOrderPay.setIntegralStatus(1);
                        shoppingOrderPayDao.update(shoppingOrderPay);
                        return true;
                    } else {
                        //积分扣除成功或失败，均需要修改支付表相应状态  1：成功；2：失败
                        shoppingOrderPay.setIntegralStatus(2);
                        shoppingOrderPayDao.update(shoppingOrderPay);
                        return false;
                    }
                }
            }else{
                if (shoppingOrderPay.getIntegralStatus() == 0 || shoppingOrderPay.getIntegralStatus() == 2) {
                    shoppingOrderPay.setIntegralStatus(1);
                    shoppingOrderPayDao.update(shoppingOrderPay);
                }
                return true;
            }


        } catch (Exception e) {
            log.error("订单" + orderform.getOrderId() + "扣除积分失败：" + e);
            return false;
        }
        return true;
    }

    /**
     * 余额扣除
     */
    public boolean costBalance(ShoppingOrderform orderform, ShoppingOrderPay shoppingOrderPay) {
        try {
            //待扣除余额数值大于0
            if(orderform.getDeductionBalancePrice().compareTo(BigDecimal.ZERO) == 1){
                //余额为待支付或支付失败状态，可以发起支付
                if (shoppingOrderPay.getBalanceStatus() == 0 || shoppingOrderPay.getBalanceStatus() == 2) {
                    //如果需要扣除的余额数额超过限额，则需要传递“资产使用业务key”，该key值之前在调用验证码接口时已经存入数据库
                    String asset_biz_key = null;
                    ShoppingOrderPaykey shoppingOrderPaykey = new ShoppingOrderPaykey();
                    shoppingOrderPaykey.setOfId(orderform.getId());
                    shoppingOrderPaykey = shoppingOrderPaykeyDao.queryDetail(shoppingOrderPaykey);
                    if (null != shoppingOrderPaykey) {
                        if(null !=shoppingOrderPaykey.getAccountMoneyPayKey()&&!"".equals(shoppingOrderPaykey.getAccountMoneyPayKey())){
                            asset_biz_key = shoppingOrderPaykey.getAccountMoneyPayKey().split("_")[0];
                        }
                    }
                    //将余额抵扣值转换为分
                    int deductionBalanceValue = orderform.getDeductionBalancePrice().multiply(new BigDecimal(100)).intValue();
                    //调用麦座接口扣除余额
                    if (MZService.cutMoney(CommonUtil.getMzUserId(orderform.getUserId()), deductionBalanceValue, orderform, asset_biz_key)) {
                        //余额扣除成功或失败，均需要修改支付表相应状态  1：成功；2：失败
                        shoppingOrderPay.setBalanceStatus(1);
                        shoppingOrderPayDao.update(shoppingOrderPay);
                        return true;
                    } else {
                        //余额扣除成功或失败，均需要修改支付表相应状态  1：成功；2：失败
                        shoppingOrderPay.setBalanceStatus(2);
                        shoppingOrderPayDao.update(shoppingOrderPay);
                        return false;
                    }
                }
            }else{
                //待扣除余额数值为0时，直接标识余额支付成功
                if (shoppingOrderPay.getBalanceStatus() == 0 || shoppingOrderPay.getBalanceStatus() == 2) {
                    shoppingOrderPay.setBalanceStatus(1);
                    shoppingOrderPayDao.update(shoppingOrderPay);
                }
                return true;
            }

        } catch (Exception e) {
            log.error("订单" + orderform.getOrderId() + "扣除余额失败：" + e);
            return false;
        }
        return true;
    }

    /**
     * 停车支付
     */
    public Boolean PayParkingFee(ShoppingOrderform orderform) {
        try{
            String serviceCode = "payParkingFee";
            String ts = String.valueOf(System.currentTimeMillis());
            String reqId = ts;
            JSONObject reqJson = new JSONObject();
            reqJson.put("appId", parkAppID);
            reqJson.put("parkId", parkId);
            reqJson.put("serviceCode", serviceCode);
            reqJson.put("ts", ts);
            reqJson.put("reqId", ts);

            //根据订单号查询停车订单信息
            ParkOrder parkOrder = new ParkOrder();
            parkOrder.setOrderId(orderform.getOrderId());
            parkOrder = parkOrderDao.queryDetail(parkOrder);
            reqJson.put("orderNo", parkOrder.getOrderNo());

//            reqJson.put("plateNo", parkOrder.getPlateNo());

            reqJson.put("payableAmount", parkOrder.getPayable());
            //从本地订单详情获取实际支付金额，并转换为分
            BigDecimal payPrice = orderform.getPayPrice();
            int amount = payPrice.multiply(new BigDecimal(100)).intValue();
            reqJson.put("amount", amount);
            //收费终端 5:APP（安卓/IOS）
            reqJson.put("payType", 5);
            if (null != orderform.getPaymentId()) {
                ShoppingPayment shoppingPayment = new ShoppingPayment();
                shoppingPayment.setId(orderform.getPaymentId());
                shoppingPayment = shoppingPaymentDao.queryDetail(shoppingPayment);

                if (shoppingPayment.getMark().equals("wxpay")) {
                    reqJson.put("payMethod", 4);
                } else if (shoppingPayment.getMark().equals("alipay")) {
                    reqJson.put("payMethod", 5);
                } else {
                    reqJson.put("payMethod", 7);
                }
            } else {
                reqJson.put("payMethod", 7);
            }
            //如果订单详情中的优惠券抵扣金额或者积分抵扣金额或者余额抵扣金额大于0，则表示对于停车支付接口来说，存在“减免金额”,建议将这部分信息传递给第三方，方便后期对账
            if (orderform.getDeductionCouponPrice().compareTo(BigDecimal.ZERO) == 1 ||orderform.getDeductionIntegralPrice().compareTo(BigDecimal.ZERO) == 1 || orderform.getDeductionBalancePrice().compareTo(BigDecimal.ZERO) == 1) {
                JSONArray freeDetail = new JSONArray();
                int freeMoney = 0;
                if (orderform.getDeductionCouponPrice().compareTo(BigDecimal.ZERO) == 1) {
                    JSONObject couponObj = new JSONObject();
                    int money_fen = orderform.getDeductionCouponPrice().multiply(new BigDecimal(100)).intValue();
                    freeMoney += money_fen;
                    couponObj.put("money", money_fen);  //减免金额，单位：分
                    couponObj.put("time", 0);    //减免时间 单位：秒
                    couponObj.put("code", orderform.getCiId());    //抵扣券id
                    couponObj.put("type", 1);    //抵扣来源:1
                    couponObj.put("freeName", "优惠券抵扣");    //减免项目名称
                    freeDetail.add(couponObj);
                }
                if (orderform.getDeductionBalancePrice().compareTo(BigDecimal.ZERO) == 1) {
                    JSONObject balanceObj = new JSONObject();
                    int money_fen = orderform.getDeductionBalancePrice().multiply(new BigDecimal(100)).intValue();
                    freeMoney += money_fen;
                    balanceObj.put("money", money_fen);  //减免金额，单位：分
                    balanceObj.put("time", 0);    //减免时间 单位：秒
                    balanceObj.put("code", orderform.getUserId());    //暂时传递用户id
                    balanceObj.put("type", 3202);    //抵扣来源:余额抵扣
                    balanceObj.put("freeName", "余额抵扣");    //减免项目名称
                    freeDetail.add(balanceObj);
                }
                if (orderform.getDeductionIntegralPrice().compareTo(BigDecimal.ZERO) == 1) {
                    JSONObject integralObj = new JSONObject();
                    int money_fen = orderform.getDeductionIntegralPrice().multiply(new BigDecimal(100)).intValue();
                    freeMoney += money_fen;
                    integralObj.put("money", money_fen);  //减免金额，单位：分
                    integralObj.put("time", 0);    //减免时间 单位：秒
                    integralObj.put("code", orderform.getUserId());    //暂时传递用户id
                    integralObj.put("type", 3201);    //抵扣来源:积分抵扣
                    integralObj.put("freeName", "积分抵扣");    //减免项目名称
                    freeDetail.add(integralObj);
                }
                reqJson.put("freeMoney", freeMoney);   //减免总金额（单位 分）
                reqJson.put("freeDetail", freeDetail);
            } else {
                reqJson.put("freeMoney", 0);
            }
            reqJson.put("freeTime", 0);
            reqJson.put("outOrderNo", orderform.getOrderId());

            String signValue = SignUtil.paramsSign(reqJson, parkAppSecret);
            reqJson.put("key", signValue);

            //处理freeDetail
            if(null !=reqJson.get("freeDetail")){
                Gson gson = new Gson();
                String jsonInString = gson.toJson(reqJson.get("freeDetail"));
                reqJson.put("freeDetail",jsonInString);
            }
            String reqtime = StringUtil.nowTimeString();

            JSONObject res = HttpSendUtil.doPost(parkApiPayParkingFee, reqJson.toJSONString());
            String rettime = StringUtil.nowTimeString();
            if(null==res){
                CommonUtil.addThirdLog(1,Const.THIRDLOG_TYPE_PARK,"停车收费确认","POST",parkApiPayParkingFee,
                        reqtime,reqJson.toJSONString(),rettime,"");
                return false;
            }else{
                CommonUtil.addThirdLog(0,Const.THIRDLOG_TYPE_PARK,"停车收费确认","POST",parkApiPayParkingFee,
                        reqtime,reqJson.toJSONString(),rettime,res.toString());
                if(res.get("resCode").equals("0")||(res.get("resCode").equals("200")&&res.get("resMsg").equals("操作成功，订单已支付"))){
                    //向第三方确认停车支付成功
                    return true;
                }else{
                    //向第三方确认停车支付失败
                    return false;
                }
            }

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 更新点播购买记录表购买状态
     */
    public Boolean setOndemandBuyStatus(ShoppingOrderform orderform){
        try{
            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("isbuy","1");
            reqMap.put("ofId",orderform.getId());
            reqMap.put("userId",orderform.getUserId());
            tOnOndemandhistoryDao.setOndemandBuyStatus(reqMap);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;

    }

    /**
     * 更新充值记录表充值状态
     */
    public void setRechargeStatus(ShoppingOrderform orderform,int status){
        try{
            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("status",status);
            reqMap.put("ofId",orderform.getId());
            reqMap.put("userId",orderform.getUserId());
            shoppingRechargeDao.setRechargeStatus(reqMap);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 余额充值
     */
    public Boolean addMoney(ShoppingOrderform orderform){
        try{
        HashMap<String, Object> reqMap = new HashMap<>();
        reqMap.put("status",0);
        reqMap.put("ofId",orderform.getId());
        reqMap.put("userId",orderform.getUserId());
        ShoppingRecharge recharge = shoppingRechargeDao.queryRecharge(reqMap);
        ShoppingOrderPaykey shoppingOrderPaykey = new ShoppingOrderPaykey();
        shoppingOrderPaykey.setOfId(orderform.getId());
        shoppingOrderPaykey = shoppingOrderPaykeyDao.queryDetail(shoppingOrderPaykey);
        String asset_biz_key = shoppingOrderPaykey.getAccountMoneyPayKey().split("_")[0];
        int changeValue =recharge.getMoneyAmount().multiply(new BigDecimal(100)).intValue();
        if(!MZService.addMoney(CommonUtil.getMzUserId(orderform.getUserId()), changeValue, asset_biz_key,orderform.getOrderId(),"APP余额充值")){
            //余额充值失败，将该订单转为异常订单
            return false;
        }else{
            dealRechargeActivity(orderform);  //充值活动
        }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void dealRechargeActivity(ShoppingOrderform orderform){
        try{
            HashMap<String, Object> reqMap = new HashMap<>();

            reqMap.put("isPub", "1");
            reqMap.put("isDelete", "0");
            List<ShoppingRechargeActivity> activities=shoppingRechargeActivityDao.queryList(reqMap);
            if(activities.size()==1){
                ShoppingRechargeActivity activity = activities.get(0);
                String activityId = activity.getId();
                int perNum = activity.getPernum();
                String startTime = activity.getStartTime();
                String endTime = activity.getEndTime();
                //判断当前时间是否在充值活动时间内
                SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date sDate = sf.parse(startTime);
                Date eDate = sf.parse(endTime);
                Date nowDate = new Date();
                if(nowDate.getTime()>=sDate.getTime()&&nowDate.getTime()<=eDate.getTime()){
                    //判断用户本次充值金额是否达到活动要求
                    reqMap.clear();
                    reqMap.put("status",0);
                    reqMap.put("ofId",orderform.getId());
                    reqMap.put("userId",orderform.getUserId());
                    ShoppingRecharge recharge = shoppingRechargeDao.queryRecharge(reqMap);
                    BigDecimal moneyAmount= recharge.getMoneyAmount();   //充值金额，单位：元
                    BigDecimal floor = new BigDecimal(activity.getFloor());
                    if(moneyAmount.compareTo(floor)>=0){
                        //查看该用户是否参加过该活动
                        reqMap.clear();
                        reqMap.put("userid",orderform.getUserId());
                        reqMap.put("activityId",activityId);
                        List<ShoppingRechargeActivityRecord> records = shoppingRechargeActivityRecordDao.queryList(reqMap);
                        if(records.size()<perNum){   //已参加活动次数小于限制次数
                            //根据活动设置向用户赠送余额
                            int changeValue =activity.getGive()*100;
                            ShoppingOrderPaykey shoppingOrderPaykey = new ShoppingOrderPaykey();
                            shoppingOrderPaykey.setOfId(orderform.getId());
                            shoppingOrderPaykey = shoppingOrderPaykeyDao.queryDetail(shoppingOrderPaykey);
                            String str = shoppingOrderPaykey.getAccountMoneyPayKey();
                            //余额赠送这边需要重新使用验证码再获取一次key
                            if(null !=shoppingOrderPaykey.getAccountMoneyPayKey()&&!"".equals(shoppingOrderPaykey.getAccountMoneyPayKey())){
                                String verifyCode = str.split("_")[1];
                                String userId = orderform.getUserId();
                                String mzUserId = CommonUtil.getMzUserId(userId);
                                JSONObject dataObj= MZService.checkVerifyCode(mzUserId,verifyCode);
                                if(null !=dataObj){
                                    String asset_biz_key =dataObj.getString("asset_biz_key");
                                    if(MZService.addMoney(CommonUtil.getMzUserId(orderform.getUserId()), changeValue, asset_biz_key,orderform.getOrderId()+"_1","APP余额充值活动赠送")){
                                        //保存用户参加该次活动的记录
                                        ShoppingRechargeActivityRecord record = new ShoppingRechargeActivityRecord();
                                        record.setActivityId(activityId);
                                        record.setUserid(orderform.getUserId());
                                        record.setRechargePrice(moneyAmount);
                                        shoppingRechargeActivityRecordDao.insert(record);
                                    }
                                }
                            }

                        }
                    }
                }
            }
        }catch (Exception e){

        }

    }

    public Boolean checkAlipayParams(ShoppingOrderform orderform,Map<String, String> params){
        if(null !=params.get("total_amount")&&null !=params.get("app_id")){
            BigDecimal payPrice = orderform.getPayPrice();
            BigDecimal total_amount= new BigDecimal(params.get("total_amount"));
            if(payPrice.compareTo(total_amount)!=0){
                return false;
            }
            String appId = Pay.APP_ID;
            String app_id = params.get("app_id").toString();
            if(!appId.equals(app_id)){
                return false;
            }
        }else{
            return false;
        }
        return true;

    }

    public Boolean checkWxMoney(ShoppingOrderform orderform,int total_fee){
        int price = orderform.getPayPrice().multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_EVEN).intValue();// 订单金额
        if(total_fee !=price){
            return false;
        }
        return true;

    }

    /**
     * 下单成功向商户发送短信
     */
    public void sendMsg(ShoppingOrderform orderform){
        try{

            HashMap<String, Object> reqMap =new HashMap<>();
            reqMap.put("ofId",orderform.getId());
            List<ShoppingGoodscart> goodscartList = shoppingGoodscartDao.queryList(reqMap);
            for(ShoppingGoodscart goodscart:goodscartList){
                if(goodscart.getCartType()==1||goodscart.getCartType()==2){  //文创或积分商品
                    ShoppingGoods shoppingGoods =new ShoppingGoods();
                    shoppingGoods.setId(goodscart.getGoodsId());
                    shoppingGoods = shoppingGoodsDao.queryDetail(shoppingGoods);
                    if(StringUtil.isNotNull(shoppingGoods.getPhone())){
                        //发送短信
                        sendMsg(shoppingGoods.getPhone(),shoppingGoods.getGoodsName());
                    }
                }else if(goodscart.getCartType()==3){    //活动
                    ShoppingArtactivity shoppingArtactivity = new ShoppingArtactivity();
                    shoppingArtactivity.setId(goodscart.getGoodsId());
                    shoppingArtactivity =shoppingArtactivityDao.queryDetail(shoppingArtactivity);
                    if(StringUtil.isNotNull(shoppingArtactivity.getPhone())){
                        //发送短信
                        sendMsg(shoppingArtactivity.getPhone(),shoppingArtactivity.getActivityName());
                    }
                }else if(goodscart.getCartType()==9){    //爱艺计划
                    ShoppingArtplan shoppingArtplan = new ShoppingArtplan();
                    shoppingArtplan.setId(goodscart.getGoodsId());
                    shoppingArtplan =shoppingArtplanDao.queryDetail(shoppingArtplan);
                    if(StringUtil.isNotNull(shoppingArtplan.getPhone())){
                        //发送短信
                        sendMsg(shoppingArtplan.getPhone(),shoppingArtplan.getActivityName());
                    }
                }
            }
        }catch (Exception e){

        }

    }

    public boolean sendMsg(String mobile,String content){
        Map<String, String> params=new HashMap<>();
        params.put("method", "sendUtf8Msg");
        params.put("username", "JSM4140009");
        params.put("password", "xr185whg");
        params.put("veryCode", "maoyvrgxx9h8");
        params.put("tempid", "JSM41400-0096");
        params.put("content", "@1@="+content);
        params.put("msgtype", "2");
        params.put("mobile", mobile);
        params.put("rt", "json");
//            params.put("code", "utf-8");
        JSONObject retJson = HttpSendUtil.sendMsg(url, params);
        System.out.println(retJson);
        if(retJson.get("status")!=null&&retJson.get("status").equals("0")){
            return true;
        }else{
            return false;
        }

    }

    /**
     * 出现异常订单时发送短信
     */
    public boolean sendAbnormalMsg(){
        Map<String, String> params=new HashMap<>();
        params.put("method", "sendUtf8Msg");
        params.put("username", "JSM4140009");
        params.put("password", "xr185whg");
        params.put("veryCode", "maoyvrgxx9h8");
        params.put("tempid", "JSM41400-0095");
        params.put("content", "@1@="+"有异常订单，请尽快处理");
        params.put("msgtype", "2");
        params.put("mobile", "13776407246");
        params.put("rt", "json");
//            params.put("code", "utf-8");
        JSONObject retJson = HttpSendUtil.sendMsg(url, params);
        System.out.println(retJson);
        if(retJson.get("status")!=null&&retJson.get("status").equals("0")){
            return true;
        }else{
            return false;
        }

    }

    public static void main(String[] args) throws AlipayApiException {
        String str="gmt_create###2022-04-13 09:03:16, charset###utf-8, seller_email###83600315@qq.com, subject###江苏大剧院App商城消费, sign###YFgyKr6TJq4nnLaMd03PeLqTFlBqbjmRm7s9XCZuLuCsnwDHj1iAL+DNfzMY6W2dRDAHudxVdLO2pvJElJ760Ds2EvGrjOPOwYDZAIYpEdrEj1xQpKRQ9OuZZMGToi4L+AGe9pafPzYvUSTXFbSCP0YzNJ+KU4gdALGWNwTv6qWBnK9vy11w+SeWnFYAmGi7bQqP9kf8LE70gE/LDokSQUoNqTtQ0ChOjENXMtAIefmSMJefNEgvFwaRgr9H9gZXjUzTOLM45vuGJWgHTNbgpEoLJo/TzdbY4navHjBqqIahRQBZrKYKdBq08ALOC5oTnpJj2NCPtaan2MVNe4wHBg==, body###江苏大剧院App商城消费, buyer_id###2088822772000345, invoice_amount###0.01, notify_id###2022041300222090316000341424692518, fund_bill_list###[{\"amount\":\"0.01\",\"fundChannel\":\"PCREDIT\"}], notify_type###trade_status_sync, trade_status###TRADE_SUCCESS, receipt_amount###0.01, app_id###2021002130685046, buyer_pay_amount###0.01, sign_type###RSA2, seller_id###2088931572838903, gmt_payment###2022-04-13 09:03:16, notify_time###2022-04-13 09:03:16, passback_params###shoppingOrder, version###1.0, out_trade_no###TK20220413090317651, total_amount###0.01, trade_no###2022041322001400341441188029, auth_app_id###2021002130685046, buyer_logon_id###138****7883, point_amount###0.00";
        String[] a = str.split(", ");
        Map< String , String > params =new HashMap<>();
        for(int i=0;i< a.length;i++){
            String[] b = a[i].split("###");
            params.put(b[0],b[1]);
        }



        String ALIPAY_PUBLIC_KEY="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqaO+WoEmV00L/x5Jj4KmF/jNlb7bRsnpsTH3oE0yyNiQI/qtcCbxqoN4k1eyfHTQnXQDCznRvJKCKq8snGvyHUTqKz1w5MitoJXa1wceO908wQUesM6Kcitv7tyXasBKSd3iQylFIE2JnoVGOZFfhFiS/AxGHqx3XL0ei/FONg9ToQ6gWbr0mC/8B6QQwuBgYtTaeyoW41tqAW2Ksko/BsLGRYJ5z1td+WePCMZBLvJhAn74mJYWCgmxqcu2EHv+TgIjEsFMMwWch/k5tqz4J0I6RTq0LRdqZQu6q5hEFTC0r7SVQKykwBv4zuU75Rh8t1cewJ3eCsdZkAr1hUE/BwIDAQAB";

        boolean flag = AlipaySignature.rsaCheckV1 (params,ALIPAY_PUBLIC_KEY, "utf-8","RSA2");
        System.out.println("flag===="+flag);
    }
}
