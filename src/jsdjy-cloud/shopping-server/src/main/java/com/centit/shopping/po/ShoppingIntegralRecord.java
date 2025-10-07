package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-07-07
 **/
@Data
public class ShoppingIntegralRecord implements Serializable {


    private Integer id;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 积分数额
     */
    private Integer integralCount;

    /**
     * 备注
     */
    private String remark;

    private String addTime;

    private String status;

    private String assetId;
}
