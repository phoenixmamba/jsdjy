package com.centit.shopping.po;

import java.math.BigDecimal;
import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2022-05-10
 **/
@Data
public class ShoppingArtactivityInventory implements Serializable {


    private String activityId;

    /**
     * 商品规格属性id
     */
    private String propertys;

    private BigDecimal price;

    private Integer count;


}
