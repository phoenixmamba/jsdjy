package com.centit.pay.validate;

import com.centit.core.consts.RedisConst;
import com.centit.core.dto.SmsEntity;
import com.centit.core.model.OrderPayInfo;
import com.centit.pay.biz.dao.ShoppingOrderPayDao;
import com.centit.pay.biz.dao.ShoppingOrderPaylogDao;
import com.centit.pay.biz.dao.ShoppingOrderformDao;
import com.centit.pay.biz.dao.ShoppingPaymentDao;
import com.centit.pay.biz.po.ShoppingOrderPay;
import com.centit.pay.biz.po.ShoppingOrderPaylog;
import com.centit.pay.biz.po.ShoppingOrderform;
import com.centit.pay.biz.po.ShoppingPayment;
import com.centit.pay.common.enums.Const;
import com.centit.pay.exp.PaymentException;
import com.centit.pay.feign.FeignOrderService;
import com.centit.pay.feign.FeignThirdService;
import com.centit.pay.threadPool.ThreadPoolExecutorFactory;
import com.centit.pay.utils.CommonUtil;
import com.centit.pay.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2025/9/2 9:16
 **/
@Service
@Slf4j
public class PayHandleServiceImpl implements PayHandleService {
    @Resource
    private ShoppingOrderformDao shoppingOrderformDao;
    @Resource
    private ShoppingOrderPaylogDao shoppingOrderPaylogDao;
    @Resource
    private ShoppingPaymentDao shoppingPaymentDao;
    @Resource
    private ShoppingOrderPayDao shoppingOrderPayDao;
    @Resource
    private RedissonClient redissonClient;

    @Resource
    private OrderPayStatusService orderPayStatusService;
    @Resource
    private FeignThirdService feignThirdService;
    @Resource
    private FeignOrderService feignOrderService;

    /**
     * Redis获取锁等待时间，单位：秒
     */
    private static final int LOCK_WAIT_SECONDS = 5;

    /**
     * Redis锁自动释放时间，单位：秒
     */
    private static final int LOCK_LEASE_SECONDS = 10;
    private static final int MAX_RETRY_COUNT = 3;

    private static final String SMS_URL ="http://api.jsmsxx.com:8030/service/httpService/httpInterface.do";

    private static final String SMS_WARNING_MOBILE = "13776407246";
    @Override
    public void savePayCallBackLog(ShoppingOrderPaylog shoppingOrderPaylog) {
        shoppingOrderPaylogDao.insert(shoppingOrderPaylog);
    }

    @Override
    public ShoppingPayment getPayment(String payId) {
        return shoppingPaymentDao.queryDetailById(payId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void processPayment(PayCallBackInfo payCallBackInfo) {
        String orderId = payCallBackInfo.getOrderId();
        log.info("处理订单{}的支付回调信息", orderId);
        RLock rLock = null;
        boolean isLocked = false;
        try {
            rLock = redissonClient.getLock("payment:callback:LOCK:" + orderId);
            isLocked=rLock.tryLock(RedisConst.LOCK_WAIT_SECONDS, LOCK_LEASE_SECONDS, TimeUnit.SECONDS);
            if (isLocked) {
                ShoppingOrderform orderForm = shoppingOrderformDao.queryDetailByOrderId(orderId);
                //验证金额
                validateMoney(orderForm, payCallBackInfo);

                ShoppingOrderPay shoppingOrderPay = new ShoppingOrderPay();
                shoppingOrderPay.setOfId(orderForm.getId());
                shoppingOrderPay = shoppingOrderPayDao.queryDetail(shoppingOrderPay);
                //标识现金支付完成
                shoppingOrderPay.setCashStatus(1);
                //第三方支付的订单标识
                shoppingOrderPay.setOutTradeNo(payCallBackInfo.getTransactionId());
                //只要金额验证无误，数据库就要标识订单的现金支付已完成
                orderPayStatusService.updateOrderPayStatus(shoppingOrderPay);
                //订单付款时间
                orderForm.setPayTime(StringUtil.nowTimeString());
                //验证订单状态
                validateOrderStatus(orderForm,payCallBackInfo);
                orderForm.setPaymentId(payCallBackInfo.getPayType());
                //更新订单状态为支付中
                orderForm.setOrderStatus(Const.ORDER_STATE_INPAY);
                shoppingOrderformDao.update(orderForm);
                OrderPayInfo orderPayInfo = buildOrderPayInfo(orderForm, payCallBackInfo);
                if (orderForm.getOrderId().startsWith(Const.TICKET_ORDER_PREFIX)) {
                    //调用麦座订单确认接口
                    try{
                        orderPayStatusService.confirmMzOrder(orderPayInfo, shoppingOrderPay);
                    }catch (Exception e){
                        log.error("订单{}向麦座确认订单失败，请检查订单信息", orderId, e);
                        setOrderAbnormal(orderForm,"向麦座确认订单失败");
                        throw e;
                    }
                    //演出票订单与麦座确认成功，即可直接调用订单服务，更新订单状态状态为已付款
                    feignOrderService.updateOrderStatusToPaid(orderId);
                }else{
                    //非演出票订单的余额、积分、优惠券的核销，需要本地处理
                    try{
                        orderPayStatusService.costBalance(orderPayInfo, shoppingOrderPay);
                        orderPayStatusService.costIntegral(orderPayInfo, shoppingOrderPay);
                        orderForm.setCouponId(orderPayStatusService.costCoupon(orderPayInfo, shoppingOrderPay));
                    }catch (Exception e){
                        log.error("订单{}处理第三方核销失败，请检查订单信息", orderId, e);
                        setOrderAbnormal(orderForm,"处理第三方核销失败");
                        throw new PaymentException("处理第三方核销失败");
                    }

                    // 调用订单服务，更新订单状态为已付款，执行后续操作
                    feignOrderService.updateOrderStatusToPaid(orderId);
                    //某些类型订单需要给商户发短信
                    sendMsgForOrder(orderForm);
                }
            } else {
                log.info("订单{}正在处理中，获取redis锁失败", orderId);
                throw new PaymentException("获取订单锁超时");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            if (rLock != null && isLocked) {
                try {
                    if (rLock.isHeldByCurrentThread()) {
                        rLock.unlock();
                    }
                } catch (Exception e) {
                    log.warn("释放Redis锁时发生异常，订单ID: {}", orderId, e);
                }
            }
        }
    }

    @Override
    public Void retryProcess(String orderId, PayCallBackInfo payCallBackInfo, int retryCount, Throwable ex) {
        if (retryCount > MAX_RETRY_COUNT) {
            log.error("订单{}超过最大重试次数({}次)，请人工介入处理", orderId, MAX_RETRY_COUNT, ex);
            // 达到最大重试次数后，才设置订单为异常状态，并记录具体原因
            ShoppingOrderform orderForm = shoppingOrderformDao.queryDetailByOrderId(orderId);
            if (orderForm != null) {
                String errorMsg = ex != null ? ex.getMessage() : "未知错误";
                setOrderAbnormal(orderForm, "超过最大重试次数: " + errorMsg);
            }
            return null;
        }

        // 计算重试延迟（指数退避策略）
        long delay = (long) Math.pow(2, retryCount) * 1000; // 1s, 2s, 4s...
        log.info("订单{}第{}次重试将在{}ms后执行", orderId, retryCount, delay);
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        CompletableFuture.runAsync(() -> {
            try {
                processPayment(payCallBackInfo);
                log.info("订单{}第{}次重试成功", orderId, retryCount);
            } catch (Exception e) {
                log.error("订单{}第{}次重试失败", orderId, retryCount, e);
                retryProcess(orderId, payCallBackInfo, retryCount + 1, e);
            }
        }, ThreadPoolExecutorFactory.createThreadPoolExecutor());
        return null;
    }

    /**
     * 校验金额
     *
     * @param orderForm
     * @param payCallBackInfo
     * @return
     */
    public void validateMoney(ShoppingOrderform orderForm, PayCallBackInfo payCallBackInfo) {
        if (orderForm.getPayPrice().compareTo(payCallBackInfo.getOrderPayMoney()) != 0) {
            log.error("订单{}支付金额不一致，请检查订单信息", orderForm.getOrderId());
            setOrderAbnormal(orderForm,"订单金额不一致");
            throw new PaymentException("订单金额不一致");
        }
    }

    /**
     * 校验订单状态
     *
     * @param orderForm
     * @return
     */
    public void validateOrderStatus(ShoppingOrderform orderForm,PayCallBackInfo payCallBackInfo){
        if (orderForm.getOrderStatus() == Const.ORDER_STATE_TOPAY||(orderForm.getOrderId().startsWith(Const.TICKET_ORDER_PREFIX)&&orderForm.getOrderStatus() != Const.ORDER_STATE_HASDONE)){
            return;
        }
        if(orderForm.getOrderStatus() == Const.ORDER_STATE_CALCEL){
            log.error("订单{}已取消，请检查订单信息", orderForm.getOrderId());
            setOrderAbnormal(orderForm,"订单已取消");
            throw new PaymentException("订单已取消");
        }
        if(StringUtils.isNotBlank(orderForm.getPaymentId())&&!orderForm.getPaymentId().equals(payCallBackInfo.getPayType())){
            log.error("订单{}已使用其他支付方式支付，请检查订单信息", orderForm.getOrderId());
            setOrderAbnormal(orderForm,"已使用其他支付方式支付");
            throw new PaymentException("订单已使用其他支付方式支付");
        }

        log.info("订单{}当前状态为{}，无需处理支付信息", orderForm.getOrderId(),orderForm.getOrderStatus());
        throw new PaymentException("订单状态异常");
    }

    public void setOrderAbnormal(ShoppingOrderform orderForm, String msg) {
        // 记录详细的异常信息到数据库
        orderForm.setMsg(msg);
        orderForm.setOrderStatus(Const.ORDER_STATE_ANOMALOUS);
        shoppingOrderformDao.updateStatusWithMsg(orderForm);

        // 异步通知订单服务和发送短信
        CompletableFuture.runAsync(() -> {
            try {
                feignOrderService.updateOrderStatusToAbnormal(orderForm.getOrderId(), msg);
            } catch (Exception e) {
                log.error("调用订单模块设置异常状态失败，订单ID: {}", orderForm.getOrderId(), e);
            }
        }, ThreadPoolExecutorFactory.createThreadPoolExecutor());

        // 发送短信通知
        CompletableFuture.runAsync(
                () -> sendMsg(SMS_WARNING_MOBILE, "订单" + orderForm.getOrderId() + "异常:" + msg + "，请尽快处理"),
                ThreadPoolExecutorFactory.createThreadPoolExecutor()
        );
    }

    public void sendMsgForOrder(ShoppingOrderform orderForm){
        //Todo 给商户发通知短信
    }

    public OrderPayInfo buildOrderPayInfo(ShoppingOrderform orderForm,PayCallBackInfo payCallBackInfo){
        OrderPayInfo orderPayInfo = new OrderPayInfo();
        orderPayInfo.setOfId(orderForm.getId());
        orderPayInfo.setOrderId(orderForm.getOrderId());
        orderPayInfo.setPaymentId(orderForm.getPaymentId());
        orderPayInfo.setMzPaymentId(payCallBackInfo.getMzPayType());
        orderPayInfo.setOutTradeNo(payCallBackInfo.getTransactionId());
        orderPayInfo.setPayPrice(orderForm.getPayPrice());
        orderPayInfo.setTotalPrice(orderForm.getTotalPrice());
        orderPayInfo.setOrderTolPrice(orderForm.getOrderTolPrice());
        orderPayInfo.setDeductionCouponPrice(orderForm.getDeductionCouponPrice());
        orderPayInfo.setDeductionBalancePrice(orderForm.getDeductionBalancePrice());
        orderPayInfo.setDeductionMemberPrice(orderForm.getDeductionMemberPrice());
        orderPayInfo.setDeductionIntegralPrice(orderForm.getDeductionIntegralPrice());
        orderPayInfo.setDeductionIntegral(orderForm.getDeductionIntegral());
        orderPayInfo.setMzUserId(CommonUtil.getMzUserId(orderForm.getUserId()));
        orderPayInfo.setCouponId(orderForm.getCiId());
        return orderPayInfo;
    }

    public void sendMsg(String mobile,String content){
        SmsEntity smsEntity = new SmsEntity();
        smsEntity.setMobile(mobile);
        smsEntity.setContent("@1@="+content);
        smsEntity.setTempId("JSM41400-0096");
        feignThirdService.sendTemplateSMS(smsEntity);
    }

}
