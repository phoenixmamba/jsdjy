package com.centit.zuulgateway.service;

import com.alibaba.fastjson.JSONObject;

/**
 * <p>移动端角色-用户关联<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务接口类
 * @Date : 2020-10-14
 **/
public interface TRoleuserService {

    /**
     * 查询列表
     */
    JSONObject queryList(JSONObject reqJson);

}
