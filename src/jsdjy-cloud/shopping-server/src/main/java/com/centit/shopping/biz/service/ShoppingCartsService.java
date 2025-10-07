package com.centit.shopping.biz.service;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>购物车<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务接口类
 * @Date : 2021-02-24
 **/
public interface ShoppingCartsService {


    /**
     * 添加购物车
     */
    JSONObject addCart(JSONObject reqJson);

    /**
     * 获取用户购物车数据
     */
    JSONObject getUserCartInfo(JSONObject reqJson);

    /**
     * 更新购物车商品数量
     */
    JSONObject updateCartGoodsCount(JSONObject reqJson);

    /**
     * 删除购物车商品
     */
    JSONObject delCartGoods(JSONObject reqJson);

    /**
     * 判断合并下单时是否有商品超过限购
     */
    JSONObject checkLimitNum(JSONObject reqJson);

    /**
     * 订单页面渲染（购物车多个商品合并下单）
     */
    JSONObject renderMultipleGoodsOrder(JSONObject reqJson,HttpServletRequest request);
//    JSONObject renderMultipleGoodsOrder_new(JSONObject reqJson,HttpServletRequest request);

    /**
     * 购物车合并下单
     */
    JSONObject addMultipleOrder(JSONObject reqJson, HttpServletRequest request);
//    JSONObject addMultipleOrder_new(JSONObject reqJson, HttpServletRequest request);
}
