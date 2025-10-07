package com.centit.pay.biz.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.pay.biz.service.UnionPayService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>银联支付<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 前端控制器
 * @Date : 2021-01-11
 **/
@RestController
@RequestMapping("/unionPay")
public class UnionPayController {

    @Resource
    private UnionPayService unionPayService;

    /**
     * 测试提交订单
     * @return
     */
    @PostMapping("/testPay")
    public JSONObject testPay(@RequestBody JSONObject reqJson){
        return unionPayService.testPay(reqJson);
    }

}