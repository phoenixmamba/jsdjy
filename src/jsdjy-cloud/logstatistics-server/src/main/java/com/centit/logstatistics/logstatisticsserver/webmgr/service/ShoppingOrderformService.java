package com.centit.logstatistics.logstatisticsserver.webmgr.service;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletResponse;

/**
 * <p> <p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description :  服务接口类
 * @Date : 2021-06-08
 **/
public interface ShoppingOrderformService {

    /**
     * 查询列表
     */
    JSONObject queryList(JSONObject reqJson);

    JSONObject queryMoneyList(JSONObject reqJson);

    JSONObject queryGoodsOrderList(JSONObject reqJson);

    JSONObject exportOrderList(JSONObject reqJson, HttpServletResponse response);

    JSONObject exportMoneyList(JSONObject reqJson, HttpServletResponse response);

    void exportGoodsOrderList(JSONObject reqJson, HttpServletResponse response);

    /**
     * 查询余额充值列表
     */
    JSONObject queryRechargeList(JSONObject reqJson);

    void exportRechargeOrderList(JSONObject reqJson, HttpServletResponse response);
}
