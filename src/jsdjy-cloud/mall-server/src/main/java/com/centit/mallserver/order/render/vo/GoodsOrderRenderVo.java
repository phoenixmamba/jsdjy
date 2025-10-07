package com.centit.mallserver.order.render.vo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 商品订单渲染信息
 * @Date : 2024/12/20 16:26
 **/
@Data
public class GoodsOrderRenderVo {
    /**
     * 提前生产的订单id
     */
    private String orderId;
    /**
     * 商品列表
     */
    private JSONArray goodsInfoList;
    /**
     * 实时价格
     */
    private BigDecimal currentPrice;

    /**
     * 配送信息
     */
    private Integer goodsTransfee;
    private String selfextractionSet;
    private String selfextractionAddress;

    /**
     * 收货地址
     */
    private JSONObject addObj;

    /**
     * 庄户积分和余额
     */
    private int accountPoint;
    private BigDecimal accountMoney;

    /**
     * 积分免密额度
     */
    private int accountPointLimit;
    /**
     * 余额免密限额，单位元
     */
    private BigDecimal accountMoneyLimit;

    /**
     * 积分单次支付上限
     */
    private int pointPayLimit;
    /**
     * 余额单次支付上限
     */
    private int balancePayLimit;

    /**
     * 订单可用优惠券
     */
    private JSONArray couponList;

    /**
     * 商品各优惠开关
     */
    private Integer useMembershipSet;
    private Integer useIntegralSet;
    private Integer useBalanceSet;

    /**
     * 积分抵扣数量
     */
    private int useIntegralValue;
    /**
     * 商品金额
     */
    private BigDecimal goodsPrice;
    /**
     * 订单运费
     */
    private BigDecimal shipPrice;
    /**
     * 订单总金额
     */
    private BigDecimal totalPrice;
    /**
     * 订单剩余需要支付的金额
     */
    private BigDecimal payPrice;
    /**
     * 优惠券抵扣金额
     */
    private BigDecimal deductionCouponPrice=BigDecimal.ZERO;
    /**
     * 会员体系抵扣金额
     */
    private BigDecimal deductionMemberPrice=BigDecimal.ZERO;
    /**
     * 积分抵扣金额
     */
    private BigDecimal deductionIntegralPrice=BigDecimal.ZERO;
    /**
     * 余额抵扣金额
     */
    private BigDecimal deductionBalancePrice=BigDecimal.ZERO;

    private String sign;
}
