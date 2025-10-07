package com.centit.shopping.webmgr.service;

import com.alibaba.fastjson.JSONObject;

/**
 * <p><p>
 *
 * @version : 1.0
 * @Author : su_yl
 * @Description : 服务接口类
 * @Date : 2021-04-19
 **/
public interface ShoppingCarouselService {

    /**
     * 查询列表
     */
    JSONObject queryList(JSONObject reqJson);

    JSONObject queryAllInfo(JSONObject requestParametersRetJson);

    JSONObject add(JSONObject reqParam);

    JSONObject modify(JSONObject reqParam);

    JSONObject remove(String id);

    JSONObject detail(String id);
}
