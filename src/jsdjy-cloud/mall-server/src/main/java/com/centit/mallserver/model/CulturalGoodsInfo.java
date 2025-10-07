package com.centit.mallserver.model;

import com.centit.mallserver.po.GoodsPhotoPo;
import com.centit.mallserver.po.GoodsSpecInventoryPo;
import com.centit.mallserver.po.ShoppingSpecificationPo;
import lombok.Data;

import java.util.List;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 客户端商品信息封装
 * @Date : 2024/12/13 14:06
 **/
@Data
public class CulturalGoodsInfo {

    private Boolean isOff;

    private CulturalGoodsDetail detail;

    private List<ShoppingSpecificationPo> specs;

    List<GoodsSpecInventoryPo> inventoryDetails;

    List<GoodsPhotoPo> photos;
}
