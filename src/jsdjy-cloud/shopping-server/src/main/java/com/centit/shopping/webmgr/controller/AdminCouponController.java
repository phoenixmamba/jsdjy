package com.centit.shopping.webmgr.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.utils.RequestParametersUtil;
import com.centit.shopping.webmgr.service.AdminCouponService;
import com.centit.shopping.webmgr.service.AdminGoodsManageService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>商品分类管理<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 前端控制器
 * @Date : 2021-02-21
 **/
@RestController
@RequestMapping("/webmgr/adminCoupon")
public class AdminCouponController {

    @Resource
    private AdminCouponService adminCouponService;


    /**
     * 同步优惠券
     * @return
     */
    @PostMapping("/syncCoupons")
    public JSONObject syncCoupons(@RequestBody JSONObject reqJson){
        return adminCouponService.syncCoupons(reqJson);
    }

    /**
     * 编辑优惠券
     * @return
     */
    @PostMapping("/editCoupon")
    public JSONObject editCoupon(@RequestBody JSONObject reqJson){
        return adminCouponService.editCoupon(reqJson);
    }

    /**
     * 创建兑换券
     * @return
     */
    @PostMapping("/createWriteOffCoupon")
    public JSONObject createWriteOffCoupon(@RequestBody JSONObject reqJson){
        return adminCouponService.createWriteOffCoupon(reqJson);
    }

    /**
     * 创建优惠券
     * @return
     */
    @PostMapping("/createCoupon")
    public JSONObject createCoupon(@RequestBody JSONObject reqJson){
        return adminCouponService.createCoupon(reqJson);
    }

    /**
     * 查询优惠券列表(全部)
     * @return
     */
    @GetMapping("/allCouponPageList")
    public JSONObject queryAllCouponPageList(HttpServletRequest request){
        return adminCouponService.queryAllCouponPageList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 查询线上可发放的优惠券列表
     * @return
     */
    @GetMapping("/onlineCouponPageList")
    public JSONObject queryOnlineCouponPageList(HttpServletRequest request){
        return adminCouponService.queryOnlineCouponPageList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 查询优惠券列表
     * @return
     */
    @GetMapping("/couponPageList")
    public JSONObject queryCouponPageList(HttpServletRequest request){
        return adminCouponService.queryCouponPageList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 查询兑换券列表
     * @return
     */
    @GetMapping("/writeOffCouponPageList")
    public JSONObject queryWriteOffCouponPageList(HttpServletRequest request){
        return adminCouponService.queryWriteOffCouponPageList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 设置兑换券核销账户
     * @return
     */
    @PostMapping("/editWriteOffCount")
    public JSONObject editWriteOffCount(@RequestBody JSONObject reqJson){
        return adminCouponService.editWriteOffCount(reqJson);
    }


    /**
     * 删除优惠券
     * @return
     */
    @PostMapping("/delCoupon")
    public JSONObject delCoupon(@RequestBody JSONObject reqJson){
        return adminCouponService.delCoupon(reqJson);
    }

    /**
     * 上/下架优惠券
     * @return
     */
    @PostMapping("/pubCoupon")
    public JSONObject pubCoupon(@RequestBody JSONObject reqJson){
        return adminCouponService.pubCoupon(reqJson);
    }

    /**
     * 设置优惠券单人限领数量
     * @return
     */
    @PostMapping("/setCouponLimit")
    public JSONObject setCouponLimit(@RequestBody JSONObject reqJson){
        return adminCouponService.setCouponLimit(reqJson);
    }

    /**
     * 查询优惠券详情
     * @return
     */
    @GetMapping("/couponDetail/{right_No}")
    public JSONObject queryCouponDetail(@PathVariable String right_No,HttpServletRequest request){
        return adminCouponService.queryCouponDetail(right_No);
    }

    /**
     * 获取优惠券已关联的商品/分类
     * @return
     */
    @GetMapping("/couponRelation/{right_No}")
    public JSONObject queryCouponRelation(@PathVariable String right_No,HttpServletRequest request){
        return adminCouponService.queryCouponRelation(right_No);
    }

    /**
     * 保存优惠券关联商品/分类
     * @return
     */
    @PostMapping("/saveCouponRelation")
    public JSONObject saveCouponRelation(@RequestBody JSONObject reqJson){
        return adminCouponService.saveCouponRelation(reqJson);
    }

    /**
     * 删除优惠券关联商品/分类
     * @return
     */
    @PostMapping("/delCouponRelation")
    public JSONObject delCouponRelation(@RequestBody JSONObject reqJson){
        return adminCouponService.delCouponRelation(reqJson);
    }

    /**
     * 查询优惠券指定优惠券发放记录
     * @return
     */
    @GetMapping("/couponGrantPageList")
    public JSONObject queryCouponGrantPageList(HttpServletRequest request){
        return adminCouponService.queryCouponGrantPageList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 查询优惠券指定优惠券消费记录
     * @return
     */
    @GetMapping("/couponRecordPageList")
    public JSONObject queryCouponRecordPageList(HttpServletRequest request){
        return adminCouponService.queryCouponRecordPageList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 查询已配置的可直接发放的优惠券列表
     * @return
     */
    @GetMapping("/queryDirectgrantCoupons")
    public JSONObject queryDirectgrantCoupons(HttpServletRequest request){
        return adminCouponService.queryDirectgrantCoupons(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 添加直接发放的优惠券
     * @return
     */
    @PostMapping("/addDirectgrantCoupon")
    public JSONObject addDirectgrantCoupon(@RequestBody JSONObject reqJson){
        return adminCouponService.addDirectgrantCoupon(reqJson);
    }

    /**
     * 删除直接发放的优惠券
     * @return
     */
    @PostMapping("/delDirectgrantCoupon")
    public JSONObject delDirectgrantCoupon(@RequestBody JSONObject reqJson){
        return adminCouponService.delDirectgrantCoupon(reqJson);
    }

    /**
     * 直接发放优惠券
     * @return
     */
    @PostMapping("/directGrantCoupon")
    public JSONObject directGrantCoupon(@RequestBody JSONObject reqJson){
        return adminCouponService.directGrantCoupon(reqJson);
    }
}