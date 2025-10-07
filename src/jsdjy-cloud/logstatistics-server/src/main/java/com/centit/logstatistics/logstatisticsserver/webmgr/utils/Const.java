package com.centit.logstatistics.logstatisticsserver.webmgr.utils;

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

    @Value("${order.prefix.ticket}")
    private String ticketOrderPrefix;
    @Value("${order.orderType.ticket}")
    private Integer ticketOrderType;
    @Value("${cart.cartType.ticket}")
    private Integer ticketCartType;

    @Value("${order.prefix.park}")
    private String parkOrderPrefix;
    @Value("${order.orderType.park}")
    private Integer parkOrderType;
    @Value("${cart.cartType.park}")
    private Integer parkCartType;

    @Value("${order.prefix.video}")
    private String videoOrderPrefix;
    @Value("${order.orderType.video}")
    private Integer videoOrderType;
    @Value("${cart.cartType.video}")
    private Integer videoCartType;

    @Value("${order.prefix.recharge}")
    private String rechargeOrderPrefix;
    @Value("${order.orderType.recharge}")
    private Integer rechargeOrderType;
    @Value("${cart.cartType.recharge}")
    private Integer rechargeCartType;

    @Value("${mz.client.url}")
    private String mzClientUrl;

    @Value("${mz.client.appkey}")
    private String mzClientAppkey;

    @Value("${mz.client.secret}")
    private String mzClientSecret;

    public static String SHOPPING_MERGE_ORDER;//TODO 	合并支付订单前缀
    public static Integer SHOPPING_MERGE_ORDER_TYPE;//TODO 	合并支付订单类型

    public static String SHOPPING_CUL_ORDER;//TODO 	文创商品订单前缀
    public static Integer SHOPPING_CUL_ORDER_TYPE;//TODO 	文创商品订单类型
    public static Integer SHOPPING_CUL_CART_TYPE;//TODO 	文创商品购物车类型

    public static String SHOPPING_INT_ORDER;//TODO 	积分商品订单前缀
    public static Integer SHOPPING_INT_ORDER_TYPE;//TODO 	积分商品订单类型
    public static Integer SHOPPING_INT_CART_TYPE;//TODO 	积分商品购物车类型

    public static String SHOPPING_ACT_ORDER;//TODO 	艺教活动订单前缀
    public static Integer SHOPPING_ACT_ORDER_TYPE;//TODO 	艺教活动订单类型
    public static Integer SHOPPING_ACT_CART_TYPE;//TODO 	艺教活动购物车类型

    public static String SHOPPING_PLAN_ORDER;//TODO 	爱艺计划订单前缀
    public static Integer SHOPPING_PLAN_ORDER_TYPE;//TODO 	爱艺计划订单类型
    public static Integer SHOPPING_PLAN_CART_TYPE;//TODO 	爱艺计划购物车类型

    public static String SHOPPING_CLASS_ORDER;//TODO 	艺教培训订单前缀
    public static Integer SHOPPING_CLASS_ORDER_TYPE;//TODO 	艺教培训订单类型
    public static Integer SHOPPING_CLASS_CART_TYPE;//TODO 	艺教培训购物车类型

    public static String TICKET_ORDER;//TODO 	演出票订单前缀
    public static Integer TICKET_ORDER_TYPE;//TODO 	演出票订单类型
    public static Integer TICKET_CART_TYPE;//TODO 	演出票购物车类型

    public static String PARK_ORDER;//TODO 	停车订单前缀
    public static Integer PARK_ORDER_TYPE;//TODO 	停车订单类型
    public static Integer PARK_CART_TYPE;//TODO 	停车购物车类型
    public static String PARK_ORDER_PHOTO = "467850";//TODO 	停车订单默认图片

    public static String VIDEO_ORDER;//TODO 	点播订单前缀
    public static Integer VIDEO_ORDER_TYPE;//TODO 	点播订单类型
    public static Integer VIDEO_CART_TYPE;//TODO 	点播购物车类型

    public static String RECHARGE_ORDER;//TODO 	充值订单前缀
    public static Integer RECHARGE_ORDER_TYPE;//TODO 	充值订单类型
    public static Integer RECHARGE_CART_TYPE;//TODO 	充值购物车类型
    public static String RECHARGE_ORDER_PHOTO = "468191";//TODO 	充值订单默认图片

    public static String MZ_CLIENT_URL;//TODO 	麦座接口地址
    public static String MZ_CLIENT_APPKEY;//TODO 	麦座APPKEY
    public static String MZ_CLIENT_SECRET;//TODO 	麦座APPSECRET

    public static String STORE_ID = "1";//TODO 	系统默认官方商家ID
    public static String TRANSPORT_ID = "1";//TODO 	系统默认运费模板

    @PostConstruct
    public void init(){
        SHOPPING_MERGE_ORDER = shoppingMergeOrderPrefix;
        SHOPPING_MERGE_ORDER_TYPE = shoppingMergeOrderType;

        SHOPPING_CUL_ORDER = shoppingCulOrderPrefix;
        SHOPPING_CUL_ORDER_TYPE = shoppingCulOrderType;
        SHOPPING_CUL_CART_TYPE = shoppingCulCartType;

        SHOPPING_INT_ORDER = shoppingIntOrderPrefix;
        SHOPPING_INT_ORDER_TYPE = shoppingIntOrderType;
        SHOPPING_INT_CART_TYPE = shoppingIntCartType;

        SHOPPING_ACT_ORDER=shoppingActOrderPrefix;
        SHOPPING_ACT_ORDER_TYPE=shoppingActOrderType;
        SHOPPING_ACT_CART_TYPE = shoppingActCartType;

        SHOPPING_PLAN_ORDER=shoppingPlanOrderPrefix;
        SHOPPING_PLAN_ORDER_TYPE=shoppingPlanOrderType;
        SHOPPING_PLAN_CART_TYPE = shoppingPlanCartType;

        SHOPPING_CLASS_ORDER=shoppingClassOrderPrefix;
        SHOPPING_CLASS_ORDER_TYPE = shoppingClassOrderType;
        SHOPPING_CLASS_CART_TYPE = shoppingClassCartType;

        TICKET_ORDER = ticketOrderPrefix;
        TICKET_ORDER_TYPE = ticketOrderType;
        TICKET_CART_TYPE = ticketCartType;

        PARK_ORDER = parkOrderPrefix;
        PARK_ORDER_TYPE = parkOrderType;
        PARK_CART_TYPE = parkCartType;

        VIDEO_ORDER=videoOrderPrefix;
        VIDEO_ORDER_TYPE = videoOrderType;
        VIDEO_CART_TYPE=videoCartType;

        RECHARGE_ORDER= rechargeOrderPrefix;
        RECHARGE_ORDER_TYPE = rechargeOrderType;
        RECHARGE_CART_TYPE = rechargeCartType;

        MZ_CLIENT_URL= mzClientUrl;
        MZ_CLIENT_APPKEY = mzClientAppkey;
        MZ_CLIENT_SECRET = mzClientSecret;
    }

}
