package com.centit.shopping.biz.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.po.ShoppingRefund;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>商户订单<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务接口类
 * @Date : 2021-02-25
 **/
public interface OrderService {

    /**
     * 查询订单分页列表
     */
    JSONObject queryPageList(JSONObject reqJson);

    /**
     * 订单详情
     */
    JSONObject orderDetail(String id);

    /**
     * 获取订单推荐商品
     **/
    JSONObject orderRecGoods(String id);

    /**
     * 演出票订单详情
     */
    JSONObject orderMzDetail(String id);

    /**
     * 确认收货
     */
    JSONObject confirmReceipt(JSONObject reqJson, HttpServletRequest request);

    /**
     * 取消订单
     */
    JSONObject cancelOrder(JSONObject reqJson, HttpServletRequest request);

    /**
     * @Description 获取订单待评价商品列表
     **/
    JSONObject orderEvaluateGoods(String ofId);

    /**
     * 评价
     */
    JSONObject evaluateOrder(JSONObject reqJson, HttpServletRequest request);

    /**
     * 发起退货
     */

    JSONObject addRefund(JSONObject reqJson, HttpServletRequest request);

    /**
     * 查询我的退款列表
     */
    JSONObject queryMyRefundPageList(JSONObject reqJson);

    /**
     * 查看退货详情
     */

    JSONObject refundDetail(String refundId);

    /**
     * 撤回退款申请
     */
    JSONObject cancelRefund(JSONObject reqJson);
}
