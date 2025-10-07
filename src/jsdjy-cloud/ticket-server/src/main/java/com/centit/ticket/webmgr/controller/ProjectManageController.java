package com.centit.ticket.webmgr.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.ticket.biz.service.TicketProjectService;
import com.centit.ticket.utils.RequestParametersUtil;
import com.centit.ticket.webmgr.service.ProjectManageService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>演出管理<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 前端控制器
 * @Date : 2021-04-08
 **/
@RestController
@RequestMapping("/webmgr/projectManage")
public class ProjectManageController {

    @Resource
    private ProjectManageService projectManageService;
    @Resource
    private TicketProjectService ticketProjectService;

    /**
     * 获取演出分类
     * @return
     */
    @GetMapping("/ticketClass")
    public JSONObject queryTicketClass(HttpServletRequest request, HttpServletResponse response){
        return projectManageService.queryTicketClass(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 编辑演出分类
     * @return
     */
    @PostMapping("/editTicketClass")
    public JSONObject editTicketClass(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return projectManageService.editTicketClass(reqJson);
    }

    /**
     * 查询演出列表
     * @return
     */
    @GetMapping("/projectList")
    public JSONObject queryProjectList(HttpServletRequest request, HttpServletResponse response){
        return projectManageService.queryProjectList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 项目详情
     * @return
     */
    @GetMapping("/projectDetail")
    public JSONObject projectDetail(HttpServletRequest request, HttpServletResponse response){
        return projectManageService.projectDetail(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 查询项目关联文创与积分商品列表
     * @return
     */
    @GetMapping("/projectGoodsList")
    public JSONObject projectGoodsList(HttpServletRequest request, HttpServletResponse response){
        return projectManageService.projectGoodsList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 新增项目关联文创与积分商品
     * @return
     */
    @PostMapping("/addProjectGoods")
    public JSONObject addProjectGoods(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return projectManageService.addProjectGoods(reqJson);
    }

    /**
     * 删除项目关联文创与积分商品
     * @return
     */
    @PostMapping("/delProjectGoods")
    public JSONObject delProjectGoods(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return projectManageService.delProjectGoods(reqJson);
    }

    /**
     * 修改项目关联文创与积分商品
     * @return
     */
    @PostMapping("/editProjectGoods")
    public JSONObject editProjectGoods(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return projectManageService.editProjectGoods(reqJson);
    }

//    /**
//     * 保存项目关联文创与积分商品列表
//     * @return
//     */
//    @PostMapping("/saveProjectGoodsList")
//    public JSONObject saveProjectGoodsList(@RequestBody JSONObject reqJson,HttpServletRequest request){
//        return projectManageService.saveProjectGoodsList(reqJson);
//    }

    /**
     * 设置演出项目电子节目单
     * @return
     */
    @PostMapping("/saveProjectGuide")
    public JSONObject saveProjectGuide(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return projectManageService.saveProjectGuide(reqJson);
    }

    /**
     * 查询已设置为推荐的演出列表
     * @return
     */
    @GetMapping("/recommondProjectList")
    public JSONObject queryRecommondProjectList(HttpServletRequest request, HttpServletResponse response){
        return projectManageService.queryRecommondProjectList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 新增推荐演出
     * @return
     */
    @PostMapping("/addRecProject")
    public JSONObject addRecProject(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return projectManageService.addRecProject(reqJson);
    }

    /**
     * 删除推荐演出
     * @return
     */
    @PostMapping("/delRecProject")
    public JSONObject delRecProject(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return projectManageService.delRecProject(reqJson);
    }

    /**
     * 修改推荐演出
     * @return
     */
    @PostMapping("/editRecProject")
    public JSONObject editRecProject(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return projectManageService.editRecProject(reqJson);
    }

    /**
     * 从麦座同步数据
     * @return
     */
    @PostMapping("/syncData")
    public JSONObject syncData(@RequestBody JSONObject reqJson,HttpServletRequest request){
        JSONObject obj  =projectManageService.syncData(reqJson);
        ticketProjectService.setHomeData();
        return obj;
    }

    /**
     * 演出消息推送
     * @return
     */
    @PostMapping("/pushProjectMsg")
    public JSONObject pushProjectMsg(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return projectManageService.pushProjectMsg(reqJson);
    }
}