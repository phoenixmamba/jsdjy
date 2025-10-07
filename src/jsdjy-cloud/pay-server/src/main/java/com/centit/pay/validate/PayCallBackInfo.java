package com.centit.pay.validate;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2025/9/2 9:39
 **/
@Data
public class PayCallBackInfo {
    /**
     * 订单号
     */
    String orderId;
    /**
     * 支付类型
     */
    String payType;

    /**
     * 麦座支付类型
     */
    String mzPayType;

    /**
     * 订单支付金额
     */
    BigDecimal orderPayMoney;

    /**
     * 支付流水号
     */
    String transactionId;

    public PayCallBackInfo(String orderId, String payType, String mzPayType,BigDecimal orderPayMoney, String transactionId) {
        this.orderId = orderId;
        this.payType = payType;
        this.mzPayType = mzPayType;
        this.orderPayMoney = orderPayMoney;
        this.transactionId = transactionId;
    }
}
