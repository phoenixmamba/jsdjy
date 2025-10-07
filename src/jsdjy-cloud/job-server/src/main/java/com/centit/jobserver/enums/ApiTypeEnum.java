package com.centit.jobserver.enums;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/11/25 10:18
 **/
public enum ApiTypeEnum {
    MZ(1,"麦座"),
    CRM(2,"CRM"),
    PARK(3,"速停车"),
    ;

    private Integer apiType;
    private String apiName;

    private ApiTypeEnum(int apiType,String apiName){
        this.apiType=apiType;
        this.apiName=apiName;
    }

    public Integer getApiType(){
        return apiType;
    }

    public String getApiName(){
        return apiName;
    }
}
