package com.centit.shopping.webmgr.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.utils.RequestParametersUtil;
import com.centit.shopping.webmgr.service.ArtplanService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>爱艺计划<p>
 *
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 前端控制器
 * @Date : 2021-03-19
 **/
@RestController
@RequestMapping("/webmgr/artplan")
public class ArtplanController {

    @Resource
    private ArtplanService artplanService;

    /**
     * 获取爱艺活动列表
     *
     * @param request
     * @return
     */
    @GetMapping("list")
    public JSONObject getArtplanList(HttpServletRequest request) {
        return artplanService.getArtplanList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 获取爱艺计划详情
     *
     * @param id
     * @return
     */
    @GetMapping("detail/{id}")
    public JSONObject detail(@PathVariable String id) {
        return artplanService.queryArtplanById(id);
    }

    /**
     * 添加爱艺计划
     *
     * @param param
     * @return
     */
    @PostMapping("add")
    public JSONObject addArtplan(@RequestBody JSONObject param) {
        return artplanService.addArtplan(param);
    }

    /**
     * 编辑爱艺计划信息
     */
    @PostMapping("modify")
    public JSONObject modifyArtplan(@RequestBody JSONObject param) {
        return artplanService.modifyArtplan(param);
    }

    /**
     * 删除爱艺计划
     */
    @PostMapping("delArtplan")
    public JSONObject delArtplan(@RequestBody JSONObject param) {
        return artplanService.delArtplan(param);
    }


    /**
     * 爱艺计划上下架
     */
    @PostMapping("putArtplan")
    public JSONObject putArtplan(@RequestBody JSONObject param) {
        return artplanService.putArtplan(param);
    }

    /**
     * 删除爱艺计划
     */
//    @PostMapping("remove/{id}")
//    public JSONObject removeArtplan(@PathVariable String id) {
//        return artplanService.removeArtplan(id);
//    }

    /**
     * 获取报名所需信息列表
     */
    @GetMapping("queryArtInfos")
    public JSONObject queryArtInfos(HttpServletRequest request) {
        return artplanService.queryArtInfos(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 编辑爱艺计划报名信息
     */
    @PostMapping("modifySignInfo")
    public JSONObject modifySignInfo(@RequestBody JSONObject param) {
        return artplanService.modifySignInfo(param);
    }
    /**
     * 编辑爱艺计划报名信息
     */
    @GetMapping("querySignInfo/{id}")
    public JSONObject querySignInfo(@PathVariable String id) {
        return artplanService.querySignInfo(id);
    }

    /**
     * 获取已报名信息列表
     */
    @GetMapping("signupinfoList")
    public JSONObject queryArtplanSignupinfo(HttpServletRequest request) {
        return artplanService.queryArtplanSignupList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 获取报名信息详情
     */
    @GetMapping("signupinfoDetail")
    public JSONObject queryArtplanSignupDetail(HttpServletRequest request) {
        return artplanService.queryArtplanSignupDetail(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 导出报名数据
     * @return
     */
    @GetMapping("/exportArtplanSignupList")
    public JSONObject exportArtplanSignupList(HttpServletRequest request, HttpServletResponse response){
        return artplanService.exportArtplanSignupList(RequestParametersUtil.getRequestParametersRetJson(request),response);
    }

    /**
     * 新建推送任务
     */
    @PostMapping("pushMsg")
    public JSONObject pushMsg(@RequestBody JSONObject param) {
        return artplanService.pushMsg(param);
    }

    /**
     * 获取所有推送记录
     */
    @GetMapping("artplanPushList")
    public JSONObject queryArtplanPushList(HttpServletRequest request) {
        return artplanService.queryArtplanPushList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 取消未执行的定时推送任务
     */
    @PostMapping("cancelArtplanPush")
    public JSONObject cancelArtplanPush(@RequestBody JSONObject param) {
        return artplanService.cancelArtplanPush(param);
    }

    /**
     * 查询可用的规格参数
     */
    @GetMapping("queryPlanDefaultSpecification")
    public JSONObject queryPlanDefaultSpecification(HttpServletRequest request) {
        return artplanService.queryPlanDefaultSpecification(RequestParametersUtil.getRequestParametersRetJson(request));
    }
}