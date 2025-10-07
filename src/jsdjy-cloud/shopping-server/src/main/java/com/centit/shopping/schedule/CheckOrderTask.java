package com.centit.shopping.schedule;

import com.centit.shopping.common.enums.Const;
import com.centit.shopping.dao.*;
import com.centit.shopping.po.*;
import com.centit.shopping.redis.RedisStockService;
import com.centit.shopping.utils.CommonInit;
import com.centit.shopping.utils.CommonUtil;
import com.centit.shopping.utils.StringUtil;
//import net.javacrumbs.shedlock.core.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

/**
 * @author ：cui_jian
 * @version ：1.0
 * @date ：Created in 2020/8/25 14:25
 * @description ：关闭超时未支付的订单
 */
@Component
public class CheckOrderTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(CheckOrderTask.class);

    @Resource
    private ShoppingOrderformDao shoppingOrderformDao;

    @Resource
    private ShoppingCouponUsertempDao shoppingCouponUsertempDao;

    @Resource
    private ShoppingRechargeDao shoppingRechargeDao;

    @Resource
    private ShoppingGoodsInventoryDao shoppingGoodsInventoryDao;

    @Resource
    private ShoppingGoodsDao shoppingGoodsDao;
    @Resource
    private ShoppingOrderLogDao shoppingOrderLogDao;

    @Resource
    private ShoppingIntegralRecordDao shoppingIntegralRecordDao;

    @Resource
    private ShoppingIntegralTotalDao shoppingIntegralTotalDao;

    @Resource
    private ShoppingAssetDao shoppingAssetDao;
    @Resource
    private ShoppingArtactivityDao shoppingArtactivityDao;

    @Resource
    private ShoppingArtplanDao shoppingArtplanDao;
    @Resource
    private ShoppingArtactivityInventoryDao shoppingArtactivityInventoryDao;
    @Resource
    private ShoppingArtplanInventoryDao shoppingArtplanInventoryDao;
    /**
     * 配置库存Redis缓存Key前缀
     */
    public static final String REDIS_KEY = "REDIS_KEY:STOCK:";
    public static final String REDIS_KEY_GOODS = "REDIS_KEY:STOCK:GOODS";
    public static final String REDIS_KEY_PLAN = "REDIS_KEY:STOCK:PLAN";

//    public static final String REDIS_KEY_ACT = "ACT";
//    public static final String REDIS_KEY_PLAN = "PLAN";
//    public static final String REDIS_KEY_GOODS = "GOODS";
    @Resource
    private RedisStockService redisStockService;


    @Value("${sheduleSwitch.master}")
    private Boolean masterSwitch;

    @Value("${sheduleSwitch.checkOrder}")
    private Boolean checkOrderSwitch;

    @Value("${order.orderState.toPay}")
    private int orderStateToPay;

    @Value("${order.orderState.hasSend}")
    private int orderStateHasSend;

    @Value("${order.orderState.hasDone}")
    private int orderStateHasDone;

    @Value("${orderCancelMinutes}")
    private int orderCancelMinutes;

    @Value("${orderReceiveDays}")
    private int orderReceiveDays;

    @Value("${order.orderState.cancel}")
    private int orderStateCancel;

    @Scheduled(cron = "${checkOrderCorn}")
//    @SchedulerLock(name = "CheckOrderTask",
//            lockAtMostFor = 10 * 60 * 1000, lockAtLeastFor = 2 * 60 * 1000)
    public void scheduledCronDemo() {
        //定时任务开关
        if (masterSwitch && checkOrderSwitch) {
            cancelOrder();
            confimOrder();
        }

    }

    public void cancelOrder(){
        LOGGER.info("扫描未支付订单-定时任务开始");
        try{
            HashMap<String, Object> reqMap =new HashMap<>();
            reqMap.put("orderStatus", orderStateToPay);   //未支付订单
            reqMap.put("deleteStatus","0");
            List<ShoppingOrderform> orderList =shoppingOrderformDao.queryList(reqMap);
            for(ShoppingOrderform shoppingOrderform:orderList){
                String addTime = shoppingOrderform.getAddTime();

                //演出票订单（已调整为不调麦座接口取消订单）
                if(shoppingOrderform.getOrderId().startsWith(Const.TICKET_ORDER)){
                    if(StringUtil.differentMinutesByMillisecond(addTime)>orderCancelMinutes){
                        //自动取消订单

                        String reason = "超时未支付自动取消";
                        String userId = shoppingOrderform.getUserId();
                        String mzUserId = CommonUtil.getMzUserId(userId);

                        // 添加订单日志
                        ShoppingOrderLog shoppingOrderLog = new ShoppingOrderLog();
                        shoppingOrderLog.setLogInfo("取消订单");
                        shoppingOrderLog.setStateInfo(reason);
                        shoppingOrderLog.setOfId(shoppingOrderform.getId());
                        shoppingOrderLogDao.insert(shoppingOrderLog);

                        // 取消订单
                        shoppingOrderformDao.cancelOrder(shoppingOrderform);

                    }

                }else{
                    //商城订单超时时间改为20分钟
                    if(StringUtil.differentMinutesByMillisecond(addTime)>15){
                        //自动取消订单

                        String reason = "超时未支付自动取消";
                        String userId = shoppingOrderform.getUserId();
                        String mzUserId = CommonUtil.getMzUserId(userId);

                        //如果订单使用了优惠券，需要解除优惠券的绑定状态
                        if (null != shoppingOrderform.getCiId()) {
                            ShoppingCouponUsertemp shoppingCouponUsertemp = new ShoppingCouponUsertemp();
                            shoppingCouponUsertemp.setUserId(shoppingOrderform.getUserId());
                            shoppingCouponUsertemp.setCouponId(shoppingOrderform.getCiId());
                            shoppingCouponUsertempDao.delete(shoppingCouponUsertemp);
                        }

                        //如果是充值订单，需要同时关闭充值记录表中的记录
                        if(shoppingOrderform.getOrderId().startsWith(Const.RECHARGE_ORDER)){
                            reqMap.clear();
                            reqMap.put("status",2);  //交易关闭
                            reqMap.put("ofId",shoppingOrderform.getId());
                            reqMap.put("userId",shoppingOrderform.getUserId());
                            shoppingRechargeDao.setRechargeStatus(reqMap);
                        }

                        //更新商品库存
                        //获取订单关联物品
                        reqMap.clear();
                        reqMap.put("ofId",shoppingOrderform.getId());
                        reqMap.put("deleteStatus","0");
                        List<ShoppingGoodscart> cartGoods = CommonInit.staticShoppingGoodscartDao.queryList(reqMap);
                        for(ShoppingGoodscart shoppingGoodscart:cartGoods){
                            if(shoppingGoodscart.getCartType()!=null&&(shoppingGoodscart.getCartType().equals(Const.SHOPPING_CUL_CART_TYPE)||shoppingGoodscart.getCartType().equals(Const.SHOPPING_INT_CART_TYPE))){
                                ShoppingGoods shoppingGoods = new ShoppingGoods();
                                shoppingGoods.setId(shoppingGoodscart.getGoodsId());
                                //商品主体信息
                                shoppingGoods = shoppingGoodsDao.queryDetail(shoppingGoods);

                                if(shoppingGoodscart.getPropertys()!=null&&!shoppingGoodscart.getPropertys().equals("")){
                                    ShoppingGoodsInventory shoppingGoodsInventory = new ShoppingGoodsInventory();
                                    shoppingGoodsInventory.setGoodsId(shoppingGoodscart.getGoodsId());
                                    shoppingGoodsInventory.setPropertys(shoppingGoodscart.getPropertys());

                                    shoppingGoodsInventory = shoppingGoodsInventoryDao.queryDetail(shoppingGoodsInventory);
                                    int inventoryCount = shoppingGoodsInventory.getCount();
                                    shoppingGoodsInventory.setCount(inventoryCount + shoppingGoodscart.getCount());
                                    shoppingGoodsInventoryDao.update(shoppingGoodsInventory);

                                    String key = REDIS_KEY_GOODS + shoppingGoods.getId()+shoppingGoodscart.getPropertys();
                                    int currentStock= redisStockService.currentStock(key);
                                    redisStockService.initStock(key,currentStock+shoppingGoodscart.getCount());
                                }

                                shoppingGoods.setGoodsInventory(shoppingGoods.getGoodsInventory() + shoppingGoodscart.getCount());
                                shoppingGoodsDao.updateGoodsInventory(shoppingGoods);
                                String key = REDIS_KEY_GOODS + shoppingGoods.getId();
                                int currentStock= redisStockService.currentStock(key);
                                redisStockService.initStock(key,currentStock+shoppingGoodscart.getCount());
                            }
                            if(shoppingGoodscart.getCartType()!=null&&(shoppingGoodscart.getCartType().equals(Const.SHOPPING_ACT_CART_TYPE))){
                                ShoppingArtactivity shoppingArtactivity = new ShoppingArtactivity();
                                shoppingArtactivity.setId(shoppingGoodscart.getGoodsId());
                                //活动主体信息
                                shoppingArtactivity = shoppingArtactivityDao.queryDetail(shoppingArtactivity);
                                shoppingArtactivity.setLeftnum(shoppingArtactivity.getLeftnum() + shoppingGoodscart.getCount());
                                shoppingArtactivityDao.updateActivityNum(shoppingArtactivity);
                                String key = REDIS_KEY + shoppingArtactivity.getId();
                                int currentStock= redisStockService.currentStock(key);
                                redisStockService.initStock(key,currentStock+shoppingGoodscart.getCount());

                                if(shoppingGoodscart.getPropertys()!=null&&!shoppingGoodscart.getPropertys().equals("")){
                                    ShoppingArtactivityInventory shoppingArtactivityInventory = new ShoppingArtactivityInventory();
                                    shoppingArtactivityInventory.setActivityId(shoppingArtactivity.getId());
                                    shoppingArtactivityInventory.setPropertys(shoppingGoodscart.getPropertys());
                                    shoppingArtactivityInventory = shoppingArtactivityInventoryDao.queryDetail(shoppingArtactivityInventory);
                                    int inventoryCount = shoppingArtactivityInventory.getCount();
                                    shoppingArtactivityInventory.setCount(inventoryCount + shoppingGoodscart.getCount());
                                    shoppingArtactivityInventoryDao.update(shoppingArtactivityInventory);

                                    key = REDIS_KEY + shoppingArtactivity.getId()+shoppingGoodscart.getPropertys();
                                    currentStock= redisStockService.currentStock(key);
                                    redisStockService.initStock(key,currentStock+shoppingGoodscart.getCount());
                                }
                            }
                            if(shoppingGoodscart.getCartType()!=null&&(shoppingGoodscart.getCartType().equals(Const.SHOPPING_PLAN_CART_TYPE))){
                                ShoppingArtplan shoppingArtplan = new ShoppingArtplan();
                                shoppingArtplan.setId(shoppingGoodscart.getGoodsId());
                                //爱艺计划主体信息
                                shoppingArtplan = shoppingArtplanDao.queryDetail(shoppingArtplan);
                                shoppingArtplan.setLeftnum(shoppingArtplan.getLeftnum() + shoppingGoodscart.getCount());
                                shoppingArtplanDao.updatePlanNum(shoppingArtplan);

                                String key = REDIS_KEY_PLAN + shoppingArtplan.getId();
                                int currentStock= redisStockService.currentStock(key);
                                redisStockService.initStock(key,currentStock+shoppingGoodscart.getCount());

                                if(shoppingGoodscart.getPropertys()!=null&&!shoppingGoodscart.getPropertys().equals("")){
                                    ShoppingArtplanInventory shoppingArtplanInventory = new ShoppingArtplanInventory();
                                    shoppingArtplanInventory.setActivityId(shoppingArtplan.getId());
                                    shoppingArtplanInventory.setPropertys(shoppingGoodscart.getPropertys());
                                    shoppingArtplanInventory = shoppingArtplanInventoryDao.queryDetail(shoppingArtplanInventory);
                                    int inventoryCount = shoppingArtplanInventory.getCount();
                                    shoppingArtplanInventory.setCount(inventoryCount + shoppingGoodscart.getCount());
                                    shoppingArtplanInventoryDao.update(shoppingArtplanInventory);

                                    key = REDIS_KEY_PLAN + shoppingArtplan.getId()+shoppingGoodscart.getPropertys();
                                    currentStock= redisStockService.currentStock(key);
                                    redisStockService.initStock(key,currentStock+shoppingGoodscart.getCount());
                                }
                            }


                        }


                        // 添加订单日志
                        ShoppingOrderLog shoppingOrderLog = new ShoppingOrderLog();
                        shoppingOrderLog.setLogInfo("取消订单");
                        shoppingOrderLog.setStateInfo(reason);
//                        shoppingOrderLog.setLogUserId(userId);
                        shoppingOrderLog.setOfId(shoppingOrderform.getId());
                        shoppingOrderLogDao.insert(shoppingOrderLog);

                        // 取消订单
                        shoppingOrderform.setOrderStatus(orderStateCancel);
                        shoppingOrderformDao.cancelOrder(shoppingOrderform);

                    }

                }
            }
            LOGGER.info("扫描未支付订单-定时任务结束");
        }catch (Exception e){
            e.printStackTrace();
            LOGGER.error("扫描未支付订单-定时任务出现异常");
        }
    }

    public void confimOrder(){
        LOGGER.info("扫描未确认收货订单-定时任务开始");
        try{
            HashMap<String, Object> reqMap =new HashMap<>();
            reqMap.put("orderStatus", orderStateHasSend);   //已发货订单
            reqMap.put("deleteStatus","0");
            List<ShoppingOrderform> orderList =shoppingOrderformDao.queryList(reqMap);
            for(ShoppingOrderform shoppingOrderform:orderList){
                String shipTime = shoppingOrderform.getShipTime();
                if(Math.abs(StringUtil.differentDaysByMillisecond(shipTime))>orderReceiveDays){
                    //自动确认收货
                    String reason = "系统自动确认收货";
                    String userId = shoppingOrderform.getUserId();

                    // 添加订单日志
                    ShoppingOrderLog shoppingOrderLog = new ShoppingOrderLog();
                    shoppingOrderLog.setLogInfo("系统自动确认收货");
                    shoppingOrderLog.setStateInfo(reason);
//                        shoppingOrderLog.setLogUserId(userId);
                    shoppingOrderLog.setOfId(shoppingOrderform.getId());
                    shoppingOrderLogDao.insert(shoppingOrderLog);

                    // 确认收货
                    // 修改订单状态为完成
                    shoppingOrderform.setOrderStatus(orderStateHasDone);
                    shoppingOrderformDao.confimOrder(shoppingOrderform);

                    if(CommonUtil.getShoppingIntegralSwitch()){
                        //如果赠送积分的开关打开了，则需要根据实际支付的金额赠送积分
                        BigDecimal payPrice = shoppingOrderform.getPayPrice();
                        int integralValue = payPrice.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();  //按付款金额1:1赠送积分

                        if(integralValue>0){
                            List<ShoppingIntegralTotal> totalList = shoppingIntegralTotalDao.queryList(new HashMap<>());
                            int total = totalList.isEmpty() ? 0 : totalList.get(0).getDailyTotalIntegral();
                            //获取当天已赠送的积分总数
                            String dayStr = StringUtil.nowDateString();
                            reqMap = new HashMap<>();
                            reqMap.put("dayStr", dayStr);
                            reqMap.put("userId",userId);
                            int dailySum = shoppingIntegralRecordDao.queryDailySum(reqMap);
                            if (dailySum < total||total==0) {
                                if (total>0&&dailySum + integralValue > total) {
                                    integralValue = total - dailySum;
                                }

                                ShoppingAsset shoppingAsset = new ShoppingAsset();
                                shoppingAsset.setUserId(userId);
                                shoppingAsset = shoppingAssetDao.queryDetail(shoppingAsset);
                                if (shoppingAsset == null) {
                                    shoppingAsset = new ShoppingAsset();
                                    shoppingAsset.setUserId(userId);
                                    shoppingAsset.setIntegralValue(integralValue);
                                    shoppingAssetDao.insert(shoppingAsset);
                                } else {
                                    shoppingAsset.setIntegralValue(shoppingAsset.getIntegralValue() + integralValue);
                                    shoppingAssetDao.update(shoppingAsset);
                                }

                                //用户积分新增记录
                                ShoppingIntegralRecord shoppingIntegralRecord = new ShoppingIntegralRecord();
                                shoppingIntegralRecord.setUserId(userId);
                                shoppingIntegralRecord.setIntegralCount(integralValue);
                                shoppingIntegralRecord.setRemark("购买文创商品");
                                shoppingIntegralRecordDao.insert(shoppingIntegralRecord);
                            }
                        }

                    }

                }
            }
            LOGGER.info("扫描未确认收货订单-定时任务结束");
        }catch (Exception e){
            e.printStackTrace();
            LOGGER.error("扫描未确认收货订单-定时任务出现异常");
        }
    }

    public static void main(String[] args) throws ParseException {
        int total = 7011;
        int num = 100;
        System.out.println(total / num);
    }

}