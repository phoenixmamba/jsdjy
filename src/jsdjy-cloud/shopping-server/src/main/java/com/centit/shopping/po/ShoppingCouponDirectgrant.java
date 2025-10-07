package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2022-01-26
 **/
@Data
public class ShoppingCouponDirectgrant implements Serializable {


    /**
     * 优惠券Id
     */
    private String rightNo;

    private String addTime;

    private String isDelete;
}
