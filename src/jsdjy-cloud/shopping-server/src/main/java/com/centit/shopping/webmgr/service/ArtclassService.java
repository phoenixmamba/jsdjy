package com.centit.shopping.webmgr.service;

import com.alibaba.fastjson.JSONObject;

/**
 * company: www.abc.com
 * Author: 苏依林
 * Create Data: 2021/4/9
 */
public interface ArtclassService {
    JSONObject getArtclassList(JSONObject requestParametersRetJson);

    JSONObject addArtclass(JSONObject param);

    JSONObject modifyArtclass(JSONObject param);

    JSONObject delArtclass(JSONObject param);

    JSONObject removeArtclass(String param);

    JSONObject queryArtInfos(JSONObject requestParametersRetJson);

    JSONObject queryArtclassById(String id);

    JSONObject putArtclass(JSONObject param);

    JSONObject modifySignInfo(JSONObject param);

    JSONObject querySignInfo(String id);

    JSONObject queryArtclassSignupinfo(JSONObject reqJson);
}
