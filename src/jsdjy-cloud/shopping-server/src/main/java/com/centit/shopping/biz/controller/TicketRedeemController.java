package com.centit.shopping.biz.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.biz.service.TicketRedeemService;
import com.centit.shopping.utils.RequestParametersUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>兑换码<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 前端控制器
 * @Date : 2022-07-18
 **/
@RestController
@RequestMapping("/redeemCode")
public class TicketRedeemController {

    @Resource
    private TicketRedeemService ticketRedeemService;

    /**
     * 查询演出列表
     * @return
     */
    @GetMapping("/projectList")
    public JSONObject queryProjectList(HttpServletRequest request, HttpServletResponse response){
        return ticketRedeemService.queryProjectList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 项目详情
     * @return
     */
    @GetMapping("/projectDetail")
    public JSONObject projectDetail(HttpServletRequest request, HttpServletResponse response){
        return ticketRedeemService.projectDetail(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 兑换项目
     * @return
     */
    @PostMapping("/exchangeProject")
    public JSONObject exchangeProject(@RequestBody JSONObject reqJson){
        return ticketRedeemService.exchangeProject(reqJson);
    }


    /**
     * 查询我的兑换记录
     * @return
     */
    @GetMapping("/myExchangeList")
    public JSONObject queryMyExchangeList(HttpServletRequest request, HttpServletResponse response){
        return ticketRedeemService.queryMyExchangeList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 获取兑换详情
     * @return
     */
    @GetMapping("/myExchangeDetail")
    public JSONObject queryMyExchangeDetail(HttpServletRequest request, HttpServletResponse response){
        return ticketRedeemService.queryMyExchangeDetail(RequestParametersUtil.getRequestParametersRetJson(request));
    }


    /**
     * 刷新核销码
     * @return
     */
    @PostMapping("/refreshOffCode")
    public JSONObject refreshOffCode(@RequestBody JSONObject reqJson){
        return ticketRedeemService.refreshOffCode(reqJson);
    }
}