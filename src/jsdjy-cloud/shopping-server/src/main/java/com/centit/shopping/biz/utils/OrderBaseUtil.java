package com.centit.shopping.biz.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.biz.bo.SimpleOrdernfo;
import com.centit.shopping.common.enums.Const;
import com.centit.shopping.dao.*;
import com.centit.shopping.po.*;
import com.centit.shopping.utils.CRMService;
import com.centit.shopping.utils.CommonUtil;
import com.centit.shopping.utils.MZService;
import com.centit.shopping.utils.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

@Component
public class OrderBaseUtil {
    @Resource
    private ShoppingCouponDao shoppingCouponDao;
    @Resource
    private ShoppingGoodsspecpropertyDao shoppingGoodsspecpropertyDao;
    @Resource
    private ShoppingGoodscartDao shoppingGoodscartDao;
    @Resource
    private ShoppingCouponUsertempDao shoppingCouponUsertempDao;
    @Resource
    private ShoppingOrderformDao shoppingOrderformDao;
    @Resource
    private ShoppingOrderPaykeyDao shoppingOrderPaykeyDao;
    @Resource
    private ShoppingOrderPayDao shoppingOrderPayDao;
    @Resource
    private ShoppingOrderLogDao shoppingOrderLogDao;
    @Resource
    private ShoppingWriteoffDao shoppingWriteoffDao;

    @Value("${moneyToIntegralScale}")
    private int moneyToIntegralScale;

    @Value("${offcodeLength}")
    private int offcodeLength;


    /**
     * 拼接规格属性信息
     * @param propertys 规格id
     * @return 规格属性字符串
     */
    public String getGoodsSpecInfo(String propertys) {
        String[] strs = propertys.split("_");
        String specInfo = "";
        //拼接规格属性信息
        for (int i = 0; i < strs.length; i++) {
            ShoppingGoodsspecproperty shoppingGoodsspecproperty = new ShoppingGoodsspecproperty();
            shoppingGoodsspecproperty.setId(strs[i]);
            shoppingGoodsspecproperty = shoppingGoodsspecpropertyDao.queryDetail(shoppingGoodsspecproperty);
            specInfo += shoppingGoodsspecproperty.getValue() + ";";
        }
        return specInfo;
    }

    /**
     * 获取用户收货地址
     * @param addressId
     * @param mzUserId
     * @return 收货地址（没有传递addressId时返回默认收货地址）
     */
    public JSONObject getUserAddress(String addressId, String mzUserId) {
        JSONObject addObj = null;
        if (StringUtil.isNotNull(addressId)) {
            addObj = MZService.getAddressDetail(mzUserId, addressId);
        } else {
            //没有选择地址时，取用户默认收货地址
            JSONObject addressList = MZService.getUserAddress(mzUserId, 100, 1);
            if (null != addressList) {
                JSONObject data_list = addressList.getJSONObject("data_list");
                if (null != data_list.get("user_address_detail_v_o")) {
                    JSONArray addressArray = data_list.getJSONArray("user_address_detail_v_o");
                    for (int i = 0; i < addressArray.size(); i++) {
                        JSONObject addressObj = addressArray.getJSONObject(i);
                        addObj = addressObj;
                        if (addressObj.getBoolean("default_address_boolean")) {
                            break;
                        }
                    }
                }

            }
        }
        return addObj;
    }

    /**
     * 获取用户选择的优惠券详情
     *
     * @param couponId 优惠券Id
     * @return 优惠券详情
     */
    public ShoppingCoupon getUserCoupon(String couponId) {
        try {
            //从CRM获取用户选择的优惠券详情
            JSONObject couponDtl = CRMService.getUserCouponDtl(couponId);
            if (null != couponDtl) {
                String right_No = couponDtl.getString("right_No");
                ShoppingCoupon shoppingCoupon = new ShoppingCoupon();
                shoppingCoupon.setRight_No(right_No);
                shoppingCoupon = shoppingCouponDao.queryDetail(shoppingCoupon);
                return shoppingCoupon;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * 当前订单是否超出单人购买上限
     * @param goodsId 商品唯一标识
     * @param cartType 商品类型 1:文创商品2;:积分商品;3:艺术活动;4:艺术培训;5:演出票;6:停车;7:点播;8:充值;9:爱艺计划
     * @param limitBuy 限购值，0表示不限购
     * @param count 下单商品数量
     * @param userId 用户id
     * @return true:超出上限；false:未超出上限，没有设置上限时直接返回false
     */
    public Boolean upLimitBuy(String goodsId,int cartType,int limitBuy,int count,String userId) {
        if (limitBuy == 0) {  //商品不限购
            return false;
        } else {
            //已提交订单内的该商品数量
            int cartCount = 0;
            String scId = CommonUtil.getUserScId(userId);
            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("scId", scId);
            reqMap.put("goodsId", goodsId);
            reqMap.put("cartType", cartType);
            reqMap.put("userId", userId);
            reqMap.put("deleteStatus", "0");
            List<ShoppingGoodscart> shoppingGoodscartList = shoppingGoodscartDao.queryBuyList(reqMap);
            for (ShoppingGoodscart shoppingGoodscart : shoppingGoodscartList) {
                cartCount += shoppingGoodscart.getCount();
            }
            return (cartCount + count > limitBuy)?true:false;
        }
    }

    /**
     * 判断当前提交的购物车记录id是否可用，避免同一购物车信息的多次提交
     * @param cartId 购物车记录id
     * @return true:可用；false:不可用
     */
    public Boolean isCartEnable(String cartId){
        if (StringUtil.isNotNull(cartId)) {
            ShoppingGoodscart goodscart = new ShoppingGoodscart();
            goodscart.setId(cartId);
            goodscart = shoppingGoodscartDao.queryDetail(goodscart);
            if(StringUtil.isNotNull(goodscart.getOfId())){   //有订单id则表示该购物车信息已经被提交
                return false;
            }
        }
        return true;
    }

    /**
     * 提交订单
     * @param orderId 订单Id
     * @param orderType 订单类型
     * @param cartType 商品类型 1:文创商品2;:积分商品;3:艺术活动;4:艺术培训;5:演出票;6:停车;7:点播;8:充值;9:爱艺计划
     * @param goodsId 商品id
     * @param shoppingOrderInfo  订单金额信息
     * @return  订单
     */
    @Transactional
    public ShoppingOrderform addOrder(String orderId, int orderType, int cartType, String goodsId, SimpleOrdernfo shoppingOrderInfo){
        //创建订单信息
        ShoppingOrderform orderform = new ShoppingOrderform();
//        //订单id（系统订单全局唯一标识）
//        String orderId = PayUtil.getOrderNo(Const.SHOPPING_CUL_ORDER);
        orderform.setOrderId(orderId);
        //订单类型
        orderform.setOrderType(orderType);
//        orderform.setOrderType(Const.SHOPPING_CUL_ORDER_TYPE);
        //订单状态：待支付
        orderform.setOrderStatus(10);
        //订单金额
        orderform.setTotalPrice(shoppingOrderInfo.getTotalAmount());
        //运费金额
        orderform.setShipPrice(shoppingOrderInfo.getShipAmount());
        //需支付的现金金额
        orderform.setPayPrice(shoppingOrderInfo.getPayAmount());

        //快递/自提
        orderform.setTransport(shoppingOrderInfo.getTransport());
        //收货地址
        orderform.setAddrId(shoppingOrderInfo.getAddressId());
        //商店id
        orderform.setStoreId(Const.STORE_ID);
        //用户id
        orderform.setUserId(shoppingOrderInfo.getUserId());

        //订单支付信息
        ShoppingOrderPay shoppingOrderPay = new ShoppingOrderPay();
        shoppingOrderPay.setUserId(shoppingOrderInfo.getUserId());
        if (shoppingOrderInfo.getPayAmount().compareTo(BigDecimal.ZERO) == 1) {
            shoppingOrderPay.setCashStatus(0);
        }
        //优惠券信息
        if (StringUtil.isNotNull(shoppingOrderInfo.getCouponId())) {
            //订单使用的优惠券id
            orderform.setCiId(shoppingOrderInfo.getCouponId());
            //优惠券抵扣金额
            orderform.setDeductionCouponPrice(shoppingOrderInfo.getCouponCut());
            shoppingOrderPay.setCouponStatus(0);

            //将用户选择的优惠券保存到优惠券临时锁定表中，该优惠券不可在地方再被使用
            ShoppingCouponUsertemp shoppingCouponUsertemp = new ShoppingCouponUsertemp();
            shoppingCouponUsertemp.setUserId(shoppingOrderInfo.getUserId());
            shoppingCouponUsertemp.setCouponId(shoppingOrderInfo.getCouponId());
            shoppingCouponUsertempDao.insert(shoppingCouponUsertemp);
        }

        //会员权益抵扣金额
        orderform.setDeductionMemberPrice(shoppingOrderInfo.getAccountCut());

        ShoppingOrderPaykey shoppingOrderPaykey = new ShoppingOrderPaykey();
        //积分抵扣数额>0
        if (shoppingOrderInfo.getIntegralValue()>0) {
            orderform.setDeductionIntegralPrice(shoppingOrderInfo.getIntegralCut());
            orderform.setDeductionIntegral(shoppingOrderInfo.getIntegralValue());
            shoppingOrderPay.setIntegralStatus(0);

            //积分支付限额验证码
            if (StringUtil.isNotNull(shoppingOrderInfo.getAccountPointPayKey())) {
                shoppingOrderPaykey.setAccountPointPayKey(shoppingOrderInfo.getAccountPointPayKey());
            }
        }

        if (shoppingOrderInfo.getBalanceCut().compareTo(BigDecimal.ZERO)>0) {
            //账户余额支付金额
            orderform.setDeductionBalancePrice(shoppingOrderInfo.getBalanceCut());
            shoppingOrderPay.setBalanceStatus(0);

            //余额支付限额验证码
            if (StringUtil.isNotNull(shoppingOrderInfo.getAccountMoneyPayKey())) {
                shoppingOrderPaykey.setAccountMoneyPayKey(shoppingOrderInfo.getAccountMoneyPayKey());
            }
        }
        //保存订单信息
        shoppingOrderformDao.insert(orderform);

        //保存资产业务key
        if (null != shoppingOrderPaykey.getAccountPointPayKey() || null != shoppingOrderPaykey.getAccountMoneyPayKey()) {
            shoppingOrderPaykey.setOfId(orderform.getId());
            shoppingOrderPaykeyDao.insert(shoppingOrderPaykey);
        }

        //保存订单支付信息
        shoppingOrderPay.setOfId(orderform.getId());
        shoppingOrderPayDao.insert(shoppingOrderPay);

        // 添加订单日志
        ShoppingOrderLog shoppingOrderLog = new ShoppingOrderLog();
        shoppingOrderLog.setLogInfo("提交订单");
        shoppingOrderLog.setLogUserId(shoppingOrderInfo.getUserId());
        shoppingOrderLog.setOfId(orderform.getId());
        shoppingOrderLogDao.insert(shoppingOrderLog);

        //保存订单-商品关联信息
        boolean creatNewCart =true;  //是否要创建新的cart信息
        ShoppingGoodscart goodscart = new ShoppingGoodscart();
        if(StringUtil.isNotNull(shoppingOrderInfo.getCartId())){
            goodscart.setId(shoppingOrderInfo.getCartId());
            goodscart = shoppingGoodscartDao.queryDetail(goodscart);
            if(!StringUtil.isNotNull(goodscart.getOfId())){   //没有订单id表示该购物车记录尚未被提交，不需要重新创建
                creatNewCart =false;
            }else{
                goodscart = new ShoppingGoodscart();
            }
        }
        goodscart.setCount(shoppingOrderInfo.getGoodsCount());
        goodscart.setTransport(shoppingOrderInfo.getTransport());  //快递/自提
        goodscart.setDeductionCouponPrice(orderform.getDeductionCouponPrice());
        goodscart.setDeductionMemberPrice(orderform.getDeductionMemberPrice());
        goodscart.setDeductionIntegral(orderform.getDeductionIntegral());
        goodscart.setDeductionIntegralPrice(orderform.getDeductionIntegralPrice());
        goodscart.setDeductionBalancePrice(orderform.getDeductionBalancePrice());
        goodscart.setPayPrice(orderform.getPayPrice());
        goodscart.setShipPrice(orderform.getShipPrice());
        //下单时的商品价格
        goodscart.setPrice(shoppingOrderInfo.getCurrentPrice());
        goodscart.setOfId(orderform.getId());
        if(creatNewCart){
            String scId = CommonUtil.getUserScId(shoppingOrderInfo.getUserId());
            goodscart.setScId(scId);
            goodscart.setGoodsId(goodsId);
            goodscart.setCartType(cartType);
            goodscart.setSpecInfo(shoppingOrderInfo.getSpecInfo());
            goodscart.setPropertys(shoppingOrderInfo.getPropertys());
            shoppingGoodscartDao.insert(goodscart);
        }else{
            shoppingGoodscartDao.update(goodscart);
        }
        //自提默认需要核销
        if(shoppingOrderInfo.getTransport().equals("自提")||orderType==Const.SHOPPING_ACT_ORDER_TYPE||orderType==Const.SHOPPING_PLAN_ORDER_TYPE){
            //如果该商品需要核销，则需要保存核销信息表
            String gcId = goodscart.getId();
            ShoppingWriteoff shoppingWriteoff = new ShoppingWriteoff();
            shoppingWriteoff.setGcId(gcId);
            shoppingWriteoff.setGoodsCount(goodscart.getCount());
            shoppingWriteoff.setOffCode(StringUtil.randomOffCode(offcodeLength));
            shoppingWriteoffDao.insert(shoppingWriteoff);
        }
        return orderform;
    }
}
