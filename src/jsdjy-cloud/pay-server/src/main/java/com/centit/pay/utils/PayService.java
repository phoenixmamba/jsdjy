package com.centit.pay.utils;


import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.centit.pay.common.contst.Pay;
import com.centit.pay.common.contst.PayConfig;
import com.centit.pay.common.enums.Const;
import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Component
public class PayService {
    @Value("${pay.istest}")
    private Boolean istest;

    private static String ticketOrderPrefix;

    private static Integer orderExpireMinutes_ticket;

    private static Integer orderExpireMinutes_other;

    private static SimpleDateFormat sf=new SimpleDateFormat("yyyyMMddHHmmss");

    private static final Logger log = LoggerFactory.getLogger(PayService.class);

    @Value("${order.prefix.ticket}")
    public void setTicketOrderPrefix(String ticketOrderPrefix) {
        PayService.ticketOrderPrefix = ticketOrderPrefix;
    }
    @Value("${orderExpireMinutes_ticket}")
    public void setOrderExpireMinutes_ticket(Integer orderExpireMinutes_ticket) {
        PayService.orderExpireMinutes_ticket = orderExpireMinutes_ticket;
    }
    @Value("${orderExpireMinutes_other}")
    public void setOrderExpireMinutes_other(Integer orderExpireMinutes_other) {
        PayService.orderExpireMinutes_other = orderExpireMinutes_other;
    }

    /**
     * @描述:微信APP支付下单
     * @作者: zhouChaoXi
     * @时间: 2018年6月3日
     */
    public static SortedMap<Object, Object> wxAppPay(PayConfig config, HttpServletRequest request) {
        return wxPay(config, "APP", -1, request);
    }
    

    /**
     * 微信支付
     *
     * @return
     */
    public static SortedMap<Object, Object> wxPay(PayConfig config, String tradeType, int n, HttpServletRequest request) {
        log.info("WX支付提交：{}", config.getOrderId());
        SortedMap<Object, Object> signParam = null;
        try {
            int price = config.getPrice().multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_EVEN).intValue();// 订单金额
//            int price =1;  //测试默认支付一分钱
            String strTime = PayUtil.getWxTimeStamp(config.getPayTime());
            SortedMap<Object, Object> packageParams = new TreeMap<Object, Object>();
            packageParams.put("appid", config.getAppId());
            packageParams.put("body", config.getBody());//商品描述
            packageParams.put("mch_id", config.getMchId());
            packageParams.put("nonce_str", strTime);//随机字符串
            // 微信异步回调
            if(config.getOrderId().startsWith(Const.PARK_ORDER_PREFIX)){  //停车专用
                packageParams.put("notify_url", Pay.WX_NOTIFY_PARK_URL);
            }else{
                packageParams.put("notify_url", Pay.WX_NOTIFY_URL);
            }

            packageParams.put("out_trade_no", config.getOrderId());//商家订单号
            packageParams.put("sign_type", "MD5");
//            packageParams.put("time_start","yyyyMMddHHmmss");//开始时间
//            packageParams.put("time_expire","yyyyMMddHHmmss");//结束时间
            if(config.getOrderId().startsWith(ticketOrderPrefix))
                packageParams.put("time_expire",StringUtil.timePlusMinutes(config.getOrderAddTime(),orderExpireMinutes_ticket,sf));//结束时间
            else
                packageParams.put("time_expire",StringUtil.timePlusMinutes(config.getOrderAddTime(),orderExpireMinutes_other,sf));//结束时间
            if (!CommUtil.isNull(config.getOpenId())) {
                packageParams.put("openid", config.getOpenId());
            }
//           附加信息，回调时由微信原样返回
//            packageParams.put("attach", config.getType());
            packageParams.put("spbill_create_ip", config.getIp());
                packageParams.put("total_fee", String.valueOf(price));
            packageParams.put("trade_type", tradeType);

            String sign = PayUtil.getSign("UTF-8", packageParams, config.getPartenerKey());
            packageParams.put("sign", sign);
            String requestXML = PayUtil.getRequestXml(packageParams);
            log.info("统一下单参数：{}" + requestXML);
            String reqtime = StringUtil.nowTimeString();
            try {
            String resXml = PayUtil.postData(Pay.ORDER_PAY, requestXML);
            Map<String, String> map = PayUtil.doXMLParse(resXml);
            String rettime = StringUtil.nowTimeString();
            CommonUtil.addThirdLog(0,Const.THIRDLOG_TYPE_WX,"微信统一下单","POST",Pay.ORDER_PAY,
                    reqtime,requestXML,rettime,map.toString());
            log.info("统一下单返回：{}" + map);

            signParam = new TreeMap<Object, Object>();

            if ("APP".equals(tradeType)) {
                signParam.put("appid", config.getAppId());
                signParam.put("partnerid", config.getMchId());
                signParam.put("prepayid", map.get("prepay_id"));
                signParam.put("package", "Sign=WXPay");
                signParam.put("noncestr", strTime);
                signParam.put("timestamp", strTime);
                String signtwo = PayUtil.getSign("UTF-8", signParam, config.getPartenerKey());
                signParam.put("sign", signtwo);
            }
//            else if ("JSAPI".equals(tradeType)) {
//                signParam.put("appId", config.getAppId());
//                signParam.put("signType", "MD5");
//                signParam.put("package", "prepay_id=" + map.get("prepay_id"));
//                signParam.put("nonceStr", strTime);
//                signParam.put("timeStamp", strTime);
//                String signtwo = PayUtil.getSign("UTF-8", signParam, config.getPartenerKey());
//                signParam.put("paySign", signtwo);
//                signParam.put("prepay_id", map.get("prepay_id"));
//            } else if ("MWEB".equals(tradeType)) {
//                signParam.put("url", map.get("mweb_url"));
//            } else if ("NATIVE".equals(tradeType)) {
//                signParam.put("url", map.get("code_url"));
//            }
            return signParam;
            } catch (Exception e) {
                String rettime = StringUtil.nowTimeString();
                CommonUtil.addThirdLog(1,Const.THIRDLOG_TYPE_WX,"微信统一下单","POST",Pay.ORDER_PAY,
                        reqtime,requestXML,rettime,e.getMessage());
                log.info("WX支付异常:{}" + e.getMessage());
                return null;
            }
        } catch (Exception e) {
            log.info("WX支付异常:{}" + e.getMessage());
            return null;
        }

    }

    /**
     * @描述: 支付宝App生成订单
     * @参数: [orderId 订单号,body 详细,title 订单标题,price 金额单位元,type 需要支付宝回调时返回的类型]
     * @返回: java.lang.String
     */
    public static String alipayAppPay(HttpServletRequest req, String orderId, String orderAddTime,String body, String title, String price, String type) {

        try {
            AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
            //对一笔交易的具体描述信息。如果是多种商品，请将商品描述字符串累加传给body。
            model.setBody(body);
            //商品的标题/交易标题/订单标题/订单关键字等。
            model.setSubject(title);
            //商户网站唯一订单号
            model.setOutTradeNo(orderId);
//            //设置未付款支付宝交易的超时时间，一旦超时，该笔交易就会自动被关闭。
//            model.setTimeoutExpress("60m");
            System.out.println("orderId=="+orderId+";ticketOrderPrefix=="+ticketOrderPrefix);
            if(orderId.startsWith(ticketOrderPrefix))
                model.setTimeExpire(StringUtil.timePlusMinutes(orderAddTime,orderExpireMinutes_ticket));
            else
                model.setTimeExpire(StringUtil.timePlusMinutes(orderAddTime,orderExpireMinutes_other));

            //订单总金额，单位为元，精确到小数点后两位，取值范围[0.01,100000000]
            model.setTotalAmount(price);
            //销售产品码，商家和支付宝签约的产品码，为固定值QUICK_MSECURITY_PAY
            model.setProductCode("QUICK_MSECURITY_PAY");
            //商品主类型：0—虚拟类商品，1—实物类商品注：虚拟类商品不支持使用花呗渠道
            model.setGoodsType("1");
//          由支付宝回调时原样返回
            model.setPassbackParams(URLEncoder.encode(type, "UTF-8"));
            //实例化客户端
            AlipayClient alipayClient = new Pay().getAlipay();
            //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.park
            AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
            //SDK已经封装掉了公共参数，这里只需要传入业务参数。
            // 以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
            request.setBizModel(model);
            log.info("======url==="+Pay.NOTIFY_URL);
            request.setNotifyUrl(Pay.NOTIFY_URL);
            //这里和普通的接口调用不同，使用的是sdkExecute
            String reqtime = StringUtil.nowTimeString();
            try {
            AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
            String rettime = StringUtil.nowTimeString();
            CommonUtil.addThirdLog(0,Const.THIRDLOG_TYPE_ALI,"支付宝下单","POST","AlipayTradeAppPayRequest",
                    reqtime,request.getTextParams().toString(),rettime,response.getBody());
//            if(response.isSuccess()){
//                log.info("123");
//            }

            //就是orderString 可以直接给客户端请求，无需再做处理。
            log.info(response.getBody());
            return response.getBody();
            } catch (Exception e) {
                String rettime = StringUtil.nowTimeString();
                CommonUtil.addThirdLog(1,Const.THIRDLOG_TYPE_ALI,"支付宝下单","POST","AlipayTradeAppPayRequest",
                        reqtime,request.getTextParams().toString(),rettime,e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void main(String[] args) throws IOException, JDOMException {
        SortedMap<Object, Object> packageParams = new TreeMap<Object, Object>();
        packageParams.put("appid", "wxd1724f9f91f1e8af");
        packageParams.put("body", "江苏大剧院App商城消费");//商品描述
        packageParams.put("mch_id", "1607523004");
        packageParams.put("nonce_str", "1648438006");//随机字符串
        // 微信异步回调
        packageParams.put("notify_url", "http://221.226.75.102/pay/notify/wxNotify");


        packageParams.put("out_trade_no", "VD20221324151342010");//商家订单号
        packageParams.put("sign_type", "MD5");

        packageParams.put("spbill_create_ip", "127.0.0.1");
        packageParams.put("total_fee", "1");
        packageParams.put("trade_type", "APP");

        String sign = PayUtil.getSign("UTF-8", packageParams, "4d328f68771a209dc255c06b9d85c7c3");
        packageParams.put("sign", sign);
        String requestXML = PayUtil.getRequestXml(packageParams);
        log.info("统一下单参数：{}" + requestXML);
        String reqtime = StringUtil.nowTimeString();
            String resXml = PayUtil.postData("https://api.mch.weixin.qq.com/pay/unifiedorder", requestXML);
            Map<String, String> map = PayUtil.doXMLParse(resXml);
            log.info("统一下单返回：{}" + map);
    }
}
