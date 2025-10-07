package com.centit.admin.shopping.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/12/9 21:30
 **/
@Data
public class GoodsSpecInventoryDTO {
    /**
     * 商品规格属性id
     */
    @NotNull(message = "规格属性id不能为空")
    private String propertys;
    /**
     * 该种规格价格
     */
    @DecimalMin(value = "0",message = "商品价格不能小于0")
    private BigDecimal price;
    /**
     * 该种规格库存量
     */
    @Min(value = 0,message = "商品库存不能小于0")
    private Integer count;
}
