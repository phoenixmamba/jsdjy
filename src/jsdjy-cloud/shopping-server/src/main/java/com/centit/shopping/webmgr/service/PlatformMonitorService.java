package com.centit.shopping.webmgr.service;

import com.alibaba.fastjson.JSONObject;

public interface PlatformMonitorService {

    JSONObject queryRegisterUserStatistics(JSONObject requestParametersRetJson);

    JSONObject queryActivityUserStatistics(JSONObject requestParametersRetJson);

    JSONObject queryEquipmentStatistics(JSONObject requestParametersRetJson);

    JSONObject queryInstallVersionStatistics(JSONObject requestParametersRetJson);

    JSONObject queryMoneyStatistics(JSONObject req);
}
