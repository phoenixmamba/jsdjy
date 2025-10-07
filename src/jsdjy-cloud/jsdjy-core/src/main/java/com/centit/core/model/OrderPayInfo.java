package com.centit.core.model;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 订单支付信息
 * @Date : 2025/9/2 14:41
 **/
@Data
public class OrderPayInfo {
    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    private String ofId;

    /**
      * 订单ID
      */
    private String orderId;

    /**
     * 麦座用户ID
     */
    private String mzUserId;

//    /**
//     * 订单状态
//     */
//    private Integer orderStatus;

//    /**
//     * 支付时间
//     */
//    private String payTime;

    /**
     * 支付方式ID
     */
    private String paymentId;

    /**
     * 麦座支付方式ID
     */
    private String mzPaymentId;

    /**
     * 支付金额
     */
    private BigDecimal payPrice;

    /**
     * 订单总金额
     */
    private BigDecimal totalPrice;

    /**
     * 订单总金额
     */
    private BigDecimal orderTolPrice;

    /**
     * 外部交易流水号
     */
    private String outTradeNo;

    /**
     * 积分抵扣金额
     */
    private BigDecimal deductionIntegralPrice;

    private Integer deductionIntegral=0;

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
    
    private String assetBizKey;

    /**
     * 优惠券ID
     */
    private String couponId;
}
