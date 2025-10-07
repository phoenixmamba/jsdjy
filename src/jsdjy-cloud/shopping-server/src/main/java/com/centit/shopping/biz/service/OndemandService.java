package com.centit.shopping.biz.service;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>点播<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务接口类
 * @Date : 2021-04-21
 **/
public interface OndemandService {

    /**
     * 订单渲染
     */
    JSONObject renderOndemandOrder(JSONObject reqJson, HttpServletRequest request);

    /**
     * 下单
     */
    JSONObject addOndemandOrder(JSONObject reqJson, HttpServletRequest request);

}
