package com.centit.zuulgateway.po;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>授权信息<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2020-05-06
 **/
@Data
public class ApiAuthorize implements Serializable {


    private String id;

    /**
     * 授权key
     */
    private String customKey;

    /**
     * 授权secret
     */
    private String customSecret;

    private String jsapiticket;

    /**
     * 令牌
     */
    private String accessToken;

    /**
     * 有效期（单位秒）
     */
    private String validperiod;

    /**
     * 授权此部门及以下的通讯录权限
     */
    private String topDeptId;

    /**
     * ip白名单，多个ip用英文逗号分隔
     */
    private String ipWhiteList;

    /**
     * 回调地址
     */
    private String callBackUrl;

    private String deptId;

    private String userId;

    private String appId;

    /**
     * 注册类型：1部门；2用户；3应用
     */
    private String type;

    private String createTime;

    private String updateTime;

    private String creator;

    private String updator;

    /**
     * 最近一次accessToken刷新时间
     */
    private String reTokenTime;

    /**
     * T:生效 F:无效
     */
    private String isValid;



}
