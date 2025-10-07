package com.centit.shopping.biz.controller;

import com.alibaba.fastjson.JSONObject;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.*;
import com.centit.shopping.biz.service.OndemandService;

/**
 * <p>点播<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 前端控制器
 * @Date : 2021-04-21
 **/
@RestController
@RequestMapping("/ondemand")
public class OndemandController {

    @Resource
    private OndemandService ondemandService;

    /**
     * 订单页面渲染
     * @return
     */
    @PostMapping("/renderOndemandOrder")
    public JSONObject renderOndemandOrder(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return ondemandService.renderOndemandOrder(reqJson,request);
    }

    /**
     * 下单
     * @return
     */
    @PostMapping("/addOndemandOrder")
    public JSONObject addOndemandOrder(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return ondemandService.addOndemandOrder(reqJson,request);
    }

}