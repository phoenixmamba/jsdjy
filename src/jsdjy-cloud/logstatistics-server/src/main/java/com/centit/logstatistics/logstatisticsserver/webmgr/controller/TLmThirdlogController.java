package com.centit.logstatistics.logstatisticsserver.webmgr.controller;

import com.alibaba.fastjson.JSONObject;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.centit.logstatistics.logstatisticsserver.webmgr.utils.RequestParametersUtil;
import org.springframework.web.bind.annotation.*;
import com.centit.logstatistics.logstatisticsserver.webmgr.service.TLmThirdlogService;

/**
 * <p> <p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description :  前端控制器
 * @Date : 2021-07-05
 **/
@RestController
@RequestMapping("/tLmThirdlog")
public class TLmThirdlogController {

    @Resource
    private TLmThirdlogService tLmThirdlogService;

    /**
     * 查询分页列表
     * @return
     */
    @GetMapping("/pageList")
    public JSONObject queryPageList(HttpServletRequest request, HttpServletResponse response){
        return tLmThirdlogService.queryPageList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

}