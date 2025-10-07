package com.centit.logstatistics.logstatisticsserver.webmgr.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.logstatistics.logstatisticsserver.webmgr.service.ShoppingOrderformService;
import com.centit.logstatistics.logstatisticsserver.webmgr.utils.RequestParametersUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p> <p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description :  前端控制器
 * @Date : 2020-05-28
 **/
@RestController
@RequestMapping("/reportForm")
public class ReportFormController {

    @Resource
    private ShoppingOrderformService shoppingOrderformService;


    /**
     * 查询订单明细表
     * @return
     */
    @GetMapping("/orderReport")
    public JSONObject queryList(HttpServletRequest request, HttpServletResponse response){
        return shoppingOrderformService.queryList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 查询账款核对表
     * @return
     */
    @GetMapping("/moneyCheckList")
    public JSONObject queryMoneyList(HttpServletRequest request, HttpServletResponse response){
        return shoppingOrderformService.queryMoneyList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 查询指定商品订单列表
     * @return
     */
    @GetMapping("/goodsOrderList")
    public JSONObject queryGoodsOrderList(HttpServletRequest request, HttpServletResponse response){
        return shoppingOrderformService.queryGoodsOrderList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 导出订单数据
     * @return
     */
    @GetMapping("/exportOrderList")
    public JSONObject exportOrderList(HttpServletRequest request, HttpServletResponse response){
        return shoppingOrderformService.exportOrderList(RequestParametersUtil.getRequestParametersRetJson(request),response);
    }

    /**
     * 导出账款数据
     * @return
     */
    @GetMapping("/exportMoneyList")
    public JSONObject exportMoneyList(HttpServletRequest request, HttpServletResponse response){
        return shoppingOrderformService.exportMoneyList(RequestParametersUtil.getRequestParametersRetJson(request),response);
    }

    /**
     * 导出指定商品订单数据
     * @return
     */
    @GetMapping("/exportGoodsOrderList")
    public void exportGoodsOrderList(HttpServletRequest request, HttpServletResponse response){
        shoppingOrderformService.exportGoodsOrderList(RequestParametersUtil.getRequestParametersRetJson(request),response);
    }

    /**
     * 查询充值明细表
     * @return
     */
    @GetMapping("/rechargeList")
    public JSONObject queryRechargeList(HttpServletRequest request, HttpServletResponse response){
        return shoppingOrderformService.queryRechargeList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 导出充值明细数据
     * @return
     */
    @GetMapping("/exportRechargeOrderList")
    public void exportRechargeOrderList(HttpServletRequest request, HttpServletResponse response){
        shoppingOrderformService.exportRechargeOrderList(RequestParametersUtil.getRequestParametersRetJson(request),response);
    }
}