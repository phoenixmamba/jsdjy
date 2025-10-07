package com.centit.zuulgateway.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2020-08-23
 **/
@Data
public class TStLogin implements Serializable {


    private String id;

    /**
     * 日期时间
     */
    private String logintime;

    /**
     * 用户标识
     */
    private String userid;

    /**
     * 终端型号(iPhone,iPad...)
     */
    private String terminalType;

    /**
     * 终端设备号
     */
    private String terminalNumber;

    /**
     * 终端系统版本号
     */
    private String terminalSystemVersion;

    /**
     * app版本号
     */
    private String appVersion;

    /**
     * 设备频偏
     */
    private String terminalBrand;

    private String terminalBrandvalue;


}
