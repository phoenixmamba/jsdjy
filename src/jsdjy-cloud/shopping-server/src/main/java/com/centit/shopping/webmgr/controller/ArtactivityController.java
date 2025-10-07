package com.centit.shopping.webmgr.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.utils.RequestParametersUtil;
import com.centit.shopping.webmgr.service.ArtactivityService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>艺术活动<p>
 *
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 前端控制器
 * @Date : 2021-03-19
 **/
@RestController
@RequestMapping("/webmgr/artactivity")
public class ArtactivityController {

    @Resource
    private ArtactivityService artactivityService;

    /**
     * 获取艺术活动列表
     *
     * @param request
     * @return
     */
    @GetMapping("list")
    public JSONObject getArtactivityList(HttpServletRequest request) {
        return artactivityService.getArtactivityList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 获取艺术活动详情
     *
     * @param id
     * @return
     */
    @GetMapping("detail/{id}")
    public JSONObject detail(@PathVariable String id) {
        return artactivityService.queryArtactivityById(id);
    }

    /**
     * 添加艺术活动
     *
     * @param param
     * @return
     */
    @PostMapping("add")
    public JSONObject addArtactivity(@RequestBody JSONObject param) {
        return artactivityService.addArtactivity(param);
    }

    /**
     * 编辑艺术活动信息
     */
    @PostMapping("modify")
    public JSONObject modifyArtactivity(@RequestBody JSONObject param) {
        return artactivityService.modifyArtactivity(param);
    }

    /**
     * 艺术活动上下架
     */
    @PostMapping("putArtactivity")
    public JSONObject putArtactivity(@RequestBody JSONObject param) {
        return artactivityService.putArtactivity(param);
    }

    /**
     * 删除艺术活动
     */
    @PostMapping("delArtactivity")
    public JSONObject delArtactivity(@RequestBody JSONObject param) {
        return artactivityService.delArtactivity(param);
    }

    /**
     * 删除艺术活动
     */
//    @PostMapping("remove/{id}")
//    public JSONObject removeArtactivity(@PathVariable String id) {
//        return artactivityService.removeArtactivity(id);
//    }

    /**
     * 获取报名所需信息列表
     */
    @GetMapping("queryArtInfos")
    public JSONObject queryArtInfos(HttpServletRequest request) {
        return artactivityService.queryArtInfos(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 编辑艺术活动报名信息
     */
    @PostMapping("modifySignInfo")
    public JSONObject modifySignInfo(@RequestBody JSONObject param) {
        return artactivityService.modifySignInfo(param);
    }
    /**
     * 编辑艺术活动报名信息
     */
    @GetMapping("querySignInfo/{id}")
    public JSONObject querySignInfo(@PathVariable String id) {
        return artactivityService.querySignInfo(id);
    }

    /**
     * 获取已报名信息列表
     */
    @GetMapping("signupinfoList")
    public JSONObject queryArtactivitySignupinfo(HttpServletRequest request) {
        return artactivityService.queryArtactivitySignupList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 获取报名信息详情
     */
    @GetMapping("signupinfoDetail")
    public JSONObject queryArtactivitySignupDetail(HttpServletRequest request) {
        return artactivityService.queryArtactivitySignupDetail(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 导出报名数据
     * @return
     */
    @GetMapping("/exportArtactivitySignupList")
    public JSONObject exportArtactivitySignupList(HttpServletRequest request, HttpServletResponse response){
        return artactivityService.exportArtactivitySignupList(RequestParametersUtil.getRequestParametersRetJson(request),response);
    }

    /**
     * 新建推送任务
     */
    @PostMapping("pushMsg")
    public JSONObject pushMsg(@RequestBody JSONObject param) {
        return artactivityService.pushMsg(param);
    }

    /**
     * 获取所有推送记录
     */
    @GetMapping("artactivityPushList")
    public JSONObject queryArtactivityPushList(HttpServletRequest request) {
        return artactivityService.queryArtactivityPushList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 取消未执行的定时推送任务
     */
    @PostMapping("cancelArtactivityPush")
    public JSONObject cancelArtactivityPush(@RequestBody JSONObject param) {
        return artactivityService.cancelArtactivityPush(param);
    }

    /**
     * 查询活动可用的规格参数
     */
    @GetMapping("queryActDefaultSpecification")
    public JSONObject queryActDefaultSpecification(HttpServletRequest request) {
        return artactivityService.queryActDefaultSpecification(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 导出报名数据
     * @return
     */
    @GetMapping("/exportArtactivitySignup")
    public void exportArtactivitySignup(HttpServletRequest request, HttpServletResponse response){
        artactivityService.exportArtactivitySignup(RequestParametersUtil.getRequestParametersRetJson(request),response);
    }
}