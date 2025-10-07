package com.centit.shopping.webmgr.service;

import com.alibaba.fastjson.JSONObject;

/**
 * Description:
 * Author: 苏依林
 * Create Data: 2021/4/26
 */
public interface StoreRecommendService {
    JSONObject getList(JSONObject requestParametersRetJson);

    JSONObject queryAllInfo(JSONObject requestParametersRetJson);

    JSONObject getDetail(String id);

    JSONObject add(JSONObject param);

    JSONObject modify(JSONObject param);

    JSONObject remove(String id);
}
