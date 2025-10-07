package com.centit.shopping.biz.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.biz.service.CarouselService;
import com.centit.shopping.utils.RequestParametersUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p><p>
 *
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 前端控制器
 * @Date : 2021-04-19
 **/
@RestController
@RequestMapping("/carousel")
public class CarouselController {

    @Resource
    private CarouselService carouselService;

    /**
     * 查询列表
     *
     * @return
     */
    @GetMapping("/queryList")
    public JSONObject queryList(HttpServletRequest request) {
        JSONObject retJson = carouselService.queryList(RequestParametersUtil.getRequestParametersRetJson(request));
        return retJson;
    }


}