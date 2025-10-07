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
 * @Date : 2021-10-11
 **/
@Data
public class ShoppingRechargeActivityRecord implements Serializable {


    private String id;

    private String activityId;

    private String userid;

    private String rechargeTime;

    private BigDecimal rechargePrice;


}
