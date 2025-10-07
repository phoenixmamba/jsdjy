package com.centit.shopping.common.enums;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 *
 * @author:cui_jian
 */
@Component
public class CRMConst {

    @Value("${crm.authorization.systemName}")
    private  String authorizationSystemName;
    @Value("${crm.authorization.url}")
    private  String authorizationUrl;

    @Value("${crm.couponList.roleType}")
    private  String couponListRoleType;
    @Value("${crm.couponList.url}")
    private  String couponListUrl;

    @Value("${crm.couponDtl.roleType}")
    private  String couponDtlRoleType;
    @Value("${crm.couponDtl.url}")
    private  String couponDtlUrl;

    @Value("${crm.memcpList.roleType}")
    private  String memcpListRoleType;
    @Value("${crm.memcpList.url}")
    private  String memcpListUrl;

    @Value("${crm.createCoupon.roleType}")
    private  String createCouponRoleType;
    @Value("${crm.createCoupon.url}")
    private  String createCouponUrl;

    @Value("${crm.deleteCoupon.roleType}")
    private  String deleteCouponRoleType;
    @Value("${crm.deleteCoupon.url}")
    private  String deleteCouponUrl;

    @Value("${crm.grantcoupon.roleType}")
    private  String grantcouponRoleType;
    @Value("${crm.grantcoupon.url}")
    private  String grantcouponUrl;

    @Value("${crm.untie.roleType}")
    private  String untieRoleType;
    @Value("${crm.untie.url}")
    private  String untieUrl;

    @Value("${crm.writeOff.roleType}")
    private  String writeOffRoleType;
    @Value("${crm.writeOff.url}")
    private  String writeOffUrl;


    public static String AUTHORIZATION_SYSTEMNAME;//TODO 	鉴权信息接口外部系统代码
    public static String AUTHORIZATION_URL;//TODO 	获取鉴权信息接口

    public static String COUPONLIST_ROLETYPE;//TODO 获取优惠券列表
    public static String COUPONLIST_URL;//TODO

    public static String COUPONDTL_ROLETYPE;//TODO 	会员优惠券详情
    public static String COUPONDTL_URL;//TODO

    public static String MEMCPLIST_ROLETYPE;//TODO 	会员优惠券列表
    public static String MEMCPLIST_URL;//TODO

    public static String CREATECOUPON_ROLETYPE;//TODO 	创建优惠券
    public static String CREATECOUPON_URL;//TODO

    public static String DELETECOUPON_ROLETYPE;//TODO 	删除优惠券
    public static String DELETECOUPON_URL;//TODO

    public static String GRANTCOUPON_ROLETYPE;//TODO 	发放优惠券
    public static String GRANTCOUPON_URL;//TODO

    public static String UNTIE_ROLETYPE;//TODO 	解绑优惠券
    public static String UNTIE_URL;//TODO


    public static String WRITEOFF_ROLETYPE;//TODO 	核销优惠券
    public static String WRITEOFF_URL;//TODO


    @PostConstruct
    public void init(){
        AUTHORIZATION_SYSTEMNAME = authorizationSystemName;
        AUTHORIZATION_URL = authorizationUrl;

        COUPONLIST_ROLETYPE = couponListRoleType;
        COUPONLIST_URL = couponListUrl;

        COUPONDTL_ROLETYPE = couponDtlRoleType;
        COUPONDTL_URL = couponDtlUrl;

        MEMCPLIST_ROLETYPE=memcpListRoleType;
        MEMCPLIST_URL=memcpListUrl;

        CREATECOUPON_ROLETYPE=createCouponRoleType;
        CREATECOUPON_URL = createCouponUrl;

        DELETECOUPON_ROLETYPE = deleteCouponRoleType;
        DELETECOUPON_URL = deleteCouponUrl;

        GRANTCOUPON_ROLETYPE = grantcouponRoleType;
        GRANTCOUPON_URL = grantcouponUrl;

        UNTIE_ROLETYPE=untieRoleType;
        UNTIE_URL = untieUrl;

        WRITEOFF_ROLETYPE= writeOffRoleType;
        WRITEOFF_URL = writeOffUrl;

    }

}
