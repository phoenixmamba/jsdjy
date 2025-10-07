package com.centit.shopping.webmgr.service;

import com.alibaba.fastjson.JSONObject;

/**
 * <p>演职人员<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务接口类
 * @Date : 2021-04-13
 **/
public interface ShoppingMovieActorInfoService {

    /**
     * 查询列表
     */
    JSONObject queryList(JSONObject reqJson);

}
