package com.centit.pay.biz.controller;

import com.alibaba.fastjson.JSONObject;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.centit.pay.biz.service.AppPayService;
import com.centit.pay.utils.RequestParametersUtil;
import org.springframework.web.bind.annotation.*;

/**
 * <p>移动支付<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 前端控制器
 * @Date : 2021-03-10
 **/
@RestController
@RequestMapping("/appPay")
public class AppPayController {

    @Resource
    private AppPayService appPayService;

    /**
     * 吊起支付（微信/支付宝）
     */
    @PostMapping("/InPayment")
    public JSONObject queryList(@RequestBody JSONObject reqJson, HttpServletRequest request, HttpServletResponse response){
        return appPayService.InPayment(reqJson,request);
    }

    /**
     * 获取卖座验证码
     * @return
     */
    @PostMapping("/mzVerifycode")
    public JSONObject mzVerifycode(@RequestBody JSONObject reqJson,HttpServletRequest request, HttpServletResponse response){
        return appPayService.mzVerifycode(reqJson,request);
    }

    /**
     * 校验卖座验证码（获取资产业务key）
     */
    @PostMapping("/checkVerifycode")
    public JSONObject checkVerifycode(@RequestBody JSONObject reqJson, HttpServletRequest request, HttpServletResponse response){
        return appPayService.checkVerifycode(reqJson,request);
    }

    /**
     * 获取订单支付是否结束
     * @return
     */
    @GetMapping("/orderPayStatus/{id}")
    public JSONObject orderPayStatus(@PathVariable String id,HttpServletRequest request){
        return appPayService.orderPayStatus(id,request);
    }

    /**
     * 获取麦座积分和余额免密限额值
     * @return
     */
    @GetMapping("/accountLimit")
    public JSONObject accountLimit(HttpServletRequest request){
        return appPayService.accountLimit(request);
    }

    /**
     * 更新订单资产业务key
     */
    @PostMapping("/updateOrderPayKey")
    public JSONObject updateOrderPayKey(@RequestBody JSONObject reqJson, HttpServletRequest request, HttpServletResponse response){
        return appPayService.updateOrderPayKey(reqJson,request);
    }

    /**
     * 获取系统支付方式
     * @return
     */
    @GetMapping("/payments")
    public JSONObject queryPayments(HttpServletRequest request){
        return appPayService.queryPayments(RequestParametersUtil.getRequestParametersRetJson(request),request);
    }

}