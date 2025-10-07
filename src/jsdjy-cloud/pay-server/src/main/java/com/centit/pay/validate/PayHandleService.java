package com.centit.pay.validate;

import com.centit.pay.biz.po.ShoppingOrderPaylog;
import com.centit.pay.biz.po.ShoppingPayment;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2025/9/2 9:15
 **/
public interface PayHandleService {
    /**
     * 保存支付回调日志
     * @param shoppingOrderPaylog
     */
    void savePayCallBackLog(ShoppingOrderPaylog shoppingOrderPaylog);

    /**
     * 获取支付信息
     * @param payId
     * @return
     */
    ShoppingPayment getPayment(String payId);

    /**
     * 处理支付信息
     * @param payCallBackInfo
     */
    void processPayment(PayCallBackInfo payCallBackInfo);

    /**
     * 重试处理
     * @param orderId
     * @param payCallBackInfo
     * @param retryCount
     * @param ex
     * @return
     */
    Void retryProcess(String orderId, PayCallBackInfo payCallBackInfo, int retryCount, Throwable ex);
}
