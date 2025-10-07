package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;
import java.math.BigDecimal;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-02-21
 **/
@Data
public class ShoppingSysconfig implements Serializable {


    private Long id;

    private Date addTime;

    private Boolean deleteStatus;

    private String address;

    private Integer bigHeight;

    private Integer bigWidth;

    private String closeReason;

    private String codeStat;

    private Integer complaintTime;

    private Integer consumptionRatio;

    private String copyRight;

    private String creditrule;

    private Boolean deposit;

    private String description;

    private Boolean emailEnable;

    private String emailHost;

    private Integer emailPort;

    private String emailPws;

    private String emailTest;

    private String emailUser;

    private String emailUserName;

    private Integer everyIndentLimit;

    private Boolean gold;

    private Integer goldMarketValue;

    private Boolean groupBuy;

    private String hotSearch;

    private Integer imageFilesize;

    private String imageSaveType;

    private String imageSuffix;

    private Integer indentComment;

    private Boolean integral;

    private Integer integralRate;

    private Boolean integralStore;

    private String keywords;

    private Integer memberDayLogin;

    private Integer memberRegister;

    private Integer middleHeight;

    private Integer middleWidth;

    private Boolean securityCodeConsult;

    private Boolean securityCodeLogin;

    private Boolean securityCodeRegister;

    private String securityCodeType;

    private String shareCode;

    private Integer smallHeight;

    private Integer smallWidth;

    private Boolean smsEnbale;

    private String smsPassword;

    private String smsTest;

    private String smsURL;

    private String smsUserName;

    private Boolean appEnbale;

    private String appUserName;

    private String appPassword;

    private Boolean storeAllow;

    private String storePayment;

    private String sysLanguage;

    private String templates;

    private String title;

    private String uploadFilePath;

    private String userCreditrule;

    private Boolean visitorConsult;

    private Boolean voucher;

    private String websiteName;

    private Boolean websiteState;

    private Integer ztcPrice;

    private Boolean ztcStatus;

    private Long goodsimageId;

    private Long membericonId;

    private Long storeimageId;

    private Long websitelogoId;

    private Integer domainAllowCount;

    private Boolean secondDomainOpen;

    private String sysDomain;

    private Boolean qqLogin;

    private String qqLoginId;

    private String qqLoginKey;

    private String qqDomainCode;

    private String sinaDomainCode;

    private Boolean sinaLogin;

    private String sinaLoginId;

    private String sinaLoginKey;

    private String imageWebServer;

    private Date luceneUpdate;

    private Integer alipayFenrun;

    private Integer balanceFenrun;

    private Integer autoOrderConfirm;

    private Integer autoOrderNotice;

    private Integer bargainMaximum;

    private BigDecimal bargainRebate;

    private String bargainState;

    private Integer bargainStatus;

    private String bargainTitle;

    private String serviceQqList;

    private String serviceTelphoneList;

    private Integer sysDeliveryMaximum;

    private Boolean ucBbs;

    private String kuaidiId;

    private String ucApi;

    private String ucAppid;

    private String ucDatabase;

    private String ucDatabasePort;

    private String ucDatabasePws;

    private String ucDatabaseUrl;

    private String ucDatabaseUsername;

    private String ucIp;

    private String ucKey;

    private String ucTablePreffix;

    private String currencyCode;

    private Integer bargainValidity;

    private Integer deliveryAmount;

    private Integer deliveryStatus;

    private String deliveryTitle;

    private String websiteCss;

    private Integer combinAmount;

    private Integer combinCount;

    private Integer ztcGoodsView;

    private Integer autoOrderEvaluate;

    private Integer autoOrderReturn;

    private Boolean weixinStore;

    private Integer weixinAmount;

    private Integer configPaymentType;

    private String weixinAccount;

    private String weixinAppid;

    private String weixinAppsecret;

    private String weixinToken;

    /**
     * 三方平台_token
     */
    private String weixinComponentAccessToken;

    /**
     * 预授权码
     */
    private String weixinComponentVerifyTicket;

    private String weixinWelecomeContent;

    private Long storeWeixinLogoId;

    private Long weixinQrImgId;

    /**
     * 分销设置,0关闭，1启用
     */
    private Boolean fenxiao;

    private String siteUrl;

    /**
     * 1余额，2,积分
     */
    private Integer fenxiaoType;

    /**
     * 推荐商家奖励0关闭，1启用
     */
    private Boolean merchants;

    /**
     * 推荐商家分润比例
     */
    private Double merchantsProportion;

    /**
     * 是否开启审核：1开启0关闭默认开启
     */
    private Boolean ifToexamine;

    /**
     * 代理分润
     */
    private Boolean agency;

    /**
     * 省分润
     */
    private Double agencyProvince;

    /**
     * 市分润
     */
    private Double agencyCity;

    /**
     * 区分润
     */
    private Double agencyArea;

    private String integralTitle;

    private BigDecimal poundage1;

    private BigDecimal poundage2;

    private BigDecimal poundage3;

    private Long storeId;

    private Double cashScale;

    private Integer shareStatus;

    private BigDecimal lowCach;

    /**
     * 快递100企业接口编号
     */
    private String kuaidiCustomer;

    private BigDecimal balanceRegister;

    private BigDecimal integralRegister;

    private BigDecimal redbagsRegister;

    private BigDecimal balanceInviter;

    private BigDecimal poundage10;

    private BigDecimal poundage11;

    private BigDecimal poundage12;

    private BigDecimal poundage13;

    private BigDecimal poundage14;

    private BigDecimal poundage15;

    private BigDecimal poundage16;

    private BigDecimal poundage4;

    private BigDecimal poundage5;

    private BigDecimal poundage6;

    private BigDecimal poundage7;

    private BigDecimal poundage8;

    private BigDecimal poundage9;

    private Double integralCode;

    private Double integralMonth;

    private Double inviterRegister;

    private Double mortal;

    private Integer parseSpuerVip;

    private Integer parseVip;

    private Double queuingAward;

    private Double storeScale;

    private Double storeScale2;

    private Double superScale;

    private BigDecimal dayPay;

    private BigDecimal monthPay;

    private BigDecimal yearPay;

    private BigDecimal poundage17;

    private BigDecimal homeMaintainMoney;

    /**
     * 闲置商品验机费
     */
    private BigDecimal verifyCost;

    private BigDecimal deductionAmount;

    private Integer give;

    private BigDecimal annualFee;

    private BigDecimal lifelongFee;

    private BigDecimal monthlyFee;

    private BigDecimal quarterFee;

    private BigDecimal deductAmount;

    private Integer deductEndTime;

    private String deductName;

    private String verifyText;

    private String shipmentRemindContent;

    private Boolean shipmentRemindStatus;

    private String playServer;

    private String pushServer;

    private String qlyServer;

    private String serviceUrl;


}
