package com.centit.shopping.webmgr.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.utils.RequestParametersUtil;
import com.centit.shopping.webmgr.service.TEarlywarningService;
import com.centit.shopping.webmgr.service.WebCommonService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>预警任务<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 前端控制器
 * @Date : 2021-02-21
 **/
@RestController
@RequestMapping("/webmgr/earlyWarning")
public class EarlyWarningTaskController {

    @Resource
    private TEarlywarningService tEarlywarningService;


    /**
     * 查询任务列表
     * @return
     */
    @GetMapping("/queryTaskList")
    public JSONObject queryTaskList(HttpServletRequest request){
        return tEarlywarningService.queryTaskList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 新增预警任务
     * @return
     */
    @PostMapping("/addWarningTask")
    public JSONObject addWarningTask(@RequestBody JSONObject reqJson){
        return tEarlywarningService.addWarningTask(reqJson);
    }

    /**
     * 编辑预警任务
     * @return
     */
    @PostMapping("/editWarningTask")
    public JSONObject editWarningTask(@RequestBody JSONObject reqJson){
        return tEarlywarningService.editWarningTask(reqJson);
    }

    /**
     * 停止本轮预警
     * @return
     */
    @PostMapping("/stopThisTurnWarningTask")
    public JSONObject stopThisTurnWarningTask(@RequestBody JSONObject reqJson){
        return tEarlywarningService.stopThisTurnWarningTask(reqJson);
    }

    /**
     * 开启/关闭预警任务
     * @return
     */
    @PostMapping("/closeWarningTask")
    public JSONObject closeWarningTask(@RequestBody JSONObject reqJson){
        return tEarlywarningService.closeWarningTask(reqJson);
    }

    /**
     * 删除预警任务
     * @return
     */
    @PostMapping("/delWarningTask")
    public JSONObject delWarningTask(@RequestBody JSONObject reqJson){
        return tEarlywarningService.delWarningTask(reqJson);
    }

}