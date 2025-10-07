package com.centit.admin.system.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.admin.system.service.FRoleinfoService;
import com.centit.core.util.RequestParametersUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>后台角色管理<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 前端控制器
 * @Date : 2020-06-23
 **/
@RestController
@RequestMapping("/webmgr/roleinfo")
public class FRoleinfoController {

    @Resource
    private FRoleinfoService fRoleinfoService;

    /**
     * 获取系统角色
     * @return
     */
    @GetMapping("/querySysRoles")
    public JSONObject querySysRoles(HttpServletRequest request, HttpServletResponse response){
        return fRoleinfoService.querySysRoles(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 按roleType获取所有角色
     * @return
     */
    @GetMapping("/queryRolePageList/{userCode}")
    public JSONObject queryRolePageList(@PathVariable String userCode, HttpServletRequest request, HttpServletResponse response){
        return fRoleinfoService.queryRolePageList(userCode,RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 新增角色
     * @return
     */
    @PostMapping("/add")
    public JSONObject addRole(@RequestBody JSONObject reqJson){
        return fRoleinfoService.addRole(reqJson);
    }

    /**
     * 获取角色详情
     * @return
     */
    @GetMapping("/detail/{roleCode}")
    public JSONObject detail(@PathVariable String roleCode, HttpServletRequest request, HttpServletResponse response){
        return fRoleinfoService.queryRoleDetail(roleCode);
    }

    /**
     * 编辑角色
     * @return
     */
    @PostMapping("/edit")
    public JSONObject editRole(@RequestBody JSONObject reqJson){
        return fRoleinfoService.editRole(reqJson);
    }

    /**
     * 删除角色
     * @return
     */
    @PostMapping("/delete")
    public JSONObject delete(@RequestBody JSONObject reqJson){
        return fRoleinfoService.delete(reqJson);
    }

    /**
     * 校验角色编码是否可用
     * @return
     */
    @GetMapping("/codenotexists/{roleCode}")
    public JSONObject codenotexists(@PathVariable String roleCode, HttpServletRequest request, HttpServletResponse response){
        return fRoleinfoService.codenotexists(roleCode);
    }

    /**
     * 校验角色名是否可用
     * @return
     */
    @GetMapping("/namenotexists/{roleName}")
    public JSONObject namenotexists(@PathVariable String roleName, HttpServletRequest request, HttpServletResponse response){
        return fRoleinfoService.namenotexists(roleName);
    }

    /**
     * 获取角色权限
     * @return
     */
    @GetMapping("/power/{roleCode}")
    public JSONObject power(@PathVariable String roleCode, HttpServletRequest request, HttpServletResponse response){
        return fRoleinfoService.power(roleCode);
    }

    /**
     * 保存角色权限
     * @return
     */
    @PostMapping("/savePower")
    public JSONObject savePower(@RequestBody JSONObject reqJson){
        return fRoleinfoService.savePower(reqJson);
    }

    /**
     * 获取角色用户
     * @return
     */
    @GetMapping("/roleusers/{roleCode}")
    public JSONObject roleusers(@PathVariable String roleCode, HttpServletRequest request, HttpServletResponse response){
        return fRoleinfoService.roleusers(roleCode,RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 获取角色机构
     * @return
     */
    @GetMapping("/roleunits/{roleCode}")
    public JSONObject roleunits(@PathVariable String roleCode, HttpServletRequest request, HttpServletResponse response){
        return fRoleinfoService.roleunits(roleCode,RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 删除角色用户
     * @return
     */
    @PostMapping("/roleusers/delete")
    public JSONObject deleteUserRole(@RequestBody JSONObject reqJson){
        return fRoleinfoService.deleteUserRole(reqJson);
    }

    /**
     * 新增角色用户
     * @return
     */
    @PostMapping("/roleusers/add")
    public JSONObject addUserRole(@RequestBody JSONObject reqJson){
        return fRoleinfoService.addUserRole(reqJson);
    }

    /**
     * 删除角色机构
     * @return
     */
    @PostMapping("/roleunits/delete")
    public JSONObject deleteUnitRole(@RequestBody JSONObject reqJson){
        return fRoleinfoService.deleteUnitRole(reqJson);
    }

    /**
     * 新增角色机构
     * @return
     */
    @PostMapping("/roleunits/add")
    public JSONObject addUnitRole(@RequestBody JSONObject reqJson){
        return fRoleinfoService.addUnitRole(reqJson);
    }

    /**
     * 获取子部门列表
     *
     * @return
     */
    @GetMapping("/getChildDeptList/{parentUnit}")
    public JSONObject getChildDeptList(@PathVariable String parentUnit) {
        return fRoleinfoService.getChildDeptList(parentUnit);
    }

    /**
     * 获取部门用户列表
     *
     * @return
     */
    @GetMapping("/getDeptUserList/{unitCode}")
    public JSONObject getDeptUserList(@PathVariable String unitCode) {
        return fRoleinfoService.getDeptUserList(unitCode);
    }
}