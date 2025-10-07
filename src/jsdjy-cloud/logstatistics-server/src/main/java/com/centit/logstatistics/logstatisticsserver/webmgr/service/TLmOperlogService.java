package com.centit.logstatistics.logstatisticsserver.webmgr.service;

import com.alibaba.fastjson.JSONObject;

/**
 * <p> <p>
 *
 * @version : 1.0
 * @Author : cui_jian
 * @Description :  服务接口类
 * @Date : 2020-05-28
 **/
public interface TLmOperlogService {

    /**
     * 查询列表
     */
    JSONObject queryList(JSONObject reqJson);

    /**
     * 新增日志
     */
    JSONObject addOperLog(JSONObject reqJson);

    JSONObject queryOperPageList(JSONObject requestParametersRetJson);
}

