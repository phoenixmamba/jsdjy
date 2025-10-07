package com.centit.admin.system.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.admin.system.service.FUserinfoService;
import com.centit.core.util.RequestParametersUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>用户信息<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 前端控制器
 * @Date : 2020-06-23
 **/
@RestController
@RequestMapping("/webmgr/userinfo")
public class FUserinfoController {

    @Resource
    private FUserinfoService fUserinfoService;


    /**
     * 查询列表
     * @return
     */
    @GetMapping("/userinfo")
    public JSONObject queryUserPageList(HttpServletRequest request, HttpServletResponse response){
        return fUserinfoService.queryUserPageList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 校验用户登录名是否可用
     * @return
     */
    @GetMapping("/checkLoginName")
    public JSONObject checkLoginName(HttpServletRequest request, HttpServletResponse response){
        return fUserinfoService.checkLoginName(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 新增用户
     * @return
     */
    @PostMapping("/addUser")
    public JSONObject addUser(@RequestBody JSONObject reqJson){
        return fUserinfoService.addUser(reqJson);
    }

    /**
     * 编辑用户
     * @return
     */
    @PostMapping("/editUser")
    public JSONObject editUser(@RequestBody JSONObject reqJson){
        return fUserinfoService.editUser(reqJson);
    }

    /**
     * 删除用户
     * @return
     */
    @PostMapping("/deleteUser")
    public JSONObject deleteUser(@RequestBody JSONObject reqJson){
        return fUserinfoService.deleteUser(reqJson);
    }


    /**
     * 查询用户信息
     * @return
     */
    @GetMapping("/userinfo/{userCode}")
    public JSONObject getUserInfo(@PathVariable String userCode, HttpServletRequest request, HttpServletResponse response){
        return fUserinfoService.getUserInfo(userCode);
    }

    /**
     * 查询用户机构
     * @return
     */
    @GetMapping("/userunits/{userCode}")
    public JSONObject getUserUnitPageList(@PathVariable String userCode, HttpServletRequest request, HttpServletResponse response){
        return fUserinfoService.getUserUnitPageList(userCode,RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 查询用户机构
     * @return
     */
    @GetMapping("/resetPwd/{userCode}")
    public JSONObject resetPwd(@PathVariable String userCode, HttpServletRequest request, HttpServletResponse response){
        return fUserinfoService.resetPwd(userCode);
    }
}