package com.centit.shopping.biz.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.biz.service.ClassLiveService;
import com.centit.shopping.utils.RequestParametersUtil;
import com.centit.shopping.webmgr.service.ArtClassLiveService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>艺教课程开课<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 前端控制器
 * @Date : 2021-02-25
 **/
@RestController
@RequestMapping("/classLive")
public class ClassLiveController {

    @Resource
    private ClassLiveService classLiveService;

    /**
     * 获取UserSig
     * @return
     */
    @PostMapping("/getUserSig")
    public JSONObject getUserSig(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return classLiveService.getUserSig(reqJson);
    }

    /**
     * 获取艺教课程列表
     * @return
     */
    @GetMapping("/classList")
    public JSONObject getClassList(HttpServletRequest request){
        return classLiveService.getClassList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 校验用户是否可进入课程
     * @return
     */
    @PostMapping("/checkUser")
    public JSONObject checkUser(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return classLiveService.checkUser(reqJson);
    }
}