package com.centit.shopping.biz.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.biz.service.WalletService;
import com.centit.shopping.utils.RequestParametersUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>充值<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 前端控制器
 * @Date : 2021-03-19
 **/
@RestController
@RequestMapping("/wallet")
public class WalletController {

    @Resource
    private WalletService walletService;


    /**
     * 获取用户当前账户积分与余额
     * @return
     */
    @PostMapping("/accountInfo")
    public JSONObject queryAccountInfo(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return walletService.queryAccountInfo(reqJson);
    }

    /**
     * 获取用户会员资产变更记录
     * @return
     */
    @GetMapping("/assetRecord")
    public JSONObject queryAssetRecord(HttpServletRequest request, HttpServletResponse response){
        return walletService.queryAssetRecord(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 领取账户资产
     * @return
     */
    @PostMapping("/addAsset")
    public JSONObject addAsset(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return walletService.addAsset(reqJson);
    }

    /**
     * 订单页面渲染
     * @return
     */
    @PostMapping("/renderRechargeOrder")
    public JSONObject renderRechargeOrder(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return walletService.renderRechargeOrder(reqJson,request);
    }

    /**
     * 下单
     * @return
     */
    @PostMapping("/addRechargeOrder")
    public JSONObject addRechargeOrder(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return walletService.addRechargeOrder(reqJson,request);
    }

    /**
     * 我的充值记录
     * @return
     */
    @GetMapping("/myRechargeList")
    public JSONObject myRechargeList(HttpServletRequest request, HttpServletResponse response){
        return walletService.myRechargeList(RequestParametersUtil.getRequestParametersRetJson(request));
    }
}