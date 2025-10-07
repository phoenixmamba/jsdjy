package com.centit.core.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/12/19 14:04
 **/
@Data
public class OrderDto {
    private String id;
    @NotNull
    private String orderId;

    private Integer orderType;

    @NotNull
    private String userId;
    @NotNull
    private String goodsId;
    @NotNull
    private Integer goodsCount;

    private String propertys;
    private String specInfo;

    private String couponId;
    private String transport;
    private String addressId;

    private Integer useIntegral;
    private Integer useBalance;

    private BigDecimal unitPrice;
    private BigDecimal orderTotalPrice;
    private BigDecimal orderShipPrice;
    private BigDecimal orderPayPrice;
    private int orderUseIntegralValue;

    /**
     * 优惠券抵扣金额
     */
    private BigDecimal orderDeductionCouponPrice;
    /**
     * 会员体系抵扣金额
     */
    private BigDecimal orderDeductionMemberPrice;
    /**
     * 积分抵扣金额
     */
    private BigDecimal orderDeductionIntegralPrice;
    /**
     * 余额抵扣金额
     */
    private BigDecimal orderDeductionBalancePrice;

    private String accountPointPayKey;
    private String accountMoneyPayKey;

    private String cartId;

    private String sign;

}
