package com.centit.shopping.webmgr.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.utils.RequestParametersUtil;
import com.centit.shopping.webmgr.service.EarlyWarningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 告警平台
 */
@RestController
@RequestMapping("/webmgr/earlyWarning/")
public class EarlyWarningController {
    @Autowired
    private EarlyWarningService earlyWarningService;


    //告警平台设置服务器信息以及告警阈值
    @GetMapping("config/list")
    public JSONObject configList(HttpServletRequest req) {
        return earlyWarningService.configList(RequestParametersUtil.getRequestParametersRetJson(req));
    }

    @PostMapping("config/add")
    public JSONObject addConfig(@RequestBody JSONObject req) {
        return earlyWarningService.addConfig(req);
    }

    @PostMapping("config/remove/{id}")
    public JSONObject removeConfig(@PathVariable("id") String id) {
        return earlyWarningService.removeConfig(id);
    }

    @GetMapping("config/detail/{id}")
    public JSONObject configDetail(@PathVariable("id") String id) {
        return earlyWarningService.configDetail(id);
    }

    @PostMapping("config/modify")
    public JSONObject modifyConfig(@RequestBody JSONObject req) {
        return earlyWarningService.modifyConfig(req);
    }


    // 管理员信息管理
    @GetMapping("admin/list")
    public JSONObject adminList(HttpServletRequest req) {
        return earlyWarningService.adminList(RequestParametersUtil.getRequestParametersRetJson(req));
    }

    @PostMapping("admin/add")
    public JSONObject addAdmin(@RequestBody JSONObject req) {
        return earlyWarningService.addAdmin(req);
    }

    @PostMapping("admin/remove/{id}")
    public JSONObject removeAdmin(@PathVariable("id") String id) {
        return earlyWarningService.removeAdmin(id);
    }

    @GetMapping("admin/detail/{id}")
    public JSONObject adminDetail(@PathVariable("id") String id) {
        return earlyWarningService.adminDetail(id);
    }

    @PostMapping("admin/modify")
    public JSONObject modifyAdmin(@RequestBody JSONObject req) {
        return earlyWarningService.modifyAdmin(req);
    }
}
