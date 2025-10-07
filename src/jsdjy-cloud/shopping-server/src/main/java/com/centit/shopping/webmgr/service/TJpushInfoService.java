package com.centit.shopping.webmgr.service;

import com.alibaba.fastjson.JSONObject;

/**
 * <p>极光推送历史记录<p>
 * @version : 1.0
 * @Author : lihao
 * @Description : 服务接口类
 * @Date : 2021-01-19
 **/
public interface TJpushInfoService {

    /**
     * 新增
     */
    JSONObject create(JSONObject reqJson);

    /**
     * 分页列表查询
     */
    JSONObject queryPageList(JSONObject reqJson);

}
