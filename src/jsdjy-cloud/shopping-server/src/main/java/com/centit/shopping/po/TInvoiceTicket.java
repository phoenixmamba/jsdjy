package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-11-08
 **/
@Data
public class TInvoiceTicket implements Serializable {


    private String id;

    private String userid;

    /**
     * 开票表id
     */
    private String invoiceId;

    /**
     * 麦座订单id
     */
    private String mzOrderId;

    /**
     * 项目id
     */
    private String projectId;


}
