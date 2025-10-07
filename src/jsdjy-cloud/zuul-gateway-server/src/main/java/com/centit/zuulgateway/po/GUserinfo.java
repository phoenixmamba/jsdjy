package com.centit.zuulgateway.po;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class GUserinfo implements Serializable {

    private String userCode;

    private String userName;

    /**
     * 加密密码
     */
    private String userPin;

    private String userPwd;

    private String loginName;

    /**
     * y用户描述
     */
    private String userDesc;

    private String regCellPhone;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 邮箱
     */
    private String regEmail;

    /**
     * 性别：1男；2女
     */
    private String sex;

    /**
     * 头像附件id
     */
    private String photo;

    /**
     * T:生效 F:无效
     */
    private String isValid;

    private String userType;

    private String englishName;

    /**
     * 身份证号
     */
    private String idCardNo;

    private Date updateDate;

    private Date createDate;

    private String creator;

    private String updator;

    /**
     * 个性签名
     */
    private String signature;

    private String birthday;

    private String labelid;

    private String labelname;

    private String mzuserid;

    private String roletype;

    private String systemname;

    private String url;

    private String crmid;
}
