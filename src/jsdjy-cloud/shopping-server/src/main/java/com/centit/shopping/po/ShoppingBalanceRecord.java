package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2022-01-25
 **/
@Data
public class ShoppingBalanceRecord implements Serializable {


    private Integer id;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 余额数额，单位分
     */
    private Integer balanceCount;

    /**
     * 备注
     */
    private String remark;

    private Date addTime;

    /**
     * 0：未领取；1：已领取
     */
    private String status;

    /**
     * 资产领取记录id
     */
    private String assetId;


}
