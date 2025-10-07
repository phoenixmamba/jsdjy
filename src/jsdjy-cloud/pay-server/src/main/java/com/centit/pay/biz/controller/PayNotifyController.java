package com.centit.pay.biz.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.pay.biz.service.PayNotifyService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>移动支付<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 前端控制器
 * @Date : 2021-03-10
 **/
@RestController
@RequestMapping("/notify")
public class PayNotifyController {

    @Resource
    private PayNotifyService payNotifyService;

    /**
     * 订单支付（无现金）
     */
    @PostMapping("/accountPay")
    public JSONObject accountPay(@RequestBody JSONObject reqJson, HttpServletRequest request, HttpServletResponse response){
        return payNotifyService.appNotify(reqJson,request);
    }

    /**
     * 微信支付回调
     */
    @RequestMapping("/wxNotify")
    public void wxNotify( HttpServletRequest request, HttpServletResponse response){
        payNotifyService.wxNotify(request,response);
    }

    /**
     * 微信支付回调-停车专用
     */
    @RequestMapping("/wxNotify_park")
    public void wxParkNotify( HttpServletRequest request, HttpServletResponse response){
        payNotifyService.wxParkNotify(request,response);
    }

    /**
     * 支付宝支付回调
     */
    @RequestMapping("/aliNotify")
    public void aliNotify( HttpServletRequest request, HttpServletResponse response){
        payNotifyService.aliNotify(request,response);
    }
}