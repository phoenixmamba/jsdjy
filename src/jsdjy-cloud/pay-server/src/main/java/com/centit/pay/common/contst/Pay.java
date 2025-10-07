package com.centit.pay.common.contst;

import com.alipay.api.DefaultAlipayClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ResourceBundle;

@Component
public class Pay {
    @Value("${pay.alipay.appId}")
    private String alipayAppId;
    @Value("${pay.alipay.appPrivateKey}")
    private String alipayAppPrivateKey;
    @Value("${pay.alipay.publicKey}")
    private String alipayPublicKey;
    @Value("${pay.alipay.notifyUrl}")
    private String alipayNotifyUrl;

    @Value("${pay.wxpay.notifyUrl}")
    private String wxpayNotifyUrl;
    @Value("${pay.wxpay.notifyUrl_park}")
    private String wxpayParkNotifyUrl;   //停车专用

    // 微信各请求路径,固定不变
    public static final String ORDER_REFUND = "https://api.mch.weixin.qq.com/secapi/pay/refund";//TODO 微信退款API
    public static final String ORDER_REFUND_QUERY = "https://api.mch.weixin.qq.com/pay/refundquery";//TODO 微信退款查询API
    public static final String ORDER_QUERY = "https://api.mch.weixin.qq.com/pay/orderquery";//TODO 微信订单查询API
    public static final String ORDER_CLOSE = "https://api.mch.weixin.qq.com/pay/closeorder";//TODO 微信关闭订单API
    public static final String ORDER_PAY = "https://api.mch.weixin.qq.com/pay/unifiedorder";//TODO 微信下单API

    /****************支付宝配置*********************/
//    支付宝请求，固定值
    public static final String GET_WAY = "https://openapi.alipay.com/gateway.do";
    public static final String FORMAT = "JSON";
    public static final String SIGN_TYPE = "RSA2";
    public static final String CHARSET = "utf-8";
    //TODO    支付宝退款回调
    public static final String REFUND_URL = "/alipayRefundUrl.htm";
    //TODO    wap支付途中退出的页面
    public static final String QUIT_URL = "";
    //TODO  商户号
    public static String APP_ID;
//    public static String APP_ID = "2021002130685046";     //TODO 测试
    //public static String APP_ID = "2021002155658242";
    //TODO  支付宝应用私钥
    public static String APP_PRIVATE_KEY;
    //测试
//    public static String APP_PRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCpo75agSZXTQv/HkmPgqYX+M2VvttGyemxMfegTTLI2JAj+q1wJvGqg3iTV7J8dNCddAMLOdG8koIqryyca/IdROorPXDkyK2gldrXBx473TzBBR6wzopyK2/u3JdqwEpJ3eJDKUUgTYmehUY5kV+EWJL8DEYerHdcvR6L8U42D1OhDqBZuvSYL/wHpBDC4GBi1Np7KhbjW2oBbYqySj8GwsZFgnnPW135Z48IxkEu8mECfviYlhYKCbGpy7YQe/5OAiMSwUwzBZyH+Tm2rPgnQjpFOrQtF2plC7qrmEQVMLSvtJVArKTAG/jO5TvlGHy3Vx7And4Kx1mQCvWFQT8HAgMBAAECggEAcwNKXrArX4skNA7DzuHEXIJaWEly/5aLs5BuFCfi45s4drxRdLViqbnDORp/L7Q/cFEkcyr7UT9/sxLbSx5Ao6mCnlhkvyhz12liWuE/lVTVCQTAnx0ZbniWxjqXClBezZ/69I/5hwIq6OfxCbzPJrqqxOpJbCiAMMcj+7tZzHoNNZmxUlSU4ofEGRMlxSepZBGGyby6mzNLLdpHkn2GeKL3mVJhQxA7v8CI+2wGYXixrzChZ7OS7N0vN3FRI7Kv3Oma+6MBP2eyrUHGOZQYF0hQ3TQd9h5uR95RbC3K0UuegOXeNBX9e9HYC2CCGKE07O7/+RY9+6gn3KoXq/iRIQKBgQD/sE3a2knHmbFfZiE6bAotnFGidpKSD0ZizsvfTy0BcXZ1ooeqadHLstqFHZLyaYowhDwePekLgqzZkgcfhMvYDfYK4n7nm9s5ueZpPFqY6ApXgCsdIBonXcaqS6/QjcnKlC9iI8PiqZ1dk5VswwV8ssZbUA8RfUSMnmZ6VoOQNwKBgQCp2J5kpauLSqtOSYO4O9SEw03Mlnm0+7u2HxAY7nvj82vCgr0UB9deqg/HpQgBaiIVMirb08VbU0XAalC9EpalVtwqUYmpxCm+90brH2XgZ9z8ba00vETG3eA2mWaPBDXkJDHpO9L/4AeEbSyZE/xTT5AqLwf9zuuWYjpY0hs/sQKBgQDTdUNuE8mgwYLnmtcwMdnZDsaUzes50Fgtr0j8TcaywtttPhVk4k8XX7tJC8RTpuUaasItYJYzZsb8yMALWRAchq9NITRC56rOeugLcFaczop/Awik9b+rfuqmPCITILAaUaM+TsXZ1tzGXx/c6wzGrDNPoU5U8HyYrwehdZso0QKBgGnx5VuCu+63NQQwdymEqJvn96+WbXSnUf/YgIIMwNsADEfYpXjJK16xVgaCuuum+HugP3vALWs6Flhf+Nz3q2CXPd5n3ic3ZZLpTCN+Al5oa0BKabBrf7tMy39DUOYFCli0+y0xU/yoEghY0WjrPon5J0Qo8iMv32R/AUZGSRkBAoGBAPGTCqUv9f4nPCNSX70ygeEgSA09EFhR4etBM1QyEZc4RSVyF9rjrLbDNSBxId040luFmEY5HEvywx+HFJIZPGqm03uAJvB7QnT/OTchyIpG0dp+eo0wSf8V6ldItFIlznlNW8/toa8AcuMc0h8xOvj9VohFrW50joBoik6TTM/P";
    //生产
    //public static String APP_PRIVATE_KEY = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQCkwREjcR3TEdP0IRKoVmC4ySRW1VAxK5IxSctUHwNsJ20IjmLULl042aNrDuXbaK4PHq87VR9HTnteb+fWG0smRQB5t7tV4j6J5FqLWiwhYch9uaarm7/6NQ1OTfAHp89nl6d6zqqsD93mgIfJVIeAP+0TMGqqhqwN7AnxDVjYiPr69R97a7/c4KTW1LCbrcQCx6MoXW8X1sbRNEybLgi3q/xCcb9cifpNC6P5F8kKFYnKzQnsGnJ+3lTwbfxOmNnUmHlhAJGDkpitsbz8H/83TezOJBWZ9SUd069qxQ9QCnKu2S6VMntC3GrzLb2/woyJ96tkiKwNhfeFF9Rw7jhzAgMBAAECggEBAKSkG1Bjtf47jqgkdNSsnTOMLjhoKx83/3wp1ntWEsF+FMO8Jp8LRjWNqHVQCfvc/DQ34b1VL1C+6SJSike4Nr0jr3nzVc5ZrU13WdRwZDFn2kdpw1ky6AJOCeH6mKfN1sIbF07uQDh6ErJKN2fGhTJwr6XBeJa4xi+TdcviHqsjfFhD/xa3cr/t9WE08UyGu07qXkD18uQ+rJXdgmOaeXR9OlR0BgiFLdxfRvJ6uojL+q9oN23BEjMhdMR+eAZ1j9rv9QB0zGI1l3a3iXs7HsPpclJfukaaDXa2SDblqpKWFlAn1OzNY85tQfUn1IAd1FiGf++8i39B1gf6q9p6ncECgYEA355CgSjnW4LgrFJmFUQObIsOF3B0Da7o2HueuFFwEAXoAXDqOpq0Qdg/0Fz8Kid46FbuucxiCY+Y8W0y7hdHERdKDX1NgPFUmnr0SMPzk4ee50A2aR4o7KYmmhgce5PhXSALyZXBk4ycOj4SKI8qQ9ji1iBFC480N4jCDfxy9PkCgYEAvJypRVyDkr6YjW7ck7zYtX48/SQTxNfTbPLOgdF0bv8imELopvcrrNk/9D/2G2x90KyZKWcYLXDYANBREXrz0kZyLhiqXjyVS4e5D0QN3HAH3pdpQRMB4Z/8RAGAv2tqi+jmvEXOrzi5jS6WrLCv+EzoxTtFmZlVD0Qlak9+b8sCgYEAhnsRucVUpccmwNtpAv8Dwak610PMt3HqHE7z3Bs1zp0OstcqNgZVw4d+kRtqlxtX7jphpSFSEDfoncCzMVaUaaXc5hHmK1Z3L9Qj1yLo+F9GZAJM9pipufAOJPu9HWCI1s1v6VKDsO2OAsTTfMrkexsTB/0uKM59c6xuL6UTqVkCgYAW2dQYNzEpojXRTNEkhQisVHMHKEvM2WkgFfq2961nlTmXwON29xMvidKZwPYMNIS5t2+G+JqRRs6kqphnycGohChpNUvOxwA/el4NX21ee6Q4b5es7qSXtU5W3ue+SUFLWhg6TyrPR8wTMsXGflGTf53j515zUYRgqRzvWFo1KQKBgQCgZz5hPuouJ499nHN8bps3PptMFSiuJV/7j7VwijUoZTAcOBeisnthP204XAdcoCb+8UG+OQKvpFEzZAipdwxwMzH7gMqhGZfcOWMOOYy/ZAvfg2gzr8J+RQEBR0PY+E/yQEh/uy3k6hWc1INWByuHinrIkZF6dIW0tWo51GCmMA==";

    // TODO   支付宝公钥
    public static String ALIPAY_PUBLIC_KEY;
//    public static String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqaO+WoEmV00L/x5Jj4KmF/jNlb7bRsnpsTH3oE0yyNiQI/qtcCbxqoN4k1eyfHTQnXQDCznRvJKCKq8snGvyHUTqKz1w5MitoJXa1wceO908wQUesM6Kcitv7tyXasBKSd3iQylFIE2JnoVGOZFfhFiS/AxGHqx3XL0ei/FONg9ToQ6gWbr0mC/8B6QQwuBgYtTaeyoW41tqAW2Ksko/BsLGRYJ5z1td+WePCMZBLvJhAn74mJYWCgmxqcu2EHv+TgIjEsFMMwWch/k5tqz4J0I6RTq0LRdqZQu6q5hEFTC0r7SVQKykwBv4zuU75Rh8t1cewJ3eCsdZkAr1hUE/BwIDAQAB";
    //public static String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApMERI3Ed0xHT9CESqFZguMkkVtVQMSuSMUnLVB8DbCdtCI5i1C5dONmjaw7l22iuDx6vO1UfR057Xm/n1htLJkUAebe7VeI+ieRai1osIWHIfbmmq5u/+jUNTk3wB6fPZ5enes6qrA/d5oCHyVSHgD/tEzBqqoasDewJ8Q1Y2Ij6+vUfe2u/3OCk1tSwm63EAsejKF1vF9bG0TRMmy4It6v8QnG/XIn6TQuj+RfJChWJys0J7Bpyft5U8G38TpjZ1Jh5YQCRg5KYrbG8/B//N03sziQVmfUlHdOvasUPUApyrtkulTJ7Qtxq8y29v8KMiferZIisDYX3hRfUcO44cwIDAQAB";

    // TODO   支付宝支付回调地址
    public static String NOTIFY_URL;
//    public static String NOTIFY_URL = "http://app.centit.com:443/pay/notify/aliNotify";
    //public static String NOTIFY_URL = "http://221.226.75.102/pay/notify/aliNotify";

    /*******************支付宝配置********************/

    //    应用ID
    public static String WX_APP_ID = "";
    //    应用对应的商户号
    public static String WX_MCHID = "";
    //    商户号对应的密钥
    public static String WX_PARTNERKEY = "";
    public static String WX_SELECTKEY = "";
    //    微信支付回调接口
    public static String WX_NOTIFY_URL;
    public static String WX_NOTIFY_PARK_URL;   //停车专用
//    public static String WX_NOTIFY_URL = "http://app.centit.com:443/pay/notify/wxNotify";
    //public static String WX_NOTIFY_URL = "http://221.226.75.102/pay/notify/wxNotify";
    public static String WX_FILEKEY = "";

    /*=============微信配置=================*/
    static {
        try {
            // 加载redis配置文件
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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    /*=============微信配置=================*/

    public DefaultAlipayClient getAlipay() {
        return new DefaultAlipayClient(GET_WAY, APP_ID, APP_PRIVATE_KEY, FORMAT, CHARSET, ALIPAY_PUBLIC_KEY, SIGN_TYPE);
    }

    @PostConstruct
    public void init(){
        APP_ID=alipayAppId;
        APP_PRIVATE_KEY=alipayAppPrivateKey;
        ALIPAY_PUBLIC_KEY = alipayPublicKey;
        NOTIFY_URL = alipayNotifyUrl;

        WX_NOTIFY_URL =wxpayNotifyUrl;
        WX_NOTIFY_PARK_URL =wxpayParkNotifyUrl;

    }
}
