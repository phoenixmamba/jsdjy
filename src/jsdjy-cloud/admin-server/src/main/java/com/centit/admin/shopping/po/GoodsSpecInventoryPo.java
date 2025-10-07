package com.centit.admin.shopping.po;

import com.centit.admin.shopping.dto.GoodsSpecInventoryDTO;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 商品规格库存信息实体类
 * @Date : 2021-03-10
 **/
@Data
public class GoodsSpecInventoryPo implements Serializable {


    private String goodsId;

    /**
     * 商品规格属性id
     */
    private String propertys;

    private BigDecimal price;

    private Integer count;

    private String value;

    public GoodsSpecInventoryPo() {
    }

    public GoodsSpecInventoryPo(GoodsSpecInventoryDTO goodsSpecInventoryDTO){
        this.propertys= goodsSpecInventoryDTO.getPropertys();
        this.price= goodsSpecInventoryDTO.getPrice();
        this.count= goodsSpecInventoryDTO.getCount();
    }
}
