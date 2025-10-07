package com.centit.pay.biz.service;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ：cui_jian
 * @date ：Created in 2021/2/1 15:11
 */
public interface PayNotifyService {

    void wxNotify(HttpServletRequest request, HttpServletResponse response);

    void wxParkNotify(HttpServletRequest request, HttpServletResponse response);

    void aliNotify(HttpServletRequest request, HttpServletResponse response);

    JSONObject appNotify(JSONObject reqJson,HttpServletRequest request);
}
