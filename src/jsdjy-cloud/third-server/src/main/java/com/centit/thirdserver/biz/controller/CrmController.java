package com.centit.thirdserver.biz.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.core.result.Result;
import com.centit.thirdserver.biz.service.CrmService;
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
@RequestMapping("/crm")
public class CrmController {
    @Resource
    private CrmService crmService;
    /**
     * 获取优惠券列表
     * @return
     */
    @GetMapping("/getCouponList")
    public Result<JSONObject> getCouponList(HttpServletRequest request){
        return crmService.getCouponList();
    }

    /**
     * 获取优惠券详情
     * @return
     */
    @GetMapping("/getCouponDtl")
    public Result<JSONObject> getCouponDtl(HttpServletRequest request){
        String id =request.getParameter("id");
        return Result.jsonObjectResult(crmService.getCouponDtl(id));
    }

    /**
     * 获取会员优惠券列表
     * @return
     */
    @GetMapping("/getUserCouponList")
    public Result<JSONArray> getUserCouponList(HttpServletRequest request){
        String userId =request.getParameter("userId");
        String regPhone =request.getParameter("regPhone");
        String flag =request.getParameter("flag");
        return crmService.getUserCouponList(userId,regPhone,flag);
    }

    /**
     * 核销优惠券
     * @return
     */
    @PostMapping("/writeoffCoupon")
    public Result<String> writeoffCoupon(@RequestParam String cid){
        JSONObject couponObj = crmService.getCouponDtl(cid);
        crmService.writeoffCoupon(cid);
        return Result.defaultSuccess(couponObj.getString("right_No"));
    }
}
