package com.centit.order.po;

import lombok.Data;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/12/30 16:31
 **/
@Data
public class ShoppingOrderPayPo {
    private String id;

    private String updateTime;

    /**
     * 现金支付状态 -1：无需支付 0：待支付 1：已支付 2：支付失败
     */
    private Integer cashStatus;

    /**
     * 优惠券核销状态 -1：无需核销 0：待核销 1：已核销 2：核销失败
     */
    private Integer couponStatus;

    /**
     * 礼品卡核销状态 -1：无需核销 0：待核销 1：已核销 2：核销失败
     */
    private Integer giftcardStatus;

    /**
     * 积分支付状态 -1：无需支付 0：待支付 1：已支付 2：支付失败
     */
    private Integer integralStatus;

    private Integer balanceStatus;

    /**
     * 总体支付状态 0：未支付完成；1：支付完成
     */
    private Integer payStatus;

    private String ofId;

    private String userId;

    private String outTradeNo;
}
