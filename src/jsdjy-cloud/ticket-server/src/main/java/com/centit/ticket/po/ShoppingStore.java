package com.centit.ticket.po;

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
public class ShoppingStore implements Serializable {


    private String id;

    private Date addTime;

    private Boolean deleteStatus;

    private Boolean cardApprove;

    private Boolean realstoreApprove;

    private String storeAddress;

    private Integer storeCredit;

    private String storeInfo;

    private String storeMsn;

    private String storeName;

    private String storeOwer;

    private String storeOwerCard;

    private String storeQq;

    private Boolean storeRecommend;

    private Date storeRecommendTime;

    private String storeSeoDescription;

    private String storeSeoKeywords;

    private Integer storeStatus;

    private String storeTelephone;

    private String storeZip;

    private String template;

    private Date validity;

    private String violationReseaon;

    private Long areaId;

    private Long cardId;

    private Long gradeId;

    private Long scId;

    private Long storeBannerId;

    private Long storeLicenseId;

    private Long storeLogoId;

    private Long updateGradeId;

    private Integer domainModifyCount;

    private String storeSecondDomain;

    private Integer favoriteCount;

    private BigDecimal storeLat;

    private BigDecimal storeLng;

    private String storeWw;

    private String mapType;

    private Date deliveryBeginTime;

    private Date deliveryEndTime;

    private Date combinBeginTime;

    private Date combinEndTime;

    private BigDecimal freeMailPrice;

    private Boolean isOutline;

    private Boolean isOnline;

    private String storeType;

    private Long cardFanId;

    private Date auditTime;

    private Integer bargainAllowtime;

    private Integer bargainNumber;

    private BigDecimal bargainPrice;

    private Boolean bargainStatus;

    private Date bargainStatusBeginTime;

    private Date bargainStatusEndTime;

    private Integer maintainAuthority;

    private String weixinAccount;

    private String weixinAppid;

    private String weixinAppsecret;

    private Date weixinBeginTime;

    private Date weixinEndTime;

    private Integer weixinStatus;

    private String weixinToken;

    private String weixinWelecomeContent;

    private Long storeWeixinLogoId;

    private Long weixinQrImgId;

    private Long allowOneGoodsClassId;


}
