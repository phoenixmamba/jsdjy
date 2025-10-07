package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-11-24
 **/
@Data
public class TInvoicePush implements Serializable {


    private String id;

    /**
     * 开票记录id
     */
    private String invoiceId;

    /**
     * 推送手机号
     */
    private String pushPhone;

    /**
     * 推送邮箱
     */
    private String pushEmail;

    /**
     * 推送时间
     */
    private String pushTime;


}
