package com.centit.shopping.webmgr.controller;

import com.alibaba.fastjson.JSONObject;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.centit.shopping.utils.RequestParametersUtil;
import org.springframework.web.bind.annotation.*;
import com.centit.shopping.webmgr.service.TExportFileService;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 前端控制器
 * @Date : 2022-07-24
 **/
@RestController
@RequestMapping("/webmgr/exportFile")
public class TExportFileController {

    @Resource
    private TExportFileService tExportFileService;

    /**
     * 查询列表
     * @return
     */
    @GetMapping("/queryList")
    public JSONObject queryList(HttpServletRequest request, HttpServletResponse response){
        return tExportFileService.queryList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 下载文件
     * @return
     */
    @GetMapping("/downloadFile")
    public void downloadFile(HttpServletRequest request, HttpServletResponse response){
        tExportFileService.exportFile(RequestParametersUtil.getRequestParametersRetJson(request),response);
    }
}