package com.centit.mallserver.service;

import com.alibaba.fastjson.JSONObject;
import com.centit.core.result.Result;
import com.centit.core.dto.OrderDto;
import com.centit.mallserver.dto.ProductDto;

import javax.servlet.http.HttpServletRequest;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/12/12 14:44
 **/
public interface CulturalGoodsService {
    /**
     * 获取文创商品分类
     * @param parntId 父分类id
     * @return
     */
    Result culGoodsClass(String parntId);

    /**
     * 获取文创商品分类树
     * @return
     */
    Result culGoodsClassTree();

    /**
     * 查询指定分类下的商品列表
     * @param request
     * @return
     */
    Result culGoodsList(HttpServletRequest request);

    /**
     * 查询商品详细信息
     * @param goodsId
     * @param request
     * @return
     */
    Result goodsDetail(String goodsId, HttpServletRequest request);

    /**
     * 获取商品详情额外信息
     * @param request
     * @return
     */
    Result getGoodsExtraInfo(HttpServletRequest request);

    /**
     * 判断下单或加购是否超过限购数量
     * @param reqJson
     * @return true:超过，false:未超过
     */
    Result checkLimitBuy(JSONObject reqJson);

    /**
     * 订单渲染
     * @param productDto 订单渲染信息
     * @return
     */
    Result renderOrder(ProductDto productDto);

    /**
     * 下单
     * @param orderDto 下单信息
     * @return
     */
    Result addOrder(OrderDto orderDto);
}
