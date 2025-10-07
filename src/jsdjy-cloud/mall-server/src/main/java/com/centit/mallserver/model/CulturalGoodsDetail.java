package com.centit.mallserver.model;

import com.centit.mallserver.po.ShoppingGoodsPo;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 文创商品详细信息
 * @Date : 2024/12/12 15:56
 **/
@Data
public class CulturalGoodsDetail {
    private String goodsId;
    private String goodsName;
    private String photoId;
    private BigDecimal storePrice;
    private BigDecimal originPrice;
    private Integer goodsInventory;
    private String goodsNotice;
    private String returnExplain;
    private Integer limitBuy;
    private String inventoryType;
    private String goodsCheckbox;
    private String goodsDetails;

    private BigDecimal minPrice;
    private BigDecimal maxPrice;

    public CulturalGoodsDetail(){

    }
    public CulturalGoodsDetail(ShoppingGoodsPo po) {
        this.goodsId=po.getId();
        this.goodsName=po.getGoodsName();
        this.photoId=po.getGoodsMainPhotoId();
        this.storePrice=po.getStorePrice();
        this.originPrice=po.getGoodsPrice();

        this.goodsInventory=po.getGoodsInventory();
        this.goodsNotice=po.getGoodsNotice();
        this.returnExplain=po.getReturnExplain();
        this.limitBuy=po.getLimitBuy();
        this.inventoryType=po.getInventoryType();
        this.goodsCheckbox=po.getGoodsCheckbox();
        this.goodsDetails=po.getGoodsDetails();
    }
}
