package com.centit.shopping.webmgr.service;

import com.alibaba.fastjson.JSONObject;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务接口类
 * @Date : 2022-06-28
 **/
public interface TEarlywarningService {

    /**
     * 查询任务列表
     */
    JSONObject queryTaskList(JSONObject reqJson);

    /**
     * 新增预警任务
     */
    JSONObject addWarningTask(JSONObject reqJson);

    /**
     * 编辑预警任务
     */
    JSONObject editWarningTask(JSONObject reqJson);

    /**
     * 停止本轮预警
     */
    JSONObject stopThisTurnWarningTask(JSONObject reqJson);

    /**
     * 开启/关闭预警任务
     */
    JSONObject closeWarningTask(JSONObject reqJson);

    /**
     * 删除预警任务
     */
    JSONObject delWarningTask(JSONObject reqJson);
}
