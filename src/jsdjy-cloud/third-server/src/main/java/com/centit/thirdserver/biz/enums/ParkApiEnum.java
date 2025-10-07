package com.centit.thirdserver.biz.enums;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/11/21 22:31
 **/
public enum ParkApiEnum {

    //账单查询/费用查询
    GET_PARKING_PAYMENT_INFO("账单查询/费用查询","/unite-api/api/wec/GetParkingPaymentInfo","getParkingPaymentInfo"),
    //车流量查询
    GET_PARKING_REPORT_INFO("车流量查询","/unite-api/api/wec/GetHourlyFlowReport","getHourlyFlowReport"),

    ;

    /**
     * 接口名
     */
    private final String apiName;

    /**
     * 接口地址
     */
    private final String apiUrl;

    /**
     * 服务code
     */
    private final String serviceCode;

    ParkApiEnum(String apiName, String apiUrl, String serviceCode) {
        this.apiName = apiName;
        this.apiUrl = apiUrl;
        this.serviceCode = serviceCode;
    }

    public String getApiName() {
        return apiName;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public String getServiceCode() {
        return serviceCode;
    }
}
