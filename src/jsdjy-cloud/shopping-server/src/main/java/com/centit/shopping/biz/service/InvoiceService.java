package com.centit.shopping.biz.service;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>开发票<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务接口类
 * @Date : 2021-02-24
 **/
public interface InvoiceService {

    /**
     * 查询可开票的演出订单列表
     */
    JSONObject queryTicketOrderList(JSONObject reqJson);

    /**
     * 获取我的默认发票抬头
     */
    JSONObject myDefaultInvoicHeader(JSONObject reqJson);

    /**
     * 获取我的发票抬头列表
     */
    JSONObject myInvoicHeaderList(JSONObject reqJson);

    /**
     * 添加新的发票抬头
     */
    JSONObject addInvoiceHeader(JSONObject reqJson, HttpServletRequest request);

    /**
     * 修改发票抬头
     */
    JSONObject editTicketHeader(JSONObject reqJson, HttpServletRequest request);

    /**
     * 删除我的发票抬头
     */
    JSONObject delTicketHeader(JSONObject reqJson, HttpServletRequest request);

    /**
     * 演出开票
     */
    JSONObject addTicketInvoice(JSONObject reqJson, HttpServletRequest request);

    /**
     * 获取我的开票历史
     */
    JSONObject queryMyInvoiceHistoryList(JSONObject reqJson);

    /**
     * 获取开票详情
     */
    JSONObject queryMyInvoiceDetail(JSONObject reqJson);

    /**
     * 获取演出发票的订单信息
     */
    JSONObject queryTicketInvoiceOrders(JSONObject reqJson);

    /**
     * 演出开票重发发票
     */
    JSONObject reSendTicketInvoice(JSONObject reqJson, HttpServletRequest request);
}
