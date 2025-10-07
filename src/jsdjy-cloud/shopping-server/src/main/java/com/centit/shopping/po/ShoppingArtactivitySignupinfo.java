package com.centit.shopping.po;

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

    private String activityName;

    private String mobile;  //下单用户手机号

    private String ofId;

    private String addTime;  //下单时间

    private String orderId;

    private String tradeNo;   //交易流水号

    private String signupInfo;

    private String signupTime;


}
