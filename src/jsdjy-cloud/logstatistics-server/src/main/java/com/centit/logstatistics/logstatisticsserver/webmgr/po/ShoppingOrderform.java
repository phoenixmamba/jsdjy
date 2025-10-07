package com.centit.logstatistics.logstatisticsserver.webmgr.po;

import java.math.BigDecimal;
import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p> <p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description :  实体类
 * @Date : 2021-06-08
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

    private BigDecimal goodsDgold;

    private BigDecimal totalIntegral;

    private String wxPrepayId;

    private BigDecimal payPrice;

    private String signEndtime;

    private String weixinpayTime;

    private String invoice1;

    private BigDecimal deductionIntegral;

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

    private Integer redStatus;

    private String confirmTime;

    private Integer shareCount;

    private String shareEndTime;

    private BigDecimal shareTotalPrice;

    private String ordershareredgoodsId;

    private Integer shareDiedTime;

    private String couponId;

    private BigDecimal deductionIntegralPrice;

    private BigDecimal deductionMemberPrice;

    private BigDecimal deductionBalancePrice;

    private BigDecimal deductionCouponPrice;

    private BigDecimal deductionGiftcardPrice;

    private String userName;

    private String mobile;


}
