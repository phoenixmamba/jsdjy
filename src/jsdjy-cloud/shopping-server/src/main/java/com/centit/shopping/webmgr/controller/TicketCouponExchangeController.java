package com.centit.shopping.webmgr.controller;

import com.alibaba.fastjson.JSONObject;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.centit.shopping.utils.RequestParametersUtil;
import org.springframework.web.bind.annotation.*;
import com.centit.shopping.webmgr.service.TicketCouponExchangeService;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 前端控制器
 * @Date : 2022-07-04
 **/
@RestController
@RequestMapping("/webmgr/ticketCouponExchange")
public class TicketCouponExchangeController {

    @Resource
    private TicketCouponExchangeService ticketCouponExchangeService;

    /**
     * 查询列表
     * @return
     */
    @GetMapping("/queryCouponActivityList")
    public JSONObject queryCouponActivityList(HttpServletRequest request, HttpServletResponse response){
        return ticketCouponExchangeService.queryCouponActivityList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 查询已创建的兑换码列表
     * @return
     */
    @GetMapping("/queryCouponCodeList")
    public JSONObject queryCouponCodeList(HttpServletRequest request, HttpServletResponse response){
        return ticketCouponExchangeService.queryCouponCodeList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 导出兑换码文件
     * @return
     */
    @GetMapping("/exportCouponCodeList")
    public void exportCouponCodeList(HttpServletRequest request, HttpServletResponse response){
        ticketCouponExchangeService.exportCouponCodeList(RequestParametersUtil.getRequestParametersRetJson(request),response);
    }

    /**
     * 新增兑换码活动
     * @return
     */
    @PostMapping("/addTicketCouponActivity")
    public JSONObject addTicketCouponActivity(@RequestBody JSONObject reqJson){
        return ticketCouponExchangeService.addTicketCouponActivity(reqJson);
    }

    /**
     * 编辑兑换码活动
     * @return
     */
    @PostMapping("/editTicketCouponActivity")
    public JSONObject editTicketCouponActivity(@RequestBody JSONObject reqJson){
        return ticketCouponExchangeService.editTicketCouponActivity(reqJson);
    }

    /**
     * 查询麦座优惠码的优惠详情
     * @return
     */
    @GetMapping("/queryPromotionDetail")
    public JSONObject queryPromotionDetail(HttpServletRequest request, HttpServletResponse response){
        return ticketCouponExchangeService.queryPromotionDetail(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 追加指定数量的兑换码
     * @return
     */
    @PostMapping("/addCouponCode")
    public JSONObject addCouponCode(@RequestBody JSONObject reqJson){
        return ticketCouponExchangeService.addCouponCode(reqJson);
    }

    /**
     * 上/下架兑换码活动
     * @return
     */
    @PostMapping("/pubTicketCouponActivity")
    public JSONObject pubTicketCouponActivity(@RequestBody JSONObject reqJson){
        return ticketCouponExchangeService.pubTicketCouponActivity(reqJson);
    }

    /**
     * 删除兑换码活动
     * @return
     */
    @PostMapping("/delTicketCouponActivity")
    public JSONObject delTicketCouponActivity(@RequestBody JSONObject reqJson){
        return ticketCouponExchangeService.delTicketCouponActivity(reqJson);
    }
}