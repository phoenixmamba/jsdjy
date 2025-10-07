package com.centit.shopping.po;

import java.io.Serializable;

import com.centit.shopping.utils.CommonUtil;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @String : 2021-02-21
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

    private Boolean goodsRecommend;

    private Integer goodsSalenum;

    private String goodsSellerTime;

    private String goodsSerial;

    private String goodsStatus;

    private Integer goodsTransfee;

    private BigDecimal goodsWeight;

    private String inventoryType;

    private String seoDescription;

    private String seoKeywords;

    private BigDecimal storePrice;

    private Boolean storeRecommend;

    private String storeRecommendTime;

    private String ztcAdminContent;

    private String ztcApplyTime;

    private String ztcBeginTime;

    private Integer ztcClickNum;

    private Integer ztcDredgePrice;

    private Integer ztcGold;

    private Integer ztcPayStatus;

    private Integer ztcPrice;

    private Integer ztcStatus;

    private String gcId;

    public List<Map<String,Object>> getGcValue(){
        if(null !=getGcId()&&!"".equals(getGcId())){
            return CommonUtil.getGcValue(getGcId());
        }
        return null;
    }

    private String goodsBrandId;

    private String goodsMainPhotoId;

    private String goodsStoreId;

    private String ztcAdminId;

    private Integer goodsCollect;

    private Integer groupBuy;

    private Integer goodsChoiceType;

    private String groupId;

    private Integer activityStatus;

    private Integer bargainStatus;

    private Integer deliveryStatus;

    private BigDecimal goodsCurrentPrice;

    private BigDecimal goodsVolume;

    private BigDecimal emsTransFee;

    private BigDecimal expressTransFee;

    private BigDecimal mailTransFee;

    private String transportId;

    private Integer combinStatus;

    private String combinBeginTime;

    private String combinEndTime;

    private BigDecimal combinPrice;

    private BigDecimal descriptionEvaluate;

    private Boolean weixinShopHot;

    private String weixinShopHottime;

    private Boolean weixinShopRecommend;

    private String weixinShopRecommendtime;

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
    private String appRecommendTime;

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

    private String areaUserId;

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
    private String areaId;

    private Integer purchaseNum;

    private String priceRange;

    private String units;

    private String maintainClassId;

    private String screenSafeGroupId;

    private String spellgroupgoodsId;

    private String flashgoodsId;

    private BigDecimal maintainMoney;

    private Boolean screenSafe;

    private String goodsCheckbox;

    private String usergoodsclassId;

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

    private String bargainGoodsId;

    private String ordershareredgoodsId;

    private String findId;

    private Integer memberType;

    private Boolean liveDelivery;

    private BigDecimal liveDeliveryScale;

    private Integer useCashSet;

    private Integer useBalanceSet;

    private String writeOffCount;

    private String selfextractionSet;

    private String selfextractionAddress;

    private String goodsNotice;

    private String returnExplain;

    private Integer useMembershipSet;

    private Integer soldCount;

    private String addUser;
}
