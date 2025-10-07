package com.centit.admin.shopping.dto;

import javax.validation.constraints.NotNull;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/12/10 16:12
 **/
public class GoodsStockDTO {
    /**
     * 商品id
     */
    @NotNull
    private String goodsId;
    /**
     * 商品规格属性id
     */
    private String propertys;
    /**
     * 库存增减值
     */
    @NotNull(message = "库存变化值不能为空")
    private Integer stock;

    @Override
    public String toString() {
        return "GoodsStockDTO{" +
                "goodsId='" + goodsId + '\'' +
                ", propertys='" + propertys + '\'' +
                ", stock=" + stock +
                '}';
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getPropertys() {
        return propertys;
    }

    public void setPropertys(String propertys) {
        this.propertys = propertys;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}
