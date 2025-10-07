package com.centit.admin.system.po;

import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-03-03
 **/
@Data
public class FUserinfo implements Serializable {


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

    private Integer addrbookId;

    /**
     * 注册用Email，不能重复
     */
    private String regEmail;

    private String userPwd;

    private String pwdExpiredTime;

    private String regCellPhone;

    private String idCardNo;

    private String primaryUnit;

    private String primaryUnitName;
    /**
     * 微信号
     */
    private String userWord;

    private Integer userOrder;

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
     * 头像附件
     */
    private String photo;

    /**
     * 性别：1男；2女
     */
    private String sex;

    /**
     * 个性签名
     */
    private String signature;

    private String topUnit;

    /**
     * 第三方标识
     */
    private String corpid;


}
