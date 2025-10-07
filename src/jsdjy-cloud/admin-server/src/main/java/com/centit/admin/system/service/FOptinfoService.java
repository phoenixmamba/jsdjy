package com.centit.admin.system.service;

import com.alibaba.fastjson.JSONObject;

/**
 * <p><p>
 * @version : 1.0
 * @Author : li_hao
 * @Description : 服务接口类
 * @Date : 2020-06-28
 **/
public interface FOptinfoService {

    /**
     * 查询列表
     */
    JSONObject sub(JSONObject reqJson);

    /**
     * 新增菜单
     */
    JSONObject addOptinfo(JSONObject reqJson);

    /**
     * 查询菜单编码是否可用
     */
    JSONObject notexists(String optId);

    /**
     * 删除菜单
     */
    JSONObject deleteOptinfo(JSONObject reqJson);

    /**
     * 编辑菜单
     */
    JSONObject editOptinfo(JSONObject reqJson);

    /**
     * 获取菜单详情
     */
    JSONObject optinfo(String optId);

    /**
     * 新增菜单操作
     */
    JSONObject addOptdef(JSONObject reqJson);

    /**
     * 检查操作编码是否可用
     */
    JSONObject defnotexists(String optCode);

    /**
     * 编辑菜单操作
     */
    JSONObject editOptdef(JSONObject reqJson);

    /**
     * 删除菜单操作
     */
    JSONObject deleteOptdef(JSONObject reqJson);

    /**
     * 获取系统菜单树
     */
    JSONObject poweropts(String userCode, JSONObject reqJson);

    JSONObject poweropts(JSONObject reqJson);

}
