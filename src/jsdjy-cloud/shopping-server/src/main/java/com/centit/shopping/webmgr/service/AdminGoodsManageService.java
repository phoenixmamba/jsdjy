package com.centit.shopping.webmgr.service;

import com.alibaba.fastjson.JSONObject;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务接口类
 * @Date : 2021-02-21
 **/
public interface AdminGoodsManageService {

    /**
     * 获取商品分类列表
     **/
    JSONObject queryClassPageList(JSONObject reqJson);

    /**
     * 获取下级分类
     **/
    JSONObject queryChildClass(JSONObject reqJson);

    /**
     * 查询商品类型
     **/
    JSONObject queryGoodstypes(JSONObject reqJson);

    /**
     * 新增商品分类
     **/
    JSONObject addGoodsclass(JSONObject reqJson);

    /**
     * 查询商品分类详情
     **/
    JSONObject queryGoodsclassDetail(String id);

    /**
     * 编辑商品分类
     **/
    JSONObject editGoodsclass(JSONObject reqJson);

    /**
     * 删除商品分类
     **/
    JSONObject delGoodsclass(JSONObject reqJson);


    /**
     * 查询商品规格列表
     **/
    JSONObject querySpecsPageList(JSONObject reqJson);

    /**
     * 新增商品规格
     **/
    JSONObject addGoodsspecification(JSONObject reqJson);

    /**
     * 查询商品规格详情
     **/
    JSONObject queryGoodsspecificationDetail(String id);

    /**
     * 编辑商品规格
     **/
    JSONObject editGoodsspecification(JSONObject reqJson);

    /**
     * 删除规格
     **/
    JSONObject delGoodsspecification(JSONObject reqJson);

    /**
     * 查询商品类型列表
     **/
    JSONObject queryTypePageList(JSONObject reqJson);

    /**
     * 获取初始商品规格列表
     **/
    JSONObject querySpecifications(JSONObject reqJson);

    /**
     * 新增商品类型
     **/
    JSONObject addGoodstype(JSONObject reqJson);

    /**
     * 查询商品类型详情
     **/
    JSONObject queryGoodstypeDetail(String id);

    /**
     * 编辑商品类型
     **/
    JSONObject editGoodstype(JSONObject reqJson);

    /**
     * 删除商品类型
     **/
    JSONObject delGoodstype(JSONObject reqJson);
}
