package com.centit.mallserver.vo;

import com.centit.mallserver.po.ShoppingGoodsPo;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/12/12 15:56
 **/
@Data
public class CulturalGoodsPageVo {
    private String goodsId;
    private String goodsName;
    private String photoId;
    private BigDecimal goodsPrice;
    private int orderCount;

    private BigDecimal restPrice;
    private int integralValue;

    public CulturalGoodsPageVo(ShoppingGoodsPo po) {
        this.goodsId=po.getId();
        this.goodsName=po.getGoodsName();
        this.photoId=po.getGoodsMainPhotoId();
        this.goodsPrice=po.getStorePrice();
        this.orderCount=po.getOrderCount();
    }
}
