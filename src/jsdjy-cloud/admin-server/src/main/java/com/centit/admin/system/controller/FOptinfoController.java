package com.centit.admin.system.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.admin.system.service.FOptinfoService;
import com.centit.core.util.RequestParametersUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>系统菜单<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 前端控制器
 * @Date : 2020-03-04
 **/
@RestController
@RequestMapping("/webmgr/optinfo")
public class FOptinfoController {

    @Resource
    private FOptinfoService fOptinfoService;

    /**
     * 查询列表
     * @return
     */
    @GetMapping("/sub")
    public JSONObject sub(HttpServletRequest request, HttpServletResponse response){
        JSONObject retJson = fOptinfoService.sub(RequestParametersUtil.getRequestParametersRetJson(request));
        return retJson;
    }

    /**
     * 新增菜单
     * @return
     */
    @PostMapping("addOptinfo")
    public JSONObject addOptinfo(@RequestBody JSONObject reqJson){
        return fOptinfoService.addOptinfo(reqJson);
    }

    /**
     * 查询菜单编码是否可用
     * @return
     */
    @GetMapping("/notexists/{optId}")
    public JSONObject notexists(@PathVariable String optId, HttpServletRequest request, HttpServletResponse response){
        return fOptinfoService.notexists(optId);
    }

    /**
     * 删除菜单
     * @return
     */
    @PostMapping("deleteOptinfo")
    public JSONObject deleteOptinfo(@RequestBody JSONObject reqJson){
        return fOptinfoService.deleteOptinfo(reqJson);
    }

    /**
     * 编辑菜单
     * @return
     */
    @PostMapping("editOptinfo")
    public JSONObject editOptinfo(@RequestBody JSONObject reqJson){
        return fOptinfoService.editOptinfo(reqJson);
    }

    /**
     * 查询列表
     * @return
     */
    @GetMapping("/detail/{optId}")
    public JSONObject optinfo(@PathVariable String optId, HttpServletRequest request, HttpServletResponse response){
        JSONObject retJson = fOptinfoService.optinfo(optId);
        return retJson;
    }

    /**
     * 新增菜单操作
     * @return
     */
    @PostMapping("addOptdef")
    public JSONObject addOptdef(@RequestBody JSONObject reqJson){
        return fOptinfoService.addOptdef(reqJson);
    }

    /**
     * 查询操作编码是否可用
     * @return
     */
    @GetMapping("/defnotexists/{optCode}")
    public JSONObject defnotexists(@PathVariable String optCode, HttpServletRequest request, HttpServletResponse response){
        return fOptinfoService.defnotexists(optCode);
    }

    /**
     * 删除菜单操作
     * @return
     */
    @PostMapping("deleteOptdef")
    public JSONObject deleteOptdef(@RequestBody JSONObject reqJson){
        return fOptinfoService.deleteOptdef(reqJson);
    }

    /**
     * 编辑菜单操作
     * @return
     */
    @PostMapping("editOptdef")
    public JSONObject editOptdef(@RequestBody JSONObject reqJson){
        return fOptinfoService.editOptdef(reqJson);
    }

    /**
     * 查询系统菜单树
     * @return
     */
    @GetMapping("/poweropts/{userCode}")
    public JSONObject poweropts(@PathVariable String userCode, HttpServletRequest request){
        return fOptinfoService.poweropts(userCode,RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 查询系统菜单树
     * @return
     */
    @GetMapping("/poweropts")
    public JSONObject poweropts(HttpServletRequest request){
        return fOptinfoService.poweropts(RequestParametersUtil.getRequestParametersRetJson(request));
    }


}