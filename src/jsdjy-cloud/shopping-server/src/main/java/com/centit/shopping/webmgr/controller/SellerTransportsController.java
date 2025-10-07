package com.centit.shopping.webmgr.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.utils.RequestParametersUtil;
import com.centit.shopping.webmgr.service.SellerTransportsService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>商户物流<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 前端控制器
 * @Date : 2021-02-21
 **/
@RestController
@RequestMapping("/webmgr/sellerTransport")
public class SellerTransportsController {

    @Resource
    private SellerTransportsService sellerTransportsService;

    /**
     * 查询商品运费模板
     * @return
     */
    @GetMapping("/storeTransports")
    public JSONObject queryStoreTransports(HttpServletRequest request){
        return sellerTransportsService.queryStoreTransports(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 获取商户运费模板分页列表
     * @return
     */
    @GetMapping("/pageList")
    public JSONObject queryPageList(HttpServletRequest request){
        return sellerTransportsService.queryPageList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 新增运费模板
     * @return
     */
    @PostMapping("/addTransport")
    public JSONObject addTransport(@RequestBody JSONObject reqJson){
        return sellerTransportsService.addTransport(reqJson);
    }

    /**
     * 获取运费模板详情
     * @return
     */
    @GetMapping("/transportDetail/{id}")
    public JSONObject goodsDetail(@PathVariable String id,HttpServletRequest request){
        return sellerTransportsService.transportDetail(id);
    }

    /**
     * 编辑运费模板
     * @return
     */
    @PostMapping("/editTransport")
    public JSONObject editTransport(@RequestBody JSONObject reqJson){
        return sellerTransportsService.editTransport(reqJson);
    }

    /**
     * 删除运费模板
     * @return
     */
    @PostMapping("/delTransport")
    public JSONObject delTransport(@RequestBody JSONObject reqJson){
        return sellerTransportsService.delTransport(reqJson);
    }
}