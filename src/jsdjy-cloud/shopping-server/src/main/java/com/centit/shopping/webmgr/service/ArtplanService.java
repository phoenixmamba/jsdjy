package com.centit.shopping.webmgr.service;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletResponse;

/**
 * company: www.abc.com
 * Author: 苏依林
 * Create Data: 2021/4/8
 */
public interface ArtplanService {
    JSONObject getArtplanList(JSONObject requestParametersRetJson);

    JSONObject addArtplan(JSONObject param);

    JSONObject modifyArtplan(JSONObject param);

    JSONObject delArtplan(JSONObject param);

    JSONObject removeArtplan(String param);

    JSONObject queryArtInfos(JSONObject requestParametersRetJson);

    JSONObject queryArtplanById(String id);

    JSONObject putArtplan(JSONObject param);

    JSONObject modifySignInfo(JSONObject param);

    JSONObject querySignInfo(String id);

    JSONObject queryArtplanSignupList(JSONObject reqJson);

    JSONObject queryArtplanSignupDetail(JSONObject reqJson);

    JSONObject exportArtplanSignupList(JSONObject reqJson, HttpServletResponse response);

    JSONObject pushMsg(JSONObject reqJson);

    JSONObject queryArtplanPushList(JSONObject reqJson);

    JSONObject cancelArtplanPush(JSONObject reqJson);

    /**
     * 查询爱艺计划可用的规格参数
     */
    JSONObject queryPlanDefaultSpecification(JSONObject reqJson);
}
