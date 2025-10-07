package com.centit.ticket.biz.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.ticket.biz.service.TicketProjectService;
import com.centit.ticket.utils.RequestParametersUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 前端控制器
 * @Date : 2021-04-08
 **/
@RestController
@RequestMapping("/project")
public class TicketProjectController {

    @Resource
    private TicketProjectService ticketProjectService;

    /**
     * 演出首页数据
     * @return
     */
    @GetMapping("/homeData")
    public JSONObject queryHomeData(HttpServletRequest request, HttpServletResponse response){
        return ticketProjectService.queryHomeData(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 获取演出分类
     * @return
     */
    @GetMapping("/ticketClass")
    public JSONObject queryTicketClass(HttpServletRequest request, HttpServletResponse response){
        return ticketProjectService.queryTicketClass(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 获取首页演出列表
     * @return
     */
    @GetMapping("homePageList")
    public JSONObject homePageList(HttpServletRequest request, HttpServletResponse response){
        return ticketProjectService.queryProjectList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 获取演出列表
     * @return
     */
    @GetMapping("pageList")
    public JSONObject queryList(HttpServletRequest request, HttpServletResponse response){
        return ticketProjectService.queryList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    @GetMapping("/projectDetail")
    public JSONObject projectDetail(HttpServletRequest request, HttpServletResponse response){
        return ticketProjectService.projectDetail(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    @GetMapping("/queryEventDays")
    public JSONObject queryEventDays(HttpServletRequest request, HttpServletResponse response){
        return ticketProjectService.queryEventDays(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    @GetMapping("/getSign")
    public JSONObject getSign(HttpServletRequest request, HttpServletResponse response){
        return ticketProjectService.getSign(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    @GetMapping("/queryDefaultAddress")
    public JSONObject queryDefaultAddress(HttpServletRequest request, HttpServletResponse response){
        return ticketProjectService.queryDefaultAddress(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 订单渲染
     * @return
     */
    @PostMapping("/renderOrder")
    public JSONObject renderOrder(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return ticketProjectService.renderOrder(reqJson,request);
    }

    /**
     * 下单
     * @return
     */
    @PostMapping("/addOrder")
    public JSONObject addOrder(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return ticketProjectService.addOrder(reqJson,request);
    }

    /**
     * 收藏
     * @return
     */
    @PostMapping("/addFavorite")
    public JSONObject addFavorite(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return ticketProjectService.addFavorite(reqJson,request);
    }

    /**
     * 取消收藏
     * @return
     */
    @PostMapping("/cancelFavorite")
    public JSONObject cancelFavorite(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return ticketProjectService.cancelFavorite(reqJson,request);
    }

    /**
     * 我的收藏
     * @return
     */
    @GetMapping("/myFavList")
    public JSONObject myFavList(HttpServletRequest request){
        return ticketProjectService.myFavList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 我的足迹
     * @return
     */
    @GetMapping("/myHistoryList")
    public JSONObject myHistoryList(HttpServletRequest request){
        return ticketProjectService.myHistoryList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 清除历史轨迹
     * @return
     */
    @PostMapping("/clearHistory")
    public JSONObject clearHistory(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return ticketProjectService.clearHistory(reqJson,request);
    }
}