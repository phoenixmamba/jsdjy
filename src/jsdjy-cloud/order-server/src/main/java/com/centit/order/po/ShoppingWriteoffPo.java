package com.centit.order.po;

import lombok.Data;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2025/1/2 9:30
 **/
@Data
public class ShoppingWriteoffPo {
    private String gcId;

    /**
     * 核销码
     */
    private String offCode;

    private Integer goodsCount;

    private Integer offCount;

    /**
     * 0：未核销 1：已核销
     */
    private Integer offStatus;
}
