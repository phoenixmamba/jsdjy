package com.centit.mallserver.po;

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
