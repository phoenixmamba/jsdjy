package com.centit.shopping.biz.service;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>文创商品<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务接口类
 * @Date : 2021-02-24
 **/
public interface ShoppingGoodsService {

    JSONObject testCoupon(JSONObject reqJson);

    /**
     * 获取文创商品分类
     */
    JSONObject culGoodsClass(JSONObject reqJson);

    /**
     * 获取积分商城商品分类
     */
    JSONObject integralGoodsClass(JSONObject reqJson);

//    /**
//     * 获取子分类
//     */
//    JSONObject culGoodsChildClass(JSONObject reqJson);

    /**
     * 查询文创商品列表
     */
    JSONObject queryCulGoodsList(JSONObject reqJson);

    /**
     * 查询其它分类商品列表
     */
    JSONObject queryOtherGoodsList(JSONObject reqJson);

    JSONObject culGoodsClassTree(JSONObject reqJson);

    JSONObject integralGoodsClassTree(JSONObject reqJson);

    /**
     * 查询文创商品列表
     */
    JSONObject queryIntegralGoodsList(JSONObject reqJson);

    /**
     * 获取商品详情
     */
    JSONObject goodsDetail(String goodsId,JSONObject reqJson);

    /**
     * 获取商品评价列表
     */
    JSONObject queryEvaluatePageList(JSONObject reqJson);


    /**
     * 判断下单或者添加购物车商品数量是否超过限购值
     */
    JSONObject checkLimitBuy(JSONObject reqJson);

    /**
     * 查询用户可用的优惠和积分余额信息（直接下单）
     */
    JSONObject getUserPromotion(JSONObject reqJson, HttpServletRequest request);

    /**
     * 订单页面渲染（文创单商品下单）
     */
    JSONObject renderGoodsOrder(JSONObject reqJson, HttpServletRequest request);

    /**
     * 订单页面渲染（文创单商品下单）
     */
//    JSONObject renderGoodsOrder_new(JSONObject reqJson, HttpServletRequest request);

    /**
     * 订单页面渲染（积分单商品下单）
     */
    JSONObject renderInregraGoodsOrder(JSONObject reqJson,HttpServletRequest request);

//    JSONObject renderInregraGoodsOrder_new(JSONObject reqJson, HttpServletRequest request);

    /**
     * 创建订单（用户下单）
     */
    JSONObject addOrder(JSONObject reqJson,HttpServletRequest request);

    /**
     * 创建订单（用户下单）
     */
//    JSONObject addOrder_new(JSONObject reqJson,HttpServletRequest request);


    JSONObject addIntegralOrder(JSONObject reqJson,HttpServletRequest request);

//    JSONObject addIntegralOrder_new(JSONObject reqJson, HttpServletRequest request);
}
