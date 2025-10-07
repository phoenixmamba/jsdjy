package com.centit.admin.system.service;

import com.alibaba.fastjson.JSONObject;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务接口类
 * @Date : 2020-06-23
 **/
public interface FUserinfoService {

    /**
     * 查询人员分页列表
     */
    JSONObject queryUserPageList(JSONObject reqJson);


    JSONObject addUser(JSONObject reqJson);

    JSONObject editUser(JSONObject reqJson);

    JSONObject deleteUser(JSONObject reqJson);

    /**
     * 获取用户信息
     */
    JSONObject getUserInfo(String userCode);

    /**
     * 获取用户机构
     */
    JSONObject getUserUnitPageList(String userCode, JSONObject reqJson);

    /**
     * 重置密码
     */
    JSONObject resetPwd(String userCode);

    JSONObject checkLoginName(JSONObject reqJson);
}
