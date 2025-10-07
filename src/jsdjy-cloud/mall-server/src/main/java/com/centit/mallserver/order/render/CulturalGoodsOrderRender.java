package com.centit.mallserver.order.render;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.core.enums.SellTypeEnum;
import com.centit.core.exp.BusinessException;
import com.centit.core.result.ResultCodeEnum;
import com.centit.mallserver.consts.MallConst;
import com.centit.mallserver.dao.ShoppingCouponDao;
import com.centit.mallserver.po.ShoppingCouponPo;
import com.centit.mallserver.threadPool.ThreadPoolExecutorFactory;
import com.centit.mallserver.order.render.vo.GoodsOrderRenderVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 文创商品订单渲染
 * @Date : 2024/12/19 15:35
 **/
@Scope("prototype")
@Component
@Slf4j
public class CulturalGoodsOrderRender extends BaseGoodsOrderRender {
    @Resource
    private ShoppingCouponDao shoppingCouponDao;

    private CompletableFuture<Void> couponListFuture;

    /**
     * 定额抵扣积分值
     */
    private int fixedIntegralValue;
    private BigDecimal fixedValue;
    private BigDecimal scaledFixedValue;

    private static final BigDecimal MONEY_TO_POINTS_SCALE = new BigDecimal(MallConst.MONEY_TO_POINTS_SCALE);

    /**
     * 计算优惠
     */
    @Override
    protected void calculateDiscounts() {
        // 优惠券计算
        calculateCouponDiscount();

        // 会员折扣计算
        calculateMembershipDiscount();

        // 积分抵扣计算
        calculatePointDiscount();

        // 余额抵扣计算
        calculateBalanceDiscount();
    }

    /**
     * 构建文创商品订单渲染信息
     */
    @Override
    public GoodsOrderRenderVo buildRenderInfo(){
        //生成订单id
        String orderId = MallConst.ORDER_PREFIX_CUL+ IdUtil.getSnowflake(workerId,datacenterId).nextIdStr();
        //构建通用返回参数
        super.buildGoodsOrderCommonRenderInfo(orderId);
        // 确保获取订单可用优惠券列表的异步任务已完成
        if (couponListFuture != null) {
            try {
                couponListFuture.join();
            } catch (Exception e) {
                // 异常情况下，返回空的优惠券列表
                log.error("获取订单可用优惠券列表失败", e);
                couponList = new JSONArray();
            }
        }
        //订单可用优惠券信息
        goodsOrderRenderVo.setCouponList(couponList);
        return goodsOrderRenderVo;
    }

    public void calculateCouponDiscount(){
        //定额积分涉及优惠券筛选和折扣计算，需要提前处理
        calculateFixedIntegral();
        //从crm获取用户可用优惠券列表
        couponListFuture= CompletableFuture.runAsync(() -> getGoodsOrderCouponList(), ThreadPoolExecutorFactory.createThreadPoolExecutor());
        if(StringUtils.isNotBlank(productDto.getCouponId())){
            //从CRM获取用户选择的优惠券详情
            //ToDo 为避免从crm获取数据的耗时，可以改为直接从数据库获取，即使优惠券信息与CRM数据不一致，可忽略，以数据库当前已同步数据为准
            JSONObject couponDtl = crmService.getCouponDtl(productDto.getCouponId());
            String rightNo = couponDtl.getString("right_No");
            ShoppingCouponPo shoppingCoupon = new ShoppingCouponPo();
            shoppingCoupon.setRight_No(rightNo);
            shoppingCoupon = shoppingCouponDao.selectDetail(shoppingCoupon);

            BigDecimal  couponCut = BigDecimal.ZERO;
            //折扣优惠
            if (null != shoppingCoupon && shoppingCoupon.getRight_Type().equals(MallConst.COUPON_TYPE_DISCOUNT)) {
                BigDecimal discount = new BigDecimal(shoppingCoupon.getRight_Content());
                //优惠券抵扣的金额：商品金额-(商品金额*折扣值)
                couponCut = orderAmount.getPayAmount().subtract(orderAmount.getPayAmount().multiply(discount).setScale(MallConst.DECIMAL_PLACES, RoundingMode.HALF_UP));
                //这边定额积分值也要计算折扣
                fixedIntegralValue = fixedValue.multiply(discount).intValue();
                fixedValue=BigDecimal.valueOf(fixedIntegralValue);
                scaledFixedValue =fixedValue.divide(MONEY_TO_POINTS_SCALE,MallConst.DECIMAL_PLACES, RoundingMode.HALF_UP);
            }
            //抵扣优惠
            else if (null != shoppingCoupon && shoppingCoupon.getRight_Type().equals(MallConst.COUPON_TYPE_VOUCHER)) {
                BigDecimal couponAmount = new BigDecimal(Integer.parseInt(shoppingCoupon.getRight_Content()));
                //订单需要支付的现金金额，即商品金额-定额积分抵扣金额（该金额值同样不包含运费，运费在订单费用计算中会单独处理）
                BigDecimal cashAmount = orderAmount.getPayAmount().subtract(scaledFixedValue);
                //优惠券抵扣的金额：取抵扣金额和现金金额两者中的较小值
                couponCut = couponAmount.compareTo(cashAmount) > 0 ? cashAmount : couponAmount;
            }
            //剩余需要支付的商品金额
            orderAmount.setPayAmount(orderAmount.getPayAmount().subtract(couponCut));
            //优惠券抵扣金额
            orderAmount.setCouponCut(couponCut);
        }
    }

    public void calculateMembershipDiscount(){
        if(shoppingGoods.getUseMembershipSet()==1){
            //获取会员等级折扣值
            BigDecimal discount = userInfoService.getUserMemberShip(userInfo);
            //会员权益抵扣的金额：商品金额-(商品金额*折扣)
            BigDecimal accountCut = orderAmount.getPayAmount().subtract(orderAmount.getPayAmount().multiply(discount).setScale(MallConst.DECIMAL_PLACES, RoundingMode.HALF_UP));
            //定额积分值也要计算折扣
            fixedIntegralValue = fixedValue.multiply(discount).intValue();
            fixedValue=BigDecimal.valueOf(fixedIntegralValue);
            scaledFixedValue =fixedValue.divide(MONEY_TO_POINTS_SCALE,MallConst.DECIMAL_PLACES, RoundingMode.HALF_UP);
            //剩余需要支付的商品金额
            orderAmount.setPayAmount(orderAmount.getPayAmount().subtract(accountCut));
            //会员等级抵扣金额
            orderAmount.setAccountCut(accountCut);
        }
    }

    public void calculatePointDiscount(){
        //用户麦座账户积分
        int accountPoint = userAccountInfo.getAccountPoint();
        //后台设置的积分单次支付限额
        int pointLimitPay = payLimit.getPointPay();
        int integralValue=0;
        //如果是定额积分抵扣，需要支付的积分即为经过折扣计算后的fixedIntegralValue
        if (shoppingGoods.getUseIntegralSet() == 1) {
            integralValue = fixedIntegralValue;
            //定额积分抵扣时，用户账户积分必须足够
            if(integralValue>accountPoint){
                throw new BusinessException(ResultCodeEnum.ORDER_RENDER_FAIL.getCode(),"账户积分不足");
            }
            if(integralValue>pointLimitPay){
                throw new BusinessException(ResultCodeEnum.ORDER_RENDER_FAIL.getCode(),"积分支付超出单次限额");
            }
        }
        //限额积分抵扣，且用户打开了使用积分的开关
        else if (shoppingGoods.getUseIntegralSet() == 2 && productDto.getUseIntegral()==1) {
            //商品金额对应的积分值
            int payPriceToInt = orderAmount.getPayAmount().multiply(MONEY_TO_POINTS_SCALE).intValue();
            int goodPoints = shoppingGoods.getUseIntegralValue()*productDto.getGoodsCount();
            /*
            用户最大可抵扣的积分，取商品金额和（useIntegralValue * goodsCount）两者的较小值，
            因为经过前面的优惠，有可能剩余商品金额已经小于商品设置的积分抵扣最大值
            */
            int maxIntegralValue = Math.min(goodPoints, payPriceToInt);
            //用户最终需要支付的积分，为商品最大抵扣积分、账户积分、麦座单次积分支付上限三者的最小值
            integralValue = Math.min(Math.min(maxIntegralValue, accountPoint), pointLimitPay);
        }
        //积分抵扣金额
        BigDecimal integralCut = BigDecimal.valueOf(integralValue).divide(MONEY_TO_POINTS_SCALE,MallConst.DECIMAL_PLACES,RoundingMode.HALF_UP);
        orderAmount.setPayAmount(orderAmount.getPayAmount().subtract(integralCut));
        orderAmount.setIntegralValue(integralValue);
        orderAmount.setIntegralCut(integralCut);
    }

    public void calculateBalanceDiscount(){
        //运费可以用余额支付
        BigDecimal payAmount = orderAmount.getPayAmount().add(orderAmount.getShipAmount());
        if (shoppingGoods.getUseBalanceSet()==1 && productDto.getUseBalance() == 1) {
            //余额支付上限取当前账户余额和麦座余额单次支付限额两者的较小值
            BigDecimal balanceLimit = BigDecimal.ZERO;
            if (payLimit.getBalancePay() > 0) {
                //余额单次支付上限
                BigDecimal balancePay =BigDecimal.valueOf(payLimit.getBalancePay());
                balanceLimit = balancePay.compareTo(userAccountInfo.getAccountMoney()) < 0 ? balancePay : userAccountInfo.getAccountMoney();
            }
            BigDecimal balanceCut = payAmount.compareTo(balanceLimit) < 0 ? payAmount : balanceLimit;
            orderAmount.setPayAmount(orderAmount.getPayAmount().subtract(balanceCut));
            orderAmount.setBalanceCut(balanceCut);
        }
    }

    /**
     * 处理定额积分
     */
    public void calculateFixedIntegral(){
        //定额积分参与折扣类优惠，但不参与抵扣类优惠，因此要提前把定额抵扣积分拿出来，在获取可用优惠券以及计算折扣类优惠时进行处理
        fixedIntegralValue = shoppingGoods.getUseIntegralSet() == 1 ? (shoppingGoods.getUseIntegralValue() * productDto.getGoodsCount()) : 0;
        fixedValue =BigDecimal.valueOf(fixedIntegralValue);
        scaledFixedValue =fixedValue.divide(MONEY_TO_POINTS_SCALE,MallConst.DECIMAL_PLACES, RoundingMode.HALF_UP);
    }

    /**
     * 获取商品订单可用优惠券列表
     *
     */
    protected void getGoodsOrderCouponList(){
        HashMap<String, Object> reqMap = new HashMap<>();
        reqMap.put("goodsId",shoppingGoods.getId());
        reqMap.put("gcId",shoppingGoods.getGcId());
        reqMap.put("goodsType", SellTypeEnum.CULTURAL.getGoodsType());
        List<ShoppingCouponPo> coupons = shoppingCouponDao.selectUserGoodsCouponList(reqMap);
        //商品金额减去定额积分抵扣后的金额作为计算代金券型优惠券的基准
        BigDecimal payPrice= orderAmount.getGoodsAmount().subtract(scaledFixedValue);
        Map<String,ShoppingCouponPo> couponPoMap=new HashMap<>(coupons.size());
        for(ShoppingCouponPo shoppingCoupon:coupons){
            try{
                //还未到优惠券可使用时间
                if(shoppingCoupon.getStart_Date()!=null&& DateUtil.date().after(DateUtil.parse(shoppingCoupon.getStart_Date()))){
                    continue;
                }
                //已经过期
                if(shoppingCoupon.getEnd_Date()!=null&&DateUtil.offsetDay(DateUtil.date(),1).before(DateUtil.parse(shoppingCoupon.getEnd_Date()))){
                    continue;
                }
            }catch (Exception e){
                continue;
            }
            //代金券，在现金的基础上判断满减额度
            if(shoppingCoupon.getRight_Type().equals(MallConst.COUPON_TYPE_VOUCHER)){
                if(shoppingCoupon.getMax_Money()!=null&&shoppingCoupon.getMax_Money()!=0&&BigDecimal.valueOf(shoppingCoupon.getMax_Money()).compareTo(payPrice)<0){
                    continue;
                }else if(shoppingCoupon.getMin_Money()!=null&&shoppingCoupon.getMin_Money()!=0&&BigDecimal.valueOf(shoppingCoupon.getMin_Money()).compareTo(payPrice)>0){
                    continue;
                }
            }
            //折扣券，在原价的基础上判断满减额度
            if(shoppingCoupon.getRight_Type().equals(MallConst.COUPON_TYPE_DISCOUNT)){
                if(shoppingCoupon.getMax_Money()!=null&&shoppingCoupon.getMax_Money()!=0&&BigDecimal.valueOf(shoppingCoupon.getMax_Money()).compareTo(orderAmount.getPayAmount())<0){
                    continue;
                }else if(shoppingCoupon.getMin_Money()!=null&&shoppingCoupon.getMin_Money()!=0&&BigDecimal.valueOf(shoppingCoupon.getMin_Money()).compareTo(orderAmount.getGoodsAmount())>0){
                    continue;
                }
            }
            couponPoMap.put(shoppingCoupon.getRight_No(),shoppingCoupon);
        }

        JSONArray objList = new JSONArray();
        JSONArray resArray = crmService.getUserCouponList(productDto.getUserId(),userInfo.getMobile(),"0");
        if(null !=resArray){
            for (int i = 0; i < resArray.size(); i++) {
                JSONObject obj =  resArray.getJSONObject(i);
                String rightNo =  obj.get("right_No").toString();
                if(couponPoMap.containsKey(rightNo)){
                    obj.put("detail",couponPoMap.get(rightNo));
                    objList.add(obj);
                }
            }
        }
        couponList=objList;
    }
}
