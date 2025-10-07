package com.centit.thirdserver.biz.enums;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/11/21 23:00
 **/
public enum ThirdApiLogTypeEnum {

    /**
     * 日志类型 1：crm；2：麦座；3：速停车；0：其它
     */

    CRM(1),MZ(2),PARK(3),OTHER(0),

    ;

    private Integer logType;

    private ThirdApiLogTypeEnum(int logType){
        this.logType=logType;
    }

    public Integer getLogType(){
        return logType;
    }
}
