package com.centit.pay.biz.po;

import java.math.BigDecimal;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-03-09
 **/
@Data
public class ShoppingOrderform implements Serializable {


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

    private BigDecimal refund;

    private String refundType;

    private String shipCode;

    private String shipTime;

    private BigDecimal shipPrice;

    private BigDecimal totalPrice;

    private String addrId;

    private String paymentId;

    private String storeId;

    private String userId;

    private Boolean autoConfirmEmail;

    private Boolean autoConfirmApp;

    private Boolean autoConfirmSms;

    private String transport;

    private String outOrderId;

    private String ecId;

    private String ciId;

    private String orderSellerIntro;

    private String returnShipcode;

    private String returnEcId;

    private String returnContent;

    private String returnShiptime;

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

    /**
     * 签到截止时间
     */
    private String signEndtime;

    /**
     * 微信支付吊起时间
     */
    private String weixinpayTime;

    private String invoice1;

    private Integer deductionIntegral;

    /**
     * 拼团分组id
     */
    private String groupId;

    private String maintainCode;

    private Boolean homeMaintain;

    private BigDecimal maintainMoney;

    private BigDecimal homeMaintainMoney;

    private BigDecimal bargainAlreadyPrice;

    private BigDecimal bargainCanPrice;

    private String bargainEndTime;

    private Integer bargainNumber;

    private BigDecimal orderTolPrice;

    private String applyforBargainId;

    /**
     * 红包状态，10可分享，20已拆完，30已过期
     */
    private Integer redStatus;

    private String confirmTime;

    private Integer shareCount;

    private String shareEndTime;

    private BigDecimal shareTotalPrice;

    private String ordershareredgoodsId;

    private Integer shareDiedTime;

    private String couponId;

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


}
