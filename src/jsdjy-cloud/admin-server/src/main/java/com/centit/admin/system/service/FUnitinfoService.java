package com.centit.admin.system.service;

import com.alibaba.fastjson.JSONObject;

/**
 * <p>组织机构<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务接口类
 * @Date : 2020-06-23
 **/
public interface FUnitinfoService {
    /**
     * 查询所有部门
     */
    JSONObject queryUnits(JSONObject reqJson);

    /**
     * 查询列表
     */
    JSONObject queryList(JSONObject reqJson);

    /**
     * 查询部门下所有人员分页列表
     */
    JSONObject unitusers(String unitCode, JSONObject reqJson);

    /**
     * 查询部门下角色分页列表
     */
    JSONObject unitroles(String unitCode, JSONObject reqJson);

    /**
     * 新增部门角色
     */
    JSONObject addUnitrole(JSONObject reqJson);

    /**
     * 删除部门角色
     */
    JSONObject deleteUnitrole(JSONObject reqJson);

    /**
     * 保存部门-菜单权限
     */
    JSONObject authorities(JSONObject reqJson);


    JSONObject power(String unitCode);

}
