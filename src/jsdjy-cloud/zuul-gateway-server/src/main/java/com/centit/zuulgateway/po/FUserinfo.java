package com.centit.zuulgateway.po;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>系统用户<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description :  实体类
 * @Date : 2020-04-10
 **/
@Data
public class FUserinfo implements Serializable {


    private static final long serialVersionUID = 2109978821118715534L;

    private String userCode;

    private String userPin;

    /**
     * 发布任务/接收任务/系统管理
     */
    private String userType;

    private String isValid;

    private String loginName;

    private String userName;

    private String userTag;

    private String englishName;

    private String userDesc;

    private Integer loginTimes;

    private String activeTime;

    private String loginIp;

    private Long addrbookId;

    /**
     * 注册用Email，不能重复
     */
    private String regEmail;

    private String userPwd;

    private Date pwdExpiredTime;

    private String regCellPhone;

    private String idCardNo;

    private String primaryUnit;

    /**
     * 微信号
     */
    private String userWord;

    private String userOrder;

    private String updateDate;

    private String createDate;

    private String extjsoninfo;

    private String creator;

    private String updator;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 用户头像附件id
     */
    private String photo;

    /**
     * 性别：1男；2女
     */
    private String sex;

    /**
     * 个人签名
     */
    private String signature;

}
