package com.centit.shopping.biz.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.biz.service.ShoppingGoodsService;
import com.centit.shopping.utils.RequestParametersUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>文创&积分商品<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 前端控制器
 * @Date : 2021-02-24
 **/
@RestController
@RequestMapping("/shoppingGoods")
public class ShoppingGoodsController {

    @Resource
    private ShoppingGoodsService shoppingGoodsService;


    @PostMapping("/testCoupon")
    public JSONObject testCoupon(@RequestBody JSONObject reqJson){
        return shoppingGoodsService.testCoupon(reqJson);
    }

    /**
     * 获取文创商品分类
     * @return
     */
    @GetMapping("/culGoodsClass")
    public JSONObject culGoodsClass(HttpServletRequest request, HttpServletResponse response){
        return shoppingGoodsService.culGoodsClass(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 获取文创商品分类树
     * @return
     */
    @GetMapping("/culGoodsClassTree")
    public JSONObject culGoodsClassTree(HttpServletRequest request, HttpServletResponse response){
        return shoppingGoodsService.culGoodsClassTree(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 查询文创商品列表
     * @return
     */
    @GetMapping("/culGoodsList")
    public JSONObject queryClassGoodsList(HttpServletRequest request, HttpServletResponse response){
        return shoppingGoodsService.queryCulGoodsList(RequestParametersUtil.getRequestParametersRetJson(request));
    }



    /**
     * 获取积分商城商品分类
     * @return
     */
    @GetMapping("/integralGoodsClass")
    public JSONObject integralGoodsClass(HttpServletRequest request, HttpServletResponse response){
        return shoppingGoodsService.integralGoodsClass(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 获取积分商城商品分类树
     * @return
     */
    @GetMapping("/integralGoodsClassTree")
    public JSONObject integralGoodsClassTree(HttpServletRequest request, HttpServletResponse response){
        return shoppingGoodsService.integralGoodsClassTree(RequestParametersUtil.getRequestParametersRetJson(request));
    }


    /**
     * 查询积分商品列表
     * @return
     */
    @GetMapping("/integralGoodsList")
    public JSONObject integralGoodsList(HttpServletRequest request, HttpServletResponse response){
        return shoppingGoodsService.queryIntegralGoodsList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 查询其它商品列表
     * @return
     */
    @GetMapping("/otherGoodsList")
    public JSONObject queryOtherGoodsList(HttpServletRequest request, HttpServletResponse response){
        return shoppingGoodsService.queryOtherGoodsList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 获取商品详情
     * @return
     */
    @GetMapping("/goodsDetail/{id}")
    public JSONObject goodsDetail(@PathVariable String id,HttpServletRequest request){
        return shoppingGoodsService.goodsDetail(id,RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 查询商品评价列表
     * @return
     */
    @GetMapping("/evaluatePageList")
    public JSONObject queryEvaluatePageList(HttpServletRequest request, HttpServletResponse response){
        return shoppingGoodsService.queryEvaluatePageList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 判断下单或者添加购物车商品数量是否超过限购值
     * @return
     */
    @PostMapping("/checkLimitBuy")
    public JSONObject checkLimitBuy(@RequestBody JSONObject reqJson){
        return shoppingGoodsService.checkLimitBuy(reqJson);
    }

    /**
     * 查询用户可用的优惠和积分余额信息（直接下单）
     * @return
     */
    @PostMapping("/getUserPromotion")
    public JSONObject getUserPromotion(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return shoppingGoodsService.getUserPromotion(reqJson,request);
    }

    /**
     * 订单页面渲染（文创单商品下单）
     * @return
     */
    @PostMapping("/renderGoodsOrder")
    public JSONObject renderGoodsOrder(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return shoppingGoodsService.renderGoodsOrder(reqJson,request);
    }

//    /**
//     * 订单页面渲染（文创单商品下单）
//     * @return
//     */
//    @PostMapping("/renderGoodsOrder")
//    public JSONObject renderGoodsOrder_new(@RequestBody JSONObject reqJson,HttpServletRequest request){
//        return shoppingGoodsService.renderGoodsOrder_new(reqJson,request);
//    }

    /**
     * 订单页面渲染（积分商城单商品下单）
     * @return
     */
    @PostMapping("/renderInregraGoodsOrder")
    public JSONObject renderInregraGoodsOrder(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return shoppingGoodsService.renderInregraGoodsOrder(reqJson,request);
    }

//    /**
//     * 订单页面渲染（积分商城单商品下单）
//     * @return
//     */
//    @PostMapping("/renderInregraGoodsOrder")
//    public JSONObject renderInregraGoodsOrder_new(@RequestBody JSONObject reqJson,HttpServletRequest request){
//        return shoppingGoodsService.renderInregraGoodsOrder_new(reqJson,request);
//    }

    /**
     * 创建订单（文创单商品）
     * @return
     */
    @PostMapping("/addOrder")
    public JSONObject addOrder(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return shoppingGoodsService.addOrder(reqJson,request);
    }

//    /**
//     * 创建订单（文创单商品）
//     * @return
//     */
//    @PostMapping("/addOrder")
//    public JSONObject addOrder_new(@RequestBody JSONObject reqJson,HttpServletRequest request){
//        return shoppingGoodsService.addOrder_new(reqJson,request);
//    }

    /**
     * 创建订单（积分单商品）
     * @return
     */
    @PostMapping("/addIntegralOrder")
    public JSONObject addIntegralOrder(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return shoppingGoodsService.addIntegralOrder(reqJson,request);
    }

//    /**
//     * 创建订单（积分单商品）
//     * @return
//     */
//    @PostMapping("/addIntegralOrder")
//    public JSONObject addIntegralOrder_new(@RequestBody JSONObject reqJson,HttpServletRequest request){
//        return shoppingGoodsService.addIntegralOrder_new(reqJson,request);
//    }
}