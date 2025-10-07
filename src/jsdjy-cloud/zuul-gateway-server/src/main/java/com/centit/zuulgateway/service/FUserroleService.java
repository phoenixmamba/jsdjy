package com.centit.zuulgateway.service;

import com.alibaba.fastjson.JSONObject;

/**
 * <p>用户角色<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务接口类
 * @Date : 2020-09-16
 **/
public interface FUserroleService {

    /**
     * 查询列表
     */
    JSONObject queryList(JSONObject reqJson);

}
