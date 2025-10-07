package com.centit.pay.biz.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-12-20
 **/
@Data
public class ShoppingCouponRecord implements Serializable {


    private String id;

    private String userId;

    private String rightNo;

    private String couponId;

    private String ofId;

    private String createTime;


}
