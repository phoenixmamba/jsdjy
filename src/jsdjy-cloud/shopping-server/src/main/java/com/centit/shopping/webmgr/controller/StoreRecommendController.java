package com.centit.shopping.webmgr.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.utils.RequestParametersUtil;
import com.centit.shopping.webmgr.service.StoreRecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Description:
 * Author: 苏依林
 * Create Data: 2021/4/28
 */
@RestController
@RequestMapping("webmgr/storeRecommend")
public class StoreRecommendController {
    @Autowired
    private StoreRecommendService storeRecommendService;

    @GetMapping("list")
    public JSONObject list(HttpServletRequest request) {
        return storeRecommendService.getList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    @GetMapping("detail/{id}")
    public JSONObject detail(@PathVariable String id) {
        return storeRecommendService.getDetail(id);
    }

    @PostMapping("add")
    public JSONObject add(@RequestBody JSONObject param) {
        return storeRecommendService.add(param);
    }

    @PostMapping("modify")
    public JSONObject modify(@RequestBody JSONObject param) {
        return storeRecommendService.modify(param);
    }

    @PostMapping("remove/{id}")
    public JSONObject remove(@PathVariable String id) {
        return storeRecommendService.remove(id);
    }

    @GetMapping("getAllInfo")
    public JSONObject getAllInfo(HttpServletRequest request) {
        return storeRecommendService.queryAllInfo(RequestParametersUtil.getRequestParametersRetJson(request));
    }
}
