package com.centit.shopping.biz.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.biz.service.ParkService;
import com.centit.shopping.biz.service.ShoppingArtsService;
import com.centit.shopping.common.enums.Const;
import com.centit.shopping.dao.*;
import com.centit.shopping.po.*;
import com.centit.shopping.utils.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>停车缴费<p>
 *
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务实现类
 * @Date : 2021-02-24
 **/
@Transactional
@Service
public class ParkServiceImpl implements ParkService {
    public static final Log log = LogFactory.getLog(ParkService.class);

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
    private ParkOrderDao parkOrderDao;
    @Resource
    private ParkPlateDao parkPlateDao;

    @Resource
    private ShoppingCouponDao shoppingCouponDao;
    @Resource
    private ShoppingCouponUsertempDao shoppingCouponUsertempDao;


    @Value("${moneyToIntegralScale}")
    private int moneyToIntegralScale;
    @Value("${park.api.getParkingPaymentInfo}")
    private String parkApiGetParkingPaymentInfo;
    @Value("${park.api.payParkingFee}")
    private String parkApiPayParkingFee;
    @Value("${park.api.getOrderStatus}")
    private String parkApiGetOrderStatus;
    @Value("${park.api.getParkingPaymentList}")
    private String parkApiGetParkingPaymentList;
    @Value("${park.appId}")
    private String parkAppID;
    @Value("${park.appSecret}")
    private String parkAppSecret;
    @Value("${park.parkId}")
    private int parkId;

    /**
     * 获取用户默认车牌号
     */
    @Override
    public JSONObject queryDefaultPlateNo(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String userId = reqJson.getString("userId");
            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("userId", userId);
            reqMap.put("defaultPlateBoolean", "1");
            List<ParkPlate> objList = parkPlateDao.queryList(reqMap);
            bizDataJson.put("data", !objList.isEmpty() ? objList.get(0) : null);

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
     * 账单查询/费用查询
     */
    @Override
    public JSONObject getParkingPaymentInfo(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "服务器内部错误！";
        JSONObject bizDataJson = new JSONObject();
        try {
//            String data = request.getParameter("data");// 参数
//            JSONObject reqJson = JSONObject.parseObject(URLDecoder.decode(data, "utf-8"));
            //serviceCode
            String serviceCode = "getParkingPaymentInfo";
            reqJson.put("appId", parkAppID);
            reqJson.put("parkId", parkId);
            reqJson.put("serviceCode", serviceCode);
            String ts = String.valueOf(System.currentTimeMillis());
            //时间戳
            reqJson.put("ts", ts);
            //每次请求的唯一标识
            reqJson.put("reqId", ts);
            String signValue = SignUtil.paramsSign(reqJson, parkAppSecret);
            reqJson.put("key", signValue);
            String reqtime = StringUtil.nowTimeString();
            try {
                JSONObject res = HttpSendUtil.doPost(parkApiGetParkingPaymentInfo, reqJson.toJSONString());
                String rettime = StringUtil.nowTimeString();
                CommonUtil.addThirdLog(0, Const.THIRDLOG_TYPE_PARK, "账单查询/费用查询", "POST", parkApiGetParkingPaymentInfo,
                        reqtime, reqJson.toJSONString(), rettime, res.toString());
                if (res.get("resCode").equals("0")) {
                    JSONObject dataObj = res.getJSONObject("data");
                    //停车订单信息
                    ParkOrder parkOrder = JSON.parseObject(dataObj.toJSONString(), ParkOrder.class);
                    //计算订单号失效时间，第三方默认失效时间为5分钟，移动端缩短失效时间为3分钟，防止临界异常情况
                    parkOrder.setExpireTime(StringUtil.nowTimePlusMinutes(3));
                    parkOrderDao.insert(parkOrder);
                    bizDataJson.put("payable", parkOrder.getPayable());   //待支付金额
                    bizDataJson.put("expireTime", parkOrder.getExpireTime());    //停车账单号效时间
                    bizDataJson.put("orderNo", parkOrder.getOrderNo());    //停车账单号（相当于商品主键）
                    bizDataJson.put("parkName", parkOrder.getParkName());    //停车场名称
                    bizDataJson.put("entryTime", parkOrder.getEntryTime());   //入场时间
                    bizDataJson.put("elapsedTime", parkOrder.getElapsedTime());   //停车时间（分组）
                    bizDataJson.put("delayTime", parkOrder.getDelayTime());    //收费后允许延时出场的时间限制（分钟）
                    retCode = "0";
                    retMsg = "操作成功！";
                } else if (null != res.get("resCode")) {
                    retCode = res.getString("resCode");
                    retMsg = res.getString("resMsg");
                } else {
                    retCode = "1";
                    retMsg = "调用第三方接口获取账单信息失败！";
                }
            } catch (Exception e) {
                String rettime = StringUtil.nowTimeString();
                CommonUtil.addThirdLog(1, Const.THIRDLOG_TYPE_PARK, "账单查询/费用查询", "POST", parkApiGetParkingPaymentInfo,
                        reqtime, reqJson.toJSONString(), rettime, e.getMessage());
                log.error(e);
            }
        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 订单页面渲染
     */
    @Override
    public JSONObject renderParkOrder(JSONObject reqJson, HttpServletRequest request) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "服务器内部错误！";
        JSONObject bizDataJson = new JSONObject();
        try {
//            String data = request.getParameter("data");// 参数
//            JSONObject reqJson = JSONObject.parseObject(URLDecoder.decode(data, "utf-8"));

            String userId = reqJson.getString("userId");   //userId
            String orderNo = reqJson.getString("orderNo");  //账单号（订单号5分钟内有效）

            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("orderNo", orderNo);
            List<ParkOrder> orders = parkOrderDao.queryList(reqMap);
//            ParkOrder parkOrder = new ParkOrder();
//            parkOrder.setOrderNo(orderNo);
//            parkOrder = parkOrderDao.queryDetail(parkOrder);
            if(!orders.isEmpty()){
                ParkOrder parkOrder =orders.get(0);
                String expireTime = parkOrder.getExpireTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                //订单号失效，需要重新向第三方查询停车费订单信息
                if (new Date().getTime() >= sdf.parse(expireTime).getTime()) {
                    retCode = "-1";
                    retMsg = "当前订单已失效，请重新查询停车费信息！";
                } else {
                    int payable = parkOrder.getPayable();   //待支付金额
                    BigDecimal currentPrice = new BigDecimal(payable).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
                    bizDataJson.put("currentPrice", currentPrice);  //返回实时价格

                    JSONArray goodsArray = new JSONArray();
                    JSONObject goodsObj = new JSONObject();
                    goodsObj.put("orderNo", orderNo);
                    goodsObj.put("plateNo", parkOrder.getPlateNo());
                    goodsObj.put("parkName", parkOrder.getParkName());
                    goodsObj.put("currentPrice", currentPrice);
//                goodsObj.put("goodsCount", 1);
//                goodsObj.put("photoId", shoppingArtactivity.getMainPhotoId());

                    goodsArray.add(goodsObj);
                    bizDataJson.put("goodsInfoList", goodsArray);

                    //订单商品价格
                    bizDataJson.put("goodsPrice", currentPrice);

                    BigDecimal couponCut = BigDecimal.ZERO;  //优惠券折扣
                    BigDecimal integralCut = BigDecimal.ZERO;  //积分抵扣
                    BigDecimal balanceCut = BigDecimal.ZERO;  //余额抵扣

                    //积分、余额等使用条件查询
                    int useIntegralSet = 2;   //停车缴费默认为2：积分不限额抵扣
                    int useBalanceSet = 1;   //使用余额支付设置 0:不允许使用;1:可使用

                    bizDataJson.put("useIntegralSet", useIntegralSet);
                    bizDataJson.put("useBalanceSet", useBalanceSet);

                    //开始计算订单优惠和支付金额
                    BigDecimal totalPrice = currentPrice;
                    //计算优惠时只在商品费用的基础上进行计算
                    BigDecimal payPrice = currentPrice;

                    if (currentPrice.compareTo(BigDecimal.ZERO) == 1) {
                        //查询商品可用的可用优惠券
                        List<String> couponIds = new ArrayList<>();
                        JSONArray couponArray = CommonUtil.getParkCouppon(userId);
                        for (int i = 0; i < couponArray.size(); i++) {
                            JSONObject obj = couponArray.getJSONObject(i);
                            couponIds.add(obj.getString("id"));
                        }
                        bizDataJson.put("couponList", couponArray);
                        //查询账户积分和余额信息
                        boolean accountState = true;

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

                        ShoppingAssetRule shoppingAssetRule = CommonUtil.getAssetRule();
                        if (null != shoppingAssetRule) {
                            bizDataJson.put("accountPointLimit", shoppingAssetRule.getPointAvoidLimit());
                            bizDataJson.put("accountMoneyLimit", shoppingAssetRule.getAccountAvoidLimit());
                        }


                        if (!accountState) {
                            retCode = "-1";
                            retMsg = "无法获取您的账户积分和余额数据，请稍后再试！";
                        } else {
                            BigDecimal originPrice = payPrice;

                            //用户选择的优惠券
                            //需要再判断用户选择的优惠券当前是否仍可用
                            if (StringUtil.isNotNull(reqJson.get("couponId"))&& couponIds.contains(reqJson.getString("couponId"))) {
                                String couponId = reqJson.getString("couponId");
                                ///先获取用户选择的优惠券详情
                                JSONObject couponDtl = CRMService.getUserCouponDtl(couponId);
                                if (null != couponDtl) {
                                    String right_No = couponDtl.getString("right_No");
                                    ShoppingCoupon shoppingCoupon = new ShoppingCoupon();
                                    shoppingCoupon.setRight_No(right_No);
                                    shoppingCoupon = shoppingCouponDao.queryDetail(shoppingCoupon);

                                    int couponAmount = Integer.valueOf(shoppingCoupon.getRight_Content());
                                    if (new BigDecimal(couponAmount).compareTo(payPrice) == 1) {
                                        payPrice = BigDecimal.ZERO;
                                    } else {
                                        payPrice = payPrice.subtract(new BigDecimal(couponAmount));
                                    }
                                    couponCut = originPrice.subtract(payPrice);
                                    originPrice = payPrice;
                                }
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
                                if (pointPay > 0) {
                                    pointLimit = pointPay > pointLimit ? pointLimit : pointPay;
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

                            //余额抵扣
                            if (null != reqJson.get("useBalance") && reqJson.getInteger("useBalance") == 1) {
                                BigDecimal deductionBalance = BigDecimal.ZERO;
                                BigDecimal accountLimit = bizDataJson.getBigDecimal("accountMoney");
                                BigDecimal balancePay = new BigDecimal(payLimit.getBalancePay());   //余额单次支付上限
                                if (balancePay.compareTo(BigDecimal.ZERO) > 0 && balancePay.compareTo(accountLimit) < 0) {    //余额支付上限为账户余额和支付限额两者中的较小值
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
                    bizDataJson.put("deductionIntegralPrice", integralCut);  //积分抵扣金额
                    bizDataJson.put("deductionBalancePrice", balanceCut);    //账户余额抵扣金额
                    retCode = "0";
                    retMsg = "操作成功！";
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

    /**
     * 创建停车缴费订单（直接下单）
     */
    @Override
    public JSONObject addParkOrder(JSONObject reqJson, HttpServletRequest request) {
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
            int orderUseIntegralValue = reqJson.get("orderUseIntegralValue") == null ? 0 : reqJson.getInteger("orderUseIntegralValue");//积分抵扣值
            BigDecimal orderDeductionBalancePrice = reqJson.get("orderDeductionBalancePrice") == null ? BigDecimal.ZERO : reqJson.getBigDecimal("orderDeductionBalancePrice");//账户余额抵扣金额
            BigDecimal unitPrice = reqJson.getBigDecimal("unitPrice");//移动端传递的商品单价
            String orderNo = reqJson.getString("orderNo");  //外部订单

            ParkOrder parkOrder = new ParkOrder();
            parkOrder.setOrderNo(orderNo);
            parkOrder = parkOrderDao.queryDetail(parkOrder);
            String expireTime = parkOrder.getExpireTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //实时查询商品现价
            int payable = parkOrder.getPayable();   //待支付金额
            BigDecimal currentPrice = new BigDecimal(payable).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
            //订单号失效，需要重新向第三方查询停车费订单信息
            if (new Date().getTime() >= sdf.parse(expireTime).getTime()) {
                retCode = "-1";
                retMsg = "当前订单已失效，请重新查询停车费信息！";
            } else if (currentPrice.compareTo(unitPrice) != 0) {
                retCode = "-1";
                retMsg = "订单价格发生变化，请重新确认订单信息！";
            } else {
                //从卖座实时查询账户积分和余额
                int account_point = 0;   //会员账户积分剩余点数，单位：点数；
                BigDecimal accountMoney = BigDecimal.ZERO;
                int point_avoid_limit = 0;   //积分支付免密限额；
                int account_money_fen = 0;  //账户余额；单位：分
                int account_avoid_limit = 0;

                //查询账户积分和余额信息
                boolean accountState = true;
                if (orderUseIntegralValue > 0 || orderDeductionBalancePrice.compareTo(BigDecimal.ZERO) == 1 || (null != reqJson.get("useIntegral") && reqJson.getInteger("useIntegral") == 1) || (null != reqJson.get("useBalance") && reqJson.getInteger("useBalance") == 1)) {
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
                        accountState = false;
                    }

                    //免密限額
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
                } else if (accountMoney.compareTo(orderDeductionBalancePrice) < 0) {
                    retCode = "-1";
                    retMsg = "账户余额不足，请重新确认订单信息！";
                } else {

                    //订单总金额为商品费用+运费
                    BigDecimal totalPrice = currentPrice;

                    //计算优惠时只在商品费用的基础上进行计算
                    BigDecimal payPrice = BigDecimal.ZERO;
                    payPrice = currentPrice;

                    BigDecimal originPrice = payPrice;

                    BigDecimal couponCut = BigDecimal.ZERO;  //优惠券折扣
                    int integralValue = 0;  //积分抵扣数量
                    BigDecimal integralCut = BigDecimal.ZERO;  //积分抵扣
                    BigDecimal balanceCut = BigDecimal.ZERO;  //余额抵扣

                    //查询商品可用的可用优惠券
                    List<String> couponIds = new ArrayList<>();
                    JSONArray couponArray = CommonUtil.getParkCouppon(userId);
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
                            int couponAmount = Integer.valueOf(shoppingCoupon.getRight_Content());
                            if (new BigDecimal(couponAmount).compareTo(payPrice) == 1) {
                                payPrice = BigDecimal.ZERO;
                            } else {
                                payPrice = payPrice.subtract(new BigDecimal(couponAmount));
                            }
                            couponCut = originPrice.subtract(payPrice);
                            originPrice = payPrice;
                        }
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
                        if (pointPay > 0) {
                            pointLimit = pointPay > pointLimit ? pointLimit : pointPay;
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
                        BigDecimal accountLimit = bizDataJson.getBigDecimal("accountMoney");
                        BigDecimal balancePay = new BigDecimal(payLimit.getBalancePay());   //余额单次支付上限
                        if (balancePay.compareTo(BigDecimal.ZERO) > 0 && balancePay.compareTo(accountLimit) < 0) {    //余额支付上限为账户余额和支付限额两者中的较小值
                            accountLimit = balancePay;
                        }
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
                        String orderId = PayUtil.getOrderNo(Const.PARK_ORDER);
                        orderform.setOrderId(orderId);
                        //订单类型：停车
                        orderform.setOrderType(Const.PARK_ORDER_TYPE);
                        //车场账单号
                        orderform.setOutOrderId(orderNo);
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
                        if (StringUtil.isNotNull(reqJson.get("couponId"))) {
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
                        //余额
                        if (null != reqJson.get("useBalance") && reqJson.getInteger("useBalance") == 1) {
                            //账户余额支付金额
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

                        //更新停车订单中关联的orderId
                        parkOrder.setOrderId(orderId);
                        parkOrderDao.update(parkOrder);

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
                        goodscart.setGoodsId(orderNo);
                        goodscart.setCount(1);
                        goodscart.setCartType(Const.PARK_CART_TYPE);
                        String str = "车牌号：" + parkOrder.getPlateNo() + "；进场时间：" + parkOrder.getEntryTime();
                        goodscart.setSpecInfo(str);   //将车牌号和进场时间存入规格属性信息

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
                        reqMap.put("id", "21");    //停车支付仅支持微信支付方式
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

    /**
     * 查询车牌号列表
     */
    @Override
    public JSONObject queryPlateNoList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String userId = reqJson.getString("userId");
            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("userId", userId);
            List<ParkPlate> objList = parkPlateDao.queryList(reqMap);

            bizDataJson.put("objList", objList);
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
     * 设置默认车牌号
     */
    @Override
    public JSONObject setDefaultPlateNo(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            parkPlateDao.cancelDefaultPlateNo(new ParkPlate());
            ParkPlate parkPlate = JSON.parseObject(reqJson.toJSONString(), ParkPlate.class);
            //设置默认车牌号
            parkPlateDao.cancelDefaultPlateNo(new ParkPlate());
            parkPlate.setDefaultPlateBoolean("1");
            parkPlateDao.update(parkPlate);
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
     * 新增车牌号
     */
    @Override
    public JSONObject addPlateNo(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ParkPlate parkPlate = JSON.parseObject(reqJson.toJSONString(), ParkPlate.class);
            //如果新增的车牌号为默认车牌号
            if (parkPlate.getDefaultPlateBoolean().equals("1")) {
                parkPlateDao.cancelDefaultPlateNo(new ParkPlate());
            }
            parkPlateDao.insert(parkPlate);
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
     * 编辑车牌号
     */
    @Override
    public JSONObject editPlateNo(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ParkPlate parkPlate = JSON.parseObject(reqJson.toJSONString(), ParkPlate.class);
            //如果新增的车牌号为默认车牌号
            if (parkPlate.getDefaultPlateBoolean().equals("1")) {
                parkPlateDao.cancelDefaultPlateNo(new ParkPlate());
            }
            parkPlateDao.update(parkPlate);
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
     * 删除车牌号
     */
    @Override
    public JSONObject delPlateNo(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ParkPlate parkPlate = JSON.parseObject(reqJson.toJSONString(), ParkPlate.class);
            parkPlateDao.delete(parkPlate);
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
}
