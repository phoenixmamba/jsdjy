package com.centit.mallserver.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.core.result.Result;
import com.centit.core.dto.OrderDto;
import com.centit.mallserver.dto.ProductDto;
import com.centit.mallserver.service.CulturalGoodsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 文创商品
 * @Date : 2024/12/12 11:02
 **/
@RestController
@RequestMapping("/culturalGoods")
public class CulturalGoodsController {
    @Resource
    private CulturalGoodsService culturalGoodsService;

    /**
     * 获取文创商品分类
     * @return Result
     */
    @GetMapping("/culGoodsClass")
    public Result culGoodsClass(HttpServletRequest request){
        String parentId = request.getParameter("parentId");
        return culturalGoodsService.culGoodsClass(parentId);
    }

    /**
     * 获取文创商品分类树
     * @return Result
     */
    @GetMapping("/culGoodsClassTree")
    public Result culGoodsClassTree(){
        return culturalGoodsService.culGoodsClassTree();
    }

    /**
     * 获取文创商品列表
     * @return Result
     */
    @GetMapping("/culGoodsList")
    public Result culGoodsList(HttpServletRequest request){
        return culturalGoodsService.culGoodsList(request);
    }

    /**
     * 获取商品详情
     * @return
     */
    @GetMapping("/goodsDetail/{id}")
    public Result goodsDetail(@PathVariable String id, HttpServletRequest request){
        return culturalGoodsService.goodsDetail(id,request);
    }

    /**
     * 获取商品其它信息
     * @return Result
     */
    @GetMapping("/goodsExtraInfo")
    public Result goodsExtraInfo(HttpServletRequest request){
        return culturalGoodsService.getGoodsExtraInfo(request);
    }

    /**
     * 判断下单或加购是否超过限购数量
     * @return
     */
    @PostMapping("/checkLimitBuy")
    public Result checkLimitBuy(@RequestBody JSONObject reqJson){
        return culturalGoodsService.checkLimitBuy(reqJson);
    }

    /**
     * 订单渲染
     * @return
     */
    @PostMapping("/renderGoodsOrder")
    public Result renderGoodsOrder(@RequestBody @Validated ProductDto productDto){
        return culturalGoodsService.renderOrder(productDto);
    }

    /**
     * 订单提交
     * @return
     */
    @PostMapping("/addOrder")
    public Result addOrder(@RequestBody @Validated OrderDto orderDto){
        return culturalGoodsService.addOrder(orderDto);
    }
}
