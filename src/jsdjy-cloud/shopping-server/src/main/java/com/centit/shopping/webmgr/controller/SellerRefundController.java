package com.centit.shopping.webmgr.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.utils.RequestParametersUtil;
import com.centit.shopping.webmgr.service.SellerRefundService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>退货管理<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 前端控制器
 * @Date : 2021-02-21
 **/
@RestController
@RequestMapping("/webmgr/sellerRefund")
public class SellerRefundController {

    @Resource
    private SellerRefundService sellerRefundService;

    /**
     * 查询退货分页列表
     * @return
     */
    @GetMapping("/pageList")
    public JSONObject queryPageList(HttpServletRequest request){
        return sellerRefundService.queryPageList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 查看退货详情
     * @return
     */
    @GetMapping("/refundDetail/{refundId}")
    public JSONObject refundDetail(@PathVariable String refundId,HttpServletRequest request){
        return sellerRefundService.refundDetail(refundId);
    }

    /**
     * 通过/不通过退货
     * @return
     */
    @PostMapping("/adminRefund")
    public JSONObject adminRefund(@RequestBody JSONObject reqJson){
        return sellerRefundService.adminRefund(reqJson);
    }


}