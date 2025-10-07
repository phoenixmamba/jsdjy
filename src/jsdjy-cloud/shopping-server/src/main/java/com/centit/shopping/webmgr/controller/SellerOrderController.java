package com.centit.shopping.webmgr.controller;

import com.alibaba.fastjson.JSONObject;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.centit.shopping.utils.RequestParametersUtil;
import org.springframework.web.bind.annotation.*;
import com.centit.shopping.webmgr.service.SellerOrderService;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 前端控制器
 * @Date : 2021-02-25
 **/
@RestController
@RequestMapping("/webmgr/sellerOrder")
public class SellerOrderController {

    @Resource
    private SellerOrderService sellerOrderService;

    /**
     * 查询列表
     * @return
     */
    @GetMapping("/pageList")
    public JSONObject queryPageList(HttpServletRequest request){
        return sellerOrderService.queryPageList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 导出订单数据
     * @return
     */
    @GetMapping("/exportOrderList")
    public JSONObject exportOrderList(HttpServletRequest request, HttpServletResponse response){
        return sellerOrderService.exportOrderList(RequestParametersUtil.getRequestParametersRetJson(request),response);
    }

    /**
     * 获取订单详情
     * @return
     */
    @GetMapping("/orderDetail/{ofId}")
    public JSONObject goodsDetail(@PathVariable String ofId,HttpServletRequest request){
        return sellerOrderService.orderDetail(ofId);
    }

    /**
     * 调整订单费用
     * @return
     */
    @PostMapping("/changeOrderPrice")
    public JSONObject changeOrderPrice(@RequestBody JSONObject reqJson){
        return sellerOrderService.changeOrderPrice(reqJson);
    }

    /**
     * 取消订单
     * @return
     */
    @PostMapping("/cancelOrder")
    public JSONObject cancelOrder(@RequestBody JSONObject reqJson){
        return sellerOrderService.cancelOrder(reqJson);
    }

    /**
     * 获取所有快递公司
     * @return
     */
    @GetMapping("/queryExpressCompanys")
    public JSONObject queryExpressCompanys(HttpServletRequest request){
        return sellerOrderService.queryExpressCompanys(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 确认发货
     * @return
     */
    @PostMapping("/confirmDelivery")
    public JSONObject confirmDelivery(@RequestBody JSONObject reqJson){
        return sellerOrderService.confirmDelivery(reqJson);
    }

    /**
     * 修改物流
     * @return
     */
    @PostMapping("/modifyLogistics")
    public JSONObject modifyLogistics(@RequestBody JSONObject reqJson){
        return sellerOrderService.modifyLogistics(reqJson);
    }

    /**
     * 查询物流
     * @return
     */
    @GetMapping("/queryLogistics/{ofId}")
    public JSONObject queryLogistics(@PathVariable String ofId,HttpServletRequest request){
        return sellerOrderService.queryLogistics(ofId);
    }

    /**
     * 发起退款
     * @return
     */
    @PostMapping("/addRefund")
    public JSONObject addRefund(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return sellerOrderService.addRefund(reqJson,request);
    }

    /**
     * 异常处理
     * @return
     */
    @PostMapping("/handException")
    public JSONObject handException(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return sellerOrderService.handException(reqJson,request);
    }
}