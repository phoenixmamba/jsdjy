package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-07-08
 **/
@Data
public class FUserinfo implements Serializable {


    private String userCode;

    private String userPin;

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

    private String regEmail;

    private String userPwd;

    private String pwdExpiredTime;

    private String regCellPhone;

    private String idCardNo;

    private String primaryUnit;

    private String userWord;

    private Integer userOrder;

    private String updateDate;

    private String createDate;

    private String extjsoninfo;

    private String creator;

    private String updator;

    private String nickName;

    private String photo;

    private String sex;

    private String signature;

    private String topUnit;

    private String corpid;


}
