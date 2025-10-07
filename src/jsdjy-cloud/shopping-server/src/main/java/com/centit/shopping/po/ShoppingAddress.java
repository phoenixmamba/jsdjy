package com.centit.shopping.po;

import java.io.Serializable;
import lombok.Data;

/**
 * <p>收货地址<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-02-25
 **/
@Data
public class ShoppingAddress implements Serializable {


    private String id;

    private String addTime;

    private Boolean deleteStatus;

    private String areaInfo;

    private String mobile;

    private String telephone;

    private String trueName;

    private String zip;

    private String areaId;

    private String userId;

    /**
     * 默认地址
     */
    private Boolean defaultAddr;

    /**
     * 客户姓名
     */
    private String customerName;


}
