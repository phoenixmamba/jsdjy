package com.centit.shopping.po;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p> <p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description :  实体类
 * @Date : 2021-06-08
 **/
@Data
public class OrderDetail implements Serializable {


    private String ofId;

    private String orderId;

    private String addTime;

    private String goodsName;

    private String eventName;

    private Integer orderType;

    private Integer orderStatus;

    private BigDecimal shipPrice;

    private BigDecimal goodsPrice;

    private BigDecimal totalPrice;

    private String couponId;

    private ShoppingCoupon couponInfo;

    private Integer deductionIntegral;
    private Integer deductionIntegralState=0;

    private BigDecimal deductionIntegralPrice;
    private Integer deductionIntegralPriceState=0;

    private BigDecimal deductionMemberPrice;
    private Integer deductionMemberPriceState=0;

    private BigDecimal deductionBalancePrice;
    private Integer deductionBalancePriceState=0;

    private BigDecimal deductionCouponPrice;
    private Integer deductionCouponPriceState=0;

    private BigDecimal deductionGiftcardPrice;
    private Integer deductionGiftcardPriceState=0;

    private BigDecimal payPrice;
    private Integer payPriceState=0;

    private BigDecimal wxPayPrice;   //微信支付金额
    private BigDecimal wxPayPrice_park;   //微信支付（停车场）金额
    private BigDecimal aliPayPrice;  //支付宝支付金额

    private String paymentId;

    private String payTime;

    private String payOrderId;

    private String userId;
    private String userName;
    private String mobile;

    private String outOrderId;

    private List<HashMap<String,Object>> goodsInfo = new ArrayList<>();

    private Integer goodsCount;

    private Integer offStatus;

    private String offAccount;

    private String offTime;

    private String signInfo;

    private String specInfo;

    private String shipPerson;
    private String shipPhone;
    private String shipAddress;

}
