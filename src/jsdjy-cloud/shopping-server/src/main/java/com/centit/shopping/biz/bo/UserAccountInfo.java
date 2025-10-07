package com.centit.shopping.biz.bo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description : 用户卖座账户信息
 **/
@Data
public class UserAccountInfo {

    private Boolean accountEnable =false;

    private String mzUserId;

    private int account_point = 0;   //会员账户积分剩余点数，单位：点数；

    private BigDecimal accountMoney = BigDecimal.ZERO;

    private int account_money_fen = 0;  //账户余额；单位：分

    public UserAccountInfo(String mzUserId) {
        this.mzUserId = mzUserId;
    }

    public BigDecimal getAccountMoney(){
        BigDecimal accountMoney = new BigDecimal(this.account_money_fen).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);  //分转换为元
        return accountMoney;
    }
}
