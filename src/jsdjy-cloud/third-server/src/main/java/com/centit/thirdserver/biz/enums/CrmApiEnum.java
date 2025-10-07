package com.centit.thirdserver.biz.enums;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/11/21 22:31
 **/
public enum CrmApiEnum {

    //获取优惠券列表
    APP_COUPON_LIST("ROLE_APP_COUPON_LIST","获取优惠券列表","/crm/coupon/couponList","GET"),
    //删除优惠券
    APP_COUPON_DEL("ROLE_APP_COUPON_DEL","删除优惠券","/crm/coupon/deleteCoupon","POST"),
    //获取优惠券详情
    APP_MEMBER_COUPON_INFO("ROLE_APP_MEMBER_COUPON_INFO","获取优惠券详情","/crm/coupon/couponDtl","GET"),
    //获取会员优惠券列表
    APP_MEMBER_COUPON_LIST("ROLE_APP_MEMBER_COUPON_LIST","获取优惠券详情","/crm/coupon/memcpList","GET"),
    //核销优惠券
    APP_COUPON_WRITEOFF("ROLE_APP_COUPON_WRITEOFF","优惠券核销","/crm/coupon/writeOff","POST"),

    ;

    /**
     * 外部系统角色
     */
    private final String roleType;

    /**
     * 接口名
     */
    private final String apiName;

    /**
     * 接口地址
     */
    private final String apiUrl;

    /**
     * 请求
     */
    private final String reqMethod;

    CrmApiEnum(String roleType,String apiName, String apiUrl, String reqMethod) {
        this.roleType=roleType;
        this.apiName = apiName;
        this.apiUrl = apiUrl;
        this.reqMethod = reqMethod;
    }

    public String getRoleType() {
        return roleType;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public String getApiName() {
        return apiName;
    }

    public String getReqMethod() {
        return reqMethod;
    }
}
