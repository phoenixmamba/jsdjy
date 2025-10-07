package com.centit.ticket.po;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-10-19
 **/
@Data
public class ShoppingAssetRule implements Serializable {


    /**
     * 积分是否免密
     */
    private String pointAvoidPay;

    private int pointAvoidLimit;

    private String accountAvoidPay;

    /**
     * 余额免密限额，单位元
     */
    private BigDecimal accountAvoidLimit;


}
