package com.centit.ticket.biz.controller;

import com.alibaba.fastjson.JSONObject;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.centit.ticket.utils.RequestParametersUtil;
import org.springframework.web.bind.annotation.*;
import com.centit.ticket.biz.service.TicketRealnameService;

/**
 * <p>实名信息<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 前端控制器
 * @Date : 2021-04-20
 **/
@RestController
@RequestMapping("/realname")
public class TicketRealnameController {

    @Resource
    private TicketRealnameService ticketRealnameService;

    /**
     * 实名认证信息列表
     * @return
     */
    @GetMapping("/realNameList")
    public JSONObject queryRealNameList(HttpServletRequest request, HttpServletResponse response){
        return ticketRealnameService.queryRealNameList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 实名信息详情
     * @return
     */
    @GetMapping("/realNameDetail")
    public JSONObject queryRealNameDetail(HttpServletRequest request, HttpServletResponse response){
        return ticketRealnameService.queryRealNameDetail(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 新增实名信息
     * @return
     */
    @PostMapping("/addRealName")
    public JSONObject addRealName(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return ticketRealnameService.addRealName(reqJson);
    }

    /**
     * 修改实名信息
     * @return
     */
    @PostMapping("/editRealName")
    public JSONObject editRealName(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return ticketRealnameService.editRealName(reqJson);
    }

    /**
     * 删除实名信息
     * @return
     */
    @PostMapping("/delRealName")
    public JSONObject delRealName(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return ticketRealnameService.delRealName(reqJson);
    }
}