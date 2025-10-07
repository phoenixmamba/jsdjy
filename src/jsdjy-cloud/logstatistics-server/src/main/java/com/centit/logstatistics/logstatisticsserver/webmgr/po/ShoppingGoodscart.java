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
 * @Date : 2021-06-08
 **/
@Data
public class ShoppingGoodscart implements Serializable {


    private String id;

    private String addTime;

    private Boolean deleteStatus;

    private Integer count;

    private BigDecimal price;

    private Integer integral;

    private String specInfo;

    private String goodsId;

    private String ofId;

    private Integer cartType;

    private String scId;

    private BigDecimal screenPrice;

    private String screenSafe;

    private String anchorId;

    private String propertys;

    private BigDecimal deductionIntegralPrice;

    private BigDecimal deductionMemberPrice;

    private BigDecimal deductionBalancePrice;

    private BigDecimal deductionCouponPrice;

    private BigDecimal deductionGiftcardPrice;

    private BigDecimal deductionIntegral;

    private String transport;

    private BigDecimal payPrice;

    private BigDecimal shipPrice;


}
