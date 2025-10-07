package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2022-03-15
 **/
@Data
public class ShoppingWriteoffCoupon implements Serializable {


    private String id;

    private String userId;

    private String rightNo;

    private String rightId;

    /**
     * 核销码
     */
    private String offCode;

    private Integer offCount;

    /**
     * 0：未核销 1：已核销
     */
    private Integer offStatus;

    private String offTime;

    private String offAccount;

    private String userName;

    private String mobile;

}
