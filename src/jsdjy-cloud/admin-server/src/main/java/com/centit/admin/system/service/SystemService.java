package com.centit.admin.system.service;

import com.alibaba.fastjson.JSONObject;
import com.centit.core.result.Result;

import javax.servlet.http.HttpServletRequest;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务接口类
 * @Date : 2020-06-23
 **/
public interface SystemService {

    JSONObject getCode(HttpServletRequest req);

    /**
     * 登录
     */
    Result login(HttpServletRequest request,String loginName, String password);

    /**
     * 获取功能菜单
     */
    JSONObject menu(String userCode, JSONObject reqJson);

    JSONObject hasLogin(HttpServletRequest req);

    JSONObject logout(HttpServletRequest req);

    JSONObject changepwd(JSONObject reqJson);

}
