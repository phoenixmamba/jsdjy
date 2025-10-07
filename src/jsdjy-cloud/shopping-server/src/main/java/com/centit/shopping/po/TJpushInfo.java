package com.centit.shopping.po;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>极光推送历史记录<p>
 *
 * @version : 1.0
 * @Author : lihao
 * @Description : 实体类
 * @Date : 2021-01-19
 **/
@Data
public class TJpushInfo implements Serializable {


    private String id;

    /**
     * 接收手机号，极光的别名alias
     */
    private String mobile;

    private String code;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String notification;

    /**
     * 跳转url
     */
    private String url;

    /**
     * 第三方业务参数数据
     */
    private String data;

    /**
     * 创建时间
     */
    private String createDate;


    private String appName;
    private String appIconFileId;


}
