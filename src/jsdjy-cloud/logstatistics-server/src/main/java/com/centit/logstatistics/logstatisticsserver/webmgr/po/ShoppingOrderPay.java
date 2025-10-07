package com.centit.logstatistics.logstatisticsserver.webmgr.po;

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
public class ShoppingOrderPay implements Serializable {


    private String id;

    private String updateTime;

    private Integer cashStatus;

    private Integer couponStatus;

    private Integer giftcardStatus;

    private Integer integralStatus;

    private Integer balanceStatus;

    private Integer payStatus;

    private String ofId;

    private String userId;

    private String outTradeNo;


}
