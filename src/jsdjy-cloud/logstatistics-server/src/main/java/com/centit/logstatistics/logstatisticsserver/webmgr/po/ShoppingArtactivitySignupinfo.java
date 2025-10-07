package com.centit.logstatistics.logstatisticsserver.webmgr.po;

import lombok.Data;

import java.io.Serializable;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-03-22
 **/
@Data
public class ShoppingArtactivitySignupinfo implements Serializable {


    private String id;

    private String activityId;

    private String ofId;

    private String signupInfo;

    private String signupTime;


}
