package com.centit.pay.common.enums;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 *
 * @author:cui_jian
 */
@Component
public class Const {
    @Value("${order.prefix.merge}")
    private String shoppingMergeOrderPrefix;

    @Value("${order.orderType.merge}")
    private Integer shoppingMergeOrderType;

    @Value("${order.prefix.park}")
    private String parkOrderPrefix;
    @Value("${order.orderType.park}")
    private String parkorderType;

    @Value("${order.prefix.ticket}")
    private String ticketOrderPrefix;
    @Value("${order.orderType.ticket}")
    private String ticketorderType;

    @Value("${order.prefix.shopping.culgoods}")
    private String shoppingCulOrderPrefix;
    @Value("${order.orderType.shopping.culgoods}")
    private Integer shoppingCulOrderType;
    @Value("${cart.cartType.culgoods}")
    private Integer shoppingCulCartType;

    @Value("${order.prefix.shopping.integralgoods}")
    private String shoppingIntOrderPrefix;
    @Value("${order.orderType.shopping.integralgoods}")
    private Integer shoppingIntOrderType;
    @Value("${cart.cartType.integralgoods}")
    private Integer shoppingIntCartType;

    @Value("${order.prefix.shopping.activity}")
    private String shoppingActOrderPrefix;
    @Value("${order.orderType.shopping.activity}")
    private Integer shoppingActOrderType;
    @Value("${cart.cartType.activity}")
    private Integer shoppingActCartType;

    @Value("${order.prefix.shopping.plan}")
    private String shoppingPlanOrderPrefix;
    @Value("${order.orderType.shopping.plan}")
    private Integer shoppingPlanOrderType;
    @Value("${cart.cartType.plan}")
    private Integer shoppingPlanCartType;

    @Value("${order.prefix.shopping.class}")
    private String shoppingClassOrderPrefix;
    @Value("${order.orderType.shopping.class}")
    private Integer shoppingClassOrderType;
    @Value("${cart.cartType.class}")
    private Integer shoppingClassCartType;

    @Value("${order.orderState.anomalous}")
    private int orderStateAnomalous;
    @Value("${order.orderState.cancel}")
    private int orderStateCancel;
    @Value("${order.orderState.toPay}")
    private int orderStateToPay;
    @Value("${order.orderState.inPay}")
    private int orderStateInPay;
    @Value("${order.orderState.hasPay}")
    private int orderStateHasPay;
    @Value("${order.orderState.hasDone}")
    private int orderStateHasDone;
    @Value("${order.prefix.video}")
    private String videoOrderPrefix;
    @Value("${order.orderType.video}")
    private Integer videoOrderType;
    @Value("${order.prefix.recharge}")
    private String rechargeOrderPrefix;
    @Value("${order.orderType.recharge}")
    private Integer rechargeOrderType;

    @Value("${mz.client.url}")
    private String mzClientUrl;

    @Value("${mz.client.appkey}")
    private String mzClientAppkey;

    @Value("${mz.client.secret}")
    private String mzClientSecret;

    @Value("${mz.pay.ali}")
    private String mzPayAli;

    @Value("${mz.pay.wx}")
    private String mzPayWx;

    public static String SHOPPING_MERGE_ORDER;//TODO 	合并支付订单前缀
    public static Integer SHOPPING_MERGE_ORDER_TYPE;//TODO 	合并支付订单类型

    public static String SHOPPING_CUL_ORDER;//TODO 	文创商品订单前缀
    public static Integer SHOPPING_CUL_ORDER_TYPE;//TODO 	文创商品订单类型
    public static Integer SHOPPING_CUL_CART_TYPE;//TODO 	文创商品购物车类型

    public static String SHOPPING_INT_ORDER;//TODO 	积分商品订单前缀
    public static Integer SHOPPING_INT_ORDER_TYPE;//TODO 	积分商品订单类型
    public static Integer SHOPPING_INT_CART_TYPE;//TODO 	积分商品购物车类型

    public static String TICKET_ORDER_PREFIX;//TODO 	演出票订单前缀
    public static String TICKET_ORDER_TYPE;//TODO 	演出票订单类型

    public static String PARK_ORDER_PREFIX;//TODO 	停车费订单前缀
    public static String PARK_ORDER_TYPE;//TODO 	停车费订单类型

    public static String VIDEO_ORDER;//TODO 	点播订单前缀
    public static Integer VIDEO_ORDER_TYPE;//TODO 	点播订单类型

    public static String RECHARGE_ORDER;//TODO 	充值订单前缀
    public static Integer RECHARGE_ORDER_TYPE;//TODO 	充值订单类型

    public static String SHOPPING_ACT_ORDER;//TODO 	艺教活动订单前缀
    public static Integer SHOPPING_ACT_ORDER_TYPE;//TODO 	艺教活动订单类型
    public static Integer SHOPPING_ACT_CART_TYPE;//TODO 	艺教活动购物车类型

    public static String SHOPPING_PLAN_ORDER;//TODO 	爱艺计划订单前缀
    public static Integer SHOPPING_PLAN_ORDER_TYPE;//TODO 	爱艺计划订单类型
    public static Integer SHOPPING_PLAN_CART_TYPE;//TODO 	爱艺计划购物车类型

    public static String SHOPPING_CLASS_ORDER;//TODO 	艺教培训订单前缀
    public static Integer SHOPPING_CLASS_ORDER_TYPE;//TODO 	艺教培训订单类型
    public static Integer SHOPPING_CLASS_CART_TYPE;//TODO 	艺教培训购物车类型

    public static int ORDER_STATE_ANOMALOUS;//TODO 	异常订单状态
    public static int ORDER_STATE_CALCEL;//TODO 	已取消订单状态
    public static int ORDER_STATE_TOPAY;//TODO 	待支付订单状态
    public static int ORDER_STATE_INPAY;//TODO 	支付中订单状态
    public static int ORDER_STATE_HASPAY;//TODO 	已支付订单状态
    public static int ORDER_STATE_HASDONE;//已完成订单状态

    public static String MZ_CLIENT_URL;//TODO
    public static String MZ_CLIENT_APPKEY;//TODO
    public static String MZ_CLIENT_SECRET;//TODO

    public static String MZ_PAYID_ALI;//麦座支付宝支付方式id
    public static String MZ_PAYID_WX;//麦座微信支付方式id

    public static String PARK_PAYID_WX ="20";//TODO 	停车支付专用微信支付方式id

    public static String STORE_ID = "0";//TODO 	系统默认官方商家ID

    public static Integer THIRDLOG_TYPE_CRM = 1;
    public static Integer THIRDLOG_TYPE_MZ = 2;
    public static Integer THIRDLOG_TYPE_PARK = 3;
    public static Integer THIRDLOG_TYPE_WX = 4;
    public static Integer THIRDLOG_TYPE_ALI = 5;
    public static Integer THIRDLOG_TYPE_NN = 6;
    public static Integer THIRDLOG_TYPE_SMS = 7;

    @PostConstruct
    public void init(){
        MZ_PAYID_ALI = mzPayAli;
        MZ_PAYID_WX=mzPayWx;

        SHOPPING_MERGE_ORDER = shoppingMergeOrderPrefix;
        SHOPPING_MERGE_ORDER_TYPE = shoppingMergeOrderType;

        SHOPPING_CUL_ORDER = shoppingCulOrderPrefix;
        SHOPPING_CUL_ORDER_TYPE = shoppingCulOrderType;
        SHOPPING_CUL_CART_TYPE = shoppingCulCartType;

        SHOPPING_INT_ORDER = shoppingIntOrderPrefix;
        SHOPPING_INT_ORDER_TYPE = shoppingIntOrderType;
        SHOPPING_INT_CART_TYPE = shoppingIntCartType;

        SHOPPING_ACT_ORDER = shoppingActOrderPrefix;
        SHOPPING_ACT_ORDER_TYPE = shoppingActOrderType;
        SHOPPING_ACT_CART_TYPE = shoppingActCartType;

        SHOPPING_PLAN_ORDER=shoppingPlanOrderPrefix;
        SHOPPING_PLAN_ORDER_TYPE=shoppingPlanOrderType;
        SHOPPING_PLAN_CART_TYPE = shoppingPlanCartType;

        SHOPPING_CLASS_ORDER = shoppingClassOrderPrefix;
        SHOPPING_CLASS_ORDER_TYPE  = shoppingClassOrderType;
        SHOPPING_CLASS_CART_TYPE = shoppingClassCartType;

        TICKET_ORDER_PREFIX = ticketOrderPrefix;
        TICKET_ORDER_TYPE = ticketorderType;

        PARK_ORDER_PREFIX = parkOrderPrefix;
        PARK_ORDER_TYPE = parkorderType;

        VIDEO_ORDER=videoOrderPrefix;
        VIDEO_ORDER_TYPE = videoOrderType;

        RECHARGE_ORDER= rechargeOrderPrefix;
        RECHARGE_ORDER_TYPE = rechargeOrderType;

        ORDER_STATE_ANOMALOUS=orderStateAnomalous;
        ORDER_STATE_CALCEL = orderStateCancel;
        ORDER_STATE_TOPAY = orderStateToPay;
        ORDER_STATE_INPAY = orderStateInPay;
        ORDER_STATE_HASPAY= orderStateHasPay;
        ORDER_STATE_HASDONE = orderStateHasDone;


        MZ_CLIENT_URL= mzClientUrl;
        MZ_CLIENT_APPKEY = mzClientAppkey;
        MZ_CLIENT_SECRET = mzClientSecret;
    }

}
