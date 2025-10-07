package com.centit.shopping.webmgr.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.utils.RequestParametersUtil;
import com.centit.shopping.webmgr.service.PlatformMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("webmgr/platformMonitor/")
@RestController
public class PlatformMonitorController {
    @Autowired
    private PlatformMonitorService platformMonitorService;

    @GetMapping("registerUserStatistics")
    public JSONObject queryRegisterUserStatistics(HttpServletRequest request) {
        return platformMonitorService.queryRegisterUserStatistics(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    @GetMapping("activityUserStatistics")
    public JSONObject queryActivityUserStatistics(HttpServletRequest request) {
        return platformMonitorService.queryActivityUserStatistics(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    @GetMapping("equipmentStatistics")
    public JSONObject queryEquipmentStatistics(HttpServletRequest request) {
        return platformMonitorService.queryEquipmentStatistics(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    @GetMapping("installVersionStatistics")
    public JSONObject queryInstallVersionStatistics(HttpServletRequest request) {
        return platformMonitorService.queryInstallVersionStatistics(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    @GetMapping("moneyStatistics")
    public JSONObject queryMoneyStatistics(HttpServletRequest request) {
        return platformMonitorService.queryMoneyStatistics(RequestParametersUtil.getRequestParametersRetJson(request));
    }

}
