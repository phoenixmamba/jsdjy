package com.centit.order.enums;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2025/1/2 14:20
 **/
public enum OrderAction {
    /**
     * 创建
     */
    CREATE,
    /**
     * 支付
     */
    PAY,
    /**
     * 发货
     */
    SHIP,
    /**
     * 确认收货
     */
    RECEIVE,
    /**
     * 取消订单
     */
    CANCEL,
    /**
     * 申请退款
     */
    APPLY_REFUND,
    /**
     * 取消申请退款
     */
    CANCEL_REFUND,
    /**
     * 确认退款
     */
    TO_REFUND,
    /**
     * 驳回退款
     */
    REFUSE_REFUND,
    ;
}
