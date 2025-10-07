package com.centit.shopping.webmgr.service;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>商户订单<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务接口类
 * @Date : 2021-02-25
 **/
public interface SellerOrderService {

    /**
     * 查询商户订单分页列表
     */
    JSONObject queryPageList(JSONObject reqJson);

    JSONObject exportOrderList(JSONObject reqJson, HttpServletResponse response);

    /**
     * 获取订单详情
     */
    JSONObject orderDetail(String id);

    /**
     * 调整订单费用
     */
    JSONObject changeOrderPrice(JSONObject reqJson);

    /**
     * 取消订单
     */
    JSONObject cancelOrder(JSONObject reqJson);

    /**
     * 获取所有快递公司
     */
    JSONObject queryExpressCompanys(JSONObject reqJson);

    /**
     * 确认发货
     */
    JSONObject confirmDelivery(JSONObject reqJson);

    /**
     * 修改物流
     */
    JSONObject modifyLogistics(JSONObject reqJson);

    /**
     * 查询物流
     */
    JSONObject queryLogistics(String id);

    /**
     * 发起退款
     */
    JSONObject addRefund(JSONObject reqJson, HttpServletRequest request);

    /**
     * 异常处理
     */
    JSONObject handException(JSONObject reqJson, HttpServletRequest request);

}
