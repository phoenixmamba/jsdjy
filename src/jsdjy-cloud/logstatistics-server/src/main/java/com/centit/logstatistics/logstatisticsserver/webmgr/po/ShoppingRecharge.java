package com.centit.logstatistics.logstatisticsserver.webmgr.po;

import java.math.BigDecimal;
import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p> <p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description :  实体类
 * @Date : 2021-06-10
 **/
@Data
public class ShoppingRecharge implements Serializable {


    private String id;

    private String userId;

    private BigDecimal moneyAmount;

    private BigDecimal payAmount;

    private Integer status;

    private String addTime;

    private String deleteStatus;


}
