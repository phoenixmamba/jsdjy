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
public class ShoppingCouponDirectgrantRecord implements Serializable {


    private Long id;

    private String rightNo;

    private String phone;

    /**
     * '0':发放成功;'1':发放失败
     */
    private String grantStatus;

    private String msg;

    private Date addTime;


}
