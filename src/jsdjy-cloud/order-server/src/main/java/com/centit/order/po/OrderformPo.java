package com.centit.order.po;

import com.centit.core.consts.StoreConst;
import com.centit.core.dto.OrderDto;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/12/27 15:37
 **/
@Data
public class OrderformPo {

    private String id;

    private String addTime;

    private Boolean deleteStatus;

    private String finishTime;

    private BigDecimal goodsAmount;

    private String invoice;

    private Integer invoiceType;

    private String msg;

    private String orderId;

    private Integer orderStatus;

    private String payTime;

    private String payMsg;

    private String shipCode;

    private String shipTime;

    private BigDecimal shipPrice;

    private BigDecimal totalPrice;

    private String addrId;

    private String paymentId;

    private String storeId;

    private String userId;

    private String transport;

    private String outOrderId;

    private String ecId;

    private String ciId;

    private Integer orderType;

    private String weixinpayOrderId;

    /**
     * 商家赠送D币
     */
    private BigDecimal goodsDgold;

    /**
     * 订单总积分
     */
    private BigDecimal totalIntegral;

    private String wxPrepayId;

    private BigDecimal payPrice;

    private Integer deductionIntegral;

    private String couponId;

    private BigDecimal deductionIntegralPrice;

    private BigDecimal deductionMemberPrice;

    private BigDecimal deductionBalancePrice;

    private BigDecimal deductionCouponPrice;

    private BigDecimal deductionGiftcardPrice;

    public OrderformPo(){

    }

    public OrderformPo(OrderDto dto){
        this.orderId=dto.getOrderId();
        //订单类型
        this.orderType=dto.getOrderType();

        //订单金额
        this.totalPrice=dto.getOrderTotalPrice();
        //运费金额
        this.shipPrice=dto.getOrderShipPrice();
        //需支付的现金金额
        this.payPrice=dto.getOrderPayPrice();

        //快递/自提
        this.transport=dto.getTransport();
        //收货地址
        this.addrId=dto.getAddressId();

        //商店id
        this.storeId= StoreConst.DEFAULT_STORE_ID;
        //用户id
        this.userId=dto.getUserId();
        //订单使用的优惠券id
        this.ciId=dto.getCouponId();
        //优惠券抵扣金额
        this.deductionCouponPrice=dto.getOrderDeductionCouponPrice();
        //会员权益抵扣金额
        this.deductionMemberPrice=dto.getOrderDeductionMemberPrice();
        //积分抵扣
        this.deductionIntegralPrice=dto.getOrderDeductionIntegralPrice();
        this.deductionIntegral=dto.getOrderUseIntegralValue();
        //账户余额支付金额
        this.deductionBalancePrice=dto.getOrderDeductionBalancePrice();
    }
}
