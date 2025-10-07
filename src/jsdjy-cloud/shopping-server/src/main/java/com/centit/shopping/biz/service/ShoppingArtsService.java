package com.centit.shopping.biz.service;

import com.alibaba.fastjson.JSONObject;
import javax.servlet.http.HttpServletRequest;

public interface ShoppingArtsService {
    JSONObject artActivityPageList(JSONObject paramJSONObject);

    JSONObject artPlanPageList(JSONObject paramJSONObject);

    JSONObject artClassPageList(JSONObject paramJSONObject);

    JSONObject artActivityDetail(String paramString, JSONObject paramJSONObject);

    JSONObject artPlanDetail(String paramString, JSONObject paramJSONObject);

    JSONObject artClassDetail(String paramString, JSONObject paramJSONObject);

    JSONObject checkActivityLimit(JSONObject paramJSONObject);

    JSONObject checkPlanLimit(JSONObject paramJSONObject);

    JSONObject renderActivityOrder(JSONObject paramJSONObject, HttpServletRequest paramHttpServletRequest);

    JSONObject renderPlanOrder(JSONObject paramJSONObject, HttpServletRequest paramHttpServletRequest);

    JSONObject renderClassOrder(JSONObject paramJSONObject, HttpServletRequest paramHttpServletRequest);

    JSONObject addActivityOrder(JSONObject paramJSONObject, HttpServletRequest paramHttpServletRequest);

    JSONObject addPlanOrder(JSONObject paramJSONObject, HttpServletRequest paramHttpServletRequest);

    JSONObject addClassOrder(JSONObject paramJSONObject, HttpServletRequest paramHttpServletRequest);
}
