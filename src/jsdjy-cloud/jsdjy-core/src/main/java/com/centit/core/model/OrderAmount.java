package com.centit.core.model;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/12/20 8:59
 **/
@Data
public class OrderAmount {

    //商品现价
    private BigDecimal currentPrice;
    //订单运费
    private BigDecimal shipAmount;
    //商品金额
    private BigDecimal goodsAmount;
    //订单总金额
    private BigDecimal totalAmount;
    //订单剩余需要支付的金额
    private BigDecimal payAmount;
    //优惠券抵扣金额
    private BigDecimal couponCut = BigDecimal.ZERO;
    //会员体系抵扣金额
    private BigDecimal accountCut = BigDecimal.ZERO;
    //积分抵扣数量
    private Integer integralValue;
    //积分抵扣金额
    private BigDecimal integralCut = BigDecimal.ZERO;
    //余额抵扣金额
    private BigDecimal balanceCut = BigDecimal.ZERO;
}
