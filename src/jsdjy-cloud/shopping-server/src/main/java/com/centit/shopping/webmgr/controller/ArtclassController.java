package com.centit.shopping.webmgr.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.utils.RequestParametersUtil;
import com.centit.shopping.webmgr.service.ArtclassService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Description: 艺术课程
 * Author: 苏依林
 * Create Data: 2021/4/8
 */
@RestController
@RequestMapping("/webmgr/artclass")
public class ArtclassController {

    @Resource
    private ArtclassService artclassService;

    /**
     * 获取艺术课程列表
     *
     * @param request
     * @return
     */
    @GetMapping("list")
    public JSONObject getArtclassList(HttpServletRequest request) {
        return artclassService.getArtclassList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 获取艺术课程详情
     *
     * @param id
     * @return
     */
    @GetMapping("detail/{id}")
    public JSONObject detail(@PathVariable String id) {
        return artclassService.queryArtclassById(id);
    }

    /**
     * 添加艺术课程
     *
     * @param param
     * @return
     */
    @PostMapping("add")
    public JSONObject addArtclass(@RequestBody JSONObject param) {
        return artclassService.addArtclass(param);
    }

    /**
     * 编辑艺术课程信息
     */
    @PostMapping("modify")
    public JSONObject modifyArtclass(@RequestBody JSONObject param) {
        return artclassService.modifyArtclass(param);
    }


    /**
     * 删除艺术课程
     */
    @PostMapping("delArtclass")
    public JSONObject delArtclass(@RequestBody JSONObject param) {
        return artclassService.delArtclass(param);
    }

    /**
     * 艺术课程上下架
     */
    @PostMapping("putArtclass")
    public JSONObject putArtclass(@RequestBody JSONObject param) {
        return artclassService.putArtclass(param);
    }


    /**
     * 获取报名所需信息列表
     */
    @GetMapping("queryArtInfos")
    public JSONObject queryArtInfos(HttpServletRequest request) {
        return artclassService.queryArtInfos(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 编辑艺术课程报名信息
     */
    @PostMapping("modifySignInfo")
    public JSONObject modifySignInfo(@RequestBody JSONObject param) {
        return artclassService.modifySignInfo(param);
    }
    /**
     * 编辑艺术课程报名信息
     */
    @GetMapping("querySignInfo/{id}")
    public JSONObject querySignInfo(@PathVariable String id) {
        return artclassService.querySignInfo(id);
    }

    /**
     * 获取已报名信息
     */
    @GetMapping("signupinfoList")
    public JSONObject queryArtclassSignupinfo(HttpServletRequest request) {
        return artclassService.queryArtclassSignupinfo(RequestParametersUtil.getRequestParametersRetJson(request));
    }
}
