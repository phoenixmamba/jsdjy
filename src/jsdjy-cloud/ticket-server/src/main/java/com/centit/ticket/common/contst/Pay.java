//package com.centit.ticket.common.contst;
//
//import com.alipay.api.DefaultAlipayClient;
//
//import java.util.ResourceBundle;
//
///**
// *  *@Author: zhouchaoxi
// *  *@Date: Created in 2018/6/29 20:25
// *  
// */
//public class Pay {
//    // 微信各请求路径,固定不变
//    public static final String ORDER_REFUND = "https://api.mch.weixin.qq.com/secapi/park/refund";//TODO 微信退款API
//    public static final String ORDER_REFUND_QUERY = "https://api.mch.weixin.qq.com/park/refundquery";//TODO 微信退款查询API
//    public static final String ORDER_QUERY = "https://api.mch.weixin.qq.com/park/orderquery";//TODO 微信订单查询API
//    public static final String ORDER_CLOSE = "https://api.mch.weixin.qq.com/park/closeorder";//TODO 微信关闭订单API
//    public static final String ORDER_PAY = "https://api.mch.weixin.qq.com/park/unifiedorder";//TODO 微信下单API
//    /****************支付宝配置*********************/
////    支付宝请求，固定值
//    public static final String GET_WAY = "https://openapi.alipay.com/gateway.do";
//    public static final String FORMAT = "JSON";
//    public static final String SIGN_TYPE = "RSA2";
//    public static final String CHARSET = "utf-8";
//    //TODO    支付宝退款回调
//    public static final String REFUND_URL = "/alipayRefundUrl.htm";
//    //TODO    wap支付途中退出的页面
//    public static final String QUIT_URL = "";
//    //TODO  商户号
//    public static String APP_ID = "";
//    //TODO  支付宝私钥
//    public static String APP_PRIVATE_KEY = "";
//    // TODO   支付宝公钥
//    public static String ALIPAY_PUBLIC_KEY = "";
//    // TODO   支付宝支付回调地址
//    public static String NOTIFY_URL = "/notify/aliNotify.do";
//    /*******************支付宝配置********************/
//
//    //    应用ID
//    public static String WX_APP_ID = "";
//    //    应用对应的商户号
//    public static String WX_MCHID = "";
//    //    商户号对应的密钥
//    public static String WX_PARTNERKEY = "";
//    public static String WX_SELECTKEY = "";
//    //    微信支付回调接口
//    public static String WX_NOTIFY_URL = "";
//    public static String WX_FILEKEY = "";
//
//    /*=============微信配置=================*/
//    static {
//        try {
//            // 加载redis配置文件
//            ResourceBundle bundle = ResourceBundle.getBundle("config/jdbc");
//            if (bundle == null) {
//                throw new IllegalArgumentException("[config/jdbc.properties] is not found!");
//            }
//            APP_ID = bundle.getString("ALIPAY.APP_ID");
//            APP_PRIVATE_KEY = bundle.getString("ALIPAY.PRIVATE_KEY");
//            ALIPAY_PUBLIC_KEY = bundle.getString("ALIPAY.PUBLIC_KEY");
//            NOTIFY_URL = bundle.getString("ALIPAY.NOTIFY_URL");
//
//            WX_APP_ID = bundle.getString("WEIXIN.APP_ID");
//            WX_MCHID = bundle.getString("WEIXIN.MCHID");
//            WX_PARTNERKEY = bundle.getString("WEIXIN.PARTNERKEY");
//            WX_SELECTKEY = bundle.getString("WEIXIN.SELECTKEY");
//            WX_NOTIFY_URL = bundle.getString("WEIXIN.NOTIFY_URL");
//            WX_FILEKEY = bundle.getString("WEIXIN.FILEKEY");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//    /*=============微信配置=================*/
//
//    public DefaultAlipayClient getAlipay() {
//        return new DefaultAlipayClient(GET_WAY, APP_ID, APP_PRIVATE_KEY, FORMAT, CHARSET, ALIPAY_PUBLIC_KEY, SIGN_TYPE);
//    }
//
//
//}
