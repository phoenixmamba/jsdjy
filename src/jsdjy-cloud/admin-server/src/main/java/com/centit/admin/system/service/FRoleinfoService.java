package com.centit.admin.system.service;

import com.alibaba.fastjson.JSONObject;

/**
 * <p>后台角色<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务接口类
 * @Date : 2020-06-23
 **/
public interface FRoleinfoService {

    /**
     * 获取系统角色
     */
    JSONObject querySysRoles(JSONObject reqJson);

    /**
     * 查询列表
     */
    JSONObject queryRolePageList(String userCode, JSONObject reqJson);

    /**
     * 新增角色
     */
    JSONObject addRole(JSONObject reqJson);


    /**
     * 获取角色详情
     */
    JSONObject queryRoleDetail(String roleCode);

    /**
     * 编辑角色
     */
    JSONObject editRole(JSONObject reqJson);

    /**
     * 删除角色
     */
    JSONObject delete(JSONObject reqJson);

    /**
     * 检查角色编码是否可用
     */
    JSONObject codenotexists(String roleCode);

    /**
     * 检查角色名是否可用
     */
    JSONObject namenotexists(String roleName);

    /**
     * 查询角色已有权限
     */
    JSONObject power(String roleCode);

    /**
     * 保存角色已有权限
     */
    JSONObject savePower(JSONObject reqJson);

    JSONObject roleusers(String roleCode, JSONObject reqJson);

    JSONObject roleunits(String roleCode, JSONObject reqJson);

    JSONObject deleteUserRole(JSONObject reqJson);

    JSONObject addUserRole(JSONObject reqJson);

    JSONObject deleteUnitRole(JSONObject reqJson);

    JSONObject addUnitRole(JSONObject reqJson);

    /**
     * 获取子部门列表
     */
    JSONObject getChildDeptList(String parentUnit);

    /**
     * 查询部门用户列表
     */
    JSONObject getDeptUserList(String unitCode);
}
