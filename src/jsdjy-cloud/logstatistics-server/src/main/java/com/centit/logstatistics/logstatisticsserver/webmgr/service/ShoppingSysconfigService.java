package com.centit.logstatistics.logstatisticsserver.webmgr.service;

import com.alibaba.fastjson.JSONObject;

/**
 * <p> <p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description :  服务接口类
 * @Date : 2022-08-08
 **/
public interface ShoppingSysconfigService {

    /**
     * 查询列表
     */
    JSONObject queryList(JSONObject reqJson);

}
