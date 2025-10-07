package com.centit.shopping.po;

import java.math.BigDecimal;
import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-06-10
 **/
@Data
public class ShoppingOrderException implements Serializable {


    private String id;

    private String addTime;

    /**
     * 积分抵扣金额
     */
    private Integer refundIntegral;

    /**
     * 会员权益抵扣金额
     */
    private BigDecimal refundBalance;

    private BigDecimal refundCash;

    private String updateTime;

    private String adminUser;

    private String adminName;

    private String ofId;

    private String adminLog;


}
