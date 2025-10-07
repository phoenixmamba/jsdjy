package com.centit.logstatistics.logstatisticsserver.webmgr.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p> <p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description :  实体类
 * @Date : 2021-08-23
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

    private Date activeTime;

    private String loginIp;

    private Integer addrbookId;

    private String regEmail;

    private String userPwd;

    private Date pwdExpiredTime;

    private String regCellPhone;

    private String idCardNo;

    private String primaryUnit;

    private String userWord;

    private Integer userOrder;

    private Date updateDate;

    private Date createDate;

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
