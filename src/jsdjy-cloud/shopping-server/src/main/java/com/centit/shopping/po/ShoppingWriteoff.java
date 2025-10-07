package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-05-05
 **/
@Data
public class ShoppingWriteoff implements Serializable {


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
