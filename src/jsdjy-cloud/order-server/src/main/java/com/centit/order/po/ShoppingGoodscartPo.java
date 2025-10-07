package com.centit.order.po;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/12/30 17:07
 **/
@Data
public class ShoppingGoodscartPo {
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

    private String propertys;

    private Integer deductionIntegral;

    private BigDecimal deductionIntegralPrice;

    private BigDecimal deductionMemberPrice;

    private BigDecimal deductionBalancePrice;

    private BigDecimal deductionCouponPrice;

    private BigDecimal deductionGiftcardPrice;

    private BigDecimal payPrice;

    private String transport;

    private BigDecimal shipPrice;
}
