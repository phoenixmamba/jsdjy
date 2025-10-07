package com.centit.shopping.biz.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.biz.service.InvoiceService;
import com.centit.shopping.utils.RequestParametersUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>开发票<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 前端控制器
 * @Date : 2021-03-19
 **/
@RestController
@RequestMapping("/invoice")
public class InvoiceController {

    @Resource
    private InvoiceService invoiceService;

    /**
     * 查询可开票的演出订单列表
     * @return
     */
    @GetMapping("/queryTicketOrderList")
    public JSONObject queryTicketOrderList(HttpServletRequest request, HttpServletResponse response){
        return invoiceService.queryTicketOrderList(RequestParametersUtil.getRequestParametersRetJson(request));
    }


    /**
     * 获取我的默认发票抬头
     * @return
     */
    @GetMapping("/myDefaultInvoicHeader")
    public JSONObject myDefaultInvoicHeader(HttpServletRequest request, HttpServletResponse response){
        return invoiceService.myDefaultInvoicHeader(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 获取我的发票抬头列表
     * @return
     */
    @GetMapping("/myInvoicHeaderList")
    public JSONObject myInvoicHeaderList(HttpServletRequest request, HttpServletResponse response){
        return invoiceService.myInvoicHeaderList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 添加新的发票抬头
     * @return
     */
    @PostMapping("/addInvoiceHeader")
    public JSONObject addInvoiceHeader(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return invoiceService.addInvoiceHeader(reqJson,request);
    }

    /**
     * 修改发票抬头
     * @return
     */
    @PostMapping("/editTicketHeader")
    public JSONObject editTicketHeader(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return invoiceService.editTicketHeader(reqJson,request);
    }

    /**
     * 删除我的发票抬头
     * @return
     */
    @PostMapping("/delTicketHeader")
    public JSONObject delTicketHeader(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return invoiceService.delTicketHeader(reqJson,request);
    }

    /**
     * 演出开票
     * @return
     */
    @PostMapping("/addTicketInvoice")
    public JSONObject addTicketInvoice(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return invoiceService.addTicketInvoice(reqJson,request);
    }

    /**
     * 获取我的开票历史
     * @return
     */
    @GetMapping("/queryMyInvoiceHistoryList")
    public JSONObject queryMyInvoiceHistoryList(HttpServletRequest request, HttpServletResponse response){
        return invoiceService.queryMyInvoiceHistoryList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 获取开票详情
     * @return
     */
    @GetMapping("/queryMyInvoiceDetail")
    public JSONObject queryMyInvoiceDetail(HttpServletRequest request, HttpServletResponse response){
        return invoiceService.queryMyInvoiceDetail(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 获取演出发票的订单信息
     * @return
     */
    @GetMapping("/queryTicketInvoiceOrders")
    public JSONObject queryTicketInvoiceOrders(HttpServletRequest request, HttpServletResponse response){
        return invoiceService.queryTicketInvoiceOrders(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 演出开票重发发票
     * @return
     */
    @PostMapping("/reSendTicketInvoice")
    public JSONObject reSendTicketInvoice(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return invoiceService.reSendTicketInvoice(reqJson,request);
    }
}