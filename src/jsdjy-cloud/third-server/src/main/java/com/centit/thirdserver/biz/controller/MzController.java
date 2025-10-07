package com.centit.thirdserver.biz.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.core.model.OrderPayInfo;
import com.centit.core.result.Result;
import com.centit.thirdserver.biz.service.MzService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2025/1/3 16:41
 **/
@RestController
@RequestMapping("/mz")
public class MzController {
    @Resource
    private MzService mzService;
    /**
     * 获取麦座限额信息
     * @return
     */
    @GetMapping("/getAssetRule")
    public Result<JSONObject> getAssetRule(HttpServletRequest request){
        return mzService.getAssetRule();
    }

    /**
     * 获取会员账户资产信息
     * @return
     */
    @GetMapping("/getUserAccountInfo")
    public Result<JSONObject> getUserAccountInfo(HttpServletRequest request){
        String mzUserId =request.getParameter("mzUserId");
        return mzService.getUserAccountInfo(mzUserId);
    }

    /**
     * 获取会员收货地址详情
     * @return
     */
    @GetMapping("/getUserAddressDetail")
    public Result<JSONObject> getUserAddressDetail(HttpServletRequest request){
        String mzUserId =request.getParameter("mzUserId");
        String addressId =request.getParameter("addressId");
        return mzService.getUserAddressDetail(mzUserId,addressId);
    }

    /**
     * 获取会员收货地址列表
     * @return
     */
    @GetMapping("/getUserAddressList")
    public Result<JSONObject> getUserAddressList(HttpServletRequest request){
        String mzUserId =request.getParameter("mzUserId");
        int pageSize = Integer.parseInt(request.getParameter("pageSize"));
        int page = Integer.parseInt(request.getParameter("page"));
        return mzService.getUserAddressList(mzUserId,pageSize,page);
    }

    /**
     * 订单提交
     * @return 麦座订单id
     */
    @PostMapping("/confirmOrder")
    public Result<String> confirmOrder(@RequestBody @Validated OrderPayInfo orderPayInfo){
        return mzService.confirmOrder(orderPayInfo);
    }

    /**
     * 扣减余额
     * @return
     */
    @PostMapping("/cutMoney")
    public Result cutMoney(@RequestBody @Validated OrderPayInfo orderPayInfo){
        return mzService.cutMoney(orderPayInfo);
    }

    /**
     * 扣减积分
     * @return
     */
    @PostMapping("/cutPoint")
    public Result cutPoint(@RequestBody @Validated OrderPayInfo orderPayInfo){
        return mzService.cutPoint(orderPayInfo);
    }

    /**
     * 扣减积分
     * @return
     */
    @PostMapping("/checkVerifyCode")
    public Result checkVerifyCode(HttpServletRequest request){
        String mzUserId =request.getParameter("mzUserId");
        String verifyCode =request.getParameter("verifyCode");
        return mzService.checkVerifyCode(mzUserId,verifyCode);
    }
}
