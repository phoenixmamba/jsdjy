package com.centit.order.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2025/1/2 15:00;
 **/
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum OrderStatus {
    /**
     * 创建失败
     */
    UN_CREATE(-10),
    /**
     * 异常
     */
    ANOMALOUS(-1),
    /**
     * 取消
     */
    CANCEL(0),
    /**
     * 临时
     */
    TEMP(1),
    /**
     * 待付款
     */
    TO_PAY(10),
    /**
     * 已付款/待发货
     */
    HAS_PAY(20),
    /**
     * 已发货
     */
    HAS_SHIP(30),
    /**
     * 已收货
     */
    HAS_RECEIVE(40),
    /**
     * 已完成
     */
    HAS_DONE(50),
    /**
     * 申请退款中
     */
    IN_REFUND(60),
    /**
     * 已退款
     */
    HAS_REFUND(70),
    ;

    private int status;
}
