package com.centit.thirdserver.biz.enums;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/11/21 22:31
 **/
public enum MzApiEnum {

    //获取积分和余额免密限额信息
    ASSET_RULE("获取积分和余额免密限额信息","AlibabaDamaiMzUserAssetRuleGetRequest","alibaba_damai_mz_user_asset_rule_get_response"),
    //获取账户资产信息
    ASSET_INFO("获取账户资产信息","AlibabaDamaiMzUserAssetinfoGetRequest","alibaba_damai_mz_user_assetinfo_get_response"),
    //获取会员收货地址详情
    ADDRESS_DETAIL("获取会员收货地址详情","AlibabaDamaiMzUserAddressDetailRequest","alibaba_damai_mz_user_address_detail_response"),
    //获取会员收货地址列表
    ADDRESS_LIST("获取会员收货地址列表","AlibabaDamaiMzUserAddressListRequest","alibaba_damai_mz_user_address_list_response"),
    //确认麦座订单
    CONFIRM_ORDER("确认麦座订单","AlibabaDamaiMzOrderConfirmRequest","alibaba_damai_mz_order_confirm_response"),
    //会员余额抵扣
    CUT_BALANCE("会员余额抵扣","AlibabaDamaiMzUserAssetModifyRequest","alibaba_damai_mz_user_asset_modify_response"),
    //会员积分抵扣
    CUT_INTEGRAL("会员积分抵扣","AlibabaDamaiMzUserAssetModifyRequest","alibaba_damai_mz_user_asset_modify_response"),
    //获取资产业务key
    ASSET_VERIFY_KEY("获取资产业务key","AlibabaDamaiMzAssetVerifycodeCheckRequest","alibaba_damai_mz_asset_verifycode_check_response"),

    ;

    /**
     * 请求信息
     */
    private final String requestInfo;
    /**
     * 请求名
     */
    private final String requestName;
    /**
     * 返回数据字段名
     */
    private final String responseName;

    private MzApiEnum(String requestInfo, String requestName, String responseName) {
        this.requestInfo = requestInfo;
        this.requestName = requestName;
        this.responseName = responseName;
    }

    public String getRequestInfo(){
        return requestInfo;
    }
    public String getRequestName(){
        return requestName;
    }
    public String getResponseName(){
        return responseName;
    }
}
