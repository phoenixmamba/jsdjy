package com.centit.pay.biz.service;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 支付服务接口类
 * @Date : 2021-01-22
 **/
public interface AppPayService {

    /**
     * 吊起支付
     */

    JSONObject InPayment(JSONObject reqJson,HttpServletRequest request);

    JSONObject mzVerifycode(JSONObject reqJson,HttpServletRequest request);

    JSONObject checkVerifycode(JSONObject reqJson,HttpServletRequest request);

    JSONObject orderPayStatus(String orderId,HttpServletRequest request);

    JSONObject accountLimit(HttpServletRequest request);

    JSONObject updateOrderPayKey(JSONObject reqJson,HttpServletRequest request);

    JSONObject queryPayments(JSONObject reqJson,HttpServletRequest request);
}
