package com.centit.shopping.biz.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.biz.bo.SimpleOrdernfo;
import com.centit.shopping.biz.bo.UserAccountInfo;
import com.centit.shopping.common.enums.Const;
import com.centit.shopping.dao.ShoppingArtactivityInventoryDao;
import com.centit.shopping.dao.ShoppingArtplanInventoryDao;
import com.centit.shopping.po.*;
import com.centit.shopping.redis.RedisStockService;
import com.centit.shopping.utils.CommonUtil;
import com.centit.shopping.utils.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 艺术活动/爱艺计划/艺术培训订单相关
 * @Date : 2023/9/4 14:42
 **/
@Component
public class ArtOrderUtil extends OrderBaseUtil{

    @Resource
    private ShoppingArtactivityInventoryDao shoppingArtactivityInventoryDao;

    @Resource
    private ShoppingArtplanInventoryDao shoppingArtplanInventoryDao;

    @Value("${moneyToIntegralScale}")
    private int moneyToIntegralScale;

    @Value("${offcodeLength}")
    private int offcodeLength;
    /**
     * 配置库存Redis缓存Key前缀
     */
    public static final String REDIS_KEY_ACT = "ACT:";
    public static final String REDIS_KEY_PLAN ="PLAN:";
    @Resource
    private RedisStockService redisStockService;
    @Resource
    private DBService dbUtil;

    /**
     * 获取艺教订单可用优惠券列表
     *
     * @param goodsType 商品类型  3：艺术活动；4：艺术课程；6：爱艺计划
     * @param goodsId 商品id
     * @param userId 用户id
     * @param goodsAmount 商品数量
     * @param fixedIntegalValue 定额积分抵扣值
     * @return 优惠券列表
     */
    public JSONArray getArtOrderCouponList(int goodsType,String goodsId, String userId, BigDecimal goodsAmount, int fixedIntegalValue) {
        BigDecimal fixedIntegalPrice = new BigDecimal(fixedIntegalValue).divide(new BigDecimal(moneyToIntegralScale)).setScale(2, BigDecimal.ROUND_HALF_UP);
        JSONArray couponArray = CommonUtil.getArtCouppon(goodsId, goodsType, userId, goodsAmount, goodsAmount.subtract(fixedIntegalPrice));
        return couponArray;
    }

    /**
     * 获取会员活动现价
     *
     * @param shoppingArtactivity
     * @param propertys
     * @return
     */
    public BigDecimal getActivityCurrentPrice(ShoppingArtactivity shoppingArtactivity, String propertys) {
        //实时查询会员活动现价
        BigDecimal currentPrice = BigDecimal.ZERO;
        if (StringUtil.isNotNull(propertys)) {
            ShoppingArtactivityInventory shoppingArtactivityInventory = new ShoppingArtactivityInventory();
            shoppingArtactivityInventory.setActivityId(shoppingArtactivity.getId());
            shoppingArtactivityInventory.setPropertys(propertys);
            shoppingArtactivityInventory = shoppingArtactivityInventoryDao.queryDetail(shoppingArtactivityInventory);
            currentPrice = shoppingArtactivityInventory.getPrice();
        } else {
            currentPrice = shoppingArtactivity.getCurrentPrice();
        }
        return currentPrice;
    }

    /**
     * 获取爱艺计划现价
     *
     * @param shoppingArtplan
     * @param propertys
     * @return 现价
     */
    public BigDecimal getPlanCurrentPrice(ShoppingArtplan shoppingArtplan, String propertys) {
        //实时查询会员活动现价
        BigDecimal currentPrice = BigDecimal.ZERO;
        if (StringUtil.isNotNull(propertys)) {
            ShoppingArtplanInventory shoppingArtplanInventory = new ShoppingArtplanInventory();
            shoppingArtplanInventory.setActivityId(shoppingArtplan.getId());
            shoppingArtplanInventory.setPropertys(propertys);
            shoppingArtplanInventory = shoppingArtplanInventoryDao.queryDetail(shoppingArtplanInventory);
            currentPrice = shoppingArtplanInventory.getPrice();
        } else {
            currentPrice = shoppingArtplan.getCurrentPrice();
        }
        return currentPrice;
    }


    /**
     * 会员活动订单金额计算
     *
     * @param activityOrderInfo
     * @return ActivityOrderInfo
     */
    public SimpleOrdernfo countActOrderAmount(ShoppingArtactivity shoppingArtactivity, SimpleOrdernfo activityOrderInfo, UserAccountInfo userAccountInfo) {
        int goodsCount = activityOrderInfo.getGoodsCount();

        int useIntegralSet = shoppingArtactivity.getUseIntegralSet();   //使用积分抵扣设置 0:不允许使用积分抵扣;1:可使用部分积分抵扣
        int useIntegralValue = shoppingArtactivity.getUseIntegralValue();    //单个商品积分抵扣值
        int useBalanceSet = shoppingArtactivity.getUseBalanceSet();   //使用余额支付设置 0:不允许使用;1:可使用
        int useMembershipSet = shoppingArtactivity.getUseMembershipSet();   //是否支持会员权益 0:不支持;1:支持

        BigDecimal couponCut = BigDecimal.ZERO;  //优惠券折扣
        BigDecimal accountCut = BigDecimal.ZERO; //会员体系折扣
        int integralValue = 0;  //积分抵扣数量
        BigDecimal integralCut = BigDecimal.ZERO;  //积分抵扣金额
        BigDecimal balanceCut = BigDecimal.ZERO;  //余额抵扣金额

        String userId = activityOrderInfo.getUserId();
        String mzUserId = activityOrderInfo.getMzUserid();   //麦座用户id

        //实时查询商品现价
        BigDecimal currentPrice = getActivityCurrentPrice(shoppingArtactivity, activityOrderInfo.getPropertys());

        //商品金额=商品现价*购买数量
        BigDecimal goodsAmount = currentPrice.multiply(new BigDecimal(goodsCount));
        //订单总金额=商品金额+运费
        BigDecimal totalAmount = goodsAmount;
        //标识订单剩余需要支付的金额，初始为商品金额（该金额值不包含运费，运费在费用计算中会单独处理）
        BigDecimal payAmount = goodsAmount;
        //定额积分参与折扣类优惠，但不参与抵扣类优惠，因此要提前把定额抵扣积分拿出来，在获取可用优惠券以及计算折扣类优惠时进行处理
        int fixedIntegalValue = useIntegralSet == 1 ? (useIntegralValue * goodsCount) : 0;

        activityOrderInfo.setCurrentPrice(currentPrice);
        activityOrderInfo.setGoodsAmount(goodsAmount);
        activityOrderInfo.setTotalAmount(totalAmount);

        /*
                    开始计算根据优先级订单金额，优先级顺序如下：
                    1.优惠券
                    2.会员折扣
                    3.积分抵扣
                    4.余额抵扣*/

        /*
            1.折扣优惠券计算*/
        //从CRM获取用户选择的优惠券详情
        ShoppingCoupon shoppingCoupon = getUserCoupon(activityOrderInfo.getCouponId());
        JSONArray couponArray = getArtOrderCouponList(Const.COUPON_RELATE_ACT,shoppingArtactivity.getId(), userId, goodsAmount, fixedIntegalValue);
        activityOrderInfo.setCouponArray(couponArray);
        List<String> couponIds = new ArrayList<>();
        for (int i = 0; i < couponArray.size(); i++) {
            JSONObject obj = couponArray.getJSONObject(i);
            couponIds.add(obj.getString("id"));
        }
        //判断用户选择的优惠券是否是有效的优惠券，防止优惠券被篡改
        if(StringUtil.isNotNull(activityOrderInfo.getCouponId())&&(shoppingCoupon == null || !couponIds.contains(activityOrderInfo.getCouponId()))){
            activityOrderInfo.setErrorMsg("当前选择的优惠券信息不存在！");
            return activityOrderInfo;
        }
        //折扣优惠
        if (null != shoppingCoupon && shoppingCoupon.getRight_Type().equals("discount")) {
            BigDecimal discount = new BigDecimal(shoppingCoupon.getRight_Content());
            //优惠券抵扣的金额：商品金额-(商品金额*折扣值)
            couponCut = payAmount.subtract(payAmount.multiply(discount).setScale(2, BigDecimal.ROUND_HALF_UP));
            //这边定额积分值也要计算折扣
            fixedIntegalValue = new BigDecimal(fixedIntegalValue).multiply(discount).intValue();
            //剩余需要支付的商品金额
            payAmount = payAmount.subtract(couponCut);
        }
        //抵扣优惠
        else if (null != shoppingCoupon && shoppingCoupon.getRight_Type().equals("coincp")) {
            BigDecimal couponAmount = new BigDecimal(Integer.valueOf(shoppingCoupon.getRight_Content()));
            //订单需要支付的现金金额，即商品金额-定额积分抵扣金额（该金额值同样不包含运费，运费在订单费用计算中会单独处理）
            BigDecimal cashAmount = goodsAmount.subtract(new BigDecimal(fixedIntegalValue).divide(new BigDecimal(moneyToIntegralScale)).setScale(2, BigDecimal.ROUND_HALF_UP));
            //优惠券抵扣的金额：取抵扣金额和现金金额两者中的较小值
            couponCut = couponAmount.compareTo(cashAmount) == 1 ? cashAmount : couponAmount;
            //剩余需要支付的商品金额
            payAmount = payAmount.subtract(couponCut);
        }
        //优惠券抵扣金额
        activityOrderInfo.setCouponCut(couponCut);

        /*
            2.会员等级折扣计算
            */
        if (useMembershipSet == 1) {
            //获取会员等级折扣值
            BigDecimal discount = CommonUtil.getUserMemberShip(userId);
            //会员权益抵扣的金额：商品金额-(商品金额*折扣)
            accountCut = payAmount.subtract(payAmount.multiply(discount).setScale(2, BigDecimal.ROUND_HALF_UP));
            //定额积分值也要计算折扣
            fixedIntegalValue = new BigDecimal(fixedIntegalValue).multiply(discount).intValue();
            //剩余需要支付的商品金额
            payAmount = payAmount.subtract(accountCut);
        }
        activityOrderInfo.setAccountCut(accountCut);

        //积分和余额计算，需要先获取卖座的单次支付上限设置
        ShoppingPayLimit payLimit = CommonUtil.getPayLimit();
        /*
              3.积分抵扣计算
             */
        int accountPoint = userAccountInfo.getAccount_point(); //用户麦座账户积分
        int pointLimitPay = payLimit.getPointPay();  //后台设置的积分单次支付限额
        //如果是定额积分抵扣，需要支付的积分即为经过折扣计算后的fixedIntegalValue
        if (useIntegralSet == 1) {
            integralValue = fixedIntegalValue;
            //定额积分抵扣时，用户账户积分必须足够
            if(integralValue>accountPoint){
                activityOrderInfo.setErrorMsg("当前账户积分不足！");
                return activityOrderInfo;
            }
            if(integralValue>pointLimitPay){
                activityOrderInfo.setErrorMsg("积分单次支付限额"+pointLimitPay+",当前订单已超出该额度！");
                return activityOrderInfo;
            }
        } else if (useIntegralSet == 2 && activityOrderInfo.isUseIntegral()) {  //限额积分抵扣，且用户打开了使用积分的开关
            //商品金额对应的积分值
            int payPriceToInt = payAmount.multiply(new BigDecimal(moneyToIntegralScale)).intValue();
            /*
                     用户最大可抵扣的积分，取商品金额和（useIntegralValue * goodsCount）两者的较小值，
                     因为经过前面的优惠，有可能剩余商品金额已经小于商品设置的积分抵扣最大值
                             */
            int maxIntegralValue = useIntegralValue * goodsCount < payPriceToInt ? useIntegralValue * goodsCount : payPriceToInt;
            //用户最终需要支付的积分，为商品最大抵扣积分、账户积分、麦座单次积分支付上限三者的最小值
            integralValue = Math.min(Math.min(maxIntegralValue, accountPoint), pointLimitPay);
        }
        //积分抵扣金额
        integralCut = new BigDecimal(integralValue).divide(new BigDecimal(moneyToIntegralScale)).setScale(2, BigDecimal.ROUND_HALF_UP);
        payAmount = payAmount.subtract(integralCut);

        activityOrderInfo.setIntegralValue(integralValue);
        activityOrderInfo.setIntegralCut(integralCut);


        /*
            4.余额抵扣计算
            */
        if (useBalanceSet == 1 && activityOrderInfo.isUseBalance()) {
            //余额支付上限取当前账户余额和麦座余额单次支付限额两者的较小值
            BigDecimal balanceLimit = BigDecimal.ZERO;
            if (payLimit.getBalancePay() > 0) {
                BigDecimal balancePay = new BigDecimal(payLimit.getBalancePay());   //余额单次支付上限
                balanceLimit = balancePay.compareTo(userAccountInfo.getAccountMoney()) < 0 ? balancePay : userAccountInfo.getAccountMoney();
            }
            balanceCut = payAmount.compareTo(balanceLimit) < 0 ? payAmount : balanceLimit;
            payAmount = payAmount.subtract(balanceCut);
        }
        activityOrderInfo.setBalanceCut(balanceCut);
        activityOrderInfo.setPayAmount(payAmount);

        activityOrderInfo.setCountSuccess(true);
        return activityOrderInfo;
    }

    /**
     * 爱艺计划订单金额计算
     * @param shoppingArtplan 爱艺计划
     * @param planOrderInfo  订单信息
     * @param userAccountInfo  用户账户信息
     * @return  订单信息
     */
    public SimpleOrdernfo countPLanOrderAmount(ShoppingArtplan shoppingArtplan, SimpleOrdernfo planOrderInfo, UserAccountInfo userAccountInfo) {
        int goodsCount = planOrderInfo.getGoodsCount();

        int useIntegralSet = shoppingArtplan.getUseIntegralSet();   //使用积分抵扣设置 0:不允许使用积分抵扣;1:可使用部分积分抵扣
        int useIntegralValue = shoppingArtplan.getUseIntegralValue();    //单个商品积分抵扣值
        int useBalanceSet = shoppingArtplan.getUseBalanceSet();   //使用余额支付设置 0:不允许使用;1:可使用
        int useMembershipSet = shoppingArtplan.getUseMembershipSet();   //是否支持会员权益 0:不支持;1:支持

        BigDecimal couponCut = BigDecimal.ZERO;  //优惠券折扣
        BigDecimal accountCut = BigDecimal.ZERO; //会员体系折扣
        int integralValue = 0;  //积分抵扣数量
        BigDecimal integralCut = BigDecimal.ZERO;  //积分抵扣金额
        BigDecimal balanceCut = BigDecimal.ZERO;  //余额抵扣金额

        String userId = planOrderInfo.getUserId();
        String mzUserId = planOrderInfo.getMzUserid();   //麦座用户id

        //实时查询商品现价
        BigDecimal currentPrice = getPlanCurrentPrice(shoppingArtplan, planOrderInfo.getPropertys());

        //商品金额=商品现价*购买数量
        BigDecimal goodsAmount = currentPrice.multiply(new BigDecimal(goodsCount));
        //订单总金额=商品金额+运费
        BigDecimal totalAmount = goodsAmount;
        //标识订单剩余需要支付的金额，初始为商品金额（该金额值不包含运费，运费在费用计算中会单独处理）
        BigDecimal payAmount = goodsAmount;
        //定额积分参与折扣类优惠，但不参与抵扣类优惠，因此要提前把定额抵扣积分拿出来，在获取可用优惠券以及计算折扣类优惠时进行处理
        int fixedIntegalValue = useIntegralSet == 1 ? (useIntegralValue * goodsCount) : 0;

        planOrderInfo.setCurrentPrice(currentPrice);
        planOrderInfo.setGoodsAmount(goodsAmount);
        planOrderInfo.setTotalAmount(totalAmount);

        /*
                    开始计算根据优先级订单金额，优先级顺序如下：
                    1.优惠券
                    2.会员折扣
                    3.积分抵扣
                    4.余额抵扣*/

        /*
            1.折扣优惠券计算*/
        //从CRM获取用户选择的优惠券详情
        ShoppingCoupon shoppingCoupon = getUserCoupon(planOrderInfo.getCouponId());
        JSONArray couponArray = getArtOrderCouponList(Const.COUPON_RELATE_PLAN,shoppingArtplan.getId(), userId, goodsAmount, fixedIntegalValue);
        planOrderInfo.setCouponArray(couponArray);
        List<String> couponIds = new ArrayList<>();
        for (int i = 0; i < couponArray.size(); i++) {
            JSONObject obj = couponArray.getJSONObject(i);
            couponIds.add(obj.getString("id"));
        }
        //判断用户选择的优惠券是否是有效的优惠券，防止优惠券被篡改
        if(StringUtil.isNotNull(planOrderInfo.getCouponId())&&(shoppingCoupon == null || !couponIds.contains(planOrderInfo.getCouponId()))){
            planOrderInfo.setErrorMsg("当前选择的优惠券信息不存在！");
            return planOrderInfo;
        }
        //折扣优惠
        if (null != shoppingCoupon && shoppingCoupon.getRight_Type().equals("discount")) {
            BigDecimal discount = new BigDecimal(shoppingCoupon.getRight_Content());
            //优惠券抵扣的金额：商品金额-(商品金额*折扣值)
            couponCut = payAmount.subtract(payAmount.multiply(discount).setScale(2, BigDecimal.ROUND_HALF_UP));
            //这边定额积分值也要计算折扣
            fixedIntegalValue = new BigDecimal(fixedIntegalValue).multiply(discount).intValue();
            //剩余需要支付的商品金额
            payAmount = payAmount.subtract(couponCut);
        }
        //抵扣优惠
        else if (null != shoppingCoupon && shoppingCoupon.getRight_Type().equals("coincp")) {
            BigDecimal couponAmount = new BigDecimal(Integer.valueOf(shoppingCoupon.getRight_Content()));
            //订单需要支付的现金金额，即商品金额-定额积分抵扣金额（该金额值同样不包含运费，运费在订单费用计算中会单独处理）
            BigDecimal cashAmount = goodsAmount.subtract(new BigDecimal(fixedIntegalValue).divide(new BigDecimal(moneyToIntegralScale)).setScale(2, BigDecimal.ROUND_HALF_UP));
            //优惠券抵扣的金额：取抵扣金额和现金金额两者中的较小值
            couponCut = couponAmount.compareTo(cashAmount) == 1 ? cashAmount : couponAmount;
            //剩余需要支付的商品金额
            payAmount = payAmount.subtract(couponCut);
        }
        //优惠券抵扣金额
        planOrderInfo.setCouponCut(couponCut);

        /*
            2.会员等级折扣计算
            */
        if (useMembershipSet == 1) {
            //获取会员等级折扣值
            BigDecimal discount = CommonUtil.getUserMemberShip(userId);
            //会员权益抵扣的金额：商品金额-(商品金额*折扣)
            accountCut = payAmount.subtract(payAmount.multiply(discount).setScale(2, BigDecimal.ROUND_HALF_UP));
            //定额积分值也要计算折扣
            fixedIntegalValue = new BigDecimal(fixedIntegalValue).multiply(discount).intValue();
            //剩余需要支付的商品金额
            payAmount = payAmount.subtract(accountCut);
        }
        planOrderInfo.setAccountCut(accountCut);

        //积分和余额计算，需要先获取卖座的单次支付上限设置
        ShoppingPayLimit payLimit = CommonUtil.getPayLimit();
        /*
              3.积分抵扣计算
             */
        int accountPoint = userAccountInfo.getAccount_point(); //用户麦座账户积分
        int pointLimitPay = payLimit.getPointPay();  //后台设置的积分单次支付限额
        //如果是定额积分抵扣，需要支付的积分即为经过折扣计算后的fixedIntegalValue
        if (useIntegralSet == 1) {
            integralValue = fixedIntegalValue;
            //定额积分抵扣时，用户账户积分必须足够
            if(integralValue>accountPoint){
                planOrderInfo.setErrorMsg("当前账户积分不足！");
                return planOrderInfo;
            }
            if(integralValue>pointLimitPay){
                planOrderInfo.setErrorMsg("积分单次支付限额"+pointLimitPay+",当前订单已超出该额度！");
                return planOrderInfo;
            }
        } else if (useIntegralSet == 2 && planOrderInfo.isUseIntegral()) {  //限额积分抵扣，且用户打开了使用积分的开关
            //商品金额对应的积分值
            int payPriceToInt = payAmount.multiply(new BigDecimal(moneyToIntegralScale)).intValue();
            /*
                     用户最大可抵扣的积分，取商品金额和（useIntegralValue * goodsCount）两者的较小值，
                     因为经过前面的优惠，有可能剩余商品金额已经小于商品设置的积分抵扣最大值
                             */
            int maxIntegralValue = useIntegralValue * goodsCount < payPriceToInt ? useIntegralValue * goodsCount : payPriceToInt;
            //用户最终需要支付的积分，为商品最大抵扣积分、账户积分、麦座单次积分支付上限三者的最小值
            integralValue = Math.min(Math.min(maxIntegralValue, accountPoint), pointLimitPay);
        }
        //积分抵扣金额
        integralCut = new BigDecimal(integralValue).divide(new BigDecimal(moneyToIntegralScale)).setScale(2, BigDecimal.ROUND_HALF_UP);
        payAmount = payAmount.subtract(integralCut);

        planOrderInfo.setIntegralValue(integralValue);
        planOrderInfo.setIntegralCut(integralCut);


        /*
            4.余额抵扣计算
            */
        if (useBalanceSet == 1 && planOrderInfo.isUseBalance()) {
            //余额支付上限取当前账户余额和麦座余额单次支付限额两者的较小值
            BigDecimal balanceLimit = BigDecimal.ZERO;
            if (payLimit.getBalancePay() > 0) {
                BigDecimal balancePay = new BigDecimal(payLimit.getBalancePay());   //余额单次支付上限
                balanceLimit = balancePay.compareTo(userAccountInfo.getAccountMoney()) < 0 ? balancePay : userAccountInfo.getAccountMoney();
            }
            balanceCut = payAmount.compareTo(balanceLimit) < 0 ? payAmount : balanceLimit;
            payAmount = payAmount.subtract(balanceCut);
        }
        planOrderInfo.setBalanceCut(balanceCut);
        planOrderInfo.setPayAmount(payAmount);

        planOrderInfo.setCountSuccess(true);
        return planOrderInfo;
    }

    /**
     * 判断当前时间是否可报名
     * @param signupStarttime
     * @param signupEndtime
     * @return true:可报名；false:不可报名
     */
    public Boolean isTimeEnable(String signupStarttime,String signupEndtime){
        try{
            SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if(StringUtil.compareMillisecond(signupStarttime,sf)>0||StringUtil.compareMillisecond(signupEndtime,sf)<0){
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 艺教课程订单金额计算
     * @param shoppingArtclass 艺教课程
     * @param classOrderInfo  订单信息
     * @param userAccountInfo  用户账户信息
     * @return  订单信息
     */
    public SimpleOrdernfo countClassOrderAmount(ShoppingArtclass shoppingArtclass, SimpleOrdernfo classOrderInfo, UserAccountInfo userAccountInfo) {
        int goodsCount = classOrderInfo.getGoodsCount();

        int useIntegralSet = shoppingArtclass.getUseIntegralSet();   //使用积分抵扣设置 0:不允许使用积分抵扣;1:可使用部分积分抵扣
        int useIntegralValue = shoppingArtclass.getUseIntegralValue();    //单个商品积分抵扣值
        int useBalanceSet = shoppingArtclass.getUseBalanceSet();   //使用余额支付设置 0:不允许使用;1:可使用
        int useMembershipSet = shoppingArtclass.getUseMembershipSet();   //是否支持会员权益 0:不支持;1:支持

        BigDecimal couponCut = BigDecimal.ZERO;  //优惠券折扣
        BigDecimal accountCut = BigDecimal.ZERO; //会员体系折扣
        int integralValue = 0;  //积分抵扣数量
        BigDecimal integralCut = BigDecimal.ZERO;  //积分抵扣金额
        BigDecimal balanceCut = BigDecimal.ZERO;  //余额抵扣金额

        String userId = classOrderInfo.getUserId();
        String mzUserId = classOrderInfo.getMzUserid();   //麦座用户id

        //实时查询商品现价
        BigDecimal currentPrice = shoppingArtclass.getCurrentPrice();

        //商品金额=商品现价*购买数量
        BigDecimal goodsAmount = currentPrice.multiply(new BigDecimal(goodsCount));
        //订单总金额=商品金额+运费
        BigDecimal totalAmount = goodsAmount;
        //标识订单剩余需要支付的金额，初始为商品金额（该金额值不包含运费，运费在费用计算中会单独处理）
        BigDecimal payAmount = goodsAmount;
        //定额积分参与折扣类优惠，但不参与抵扣类优惠，因此要提前把定额抵扣积分拿出来，在获取可用优惠券以及计算折扣类优惠时进行处理
        int fixedIntegalValue = useIntegralSet == 1 ? (useIntegralValue * goodsCount) : 0;

        classOrderInfo.setCurrentPrice(currentPrice);
        classOrderInfo.setGoodsAmount(goodsAmount);
        classOrderInfo.setTotalAmount(totalAmount);

        /*
                    开始计算根据优先级订单金额，优先级顺序如下：
                    1.优惠券
                    2.会员折扣
                    3.积分抵扣
                    4.余额抵扣*/

        /*
            1.折扣优惠券计算*/
        //从CRM获取用户选择的优惠券详情
        ShoppingCoupon shoppingCoupon = getUserCoupon(classOrderInfo.getCouponId());
        JSONArray couponArray = getArtOrderCouponList(Const.COUPON_RELATE_CLASS,shoppingArtclass.getId(), userId, goodsAmount, fixedIntegalValue);
        classOrderInfo.setCouponArray(couponArray);
        List<String> couponIds = new ArrayList<>();
        for (int i = 0; i < couponArray.size(); i++) {
            JSONObject obj = couponArray.getJSONObject(i);
            couponIds.add(obj.getString("id"));
        }
        //判断用户选择的优惠券是否是有效的优惠券，防止优惠券被篡改
        if(StringUtil.isNotNull(classOrderInfo.getCouponId())&&(shoppingCoupon == null || !couponIds.contains(classOrderInfo.getCouponId()))){
            classOrderInfo.setErrorMsg("当前选择的优惠券信息不存在！");
            return classOrderInfo;
        }
        //折扣优惠
        if (null != shoppingCoupon && shoppingCoupon.getRight_Type().equals("discount")) {
            BigDecimal discount = new BigDecimal(shoppingCoupon.getRight_Content());
            //优惠券抵扣的金额：商品金额-(商品金额*折扣值)
            couponCut = payAmount.subtract(payAmount.multiply(discount).setScale(2, BigDecimal.ROUND_HALF_UP));
            //这边定额积分值也要计算折扣
            fixedIntegalValue = new BigDecimal(fixedIntegalValue).multiply(discount).intValue();
            //剩余需要支付的商品金额
            payAmount = payAmount.subtract(couponCut);
        }
        //抵扣优惠
        else if (null != shoppingCoupon && shoppingCoupon.getRight_Type().equals("coincp")) {
            BigDecimal couponAmount = new BigDecimal(Integer.valueOf(shoppingCoupon.getRight_Content()));
            //订单需要支付的现金金额，即商品金额-定额积分抵扣金额（该金额值同样不包含运费，运费在订单费用计算中会单独处理）
            BigDecimal cashAmount = goodsAmount.subtract(new BigDecimal(fixedIntegalValue).divide(new BigDecimal(moneyToIntegralScale)).setScale(2, BigDecimal.ROUND_HALF_UP));
            //优惠券抵扣的金额：取抵扣金额和现金金额两者中的较小值
            couponCut = couponAmount.compareTo(cashAmount) == 1 ? cashAmount : couponAmount;
            //剩余需要支付的商品金额
            payAmount = payAmount.subtract(couponCut);
        }
        //优惠券抵扣金额
        classOrderInfo.setCouponCut(couponCut);

        /*
            2.会员等级折扣计算
            */
        if (useMembershipSet == 1) {
            //获取会员等级折扣值
            BigDecimal discount = CommonUtil.getUserMemberShip(userId);
            //会员权益抵扣的金额：商品金额-(商品金额*折扣)
            accountCut = payAmount.subtract(payAmount.multiply(discount).setScale(2, BigDecimal.ROUND_HALF_UP));
            //定额积分值也要计算折扣
            fixedIntegalValue = new BigDecimal(fixedIntegalValue).multiply(discount).intValue();
            //剩余需要支付的商品金额
            payAmount = payAmount.subtract(accountCut);
        }
        classOrderInfo.setAccountCut(accountCut);

        //积分和余额计算，需要先获取卖座的单次支付上限设置
        ShoppingPayLimit payLimit = CommonUtil.getPayLimit();
        /*
              3.积分抵扣计算
             */
        int accountPoint = userAccountInfo.getAccount_point(); //用户麦座账户积分
        int pointLimitPay = payLimit.getPointPay();  //后台设置的积分单次支付限额
        //如果是定额积分抵扣，需要支付的积分即为经过折扣计算后的fixedIntegalValue
        if (useIntegralSet == 1) {
            integralValue = fixedIntegalValue;
            //定额积分抵扣时，用户账户积分必须足够
            if(integralValue>accountPoint){
                classOrderInfo.setErrorMsg("当前账户积分不足！");
                return classOrderInfo;
            }
            if(integralValue>pointLimitPay){
                classOrderInfo.setErrorMsg("积分单次支付限额"+pointLimitPay+",当前订单已超出该额度！");
                return classOrderInfo;
            }
        } else if (useIntegralSet == 2 && classOrderInfo.isUseIntegral()) {  //限额积分抵扣，且用户打开了使用积分的开关
            //商品金额对应的积分值
            int payPriceToInt = payAmount.multiply(new BigDecimal(moneyToIntegralScale)).intValue();
            /*
                     用户最大可抵扣的积分，取商品金额和（useIntegralValue * goodsCount）两者的较小值，
                     因为经过前面的优惠，有可能剩余商品金额已经小于商品设置的积分抵扣最大值
                             */
            int maxIntegralValue = useIntegralValue * goodsCount < payPriceToInt ? useIntegralValue * goodsCount : payPriceToInt;
            //用户最终需要支付的积分，为商品最大抵扣积分、账户积分、麦座单次积分支付上限三者的最小值
            integralValue = Math.min(Math.min(maxIntegralValue, accountPoint), pointLimitPay);
        }
        //积分抵扣金额
        integralCut = new BigDecimal(integralValue).divide(new BigDecimal(moneyToIntegralScale)).setScale(2, BigDecimal.ROUND_HALF_UP);
        payAmount = payAmount.subtract(integralCut);

        classOrderInfo.setIntegralValue(integralValue);
        classOrderInfo.setIntegralCut(integralCut);


        /*
            4.余额抵扣计算
            */
        if (useBalanceSet == 1 && classOrderInfo.isUseBalance()) {
            //余额支付上限取当前账户余额和麦座余额单次支付限额两者的较小值
            BigDecimal balanceLimit = BigDecimal.ZERO;
            if (payLimit.getBalancePay() > 0) {
                BigDecimal balancePay = new BigDecimal(payLimit.getBalancePay());   //余额单次支付上限
                balanceLimit = balancePay.compareTo(userAccountInfo.getAccountMoney()) < 0 ? balancePay : userAccountInfo.getAccountMoney();
            }
            balanceCut = payAmount.compareTo(balanceLimit) < 0 ? payAmount : balanceLimit;
            payAmount = payAmount.subtract(balanceCut);
        }
        classOrderInfo.setBalanceCut(balanceCut);
        classOrderInfo.setPayAmount(payAmount);

        classOrderInfo.setCountSuccess(true);
        return classOrderInfo;
    }

    /**
     * 扣减会员活动库存
     * @param activityId 活动id
     * @param propertys  规格
     * @param cutCount  扣减数量
     * @return  扣减之后剩余的库存【-3:库存扣减失败; -2:库存不足; 大于等于0:扣减库存之后的剩余库存】
     */
    public Long cutActStock(String activityId,String propertys,int cutCount){
        //redis扣减商品库存
        if (StringUtil.isNotNull(propertys)) {
            String inventory_key = REDIS_KEY_ACT + activityId+":"+propertys;
            String goods_key = REDIS_KEY_ACT + activityId;
            Long stock = redisStockService.cutStockWithPropertys(goods_key,inventory_key,cutCount);
            if(stock>=0){
                try{
                    //数据库扣减库存
                    dbUtil.cutDBActInventory(activityId,propertys,cutCount);
                }catch (Exception e){
                    e.printStackTrace();
                    //数据库扣减库存失败，返库存
                    redisStockService.addStockWithPropertys(goods_key,inventory_key,cutCount);
                    return -3L;
                }
            }
            return stock;
        }else{
            String goods_key = REDIS_KEY_ACT + activityId;
            Long stock = redisStockService.updateStock(goods_key,cutCount);
            if(stock>=0){
                //扣减数据库库存
                try{
                    dbUtil.cutDBGoodsInventory(activityId,propertys,cutCount);
                }catch (Exception e){
                    e.printStackTrace();
                    //数据库扣减库存失败，返库存
                    redisStockService.addStock(goods_key,cutCount);
                    return -3L;
                }
            }
            return stock;
        }
    }

    /**
     * 增加会员活动库存
     * @param activityId
     * @param propertys
     * @param addCount
     * @return  增加之后剩余的库存
     */
    public Long addActStock(String activityId,String propertys,int addCount){
        try{
            //数据库增加库存
            dbUtil.addDBActInventory(activityId,propertys,addCount);
        }catch (Exception e){
            e.printStackTrace();
            return -3L;
        }
        //redis增加商品库存
        if (StringUtil.isNotNull(propertys)) {
            String inventory_key = REDIS_KEY_ACT + activityId+":"+propertys;
            String goods_key = REDIS_KEY_ACT + activityId;
            return redisStockService.addStockWithPropertys(goods_key,inventory_key,addCount);
        }else{
            String goods_key = REDIS_KEY_ACT + activityId;
            return redisStockService.addStock(goods_key,addCount);
        }
    }

    /**
     * 扣减爱艺计划库存
     * @param activityId 活动id
     * @param propertys  规格
     * @param cutCount  扣减数量
     * @return  扣减之后剩余的库存【-3:库存扣减失败; -2:库存不足; 大于等于0:扣减库存之后的剩余库存】
     */
    public Long cutPLanStock(String activityId,String propertys,int cutCount){
        //redis扣减商品库存
        if (StringUtil.isNotNull(propertys)) {
            String inventory_key = REDIS_KEY_PLAN + activityId+":"+propertys;
            String goods_key = REDIS_KEY_PLAN + activityId;
            Long stock = redisStockService.cutStockWithPropertys(goods_key,inventory_key,cutCount);
            if(stock>=0){
                try{
                    //数据库扣减库存
                    dbUtil.cutDBPlanInventory(activityId,propertys,cutCount);
                }catch (Exception e){
                    e.printStackTrace();
                    //数据库扣减库存失败，返库存
                    redisStockService.addStockWithPropertys(goods_key,inventory_key,cutCount);
                    return -3L;
                }
            }
            return stock;
        }else{
            String goods_key = REDIS_KEY_PLAN + activityId;
            Long stock = redisStockService.updateStock(goods_key,cutCount);
            if(stock>=0){
                //扣减数据库库存
                try{
                    dbUtil.cutDBPlanInventory(activityId,propertys,cutCount);
                }catch (Exception e){
                    e.printStackTrace();
                    //数据库扣减库存失败，返库存
                    redisStockService.addStock(goods_key,cutCount);
                    return -3L;
                }
            }
            return stock;
        }
    }

    /**
     * 增加爱艺计划库存
     * @param activityId
     * @param propertys
     * @param addCount
     * @return  增加之后剩余的库存
     */
    public Long addPlanStock(String activityId,String propertys,int addCount){
        try{
            //数据库增加库存
            dbUtil.addDBPlanInventory(activityId,propertys,addCount);
        }catch (Exception e){
            e.printStackTrace();
            return -3L;
        }
        //redis增加商品库存
        if (StringUtil.isNotNull(propertys)) {
            String inventory_key = REDIS_KEY_PLAN + activityId+":"+propertys;
            String goods_key = REDIS_KEY_PLAN + activityId;
            return redisStockService.addStockWithPropertys(goods_key,inventory_key,addCount);
        }else{
            String goods_key = REDIS_KEY_PLAN + activityId;
            return redisStockService.addStock(goods_key,addCount);
        }
    }
}
