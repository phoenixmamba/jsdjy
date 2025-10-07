package com.centit.shopping.webmgr.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.utils.RequestParametersUtil;
import com.centit.shopping.webmgr.service.WriteOffService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 前端控制器
 * @Date : 2021-02-21
 **/
@RestController
@RequestMapping("/webmgr/writeoff")
public class WriteOffController {

    @Resource
    private WriteOffService writeOffService;


    /**
     * 查询核销记录
     * @return
     */
    @GetMapping("/writeOffRecordList")
    public JSONObject queryWriteOffRecordList(HttpServletRequest request){
        return writeOffService.queryWriteOffRecordList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 导出核销记记录
     * @return
     */
    @GetMapping("/exportWriteOffRecordList")
    public JSONObject exportWriteOffRecordList(HttpServletRequest request, HttpServletResponse response){
        return writeOffService.exportWriteOffRecordList(RequestParametersUtil.getRequestParametersRetJson(request),response);
    }

}