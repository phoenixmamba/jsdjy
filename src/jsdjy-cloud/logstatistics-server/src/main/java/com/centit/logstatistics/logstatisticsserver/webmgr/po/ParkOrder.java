package com.centit.logstatistics.logstatisticsserver.webmgr.po;

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
public class ParkOrder implements Serializable {


    private String id;

    private String orderId;

    private String parkId;

    private String orderNo;

    private String plateNo;

    private String cardNo;

    private String parkName;

    private String entryTime;

    private String payTime;

    private int elapsedTime;

    private int totalAmount;

    private int payable;

    private int deductionAmount;

    private int paidAmount;

    private int delayTime;

    private String imgName;

    private String expireTime;


}
