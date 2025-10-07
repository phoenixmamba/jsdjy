package com.centit.pay.biz.po;

import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-03-10
 **/
@Data
public class ParkOrder implements Serializable {


    private String id;

    /**
     * 系统订单id，关联orderform
     */
    private String orderId;

    /**
     * 停车场id
     */
    private String parkId;

    /**
     * 速停车订单号
     */
    private String orderNo;

    /**
     * 车牌号
     */
    private String plateNo;

    /**
     * 票号
     */
    private String cardNo;

    /**
     * 车场名称
     */
    private String parkName;

    private String entryTime;

    private String payTime;

    /**
     * 停车时长（分钟）
     */
    private String elapsedTime;

    /**
     * 总金额（单位分）
     */
    private String totalAmount;

    /**
     * 本次应付金额（减去优惠金额）,单位为分
     */
    private int payable;

    /**
     * 优惠总金额（单位:分）线上优惠总金额，免费金额+ 免费时长抵扣的金额（只需计算即可，不需在停车费中减免）
     */
    private int deductionAmount;

    /**
     * 已经支付过的金额（单位:分） 线上已经支付的金额+线下已经支付的金额+线下优惠金额
     */
    private int paidAmount;

    private int delayTime;

    private String imgName;

    /**
     * 停车订单号失效时间
     */
    private String expireTime;


}
