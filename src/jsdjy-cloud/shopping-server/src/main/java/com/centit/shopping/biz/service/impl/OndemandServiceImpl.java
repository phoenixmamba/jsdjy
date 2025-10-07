package com.centit.shopping.biz.service.impl;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONArray;
import com.centit.shopping.common.enums.Const;
import com.centit.shopping.dao.*;
import com.centit.shopping.biz.service.OndemandService;
import com.centit.shopping.po.*;
import com.centit.shopping.utils.CRMService;
import com.centit.shopping.utils.CommonUtil;
import com.centit.shopping.utils.MZService;
import com.centit.shopping.utils.PayUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>点播<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务实现类
 * @Date : 2021-04-21
 **/
@Transactional
@Service
public class OndemandServiceImpl implements OndemandService {
    public static final Log log = LogFactory.getLog(OndemandService.class);

    @Resource
    private TOnOndemandDao tOnOndemandDao;
    @Resource
    private ShoppingOrderformDao shoppingOrderformDao;
    @Resource
    private ShoppingOrderLogDao shoppingOrderLogDao;
    @Resource
    private ShoppingOrderPayDao shoppingOrderPayDao;
    @Resource
    private ShoppingGoodscartDao shoppingGoodscartDao;
    @Resource
    private ShoppingPaymentDao shoppingPaymentDao;
    @Resource
    private ShoppingOrderPaykeyDao shoppingOrderPaykeyDao;

    @Resource
    private ShoppingCouponDao shoppingCouponDao;
    @Resource
    private ShoppingCouponUsertempDao shoppingCouponUsertempDao;


    @Value("${moneyToIntegralScale}")
    private int moneyToIntegralScale;

    /**
     * 订单页面渲染
     */
    @Override
    public JSONObject renderOndemandOrder(JSONObject reqJson, HttpServletRequest request) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "服务器内部错误！";
        JSONObject bizDataJson = new JSONObject();
        try {
//            String data = request.getParameter("data");// 参数
//            JSONObject reqJson = JSONObject.parseObject(URLDecoder.decode(data, "utf-8"));

            String userId = reqJson.getString("userId");   //userId
            String ondemandId = reqJson.getString("ondemandId");  //视频/专题id

            TOnOndemand tOnOndemand = new TOnOndemand();
            tOnOndemand.setId(ondemandId);
            //点播详情
            tOnOndemand = tOnOndemandDao.queryDetail(tOnOndemand);

            //实时查询商品现价
            BigDecimal currentPrice = tOnOndemand.getCurrentPrice();
            bizDataJson.put("currentPrice", currentPrice);  //返回实时价格

            JSONArray goodsArray = new JSONArray();
            JSONObject goodsObj = new JSONObject();
            goodsObj.put("ondemandId", ondemandId);
            goodsObj.put("ondemandName", tOnOndemand.getTitle());
            goodsObj.put("currentPrice", currentPrice);
            goodsObj.put("goodsCount", 1);
            goodsObj.put("photoUrl", tOnOndemand.getCoverfilepath());  //封面图片

            goodsArray.add(goodsObj);
            bizDataJson.put("ondemandInfoList", goodsArray);

            //订单商品价格
            bizDataJson.put("goodsPrice", currentPrice);

            BigDecimal couponCut = BigDecimal.ZERO;  //优惠券折扣
            BigDecimal accountCut = BigDecimal.ZERO; //会员体系折扣
            BigDecimal integralCut = BigDecimal.ZERO;  //积分抵扣
            BigDecimal balanceCut = BigDecimal.ZERO;  //余额抵扣

            //积分、余额等使用条件查询
            int useIntegralSet = tOnOndemand.getUseIntegralSet();   //使用积分抵扣设置 0：不允许；1：积分限额抵扣；2：积分不限额抵扣
//            int useIntegralValue = tOnOndemand.getUseIntegralValue();    //单个商品积分抵扣值
            int useBalanceSet = tOnOndemand.getUseBalanceSet();   //使用余额支付设置 0:不允许使用;1:可使用
            int useMembershipSet = tOnOndemand.getUseMembershipSet();   //是否支持会员权益 0:不支持;1:支持
            bizDataJson.put("useMembershipSet", useMembershipSet);
            bizDataJson.put("useIntegralSet", useIntegralSet);
            bizDataJson.put("useBalanceSet", useBalanceSet);

            //开始计算订单优惠和支付金额
            BigDecimal totalPrice = currentPrice;

            //计算优惠时只在商品费用的基础上进行计算
            BigDecimal payPrice =BigDecimal.ZERO;
            payPrice = currentPrice;

            if (currentPrice.compareTo(BigDecimal.ZERO) == 1) {
                //查询商品可用的可用优惠券
                List<String> couponIds = new ArrayList<>();
                JSONArray couponArray = CommonUtil.getVideoCouppon( userId, payPrice, payPrice);
                for (int i = 0; i < couponArray.size(); i++) {
                    JSONObject obj = couponArray.getJSONObject(i);
                    couponIds.add(obj.getString("id"));
                }
                bizDataJson.put("couponList", couponArray);

                //查询账户积分和余额信息
                boolean accountState = true;
                if (useIntegralSet != 0 || useBalanceSet != 0) {
                    //从卖座实时查询账户积分和余额
                    JSONObject accountObj = MZService.getAssetinfo(CommonUtil.getMzUserId(userId));
                    if (null != accountObj) {
                        int account_point = accountObj.get("account_point") == null ? 0 : accountObj.getInteger("account_point");   //会员账户积分剩余点数，单位：点数；
                        int account_money_fen = accountObj.get("account_money_fen") == null ? 0 : accountObj.getInteger("account_money_fen");
                        BigDecimal accountMoney = new BigDecimal(account_money_fen).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);  //分转换为元
                        bizDataJson.put("accountPoint", account_point);
                        bizDataJson.put("accountMoney", accountMoney);
                    } else {
                        accountState = false;
                        bizDataJson.put("accountPoint", 0);
                        bizDataJson.put("accountMoney", 0);
                    }

                    //查询积分和余额免密限额
                    ShoppingAssetRule shoppingAssetRule = CommonUtil.getAssetRule();
                    if (null != shoppingAssetRule) {
                        bizDataJson.put("accountPointLimit", shoppingAssetRule.getPointAvoidLimit());
                        bizDataJson.put("accountMoneyLimit", shoppingAssetRule.getAccountAvoidLimit());
                    }
                }

                if (!accountState) {
                    retCode = "-1";
                    retMsg = "无法获取您的账户积分和余额数据，请稍后再试！";
                }else{
                    BigDecimal originPrice = payPrice;

                    //用户选择的优惠券
                    //需要再判断用户选择的优惠券当前是否仍可用
                    if (null != reqJson.get("couponId") && !"".equals(reqJson.get("couponId"))&& couponIds.contains(reqJson.getString("couponId"))) {
                        String couponId = reqJson.getString("couponId");
                        //校验用户选择的优惠券是否在可用优惠券列表中
                        if (couponIds.contains(couponId)) {
                            //获取优惠券信息
                            JSONObject couponDtl = CRMService.getUserCouponDtl(couponId);
                            if (null != couponDtl) {
                                String right_No = couponDtl.getString("right_No");
                                ShoppingCoupon shoppingCoupon = new ShoppingCoupon();
                                shoppingCoupon.setRight_No(right_No);
                                shoppingCoupon = shoppingCouponDao.queryDetail(shoppingCoupon);
                                //如果是减优惠券或者代金券，直接抵扣相应金额
                                if (shoppingCoupon.getRight_Type().equals("coincp")) {
                                    int couponAmount = Integer.valueOf(shoppingCoupon.getRight_Content());
                                    if(new BigDecimal(couponAmount).compareTo(payPrice)==1){
                                        payPrice = BigDecimal.ZERO;
                                    }else{
                                        payPrice = payPrice.subtract(new BigDecimal(couponAmount));
                                    }
                                } else {
                                    //如果是折扣优惠券，需要计算折扣
                                    BigDecimal discount = new BigDecimal(shoppingCoupon.getRight_Content());
                                    payPrice = payPrice.multiply(discount).setScale(2, BigDecimal.ROUND_HALF_UP);
                                }
                                //计算优惠券抵扣的金额
                                couponCut = originPrice.subtract(payPrice);
                                originPrice = payPrice;
                            }

                        }

                    }

                    //会员等级折扣
                    if(useMembershipSet==1){
                        BigDecimal discount = CommonUtil.getUserMemberShip(userId);
                        payPrice = payPrice.multiply(discount).setScale(2, BigDecimal.ROUND_HALF_UP);
                        //会员等级抵扣的金额
                        accountCut = originPrice.subtract(payPrice);
                        originPrice = payPrice;
                    }

                   //获取积分和余额单次支付的上限配置
                    ShoppingPayLimit payLimit = CommonUtil.getPayLimit();
                    bizDataJson.put("pointPayLimit", payLimit.getPointPay());    //积分单次支付上限
                    bizDataJson.put("balancePayLimit", payLimit.getBalancePay());      //余额单次支付上限
                    int maxIntegralValue = 0;
                    int accountPoint = bizDataJson.get("accountPoint")==null?0:bizDataJson.getInteger("accountPoint");
                    int pointPay = payLimit.getPointPay();  //后台设置的积分单次支付限额
                    //用户选择使用积分支付
                    if (null != reqJson.get("useIntegral") && reqJson.getInteger("useIntegral") != 0) {
                        maxIntegralValue = payPrice.multiply(new BigDecimal(moneyToIntegralScale)).intValue();
                        int pointLimit = accountPoint;   //账户积分
                        if(pointPay>0){
                            pointLimit = pointPay>pointLimit?pointLimit:pointPay;
                        }
                        if (maxIntegralValue > pointLimit) {
                            maxIntegralValue = pointLimit;
                        }

                        bizDataJson.put("useIntegralValue", maxIntegralValue);
                        //将积分根据比例转换为相应的金额
//                        BigDecimal integralAmount = new BigDecimal(maxIntegralValue / moneyToIntegralScale).setScale(2, BigDecimal.ROUND_HALF_UP);
                        BigDecimal integralAmount = new BigDecimal(maxIntegralValue).divide(new BigDecimal(moneyToIntegralScale)).setScale(2, BigDecimal.ROUND_HALF_UP);
                        payPrice = payPrice.subtract(integralAmount);
                        integralCut = originPrice.subtract(payPrice);
                        originPrice = payPrice;
                    }

                    //用户选择扣除的余额
                    if (null != reqJson.get("useBalance") && reqJson.getInteger("useBalance") == 1) {
                        BigDecimal deductionBalance = BigDecimal.ZERO;
                        BigDecimal accountLimit = bizDataJson.getBigDecimal("accountMoney");
                        BigDecimal balancePay = new BigDecimal(payLimit.getBalancePay());   //余额单次支付上限
                        if(balancePay.compareTo(BigDecimal.ZERO)>0&&balancePay.compareTo(accountLimit)<0){    //余额支付上限为账户余额和支付限额两者中的较小值
                            accountLimit = balancePay;
                        }
                        if (payPrice.compareTo(accountLimit) == 1) {
                            deductionBalance = accountLimit;
                        } else {
                            deductionBalance = payPrice;
                        }
                        payPrice = payPrice.subtract(deductionBalance);
                        balanceCut = originPrice.subtract(payPrice);
                    }
                }
            }

            bizDataJson.put("totalPrice", totalPrice);       //订单金额（商品费用）
            bizDataJson.put("payPrice", payPrice);              //还需支付的现金金额
            bizDataJson.put("deductionCouponPrice", couponCut);      //优惠券抵扣金额
            bizDataJson.put("deductionMemberPrice", accountCut);     //会员权益抵扣金额
            bizDataJson.put("deductionIntegralPrice", integralCut);  //积分抵扣金额
            bizDataJson.put("deductionBalancePrice", balanceCut);    //账户余额抵扣金额

            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 创建订单（直接下单）
     */
    @Override
    public JSONObject addOndemandOrder(JSONObject reqJson, HttpServletRequest request) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "服务器内部错误！";
        JSONObject bizDataJson = new JSONObject();
        try {
//            String data = request.getParameter("data");// 参数
//            JSONObject reqJson = JSONObject.parseObject(URLDecoder.decode(data, "utf-8"));

            String userId = reqJson.getString("userId");//userId
            BigDecimal orderTotalPrice = reqJson.getBigDecimal("orderTotalPrice");//订单总金额（商品费用+运费）
            BigDecimal orderPayPrice = reqJson.getBigDecimal("orderPayPrice");//待支付的现金金额
            int orderUseIntegralValue = reqJson.get("orderUseIntegralValue")==null?0:reqJson.getInteger("orderUseIntegralValue");//积分抵扣值
            BigDecimal orderDeductionBalancePrice = reqJson.get("orderDeductionBalancePrice")==null?BigDecimal.ZERO:reqJson.getBigDecimal("orderDeductionBalancePrice");//账户余额抵扣金额
            BigDecimal unitPrice = reqJson.getBigDecimal("unitPrice");//移动端传递的商品单价
            String ondemandId = reqJson.getString("ondemandId");  //视频/专题id

            TOnOndemand tOnOndemand = new TOnOndemand();
            tOnOndemand.setId(ondemandId);
            //点播详情
            tOnOndemand = tOnOndemandDao.queryDetail(tOnOndemand);

            //实时查询商品现价
            BigDecimal currentPrice = tOnOndemand.getCurrentPrice();

            if (currentPrice.compareTo(unitPrice)!=0) {
                retCode = "-1";
                retMsg = "价格发生变化，请重新确认订单信息！";
            } else {
                //从卖座实时查询账户积分和余额
                int account_point = 0;   //会员账户积分剩余点数，单位：点数；
                BigDecimal accountMoney = BigDecimal.ZERO;
                int point_avoid_limit = 0;   //积分支付免密限额；
                int account_money_fen = 0;  //账户余额；单位：分
                int account_avoid_limit = 0;

                //查询账户积分和余额信息
                boolean accountState = true;
                if (orderUseIntegralValue > 0 || orderDeductionBalancePrice.compareTo(BigDecimal.ZERO) == 1||(null != reqJson.get("useIntegral") && reqJson.getInteger("useIntegral") == 1)||(null != reqJson.get("useBalance") && reqJson.getInteger("useBalance") == 1)) {
                    //从卖座实时查询账户积分和余额
                    JSONObject accountObj = MZService.getAssetinfo(CommonUtil.getMzUserId(userId));
                    if (null != accountObj) {
                        account_point = accountObj.get("account_point") == null ? 0 : accountObj.getInteger("account_point");   //会员账户积分剩余点数，单位：点数；
                        account_money_fen = accountObj.get("account_money_fen") == null ? 0 : accountObj.getInteger("account_money_fen");
                        ;  //账户余额；单位：分
                        accountMoney = new BigDecimal(account_money_fen).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);  //分转换为元
                        bizDataJson.put("accountPoint", account_point);
                        bizDataJson.put("accountMoney", accountMoney);
                    } else {
                        bizDataJson.put("accountPoint", 0);
                        bizDataJson.put("accountMoney", 0);
//                        accountState = false;
                    }

                    //查询积分和余额免密限额
                    ShoppingAssetRule shoppingAssetRule = CommonUtil.getAssetRule();
                    if (null != shoppingAssetRule) {
                        bizDataJson.put("accountPointLimit", shoppingAssetRule.getPointAvoidLimit());
                        bizDataJson.put("accountMoneyLimit", shoppingAssetRule.getAccountAvoidLimit());
                    }
                }

                //开始计算订单优惠和支付金额
                if (!accountState) {
                    retCode = "-1";
                    retMsg = "无法获取您的账户积分和余额数据，请稍后下单！";
                } else if (accountMoney.compareTo(orderDeductionBalancePrice) < 0 || account_point < orderUseIntegralValue) {
                    retCode = "-1";
                    retMsg = "账户积分或余额不足，请重新确认订单信息！";
                }  else {
                    int useIntegralSet = tOnOndemand.getUseIntegralSet();   //使用积分抵扣设置 0:不允许使用积分抵扣;1:可使用部分积分抵扣
//                    int useIntegralValue = tOnOndemand.getUseIntegralValue();    //单个商品积分抵扣值
                    int useBalanceSet = tOnOndemand.getUseBalanceSet();   //使用余额支付设置 0:不允许使用;1:可使用
                    int useMembershipSet = tOnOndemand.getUseMembershipSet();   //是否支持会员权益 0:不支持;1:支持

                    //订单总金额为商品费用
                    BigDecimal totalPrice = currentPrice;

                    //计算优惠时只在商品费用的基础上进行计算
                    BigDecimal payPrice =BigDecimal.ZERO;
                    payPrice = currentPrice;
                    BigDecimal originPrice = payPrice;

                    BigDecimal couponCut = BigDecimal.ZERO;  //优惠券折扣
                    BigDecimal accountCut= BigDecimal.ZERO;  //会员体系折扣
                    int integralValue = 0;  //积分抵扣数量
                    BigDecimal integralCut = BigDecimal.ZERO;  //积分抵扣
                    BigDecimal balanceCut = BigDecimal.ZERO;  //余额抵扣

                    //查询商品可用的可用优惠券
                    List<String> couponIds = new ArrayList<>();
                    JSONArray couponArray = CommonUtil.getVideoCouppon( userId, payPrice, payPrice);
                    for (int i = 0; i < couponArray.size(); i++) {
                        JSONObject obj = couponArray.getJSONObject(i);
                        couponIds.add(obj.getString("id"));
                    }

                    //用户选择的优惠券
                    if (null != reqJson.get("couponId") && !"".equals(reqJson.get("couponId")) && couponIds.contains(reqJson.get("couponId"))) {
                        String couponId = reqJson.getString("couponId");
                        //获取优惠券信息
                        //先获取用户选择的优惠券详情
                        JSONObject couponDtl = CRMService.getUserCouponDtl(couponId);
                        if (null != couponDtl) {
                            String right_No = couponDtl.getString("right_No");
                            ShoppingCoupon shoppingCoupon = new ShoppingCoupon();
                            shoppingCoupon.setRight_No(right_No);
                            shoppingCoupon = shoppingCouponDao.queryDetail(shoppingCoupon);
                            //如果是减优惠券或者代金券，直接抵扣相应金额
                            if (shoppingCoupon.getRight_Type().equals("coincp")) {
                                int couponAmount = Integer.valueOf(shoppingCoupon.getRight_Content());
                                if(new BigDecimal(couponAmount).compareTo(payPrice)==1){
                                    payPrice = BigDecimal.ZERO;
                                }else{
                                    payPrice = payPrice.subtract(new BigDecimal(couponAmount));
                                }
                            } else {
                                //如果是折扣优惠券，需要计算折扣
                                BigDecimal discount = new BigDecimal(shoppingCoupon.getRight_Content());
                                payPrice = payPrice.multiply(discount).setScale(2, BigDecimal.ROUND_HALF_UP);
                            }
                            //计算优惠券抵扣的金额
                            couponCut = originPrice.subtract(payPrice);
                            originPrice = payPrice;
                        }

                    }

                    //会员等级折扣
                    if(useMembershipSet==1){
                        BigDecimal discount = CommonUtil.getUserMemberShip(userId);
                        payPrice = payPrice.multiply(discount).setScale(2, BigDecimal.ROUND_HALF_UP);

                        //会员等级抵扣的金额
                        accountCut = originPrice.subtract(payPrice);
                        originPrice = payPrice;
                    }

                    //获取积分和余额单次支付的上限配置
                    ShoppingPayLimit payLimit = CommonUtil.getPayLimit();

                    int maxIntegralValue = 0;
                    int accountPoint = bizDataJson.get("accountPoint")==null?0:bizDataJson.getInteger("accountPoint");
                    int pointPay = payLimit.getPointPay();  //后台设置的积分单次支付限额
                    //用户选择使用积分抵扣，如果账户积分足够，则必须按商品抵扣积分最大值进行扣除，不支持移动端手动输入积分抵扣的数值
                    if (null != reqJson.get("useIntegral") && reqJson.getInteger("useIntegral") != 0) {
                        maxIntegralValue = payPrice.multiply(new BigDecimal(moneyToIntegralScale)).intValue();
                        int pointLimit = accountPoint;   //账户积分
                        if(pointPay>0){
                            pointLimit = pointPay>pointLimit?pointLimit:pointPay;
                        }
                        if (maxIntegralValue > pointLimit) {
                            maxIntegralValue = pointLimit;
                        }
                        integralValue = maxIntegralValue;
                        //将积分根据比例转换为相应的金额
//                        BigDecimal integralAmount = new BigDecimal(maxIntegralValue / moneyToIntegralScale).setScale(2, BigDecimal.ROUND_HALF_UP);
                        BigDecimal integralAmount = new BigDecimal(maxIntegralValue).divide(new BigDecimal(moneyToIntegralScale)).setScale(2, BigDecimal.ROUND_HALF_UP);
                        payPrice = payPrice.subtract(integralAmount);
                        integralCut = originPrice.subtract(payPrice);
                        originPrice = payPrice;
                    }

                    //用户选择使用余额支付，则表示所有剩下的待支付金额都使用余额支付，不支持移动端手动输入要支付的余额值
                    BigDecimal deductionBalance = BigDecimal.ZERO;
                    if (null != reqJson.get("useBalance") && reqJson.getInteger("useBalance") == 1) {
                        //如果账户余额不够支付所有的待支付金额，则默认扣除所有的账户余额
                        BigDecimal accountLimit = bizDataJson.getBigDecimal("accountMoney");
                        BigDecimal balancePay = new BigDecimal(payLimit.getBalancePay());   //余额单次支付上限
                        if(balancePay.compareTo(BigDecimal.ZERO)>0&&balancePay.compareTo(accountLimit)<0){    //余额支付上限为账户余额和支付限额两者中的较小值
                            accountLimit = balancePay;
                        }
                        //如果账户余额不够支付所有的待支付金额，则默认扣除所有的账户余额
                        if (payPrice.compareTo(accountLimit) == 1) {
                            deductionBalance = accountLimit;
                        } else {
                            deductionBalance = payPrice;
                        }
                        payPrice = payPrice.subtract(deductionBalance);
                        balanceCut = deductionBalance;
                    }
                    //订单总金额/待支付金额/扣除积分/余额抵扣值与移动端传值不一致，需要重新确认订单
                    if (totalPrice.compareTo(orderTotalPrice) != 0 || payPrice.compareTo(orderPayPrice) != 0 || maxIntegralValue != orderUseIntegralValue || deductionBalance.compareTo(orderDeductionBalancePrice) != 0) {
                        retCode = "-1";
                        retMsg = "订单金额发生变化，请重新确认订单信息！";
                    } else {
                        //创建订单信息
                        ShoppingOrderform orderform = new ShoppingOrderform();
                        //订单id（系统订单全局唯一标识）
                        String orderId = PayUtil.getOrderNo(Const.VIDEO_ORDER);
                        orderform.setOrderId(orderId);
                        //订单类型：点播
                        orderform.setOrderType(Const.VIDEO_ORDER_TYPE);
                        //订单状态：待支付
                        orderform.setOrderStatus(10);
                        //订单金额
                        orderform.setTotalPrice(totalPrice);
                        //需支付的现金金额
                        orderform.setPayPrice(payPrice);
//                        if(payPrice.compareTo(BigDecimal.ZERO)==1){
//                            orderform.setPayPrice(new BigDecimal(0.01));
//                        }
                        //商店id
                        orderform.setStoreId(Const.STORE_ID);
                        //用户id
                        orderform.setUserId(userId);

                        //订单支付信息
                        ShoppingOrderPay shoppingOrderPay = new ShoppingOrderPay();
                        shoppingOrderPay.setUserId(userId);
                        if (payPrice.compareTo(BigDecimal.ZERO) == 1) {
                            shoppingOrderPay.setCashStatus(0);
                        }
                        //优惠券信息
                        if (null != reqJson.get("couponId")) {
                            String couponId = reqJson.getString("couponId");//用户选择的优惠券id
                            //订单使用的优惠券id
                            orderform.setCiId(couponId);
                            //优惠券抵扣金额
                            orderform.setDeductionCouponPrice(couponCut);
                            shoppingOrderPay.setCouponStatus(0);

                            //将用户选择的优惠券保存到优惠券临时锁定表中，该优惠券不可在地方再被使用
                            ShoppingCouponUsertemp shoppingCouponUsertemp = new ShoppingCouponUsertemp();
                            shoppingCouponUsertemp.setUserId(userId);
                            shoppingCouponUsertemp.setCouponId(couponId);
                            shoppingCouponUsertempDao.insert(shoppingCouponUsertemp);
                        }

                        ShoppingOrderPaykey shoppingOrderPaykey = new ShoppingOrderPaykey();
                        //用户选择抵扣的积分数额
                        if (null != reqJson.get("useIntegral") && reqJson.getInteger("useIntegral") == 1) {
                            orderform.setDeductionIntegralPrice(integralCut);
                            orderform.setDeductionIntegral(integralValue);
                            shoppingOrderPay.setIntegralStatus(0);

                            //积分支付限额验证码
                            if (null != reqJson.get("accountPointPayKey") && !"".equals(reqJson.get("accountPointPayKey"))) {
                                shoppingOrderPaykey.setAccountPointPayKey(reqJson.getString("accountPointPayKey"));
                            }
                        }
                        //会员权益抵扣金额
                        orderform.setDeductionMemberPrice(accountCut);

                        //余额抵扣
                        if (null != reqJson.get("useBalance") && reqJson.getInteger("useBalance") == 1) {
                            //账户余额支付金额
//                            BigDecimal deductionBalancePrice = reqJson.get("deductionBalancePrice")==null?BigDecimal.ZERO:reqJson.getBigDecimal("deductionBalancePrice");
//                            orderform.setDeductionBalancePrice(deductionBalancePrice);
                            orderform.setDeductionBalancePrice(balanceCut);
                            shoppingOrderPay.setBalanceStatus(0);

                            //余额支付限额验证码
                            if (null != reqJson.get("accountMoneyPayKey") && !"".equals(reqJson.get("accountMoneyPayKey"))) {
                                if (null != reqJson.get("accountMoneyPayKey")) {
                                    shoppingOrderPaykey.setAccountMoneyPayKey(reqJson.getString("accountMoneyPayKey"));
                                }
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
//                        shoppingOrderLog.setStateInfo(orderform.getOrderType());
                        shoppingOrderLog.setLogUserId(userId);
                        shoppingOrderLog.setOfId(orderform.getId());
                        shoppingOrderLogDao.insert(shoppingOrderLog);


                        //保存订单-商品关联信息
                        ShoppingGoodscart goodscart = new ShoppingGoodscart();
                        String scId = CommonUtil.getUserScId(userId);
                        goodscart.setScId(scId);
                        goodscart.setGoodsId(ondemandId);
                        goodscart.setCount(1);
                        goodscart.setCartType(Const.VIDEO_CART_TYPE);

                        goodscart.setDeductionCouponPrice(orderform.getDeductionCouponPrice());
                        goodscart.setDeductionMemberPrice(orderform.getDeductionMemberPrice());
                        goodscart.setDeductionIntegral(orderform.getDeductionIntegral());
                        goodscart.setDeductionIntegralPrice(orderform.getDeductionIntegralPrice());
                        goodscart.setDeductionBalancePrice(orderform.getDeductionBalancePrice());
                        goodscart.setPayPrice(orderform.getPayPrice());

                        //下单时的商品价格
                        goodscart.setPrice(currentPrice);
                        goodscart.setOfId(orderform.getId());
                        shoppingGoodscartDao.insert(goodscart);

                        //获取当前系统可用支付方式
                        HashMap<String, Object> reqMap = new HashMap<>();
                        reqMap.put("deleteStatus", 0);
                        List<ShoppingPayment> payments = shoppingPaymentDao.queryList(reqMap);
                        bizDataJson.put("payments", payments);

                        bizDataJson.put("orderId", orderId);
                        bizDataJson.put("price", payPrice);
                        retCode = "0";

                        retMsg = "操作成功！";
                    }
                }
            }
        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }
}
