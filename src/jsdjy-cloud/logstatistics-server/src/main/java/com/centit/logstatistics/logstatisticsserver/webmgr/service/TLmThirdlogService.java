package com.centit.logstatistics.logstatisticsserver.webmgr.service;

import com.alibaba.fastjson.JSONObject;

/**
 * <p> <p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description :  服务接口类
 * @Date : 2021-07-05
 **/
public interface TLmThirdlogService {

    /**
     * 查询列表
     */
    JSONObject queryPageList(JSONObject reqJson);

}
