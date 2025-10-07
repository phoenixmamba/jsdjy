package com.centit.ticket.biz.service;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务接口类
 * @Date : 2021-04-08
 **/
public interface TicketProjectService {

    JSONObject queryHomeData(JSONObject reqJson);
    /**
     * 演出分类
     */
    JSONObject queryTicketClass(JSONObject reqJson);

    JSONObject queryProjectList(JSONObject reqJson);
    /**
     * 查询列表
     */
    JSONObject queryList(JSONObject reqJson);

    JSONObject projectDetail(JSONObject reqJson);

    JSONObject queryEventDays(JSONObject reqJson);

    JSONObject getSign(JSONObject reqJson);

    JSONObject queryDefaultAddress(JSONObject reqJson);

    JSONObject renderOrder(JSONObject reqJson, HttpServletRequest request);

    JSONObject addOrder(JSONObject reqJson, HttpServletRequest request);

    /**
     * 收藏
     */
    JSONObject addFavorite(JSONObject reqJson, HttpServletRequest request);

    /**
     * 取消收藏
     */
    JSONObject cancelFavorite(JSONObject reqJson, HttpServletRequest request);

    /**
     * 我的收藏列表
     */
    JSONObject myFavList(JSONObject reqJson);

    /**
     * 我的足迹
     */
    JSONObject myHistoryList(JSONObject reqJson);

    /**
     * 清除历史轨迹
     */
    JSONObject clearHistory(JSONObject reqJson, HttpServletRequest request);

    void setHomeData();
}
