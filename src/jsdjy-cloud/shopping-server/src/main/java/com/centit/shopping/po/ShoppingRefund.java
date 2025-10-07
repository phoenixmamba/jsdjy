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
 * @Date : 2021-05-07
 **/
@Data
public class ShoppingRefund implements Serializable {


    private String id;

    private String addTime;

    private String deleteStatus;

    private String gcId;

    /**
     * 退款状态 0：申请中；1：退款通过；-1：退款不通过
     */
    private Integer refundStatus;

    private String reason;

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

    private String adminLog;

    private String ofId;

    private Integer preOrderStatus;
}
