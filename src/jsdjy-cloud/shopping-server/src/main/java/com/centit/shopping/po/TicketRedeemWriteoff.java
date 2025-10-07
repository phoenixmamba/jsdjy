package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2022-07-22
 **/
@Data
public class TicketRedeemWriteoff implements Serializable {


    /**
     * 兑换码
     */
    private String code;

    /**
     * 核销码
     */
    private String offCode;

    /**
     * 失效时间
     */
    private String expTime;

    /**
     * 核销时间
     */
    private String offTime;

    /**
     * 核销账户
     */
    private String offCount;


}
