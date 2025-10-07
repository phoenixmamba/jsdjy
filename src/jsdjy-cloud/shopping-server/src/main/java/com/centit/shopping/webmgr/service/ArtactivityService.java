package com.centit.shopping.webmgr.service;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletResponse;

/**
 * company: www.abc.com
 * Author: 苏依林
 * Create Data: 2021/4/8
 */
public interface ArtactivityService {
    JSONObject getArtactivityList(JSONObject requestParametersRetJson);

    JSONObject addArtactivity(JSONObject param);

    JSONObject modifyArtactivity(JSONObject param);

    JSONObject delArtactivity(JSONObject param);

    JSONObject removeArtactivity(String param);

    JSONObject queryArtInfos(JSONObject requestParametersRetJson);

    JSONObject queryArtactivityById(String id);

    JSONObject putArtactivity(JSONObject param);

    JSONObject modifySignInfo(JSONObject param);

    JSONObject querySignInfo(String id);

    JSONObject queryArtactivitySignupList(JSONObject reqJson);

    JSONObject queryArtactivitySignupDetail(JSONObject reqJson);

    JSONObject exportArtactivitySignupList(JSONObject reqJson, HttpServletResponse response);

    JSONObject pushMsg(JSONObject reqJson);

    JSONObject queryArtactivityPushList(JSONObject reqJson);

    JSONObject cancelArtactivityPush(JSONObject reqJson);

    /**
     * 查询活动可用的规格参数
     */
    JSONObject queryActDefaultSpecification(JSONObject reqJson);

    void exportArtactivitySignup(JSONObject reqJson, HttpServletResponse response);
}
