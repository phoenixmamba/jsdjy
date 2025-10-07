package com.centit.order.service;

import com.centit.order.enums.OrderAction;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2025/1/2 14:27
 **/
public interface OrderStatusService {
    /**
     * 更新订单状态
     * @param orderId
     * @param orderAction
     */
    void setNextStatus(String orderId, OrderAction orderAction);
}
