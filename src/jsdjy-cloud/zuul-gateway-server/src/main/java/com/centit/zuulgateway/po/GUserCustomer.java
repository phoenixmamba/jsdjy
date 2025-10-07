package com.centit.zuulgateway.po;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>人员pojo增强类<p>
 *
 * @version : 1.0
 * @Author : li_hao
 * @Description : 人员pojo增强类
 * @Date : 2020-06-08
 **/
@Data
public class GUserCustomer extends GUserinfo implements Serializable {

    /**
     * 个性签名
     */
    private String signature;

    /**
     * 指纹
     */
    private String fingerPrint;

    /**
     * 手势密码
     */
    private String gestureCode;

    /**
     * 默认解锁类型：P密码；F指纹；G手势密码
     */
    private String unlockType;

    /**
     * 待办开关：Y开起；N关闭
     */
    private String todoMsgSwitch;

    /**
     * 聊天通知开关：Y开起；N关闭
     */
    private String talkMsgSwitch;

    /**
     * 字体大小
     */
    private Integer fontSize;

    /**
     * 用户状态：1Enable，2Disable，3Delete
     */
    private String epStatus;

    /**
     * 办公电话号码
     */
    private String telephonenumber;

    /**
     * 隐藏状态：T隐藏，F未隐藏
     */
    private String hideStatus;

    /**
     * 最近登录失败次数（登录成功后清零）
     */
    private Integer lastLoginFailTimes;

    /**
     * 最近登录失败时间
     */
    private Date lastLoginFailDate;


    /**
     * 地区（城市）编码
     */
    private String areaCode;


    /**
     * 备用字段1：支付宝昵称
     */
    private String userMark1;

    /**
     * 备用字段2：微信昵称
     */
    private String userMark2;

    /**
     * 备用字段3：支付宝userId
     */
    private String userMark3;

    /**
     * 备用字段4：微信unionid
     */
    private String userMark4;

    /**
     * 备用字段5
     */
    private String userMark5;

    /**
     * 备用字段6：便捷过闸：cmch:船名船号
     */
    private String userMark6;

    /**
     * 备用字段7：便捷过闸：wysbm：唯一识别码
     */
    private String userMark7;

    /**
     * 备用字段8：边界过闸：船舶类型
     */
    private String userMark8;

    /**
     * 备用字段9
     */
    private String userMark9;


    /**
     * 地区（城市）名称
     */
    private String areaName;


    /**
     * 企业id
     */
    private String corpid;

    /**
     * 企业用户类型：F法人；M管理员；Y普通员工
     */
    private String corpUserType;

    /**
     * 是否为主企业：T主企业；F兼职企业
     */
    private String isPrimary;

    /**
     * 排序号
     */
    private String userOrder;


    public Integer getLastLoginFailTimes() {  //解决特殊情况下，用户主表G_USERINFO有数据而拓展表G_USERINFO_EXTENSION没有数据的情况，登录时getLastLoginFailTimes空指针
        return lastLoginFailTimes == null ? 0 : lastLoginFailTimes;
    }

}
