package com.centit.shopping.webmgr.service;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletResponse;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务接口类
 * @Date : 2021-02-21
 **/
public interface WriteOffService {

    /**
     * 查询核销记录
     */
    JSONObject queryWriteOffRecordList(JSONObject reqJson);

    JSONObject exportWriteOffRecordList(JSONObject reqJson, HttpServletResponse response);
}
