package com.centit.shopping.webmgr.controller;

import com.alibaba.fastjson.JSONObject;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.centit.shopping.utils.RequestParametersUtil;
import org.springframework.web.bind.annotation.*;
import com.centit.shopping.webmgr.service.TSystemStatusService;

/**
 * <p><p>
 *
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 前端控制器
 * @Date : 2021-05-14
 **/
@RestController
@RequestMapping("/webmgr/tSystemStatus")
public class TSystemStatusController {

    @Resource
    private TSystemStatusService systemStatusService;

    /**
     * 查询列表
     *
     * @return
     */
    @GetMapping("/queryList")
    public JSONObject queryList(HttpServletRequest request, HttpServletResponse response) {
        return systemStatusService.queryList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

}