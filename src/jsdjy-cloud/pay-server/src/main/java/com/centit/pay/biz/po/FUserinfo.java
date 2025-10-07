package com.centit.pay.biz.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-01-06
 **/
@Data
public class FUserinfo implements Serializable {


    private String userCode;

    private String userPin;

    /**
     * 发布任务/接收任务/系统管理
     */
    private String userType;

    /**
     * T:生效 F:无效
     */
    private String isValid;

    private String loginName;

    /**
     * 姓名
     */
    private String userName;

    /**
     * 用于第三方系统关联
     */
    private String userTag;

    private String englishName;

    private String userDesc;

    private int loginTimes;

    private Date activeTime;

    private String topUnit;

    /**
     * 注册用Email，不能重复
     */
    private String regEmail;

    /**
     * 如果需要可以有
     */
    private String userPwd;

    private Date pwdExpiredTime;

    private String regCellPhone;

    private String idCardNo;

    private String primaryUnit;

    /**
     * 微信号
     */
    private String userWord;

    private int userOrder;

    private Date updateDate;

    private Date createDate;

    private String extJsonInfo;

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

    /**
     * 指纹
     */
    private String fingerprint;

    /**
     * 手势密码
     */
    private String gesturecode;

    /**
     * 默认解锁类型：P密码；F指纹；G手势密码
     */
    private String unlocktype;

    /**
     * 待办开关：Y开起；N关闭
     */
    private String todomsgswitch;

    /**
     * 聊天通知开关：Y开起；N关闭
     */
    private String talkmsgswitch;

    /**
     * 字体大小
     */
    private Integer fontsize;

    /**
     * 用户状态：1Enable，2Disable，3Delete
     */
    private String epStatus;

    /**
     * 办公电话号码
     */
    private String telephoneNumber;

    /**
     * 隐藏状态：T隐藏，F未隐藏
     */
    private String hideStatus;

    /**
     * 最近登陆失败次数（登陆成功后清零）
     */
    private Integer lastLoginFailTimes;

    /**
     * 最近登陆失败时间
     */
    private Date lastLoginFaildate;

    private String unitname1;

    private String unitname2;


}
