package com.centit.shopping.webmgr.service;

import com.alibaba.fastjson.JSONObject;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务接口类
 * @Date : 2021-05-14
 **/
public interface TSystemStatusService {

    /**
     * 查询列表
     */
    JSONObject queryList(JSONObject reqJson);

}
