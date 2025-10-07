package com.centit.pay.validate;

import com.centit.pay.biz.dao.ShoppingOrderformDao;
import com.centit.pay.biz.po.ShoppingOrderPaylog;
import com.centit.pay.biz.po.ShoppingOrderform;
import com.centit.pay.threadPool.ThreadPoolExecutorFactory;
import com.centit.pay.utils.PayUtil;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2025/9/9 14:21
 **/
@Slf4j
public abstract class AbstractPayCallBackService {

    @Resource
    protected PayHandleService payHandleService;

    @Resource
    protected ShoppingOrderformDao shoppingOrderformDao;

    @Resource
    protected OrderPayStatusService orderPayStatusService;

    protected static final int MAX_RETRY_COUNT = 3;

    /**
     * 处理支付回调的核心逻辑
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param payType 支付类型
     * @param payChannel 支付渠道
     * @return 响应结果
     */
    protected String handlePayCallBack(HttpServletRequest request, HttpServletResponse response,
                                       String payType, String payChannel) {
        ShoppingOrderPaylog shoppingOrderPaylog = new ShoppingOrderPaylog();
        shoppingOrderPaylog.setTradeType(getPayChannelName());
        shoppingOrderPaylog.setIp(PayUtil.getIpAddr(request));
        shoppingOrderPaylog.setLogInfo(getPayChannelName()+"回调通知");
        Map<String, String> paramMap;
        try {
            paramMap = parseRequest(request, response);
        } catch (Exception e) {
            log.error("{}回调通知报文解析失败", getPayChannelName(), e);
            shoppingOrderPaylog.setStateInfo(getPayChannelName()+"回调报文解析失败");
            insertPayLog(shoppingOrderPaylog);
            return getParseFailResponse();
        }
        shoppingOrderPaylog.setLogContent(paramMap.toString());
        // 签名校验
        if (!validateSign(paramMap)) {
            log.info("{}回调通知签名校验失败，paramMap:{}", getPayChannelName(), paramMap);
            shoppingOrderPaylog.setStateInfo(getPayChannelName()+"签名校验失败");
            insertPayLog(shoppingOrderPaylog);
            return getSignValidateFailResponse();
        }
        String orderId = getOrderIdFromParams(paramMap);
        ShoppingOrderform orderform = shoppingOrderformDao.queryDetailByOrderId(orderId);
        if (orderform == null) {
            log.info("{}回调通知报文校验失败，订单信息不存在,订单号：{}", getPayChannelName(), orderId);
            shoppingOrderPaylog.setStateInfo(getPayChannelName()+"报文校验失败，订单信息不存在");
            insertPayLog(shoppingOrderPaylog);
            return getOrderNotExistResponse();
        }
        shoppingOrderPaylog.setOfId(orderform.getId());
        // 验证支付状态
        if (!isPaymentSuccess(paramMap)) {
            String errorMsg = getPaymentFailMessage(paramMap);
            log.info("{}回调通知支付失败，订单号:{}，错误信息:{}", getPayChannelName(), orderId, errorMsg);
            shoppingOrderPaylog.setStateInfo(getPayChannelName()+"支付失败");
            insertPayLog(shoppingOrderPaylog);
            return getSuccessResponse();
        }

        // 构建支付回调信息
        PayCallBackInfo payCallBackInfo = buildPayCallBackInfo(paramMap, orderId, payType, payChannel);

        // 异步处理支付
        CompletableFuture.runAsync(() -> payHandleService.processPayment(payCallBackInfo),
                        ThreadPoolExecutorFactory.createThreadPoolExecutor())
                .exceptionally(ex -> {
                    log.error("订单{}首次处理失败，准备重试", orderId, ex);
                    return payHandleService.retryProcess(orderId, payCallBackInfo, 1, ex);
                });


        shoppingOrderPaylog.setOutTradeNo(payCallBackInfo.getTransactionId());
        shoppingOrderPaylog.setStateInfo(getPayChannelName()+"支付成功");
        insertPayLog(shoppingOrderPaylog);
        return getSuccessResponse();
    }

    /**
     * 解析请求参数
     */
    protected abstract Map<String, String> parseRequest(HttpServletRequest request, HttpServletResponse response) throws Exception;

    /**
     * 验证签名
     */
    protected abstract boolean validateSign(Map<String, String> paramMap);

    /**
     * 从参数中获取订单ID
     */
    protected abstract String getOrderIdFromParams(Map<String, String> paramMap);

    /**
     * 判断支付是否成功
     */
    protected abstract boolean isPaymentSuccess(Map<String, String> paramMap);

    /**
     * 获取支付失败的错误信息
     */
    protected abstract String getPaymentFailMessage(Map<String, String> paramMap);

    /**
     * 构建支付回调信息
     */
    protected abstract PayCallBackInfo buildPayCallBackInfo(Map<String, String> paramMap, String orderId,
                                                            String payType, String payChannel);

    /**
     * 获取支付渠道名称（用于日志）
     */
    protected abstract String getPayChannelName();

    /**
     * 获取解析失败的响应
     */
    protected abstract String getParseFailResponse();

    /**
     * 获取签名校验失败的响应
     */
    protected abstract String getSignValidateFailResponse();

    /**
     * 获取订单不存在的响应
     */
    protected abstract String getOrderNotExistResponse();

//    /**
//     * 获取支付失败的响应
//     */
//    protected abstract String getPaymentFailResponse();

    /**
     * 获取成功响应
     */
    protected abstract String getSuccessResponse();

    /**
     * 插入支付日志
     */
    private void insertPayLog(ShoppingOrderPaylog shoppingOrderPaylog) {
        try {
            orderPayStatusService.addOrderPayLog(shoppingOrderPaylog);
        } catch (Exception e) {
            log.error("插入支付日志失败", e);
        }
    }
}
