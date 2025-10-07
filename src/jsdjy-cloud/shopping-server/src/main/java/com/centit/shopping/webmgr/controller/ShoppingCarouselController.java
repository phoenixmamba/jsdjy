package com.centit.shopping.webmgr.controller;

import com.alibaba.fastjson.JSONObject;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.centit.shopping.utils.RequestParametersUtil;
import org.springframework.web.bind.annotation.*;
import com.centit.shopping.webmgr.service.ShoppingCarouselService;

/**
 * <p><p>
 *
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 前端控制器
 * @Date : 2021-04-19
 **/
@RestController
@RequestMapping("/webmgr/shoppingCarousel")
public class ShoppingCarouselController {

    @Resource
    private ShoppingCarouselService carouselService;

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

    /**
     * 查询列表
     *
     * @return
     */
    @GetMapping("/detail/{id}")
    public JSONObject detail(@PathVariable String id) {
        JSONObject retJson = carouselService.detail(id);
        return retJson;
    }

    /**
     * 查询所有演出以及商品电影信息
     *
     * @param request
     * @return
     */
    @GetMapping("/queryAllInfo")
    public JSONObject queryAllInfo(HttpServletRequest request) {
        JSONObject retJson = carouselService.queryAllInfo(RequestParametersUtil.getRequestParametersRetJson(request));
        return retJson;
    }


    /**
     * 添加
     *
     * @param reqParam
     * @return
     */
    @PostMapping("/add")
    public JSONObject add(@RequestBody JSONObject reqParam) {
        JSONObject retJson = carouselService.add(reqParam);
        return retJson;
    }

    /**
     * 修改
     *
     * @param reqParam
     * @return
     */
    @PostMapping("/modify")
    public JSONObject modify(@RequestBody JSONObject reqParam) {
        JSONObject retJson = carouselService.modify(reqParam);
        return retJson;
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @PostMapping("/remove/{id}")
    public JSONObject remove(@PathVariable String id) {
        JSONObject retJson = carouselService.remove(id);
        return retJson;
    }

}