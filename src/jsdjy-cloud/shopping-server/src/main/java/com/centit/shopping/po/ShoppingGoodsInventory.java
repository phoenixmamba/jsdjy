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
 * @Date : 2021-03-10
 **/
@Data
public class ShoppingGoodsInventory implements Serializable {


    private String goodsId;

    /**
     * 商品规格属性id
     */
    private String propertys;

//    private String value;

    private BigDecimal price;

    private Integer count;

    private String value;
}
