package com.centit.admin.shopping.service;

import com.alibaba.fastjson.JSONObject;
import com.centit.admin.shopping.dto.GoodInfoDTO;
import com.centit.admin.shopping.dto.GoodsStockDTO;
import com.centit.core.result.Result;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务接口类
 * @Date : 2021-02-21
 **/
public interface SellerGoodsService {

    /**
     * 查询列表
     */
    Result queryPageList(JSONObject reqJson);

    /**
     * 校验商品名称是否已存在
     */
    Result checkGoodsName(JSONObject reqJson);

    /**
     * 上/下架商品
     **/
    Result putGoods(JSONObject reqJson);

    /**
     * 删除商品
     **/
    Result delGoods(JSONObject reqJson);

    /**
     * 获取商品分类列表
     **/
    Result queryGoodsClass(JSONObject reqJson);

    /**
     * 获取商品详情
     **/
    Result goodsDetail(String goodsId);

    /**
     * 获取商品分类默认规格配置
     **/
    Result queryDefaultSpecification(JSONObject reqJson);

    /**
     * 发布新商品
     **/
    Result addGoods(GoodInfoDTO shoppingGoodsDto);

    /**
     * 编辑商品
     **/
    Result editGoods(GoodInfoDTO shoppingGoodsDto);

    Result cutGoodsStock(GoodsStockDTO stockDTO);

    Result addGoodsStock(GoodsStockDTO stockDTO);

}
