package com.centit.pay.biz.po;

import java.math.BigDecimal;
import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-06-10
 **/
@Data
public class ShoppingGoodscart implements Serializable {


    private String id;

    private String addTime;

    private Boolean deleteStatus;

    private Integer count;

    private BigDecimal price;

    /**
     * 积分
     */
    private Integer integral;

    private String specInfo;

    private String goodsId;

    private String ofId;

    private Integer cartType;

    private String scId;

    private BigDecimal screenPrice;

    private String screenSafe;

    private String anchorId;

    /**
     * 商品规格属性id
     */
    private String propertys;

    /**
     * 积分抵扣金额
     */
    private BigDecimal deductionIntegralPrice;

    /**
     * 会员权益抵扣金额
     */
    private BigDecimal deductionMemberPrice;

    /**
     * 余额抵扣金额
     */
    private BigDecimal deductionBalancePrice;

    /**
     * 优惠券抵扣金额
     */
    private BigDecimal deductionCouponPrice;

    /**
     * 礼品卡抵扣金额
     */
    private BigDecimal deductionGiftcardPrice;

    private BigDecimal deductionIntegral;

    private String transport;

    private BigDecimal payPrice;

    private BigDecimal shipPrice;


}
