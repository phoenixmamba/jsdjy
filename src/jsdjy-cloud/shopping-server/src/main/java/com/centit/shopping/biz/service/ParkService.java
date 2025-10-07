package com.centit.shopping.biz.service;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>停车缴费<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务接口类
 * @Date : 2021-02-24
 **/
public interface ParkService {

    /**
     * 获取用户默认车牌号
     */
    JSONObject queryDefaultPlateNo(JSONObject reqJson);

    /**
     * 账单查询/费用查询
     */
    JSONObject getParkingPaymentInfo(JSONObject reqJson);

    /**
     * 订单页面渲染
     */
    JSONObject renderParkOrder(JSONObject reqJson, HttpServletRequest request);

    /**
     * 下单
     */
    JSONObject addParkOrder(JSONObject reqJson, HttpServletRequest request);

    /**
     * 查询车牌号列表
     */
    JSONObject queryPlateNoList(JSONObject reqJson);

    /**
     * 设置默认车牌号
     */
    JSONObject setDefaultPlateNo(JSONObject reqJson);

    /**
     * 新增车牌号
     */
    JSONObject addPlateNo(JSONObject reqJson);

    /**
     * 编辑车牌号
     */
    JSONObject editPlateNo(JSONObject reqJson);

    /**
     * 删除车牌号
     */
    JSONObject delPlateNo(JSONObject reqJson);
}
