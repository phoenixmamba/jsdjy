package com.centit.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/12/30 17:29
 **/
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum SellTypeEnum {

    /**
     * 文创
     */
    CULTURAL(1,1,11,"CUL"),
    /**
     * 积分
     */
    INTEGRAL(2,2,12,"INT"),
    /**
     * 艺术活动
     */
    ACTIVITY(3,3,13,"AC"),
    /**
     * 艺术培训
     */
    CLASS(4,4,14,"CL"),
    /**
     * 爱艺计划
     */
    PLAN(9,9,15,"PL"),
    /**
     * 演出卖票
     */
    TICKET(5,5,2,"TK"),
    /**
     * 停车
     */
    PARK(6,6,3,"PK"),
    /**
     * 点播
     */
    VIDEO(7,7,4,"VD"),
    /**
     * 充值
     */
    RECHARGE(7,8, 5,"CG"),
    ;

    /**
     * 商品类型
     */
    private int goodsType;
    /**
     * 购物车类型
     */
    private int cartType;
    /**
     * 订单类型
     */
    private int orderType;
    /**
     * 订单前缀
     */
    private String orderPrex;
}
