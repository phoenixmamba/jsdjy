package com.centit.shopping.webmgr.controller;

import com.alibaba.fastjson.JSONObject;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.centit.shopping.utils.RequestParametersUtil;
import org.springframework.web.bind.annotation.*;
import com.centit.shopping.webmgr.service.ShoppingActivityService;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 前端控制器
 * @Date : 2021-05-27
 **/
@RestController
@RequestMapping("/webmgr/shoppingActivity")
public class ShoppingActivityController {

    @Resource
    private ShoppingActivityService shoppingActivityService;

    /**
     * 查询活动分页列表
     * @return
     */
    @GetMapping("/pageList")
    public JSONObject queryPageList(HttpServletRequest request){
        return shoppingActivityService.queryPageList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 查看活动详情
     * @return
     */
    @GetMapping("/acticityDetail/{acId}")
    public JSONObject shoppingActivityService(@PathVariable String acId,HttpServletRequest request){
        return shoppingActivityService.queryActicityDetail(acId);
    }

    /**
     * 创建活动
     * @return
     */
    @PostMapping("/addActivity")
    public JSONObject addActivity(@RequestBody JSONObject reqJson){
        return shoppingActivityService.addActivity(reqJson);
    }

    /**
     * 编辑活动
     * @return
     */
    @PostMapping("/editActivity")
    public JSONObject editActivity(@RequestBody JSONObject reqJson){
        return shoppingActivityService.editActivity(reqJson);
    }

    /**
     * 上/下架活动
     * @return
     */
    @PostMapping("/pubActivity")
    public JSONObject pubActivity(@RequestBody JSONObject reqJson){
        return shoppingActivityService.pubActivity(reqJson);
    }

    /**
     * 删除活动
     * @return
     */
    @PostMapping("/delActivity")
    public JSONObject delActivity(@RequestBody JSONObject reqJson){
        return shoppingActivityService.delActivity(reqJson);
    }

    /**
     * 查询生日活动已关联优惠券
     * @return
     */
    @GetMapping("/queryBirthCoupons")
    public JSONObject queryBirthCoupons(HttpServletRequest request){
        return shoppingActivityService.queryBirthCoupons(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 编辑生日活动关联优惠券
     * @return
     */
    @PostMapping("/editBirthCoupons")
    public JSONObject editBirthCoupons(@RequestBody JSONObject reqJson){
        return shoppingActivityService.editBirthCoupons(reqJson);
    }

    /**
     * 查询新人活动已关联优惠券
     * @return
     */
    @GetMapping("/queryNewCoupons")
    public JSONObject queryNewCoupons(HttpServletRequest request){
        return shoppingActivityService.queryNewCoupons(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 编辑新人活动关联优惠券
     * @return
     */
    @PostMapping("/editNewCoupons")
    public JSONObject editNewCoupons(@RequestBody JSONObject reqJson){
        return shoppingActivityService.editNewCoupons(reqJson);
    }
}