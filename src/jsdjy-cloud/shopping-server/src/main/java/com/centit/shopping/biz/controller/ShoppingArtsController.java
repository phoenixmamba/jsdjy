package com.centit.shopping.biz.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.biz.service.ShoppingArtsService;
import com.centit.shopping.utils.RequestParametersUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping({"/shoppingArts"})
public class ShoppingArtsController {
    @Resource
    private ShoppingArtsService shoppingArtsService;

    @GetMapping({"/artActivityPageList"})
    public JSONObject artActivityPageList(HttpServletRequest request, HttpServletResponse response) {
        return this.shoppingArtsService.artActivityPageList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    @GetMapping({"/artActivityDetail/{id}"})
    public JSONObject artActivityDetail(@PathVariable String id, HttpServletRequest request) {
        return this.shoppingArtsService.artActivityDetail(id, RequestParametersUtil.getRequestParametersRetJson(request));
    }

    @PostMapping({"/checkActivityLimit"})
    public JSONObject checkActivityLimit(@RequestBody JSONObject reqJson) {
        return this.shoppingArtsService.checkActivityLimit(reqJson);
    }

    @PostMapping({"/renderActivityOrder"})
    public JSONObject renderActivityOrder(@RequestBody JSONObject reqJson, HttpServletRequest request) {
        return this.shoppingArtsService.renderActivityOrder(reqJson, request);
    }

    @PostMapping({"/addActivityOrder"})
    public JSONObject addActivityOrder(@RequestBody JSONObject reqJson, HttpServletRequest request) {
        return this.shoppingArtsService.addActivityOrder(reqJson, request);
    }

    @GetMapping({"/artPlanPageList"})
    public JSONObject artPlanPageList(HttpServletRequest request, HttpServletResponse response) {
        return this.shoppingArtsService.artPlanPageList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    @GetMapping({"/artPlanDetail/{id}"})
    public JSONObject artPlanDetail(@PathVariable String id, HttpServletRequest request) {
        return this.shoppingArtsService.artPlanDetail(id, RequestParametersUtil.getRequestParametersRetJson(request));
    }

    @PostMapping({"/checkPlanLimit"})
    public JSONObject checkPlanLimit(@RequestBody JSONObject reqJson) {
        return this.shoppingArtsService.checkPlanLimit(reqJson);
    }

    @PostMapping({"/renderPlanOrder"})
    public JSONObject renderPlanOrder(@RequestBody JSONObject reqJson, HttpServletRequest request) {
        return this.shoppingArtsService.renderPlanOrder(reqJson, request);
    }

    @PostMapping({"/addPlanOrder"})
    public JSONObject addPlanOrder(@RequestBody JSONObject reqJson, HttpServletRequest request) {
        return this.shoppingArtsService.addPlanOrder(reqJson, request);
    }

    @GetMapping({"/artClassPageList"})
    public JSONObject artClassPageList(HttpServletRequest request, HttpServletResponse response) {
        return this.shoppingArtsService.artClassPageList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    @GetMapping({"/artClassDetail/{id}"})
    public JSONObject artClassDetail(@PathVariable String id, HttpServletRequest request) {
        return this.shoppingArtsService.artClassDetail(id, RequestParametersUtil.getRequestParametersRetJson(request));
    }

    @PostMapping({"/renderClassOrder"})
    public JSONObject renderClassOrder(@RequestBody JSONObject reqJson, HttpServletRequest request) {
        return this.shoppingArtsService.renderClassOrder(reqJson, request);
    }

    @PostMapping({"/addClassOrder"})
    public JSONObject addClassOrder(@RequestBody JSONObject reqJson, HttpServletRequest request) {
        return this.shoppingArtsService.addClassOrder(reqJson, request);
    }
}
