package com.centit.admin.system.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.admin.system.service.FUnitinfoService;
import com.centit.core.util.RequestParametersUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>组织机构<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 前端控制器
 * @Date : 2020-06-23
 **/
@RestController
@RequestMapping("/webmgr/unitinfo")
public class FUnitinfoController {

    @Resource
    private FUnitinfoService fUnitinfoService;

    /**
     * 查询列表
     * @return
     */
    @GetMapping("/units")
    public JSONObject queryUnits(HttpServletRequest request, HttpServletResponse response){
        return fUnitinfoService.queryUnits(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 查询列表
     * @return
     */
    @GetMapping("/unitinfo")
    public JSONObject queryList(HttpServletRequest request, HttpServletResponse response){
        return fUnitinfoService.queryList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 查询部门下所有人员分页列表
     * @return
     */
    @GetMapping("/unitusers/{unitCode}")
    public JSONObject unitusers(@PathVariable String unitCode, HttpServletRequest request){
        return fUnitinfoService.unitusers(unitCode,RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 查询部门下角色分页列表
     * @return
     */
    @GetMapping("/unitroles/{unitCode}")
    public JSONObject unitroles(@PathVariable String unitCode, HttpServletRequest request, HttpServletResponse response){
        return fUnitinfoService.unitroles(unitCode,RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 新增部门角色
     * @return
     */
    @PostMapping("/unitroles/add")
    public JSONObject addUnitrole(@RequestBody JSONObject reqJson){
        return fUnitinfoService.addUnitrole(reqJson);
    }

    /**
     * 删除部门角色
     * @return
     */
    @PostMapping("/unitroles/delete")
    public JSONObject deleteUnitrole(@RequestBody JSONObject reqJson){
        return fUnitinfoService.deleteUnitrole(reqJson);
    }


    /**
     * 保存部门-菜单权限
     * @return
     */
    @PostMapping("/authorities")
    public JSONObject authorities(@RequestBody JSONObject reqJson){
        return fUnitinfoService.authorities(reqJson);
    }

    /**
     * 获取部门权限
     * @return
     */
    @GetMapping("/power/{unitCode}")
    public JSONObject power(@PathVariable String unitCode, HttpServletRequest request, HttpServletResponse response){
        return fUnitinfoService.power(unitCode);
    }

}