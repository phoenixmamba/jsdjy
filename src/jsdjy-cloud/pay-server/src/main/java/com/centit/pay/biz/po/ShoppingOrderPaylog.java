package com.centit.pay.biz.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-03-23
 **/
@Data
public class ShoppingOrderPaylog implements Serializable {


    private String id;

    private String addTime;

    /**
     * 订单id
     */
    private String ofId;

    /**
     * 交易类型
     */
    private String tradeType;

    /**
     * 外部交易流水号
     */
    private String outTradeNo;

    private String reqInfo;

    /**
     * 日志信息
     */
    private String logInfo;

    /**
     * 日志详情
     */
    private String logContent;

    private String ip;

    /**
     * 状态
     */
    private String stateInfo;


}
