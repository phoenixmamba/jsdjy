package com.centit.jobserver.po;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 
 * @Date : 2024/11/21 19:55
 **/
@Data
public class MzAssetRule {
    /**
    * 积分是否免密
    */
    private String pointAvoidPay;

    private Integer pointAvoidLimit;

    private String accountAvoidPay;

    /**
    * 余额免密限额，单位元
    */
    private BigDecimal accountAvoidLimit;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", pointAvoidPay=").append(pointAvoidPay);
        sb.append(", pointAvoidLimit=").append(pointAvoidLimit);
        sb.append(", accountAvoidPay=").append(accountAvoidPay);
        sb.append(", accountAvoidLimit=").append(accountAvoidLimit);
        sb.append("]");
        return sb.toString();
    }
}