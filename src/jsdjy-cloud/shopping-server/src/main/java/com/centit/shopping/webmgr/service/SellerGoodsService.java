package com.centit.shopping.webmgr.service;

import com.alibaba.fastjson.JSONObject;

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
    JSONObject queryPageList(JSONObject reqJson);

    /**
     * 校验商品名称是否已存在
     */
    JSONObject checkGoodsName(JSONObject reqJson);

    /**
     * 上/下架商品
     **/
    JSONObject putGoods(JSONObject reqJson);

    /**
     * 删除商品
     **/
    JSONObject delGoods(JSONObject reqJson);

    /**
     * 获取商品分类列表
     **/
    JSONObject queryGoodsClass(JSONObject reqJson);

    /**
     * 获取商品详情
     **/
    JSONObject goodsDetail(String goodsId);

    /**
     * 获取商品分类默认规格配置
     **/
    JSONObject queryDefaultSpecification(JSONObject reqJson);

    /**
     * 发布新商品
     **/
    JSONObject addGoods(JSONObject reqJson);

    /**
     * 编辑商品
     **/
    JSONObject editGoods(JSONObject reqJson);

}
