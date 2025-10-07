package com.centit.shopping.webmgr.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.utils.RequestParametersUtil;
import com.centit.shopping.webmgr.service.TJpushInfoService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>极光推送历史记录<p>
 * @version : 1.0
 * @Author : lihao
 * @Description : 前端控制器
 * @Date : 2021-01-19
 **/
@RestController
@RequestMapping("/jpushInfo")
public class TJpushInfoController {

    @Resource
    private TJpushInfoService tJpushInfoService;

    /**
     * 新增
     * @return
     */
    @PostMapping("/create")
    public JSONObject create(@RequestBody JSONObject reqJson){
        return tJpushInfoService.create(reqJson);
    }

    /**
     * 分页列表查询
     * @return
     */
    @GetMapping("/queryPageList")
    public JSONObject queryPageList(HttpServletRequest request){
        return tJpushInfoService.queryPageList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

}