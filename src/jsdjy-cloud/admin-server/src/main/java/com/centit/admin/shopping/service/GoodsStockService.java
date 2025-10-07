package com.centit.admin.shopping.service;

import com.centit.admin.shopping.po.GoodsSpecInventoryPo;

import java.util.List;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/12/4 16:28
 **/
public interface GoodsStockService {
    /**
     * 查询商品库存
     * @param goodsId 商品id
     * @return Integer 库存值
     */
    Integer getGoodsStock(String goodsId);

    /**
     * 查询商品指定规格的库存
     * @param goodsId 商品id
     * @param propertys 规格属性
     * @return 库存值
     */
    Integer getGoodsStockWithPropertys(String goodsId, String propertys);

    /**
     * 初始化商品库存
     * @param goodsId 商品id
     * @param stock 库存量
     */
    void initGoodsStock(String goodsId,int stock);

    /**
     * 初始化商品指定规格属性的库存
     * @param goodsId 商品id
     * @param propertys 规格属性
     * @param stock 库存量
     */
    void initGoodsStockWithProperty(String goodsId, String propertys,int stock);

    /**
     * 初始化带有规格信息的商品库存
     * @param goodsId 商品id
     * @param stock 商品库存
     * @param goodsSpecInventoryPoList 商品规格库存列表
     */
    void initGoodsStockWithPropertyList(String goodsId, int stock, List<GoodsSpecInventoryPo> goodsSpecInventoryPoList);

    /**
     * 减少商品库存
     * @param goodsId 商品id
     * @param propertys 规格属性id
     * @param stock 减少库存量
     */
    void cutGoodsStock(String goodsId, String propertys, int stock);

    /**
     * 增加库存
     * @param goodsId 商品id
     * @param propertys 规格属性id
     * @param stock 增加库存量
     */
    void addGoodsStock(String goodsId, String propertys, int stock);
}
