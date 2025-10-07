package com.centit.pay.biz.po;

import java.math.BigDecimal;
import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-05-13
 **/
@Data
public class ShoppingRecharge implements Serializable {


    private String id;

    /**
     * 用户ID
     */
    private String userId;

    private BigDecimal moneyAmount;

    private BigDecimal payAmount;

    /**
     * 0：待支付；1：充值成功；-1：充值失败
     */
    private Integer status;

    /**
     * 添加时间
     */
    private String addTime;

    /**
     * 删除
     */
    private Boolean deleteStatus;


}
