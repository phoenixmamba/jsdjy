package com.centit.shopping.po;

import java.math.BigDecimal;
import java.util.Date;
import java.io.Serializable;
import java.util.HashSet;

import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-11-08
 **/
@Data
public class TInvoice implements Serializable {


    private String id;

    private String userid;

    /**
     * 开票类型 1：线上；2：线下
     */
    private String invoiceType;

    /**
     * 开票时间
     */
    private String invoiceTime;

    /**
     * 线上开票流水号
     */
    private String invoiceSerialNum;

    /**
     * 开票金额，单位：元
     */
    private BigDecimal invoiceAmount;

    /**
     * p：普通发票（电票）；c：普通发票（纸质）；s：专用发票
     */
    private String invoiceLine;

    /**
     * 发票代码
     */
    private String invoiceCode;

    /**
     * 发票号码
     */
    private String invoiceNo;

    /**
     * 购房名称
     */
    private String buyerName;

    /**
     * 购方税号
     */
    private String buyerTaxNum;

    /**
     * 购方地址
     */
    private String buyerAddress;

    /**
     * 购方电话
     */
    private String buyerTel;

    /**
     * 开户行
     */
    private String buyerBank;

    /**
     * 开户行账号
     */
    private String buyerAccount;

    /**
     * 订单类型 1：演出；2：充值；3：停车；4：文创
     */
    private String orderType;

    /**
     * 发票状态： 2 :开票完成（ 最终状 态），其他状态
     * 分别为: 20:开票中; 21:开票成功签章中;22:开票失
     * 败;24: 开票成功签章失败;3:发票已作废 31: 发票作
     * 废中 备注：22、24状态时，无需再查询，请确认
     * 开票失败原因以及签章失败原因；3、31只针对纸
     * 票
     */
    private String invoiceStatus;

    /**
     * 开票失败原因
     */
    private String failCause;

    /**
     * 发票图片地址
     */
    private String imgUrls;

    /**
     * 麦座订单id
     */
    private String mzOrderIds;

    /**
     * 演出项目名称
     */
    private String prjectNames;
}
