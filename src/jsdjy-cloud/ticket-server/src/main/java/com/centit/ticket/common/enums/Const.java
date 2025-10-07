package com.centit.ticket.common.enums;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 *
 * @author:cui_jian
 */
@Component
public class Const {

    @Value("${mz.client.url}")
    private String mzClientUrl;

    @Value("${mz.client.appkey}")
    private String mzClientAppkey;

    @Value("${mz.client.secret}")
    private String mzClientSecret;

    @Value("${order.prefix.ticket}")
    private String ticketOrderPrefix;

    @Value("${order.orderType.ticket}")
    private Integer ticketOrderType;

    @Value("${cart.cartType.ticket}")
    private Integer ticketCartType;

    public static String MZ_CLIENT_URL;//TODO 	麦座接口地址
    public static String MZ_CLIENT_APPKEY;//TODO 	麦座APPKEY
    public static String MZ_CLIENT_SECRET;//TODO 	麦座APPSECRET

    public static String STORE_ID = "1";//TODO 	系统默认官方商家ID
    public static String TRANSPORT_ID = "1";//TODO 	系统默认运费模板

    public static String TICKET_ORDER;//TODO 	麦座订单前缀
    public static Integer TICKET_ORDER_TYPE;//TODO 	麦座订单类型
    public static Integer TICKET_CART_TYPE;//TODO

    public static String TICKET_CLASSID_ARTMOVIE="75977";//TODO 	艺术电影分类id
    public static String TICKET_CLASSID_AIYI="626002";//TODO 	爱意活动分类id

    public static Integer THIRDLOG_TYPE_CRM = 1;
    public static Integer THIRDLOG_TYPE_MZ = 2;
    public static Integer THIRDLOG_TYPE_PARK = 3;

    @PostConstruct
    public void init(){

        MZ_CLIENT_URL= mzClientUrl;
        MZ_CLIENT_APPKEY = mzClientAppkey;
        MZ_CLIENT_SECRET = mzClientSecret;

        TICKET_ORDER = ticketOrderPrefix;
        TICKET_ORDER_TYPE = ticketOrderType;
        TICKET_CART_TYPE = ticketCartType;
    }

}
