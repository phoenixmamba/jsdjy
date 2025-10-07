package com.centit.shopping.webmgr.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.utils.RequestParametersUtil;
import com.centit.shopping.webmgr.service.ArtClassLiveService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 前端控制器
 * @Date : 2021-02-25
 **/
@RestController
@RequestMapping("/webmgr/classLive")
public class ArtClassLiveController {

    @Resource
    private ArtClassLiveService artClassLiveService;

    /**
     * 获取UserSig
     * @return
     */
    @PostMapping("/getUserSig")
    public JSONObject getUserSig(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return artClassLiveService.getUserSig(reqJson);
    }

    /**
     * 获取艺教课程列表
     * @return
     */
    @GetMapping("/classPageList")
    public JSONObject getClassList(HttpServletRequest request){
        return artClassLiveService.getClassList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 获取老师信息
     * @return
     */
    @PostMapping("/getTeacherInfo")
    public JSONObject getTeacherInfo(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return artClassLiveService.getTeacherInfo(reqJson);
    }

    /**
     * 开课
     * @return
     */
    @PostMapping("/openClass")
    public JSONObject openClass(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return artClassLiveService.openClass(reqJson);
    }

    /**
     * 开课
     * @return
     */
    @PostMapping("/closeClass")
    public JSONObject closeClass(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return artClassLiveService.closeClass(reqJson);
    }


    /**
     * 获取课程具有听课权限的用户列表
     * @return
     */
    @GetMapping("/getClassUsers")
    public JSONObject getClassUsers(HttpServletRequest request){
        return artClassLiveService.getClassUsers(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 获取课程可选用户列表
     * @return
     */
    @GetMapping("/getClassToSelectUsers")
    public JSONObject getClassToSelectUsers(HttpServletRequest request){
        return artClassLiveService.getClassToSelectUsers(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 添加额外听课用户
     * @return
     */
    @PostMapping("/addClassExtraUser")
    public JSONObject addClassExtraUser(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return artClassLiveService.addClassExtraUser(reqJson);
    }

    /**
     * 删除额外听课用户
     * @return
     */
    @PostMapping("/delClassExtraUser")
    public JSONObject delClassExtraUser(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return artClassLiveService.delClassExtraUser(reqJson);
    }
}