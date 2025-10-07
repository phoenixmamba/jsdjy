package com.centit.shopping.webmgr.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.utils.RequestParametersUtil;
import com.centit.shopping.webmgr.service.TicketInvoiceService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>发票<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 前端控制器
 * @Date : 2021-02-25
 **/
@RestController
@RequestMapping("/webmgr/invoice")
public class TicketInvoiceController {

    @Resource
    private TicketInvoiceService ticketInvoiceService;

    /**
     * 查询用户可开票的演出订单列表
     * @return
     */
    @GetMapping("/queryTicketOrderList")
    public JSONObject queryTicketOrderList(HttpServletRequest request){
        return ticketInvoiceService.queryTicketOrderList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 手动添加线下开票记录
     * @return
     */
    @PostMapping("/addTicketInvoice")
    public JSONObject addTicketInvoice(@RequestBody JSONObject reqJson){
        return ticketInvoiceService.addTicketInvoice(reqJson);
    }

    /**
     * 查询开票记录列表
     * @return
     */
    @GetMapping("/queryInvoiceRecordList")
    public JSONObject queryInvoiceRecordList(HttpServletRequest request){
        return ticketInvoiceService.queryInvoiceRecordList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 导出开票记录
     * @return
     */
    @GetMapping("/exportInvoiceRecordList")
    public void exportInvoiceRecordList(HttpServletRequest request, HttpServletResponse response){
        ticketInvoiceService.exportInvoiceRecordList(RequestParametersUtil.getRequestParametersRetJson(request),response);
    }

    /**
     * 查询演出项目列表
     * @return
     */
    @GetMapping("/queryProjectList")
    public JSONObject queryProjectList(HttpServletRequest request){
        return ticketInvoiceService.queryProjectList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 查询指定项目的开票记录
     * @return
     */
    @GetMapping("/queryProjectInvoiceList")
    public JSONObject queryProjectInvoiceList(HttpServletRequest request){
        return ticketInvoiceService.queryProjectInvoiceList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 导出指定项目开票记录列表
     * @return
     */
    @GetMapping("/exportProjectInvoiceList")
    public void exportProjectInvoiceList(HttpServletRequest request, HttpServletResponse response){
        ticketInvoiceService.exportProjectInvoiceList(RequestParametersUtil.getRequestParametersRetJson(request),response);
    }
}