package com.centit.admin.system.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.admin.system.dto.LoginInfo;
import com.centit.admin.system.service.SystemService;
import com.centit.core.result.Result;
import com.centit.core.util.RequestParametersUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>商品分类管理<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 前端控制器
 * @Date : 2021-02-21
 **/
@RestController
@RequestMapping("/webmgr/system")
public class SystemController {

    @Resource
    private SystemService systemService;

    /**
     * 获取登录验证码
     * @return
     */
    @GetMapping("/getCode")
    public JSONObject getCode( HttpServletRequest request, HttpServletResponse response){
        return systemService.getCode(request);
    }


    /**
     * 登录
     * @return
     */
    @PostMapping("/login")
    public Result login(HttpServletRequest request, @Validated @RequestBody LoginInfo loginInfo){
        return systemService.login(request,loginInfo.getLoginName(),loginInfo.getPassword());
    }


    /**
     * 获取用户功能菜单
     * @return
     */
    @GetMapping("/menu/{userCode}")
    public JSONObject menu(@PathVariable String userCode, HttpServletRequest request, HttpServletResponse response){
        return systemService.menu(userCode, RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 校验用户登录
     * @return
     */
    @GetMapping("/hasLogin")
    public JSONObject hasLogin(HttpServletRequest request, HttpServletResponse response){
        return systemService.hasLogin(request);
    }

    /**
     * 注销
     * @return
     */
    @GetMapping("/logout")
    public JSONObject logout(HttpServletRequest request, HttpServletResponse response){
        return systemService.logout(request);
    }

    /**
     * 修改密码
     * @return
     */
    @PostMapping("/changepwd")
    public JSONObject changepwd(@RequestBody JSONObject reqJson){
        return systemService.changepwd(reqJson);
    }

}