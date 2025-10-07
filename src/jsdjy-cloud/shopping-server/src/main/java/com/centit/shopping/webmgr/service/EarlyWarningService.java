package com.centit.shopping.webmgr.service;

import com.alibaba.fastjson.JSONObject;

public interface EarlyWarningService {
    JSONObject adminList(JSONObject requestParametersRetJson);

    JSONObject addAdmin(JSONObject req);

    JSONObject removeAdmin(String id);

    JSONObject adminDetail(String id);

    JSONObject modifyAdmin(JSONObject req);

    JSONObject configList(JSONObject requestParametersRetJson);

    JSONObject addConfig(JSONObject req);

    JSONObject removeConfig(String id);

    JSONObject configDetail(String id);

    JSONObject modifyConfig(JSONObject req);
}
