package com.centit.shopping.biz.bo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :单个订单金额信息
 * @Date : 2023/8/30 11:02
 **/
@Data
public class SimpleOrdernfo {
    public SimpleOrdernfo(){

    }
    public SimpleOrdernfo(String userId, String mzUserid, int goodsCount, String propertys, boolean useIntegral, boolean useBalance){
        this.userId=userId;
        this.mzUserid=mzUserid;
        this.goodsCount=goodsCount;
        this.propertys=propertys;
        this.useIntegral=useIntegral;
        this.useBalance=useBalance;
    }

    //用户id
    private String userId;
    //用户麦座id
    private String mzUserid;

    //商品数量
    private Integer goodsCount;
    //商品规格id
    private String propertys;
    //商品规格字符串
    private String specInfo;
    //收货方式
    private String transport;
    //收货地址id
    private String addressId;
    //收货地址信息
    JSONObject addObj= null;

    //订单优惠券id
    private String couponId;
    //订单可用优惠券
    private JSONArray couponArray =new JSONArray();

    //积分和余额使用开关
    private boolean useIntegral;
    private boolean useBalance;

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

    //资产验证key
    private String accountPointPayKey;
    private String accountMoneyPayKey;
    //购物车id
    private String cartId;

    //订单金额计算是否成功
    private Boolean countSuccess = false;
    //订单金额计算失败原因
    private String errorMsg;


}
