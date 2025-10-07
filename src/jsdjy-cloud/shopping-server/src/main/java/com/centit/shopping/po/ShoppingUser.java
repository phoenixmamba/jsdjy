package com.centit.shopping.po;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-02-25
 **/
@Data
public class ShoppingUser implements Serializable {


    private String id;

    private String mzuserid;

    private String levelname;

    private String addTime;

    private Boolean deleteStatus;

    private String msn;

    private String qq;

    private String ww;

    private String address;

    /**
     * 余额
     */
    private BigDecimal availableBalance;

    private String place;

    /**
     * 邀请人
     */
    private String inviterId;

    private String birthday;

    private String email;

    private BigDecimal freezeBlance;

    private Integer gold;

    private String lastLoginDate;

    /**
     * 积分
     */
    private BigDecimal integral;

    private String lastLoginIp;

    private Integer loginCount;

    private String loginDate;

    private String loginIp;

    private String mobile;

    private String password;

    private Integer report;

    /**
     * 0女,1男,2保密
     */
    private Integer sex;

    private Integer status;

    private String telephone;

    private String trueName;

    private String userName;

    private String userRole;

    private Integer userCredit;

    private String photoId;

    private String storeId;

    private String qqOpenid;

    private String sinaOpenid;

    private String storeQuickMenu;

    private String parentId;

    private Integer years;

    private String areaId;

    /**
     * 用户累计消费
     */
    private BigDecimal buyTotal;

    /**
     * 累计返现
     */
    private BigDecimal buyReturn;

    /**
     * 1为C级合伙人，2为B级，3为A级
     */
    private Integer level;

    /**
     * 拥金
     */
    private BigDecimal commission;

    /**
     * 红包
     */
    private BigDecimal redbags;

    /**
     * 累计拥金
     */
    private BigDecimal commissionTotal;

    private BigDecimal dgold;

    private String wxopenid;

    private String wxewmId;

    /**
     * 1省代理，2市代理，3区县代理
     */
    private Integer agency;

    /**
     * 商家货款
     */
    private BigDecimal paymentForGoods;

    private Integer membershipDays;

    private String card;

    /**
     * 小程序openid
     */
    private String miniopenid;

    /**
     * 客户姓名
     */
    private String customerName;

    private String partStoreId;

    private String nickName;

    private Integer version;

    private String alipay;

    private String alipayName;

    private String bankCard;

    private String bankName;

    private String bankUser;

    private Integer floor;

    private Double integralMonth;

    private Double integralMonthOld;

    private Integer myInviterSize;

    private Integer myteam;

    private String payPwd;

    private BigDecimal storeDgold;

    private String userrankId;

    /**
     * 代理城市
     */
    private String agencyareaId;

    /**
     * 区域经理
     */
    private Boolean regional;

    /**
     * 采购经理
     */
    private Boolean purchasing;

    /**
     * 公司股东
     */
    private Double corporate;

    private String sessionKey;

    private String bankAddress;

    private String IDCard;

    /**
     * 会员过期时间
     */
    private String memberTime;

    /**
     * 会员等级，0普通10月费20季费30年费40终身
     */
    private Integer memberType;

    /**
     * 是否新用户 -1为不是，1是
     */
    private Integer isNew;

    private String giftCurrency;

    private Boolean anchor;

    private String isValid;
}
