package com.centit.mallserver.model;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 会员账户信息
 * @Date : 2024/12/19 14:46
 **/
@Data
public class UserAccountInfo {

    private Boolean accountEnable =false;

    private String mzUserId;

    //会员账户积分剩余点数
    private int accountPoint = 0;

    private BigDecimal accountMoney = BigDecimal.ZERO;
    //账户余额；单位：分
    private int accountMoneyFen = 0;

    public UserAccountInfo(String mzUserId) {
        this.mzUserId = mzUserId;
    }

    public BigDecimal getAccountMoney(){
        //分转换为元
        BigDecimal accountMoney = new BigDecimal(this.accountMoneyFen).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
        return accountMoney;
    }
}
