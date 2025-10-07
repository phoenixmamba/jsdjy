package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-05-27
 **/
@Data
public class ShoppingActivityCoupon implements Serializable {


    private String id;

    private String acId;

    private String rightNo;

    /**
     * 单人限领数量，0标识不限制
     */
    private Integer acPerLimit;


}
