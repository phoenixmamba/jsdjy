package com.centit.shopping.webmgr.service;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>开发票<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务接口类
 * @Date : 2021-02-24
 **/
public interface TicketInvoiceService {

    /**
     * 查询用户可开票的演出订单列表
     */
    JSONObject queryTicketOrderList(JSONObject reqJson);

    /**
     * 手动添加线下开票记录
     */
    JSONObject addTicketInvoice(JSONObject reqJson);

    /**
     * 查询开票记录列表
     */
    JSONObject queryInvoiceRecordList(JSONObject reqJson);

    /**
     * 导出开票记录列表
     */
    void exportInvoiceRecordList(JSONObject reqJson, HttpServletResponse response);

    /**
     * 查询演出列表
     */
    JSONObject queryProjectList(JSONObject reqJson);

    /**
     * 查询指定项目的开票记录
     */
    JSONObject queryProjectInvoiceList(JSONObject reqJson);

    /**
     * 导出指定项目开票记录列表
     */
    void exportProjectInvoiceList(JSONObject reqJson, HttpServletResponse response);
}
