package com.centit.shopping.biz.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.biz.service.ShoppingCartsService;
import com.centit.shopping.utils.RequestParametersUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>购物车<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 前端控制器
 * @Date : 2021-03-24
 **/
@RestController
@RequestMapping("/shoppingCarts")
public class ShoppingCartsController {

    @Resource
    private ShoppingCartsService shoppingCartsService;


    /**
     * 添加购物车
     * @return
     */
    @PostMapping("/addCart")
    public JSONObject addCart(@RequestBody JSONObject reqJson){
        return shoppingCartsService.addCart(reqJson);
    }


    /**
     * 获取用户购物车数据
     * @return
     */
    @GetMapping("/userCartInfo")
    public JSONObject getUserCartInfo(HttpServletRequest request, HttpServletResponse response){
        return shoppingCartsService.getUserCartInfo(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 更新购物车商品数量
     * @return
     */
    @PostMapping("/updateCartGoodsCount")
    public JSONObject updateCartGoodsCount(@RequestBody JSONObject reqJson){
        return shoppingCartsService.updateCartGoodsCount(reqJson);
    }

    /**
     * 删除购物车商品
     * @return
     */
    @PostMapping("/delCartGoods")
    public JSONObject delCartGoods(@RequestBody JSONObject reqJson){
        return shoppingCartsService.delCartGoods(reqJson);
    }

    /**
     * 判断合并下单时是否有商品超过限购
     * @return
     */
    @PostMapping("/checkLimitNum")
    public JSONObject checkLimitNum(@RequestBody JSONObject reqJson){
        return shoppingCartsService.checkLimitNum(reqJson);
    }


    /**
     * 订单页面渲染（文创单商品下单）
     * @return
     */
    @PostMapping("/renderMultipleGoodsOrder")
    public JSONObject renderMultipleGoodsOrder(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return shoppingCartsService.renderMultipleGoodsOrder(reqJson,request);
    }

    /**
     * 购物车合并下单
     * @return
     */
    @PostMapping("/addMultipleOrder")
    public JSONObject addMultipleOrder(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return shoppingCartsService.addMultipleOrder(reqJson,request);
    }
}