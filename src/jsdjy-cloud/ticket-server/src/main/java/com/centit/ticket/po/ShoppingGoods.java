package com.centit.ticket.po;

import java.math.BigDecimal;
import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-04-29
 **/
@Data
public class ShoppingGoods implements Serializable {


    private String id;

    private String addTime;

    private String deleteStatus;

    private Integer goodsClick;

    private String goodsDetails;

    private String goodsFee;

    private Integer goodsInventory;

    private String goodsInventoryDetail;

    private String goodsName;

    private BigDecimal goodsPrice;

    private String goodsProperty;

    private Integer goodsRecommend;

    private Integer goodsSalenum;

    private Date goodsSellerTime;

    private String goodsSerial;

    private Integer goodsStatus;

    private Integer goodsTransfee;

    private BigDecimal goodsWeight;

    private String inventoryType;

    private String seoDescription;

    private String seoKeywords;

    private BigDecimal storePrice;

    private Boolean storeRecommend;

    private Date storeRecommendTime;

    private String ztcAdminContent;

    private Date ztcApplyTime;

    private Date ztcBeginTime;

    private Integer ztcClickNum;

    private Integer ztcDredgePrice;

    private Integer ztcGold;

    private Integer ztcPayStatus;

    private Integer ztcPrice;

    private Integer ztcStatus;

    private Long gcId;

    private Long goodsBrandId;

    private Long goodsMainPhotoId;

    private Long goodsStoreId;

    private Long ztcAdminId;

    private Integer goodsCollect;

    private Integer groupBuy;

    private Integer goodsChoiceType;

    private Long groupId;

    private Integer activityStatus;

    private Integer bargainStatus;

    private Integer deliveryStatus;

    private BigDecimal goodsCurrentPrice;

    private BigDecimal goodsVolume;

    private BigDecimal emsTransFee;

    private BigDecimal expressTransFee;

    private BigDecimal mailTransFee;

    private Long transportId;

    private Integer combinStatus;

    private Date combinBeginTime;

    private Date combinEndTime;

    private BigDecimal combinPrice;

    private BigDecimal descriptionEvaluate;

    private Boolean weixinShopHot;

    private Date weixinShopHottime;

    private Boolean weixinShopRecommend;

    private Date weixinShopRecommendtime;

    /**
     * app推荐
     */
    private Boolean appRecommend;

    /**
     * D币报销比例
     */
    private Double reimbursementRatio;

    /**
     * 可使用优惠券金额
     */
    private BigDecimal giftDCoins;

    /**
     * 商品排序
     */
    private Integer appSort;

    /**
     * 出厂价
     */
    private Double exFactoryPrice;

    /**
     * 使用积分抵扣设置 0:不允许使用积分抵扣;1:可使用部分积分抵扣(use_integral_value为最大可抵扣值);2:必须使用积分抵扣(use_integral_value为必须使用积分抵扣最小值)
     */
    private Integer useIntegralSet;

    /**
     * 使用积分抵扣值
     */
    private Integer useIntegralValue;

    /**
     * 所属专区(0普通，1签到专区，2升级商品)
     */
    private Integer goodArea;

    /**
     * app推荐时间
     */
    private Date appRecommendTime;

    /**
     * 赠送积分
     */
    private Double giveIntegral;

    /**
     * 赠送红包
     */
    private Double giveRed;

    private Integer limitBuy;

    /**
     * 签到送天数
     */
    private Integer signDay;

    private String producerName;

    private String producerPhone;

    private String producerUrl;

    private BigDecimal storeScale;

    private Integer probability;

    /**
     * 1,可提现2，不可提现
     */
    private Integer qdsType;

    private Long areaUserId;

    private BigDecimal erji;

    private BigDecimal quyu;

    private BigDecimal yiji;

    private String auditInfo;

    private String phone;

    private String qq;

    private String sendUser;

    private String wx;

    /**
     * 商品产地
     */
    private Long areaId;

    private Integer purchaseNum;

    private String priceRange;

    private String units;

    private Long maintainClassId;

    private Long screenSafeGroupId;

    private Long spellgroupgoodsId;

    private Long flashgoodsId;

    private BigDecimal maintainMoney;

    private Boolean screenSafe;

    private String goodsCheckbox;

    private Long usergoodsclassId;

    /**
     * 1为参加红包活动，-1为不参加
     */
    private Integer redActivityStatus;

    private BigDecimal fenxiao;

    /**
     * 是否参加砍价，为1表示参加
     */
    private Integer bargainMarketingStatus;

    /**
     * 是否参加秒杀，为1表示参加
     */
    private Integer flashSaleMarketingStatus;

    /**
     * 是否参加团购，为1表示参加
     */
    private Integer groupMarketingStatus;

    private Long bargainGoodsId;

    private Long ordershareredgoodsId;

    private Long findId;

    private Integer memberType;

    private Boolean liveDelivery;

    private BigDecimal liveDeliveryScale;

    /**
     * 是否允许使用现金支付 0:不允许;1:可使用
     */
    private Integer useCashSet;

    /**
     * 是否允许使用现金支付 0:不允许;1:可使用
     */
    private Integer useBalanceSet;

    private Integer useMembershipSet;

    /**
     * 核销账号
     */
    private String writeOffCount;

    /**
     * 是否支持自提,0不支持，1支持
     */
    private Integer selfextractionSet;

    /**
     * 自提地址
     */
    private String selfextractionAddress;

    /**
     * 商品须知
     */
    private String goodsNotice;

    /**
     * 退改说明
     */
    private String returnExplain;

    private String addUser;


}
