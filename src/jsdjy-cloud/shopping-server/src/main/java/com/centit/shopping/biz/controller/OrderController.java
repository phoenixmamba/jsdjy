package com.centit.shopping.biz.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.biz.service.OrderService;
import com.centit.shopping.utils.RequestParametersUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 前端控制器
 * @Date : 2021-02-25
 **/
@RestController
@RequestMapping("/shoppingOrder")
public class OrderController {

    @Resource
    private OrderService orderService;

    /**
     * 查询列表
     * @return
     */
    @GetMapping("/pageList")
    public JSONObject queryPageList(HttpServletRequest request){
        return orderService.queryPageList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 获取订单详情
     * @return
     */
    @GetMapping("/orderDetail/{ofId}")
    public JSONObject goodsDetail(@PathVariable String ofId,HttpServletRequest request){
        return orderService.orderDetail(ofId);
    }

    /**
     * 获取订单推荐商品
     * @return
     */
    @GetMapping("/orderRecGoods/{ofId}")
    public JSONObject orderRecGoods(@PathVariable String ofId,HttpServletRequest request){
        return orderService.orderRecGoods(ofId);
    }

    /**
     * 获取麦座订单详情
     * @return
     */
    @GetMapping("/orderMzDetail/{ofId}")
    public JSONObject orderMzDetail(@PathVariable String ofId,HttpServletRequest request){
        return orderService.orderMzDetail(ofId);
    }

    /**
     * 确认收货
     * @return
     */
    @PostMapping("/confirmReceipt")
    public JSONObject confirmReceipt(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return orderService.confirmReceipt(reqJson,request);
    }

    /**
     * 取消订单
     * @return
     */
    @PostMapping("/cancelOrder")
    public JSONObject cancelOrder(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return orderService.cancelOrder(reqJson,request);
    }

    /**
     * 获取订单待评价商品列表
     * @return
     */
    @GetMapping("/orderEvaluateGoods/{ofId}")
    public JSONObject orderEvaluateGoods(@PathVariable String ofId,HttpServletRequest request){
        return orderService.orderEvaluateGoods(ofId);
    }

    /**
     * 评价
     * @return
     */
    @PostMapping("/evaluateOrder")
    public JSONObject evaluateOrder(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return orderService.evaluateOrder(reqJson,request);
    }

    /**
     * 发起退款
     * @return
     */
    @PostMapping("/addRefund")
    public JSONObject addRefund(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return orderService.addRefund(reqJson,request);
    }

    /**
     * 查询我的退款列表
     * @return
     */
    @GetMapping("/myRefundPageList")
    public JSONObject queryMyRefundPageList(HttpServletRequest request){
        return orderService.queryMyRefundPageList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 获取订单待评价商品列表
     * @return
     */
    @GetMapping("/refundDetail/{refundId}")
    public JSONObject refundDetail(@PathVariable String refundId,HttpServletRequest request){
        return orderService.refundDetail(refundId);
    }

    /**
     * 撤回退款申请
     * @return
     */
    @PostMapping("/cancelRefund")
    public JSONObject cancelRefund(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return orderService.cancelRefund(reqJson);
    }
}