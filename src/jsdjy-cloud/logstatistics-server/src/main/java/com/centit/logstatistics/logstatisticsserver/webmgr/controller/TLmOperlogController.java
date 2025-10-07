package com.centit.logstatistics.logstatisticsserver.webmgr.controller;

import com.alibaba.fastjson.JSONObject;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.centit.logstatistics.logstatisticsserver.webmgr.utils.RequestParametersUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;
import com.centit.logstatistics.logstatisticsserver.webmgr.service.TLmOperlogService;

/**
 * <p> <p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description :  前端控制器
 * @Date : 2020-05-28
 **/
@RestController
@RequestMapping("/tLmOperlog")
public class TLmOperlogController {

    @Resource
    private TLmOperlogService tLmOperlogService;

    /**
     * 操作日志插入
     * @return
     */
    @PostMapping("/addOperLog")
    public JSONObject addOperLog(@RequestBody JSONObject reqJson){
        return tLmOperlogService.addOperLog(reqJson);
    }


    /**
     * 查询操作日志
     * @return
     */
    @GetMapping("/queryOperPageList")
    public JSONObject queryOperPageList(HttpServletRequest request, HttpServletResponse response){
        return tLmOperlogService.queryOperPageList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

}