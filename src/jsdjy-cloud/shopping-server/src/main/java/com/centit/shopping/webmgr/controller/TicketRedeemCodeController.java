package com.centit.shopping.webmgr.controller;

import com.alibaba.fastjson.JSONObject;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.centit.shopping.utils.RequestParametersUtil;
import org.springframework.web.bind.annotation.*;
import com.centit.shopping.webmgr.service.TicketRedeemCodeService;

/**
 * <p>兑换码详细表<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 前端控制器
 * @Date : 2022-07-18
 **/
@RestController
@RequestMapping("/webmgr/redeemCode")
public class TicketRedeemCodeController {

    @Resource
    private TicketRedeemCodeService ticketRedeemCodeService;

    /**
     * 查询兑换码创建批次列表
     * @return
     */
    @GetMapping("/batchPageList")
    public JSONObject queryBatchList(HttpServletRequest request, HttpServletResponse response){
        return ticketRedeemCodeService.queryBatchList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 创建指定数量的兑换码
     * @return
     */
    @PostMapping("/createCode")
    public JSONObject createCode(@RequestBody JSONObject reqJson){
        return ticketRedeemCodeService.createCode(reqJson);
    }

    /**
     * 按批次直接删除兑换码
     * @return
     */
    @PostMapping("/deleteBatch")
    public JSONObject deleteBatch(@RequestBody JSONObject reqJson){
        return ticketRedeemCodeService.deleteBatch(reqJson);
    }

    /**
     * 查询已创建的发卡单位列表
     * @return
     */
    @GetMapping("/companyPageList")
    public JSONObject queryCompanyList(HttpServletRequest request, HttpServletResponse response){
        return ticketRedeemCodeService.queryCompanyList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 创建单位
     * @return
     */
    @PostMapping("/createCompany")
    public JSONObject createCompany(@RequestBody JSONObject reqJson){
        return ticketRedeemCodeService.createCompany(reqJson);
    }

    /**
     * 编辑单位
     * @return
     */
    @PostMapping("/editCompany")
    public JSONObject editCompany(@RequestBody JSONObject reqJson){
        return ticketRedeemCodeService.editCompany(reqJson);
    }

    /**
     * 删除单位
     * @return
     */
    @PostMapping("/delCompany")
    public JSONObject delCompany(@RequestBody JSONObject reqJson){
        return ticketRedeemCodeService.delCompany(reqJson);
    }

    /**
     * 查询演出项目列表
     * @return
     */
    @GetMapping("/projectList")
    public JSONObject queryProjectList(HttpServletRequest request, HttpServletResponse response){
        return ticketRedeemCodeService.queryProjectList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 查询演出项目详情
     * @return
     */
    @GetMapping("/projectDetail")
    public JSONObject projectDetail(HttpServletRequest request, HttpServletResponse response){
        return ticketRedeemCodeService.projectDetail(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 场馆列表
     * @return
     */
    @GetMapping("/venueList")
    public JSONObject queryVenueList(HttpServletRequest request, HttpServletResponse response){
        return ticketRedeemCodeService.queryVenueList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 新增项目
     * @return
     */
    @PostMapping("/addProject")
    public JSONObject addProject(@RequestBody JSONObject reqJson){
        return ticketRedeemCodeService.addProject(reqJson);
    }

    /**
     * 编辑项目
     * @return
     */
    @PostMapping("/editProject")
    public JSONObject editProject(@RequestBody JSONObject reqJson){
        return ticketRedeemCodeService.editProject(reqJson);
    }

    /**
     * 删除项目
     * @return
     */
    @PostMapping("/delProject")
    public JSONObject delProject(@RequestBody JSONObject reqJson){
        return ticketRedeemCodeService.delProject(reqJson);
    }

    /**
     * 获取活动可用优惠码的起始编码
     * @return
     */
    @GetMapping("/getStartCode")
    public JSONObject getStartCode(HttpServletRequest request, HttpServletResponse response){
        return ticketRedeemCodeService.getStartCode(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 创建活动
     * @return
     */
    @PostMapping("/createActivity")
    public JSONObject createActivity(@RequestBody JSONObject reqJson){
        return ticketRedeemCodeService.createActivity(reqJson);
    }

    /**
     * 查询兑换码活动列表
     * @return
     */
    @GetMapping("/activityList")
    public JSONObject queryActivityList(HttpServletRequest request, HttpServletResponse response){
        return ticketRedeemCodeService.queryActivityList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 获取兑换码活动详情
     * @return
     */
    @GetMapping("/activityDetail")
    public JSONObject queryActivityDetail(HttpServletRequest request, HttpServletResponse response){
        return ticketRedeemCodeService.queryActivityDetail(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 编辑活动信息
     * @return
     */
    @PostMapping("/editActivity")
    public JSONObject editActivity(@RequestBody JSONObject reqJson){
        return ticketRedeemCodeService.editActivity(reqJson);
    }

    /**
     * 查询活动绑卡记录
     * @return
     */
    @GetMapping("/activityBindList")
    public JSONObject queryActivityBindList(HttpServletRequest request, HttpServletResponse response){
        return ticketRedeemCodeService.queryActivityBindList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 按绑定记录删除兑换码
     * @return
     */
    @PostMapping("/delCodeByBindId")
    public JSONObject delCodeByBindId(@RequestBody JSONObject reqJson){
        return ticketRedeemCodeService.delCodeByBindId(reqJson);
    }

    /**
     * 活动追加绑卡
     * @return
     */
    @PostMapping("/acticityAddCodes")
    public JSONObject acticityAddCodes(@RequestBody JSONObject reqJson){
        return ticketRedeemCodeService.acticityAddCodes(reqJson);
    }

    /**
     * 上/下架活动
     * @return
     */
    @PostMapping("/pubActivity")
    public JSONObject pubActivity(@RequestBody JSONObject reqJson){
        return ticketRedeemCodeService.pubActivity(reqJson);
    }

    /**
     * 删除活动
     * @return
     */
    @PostMapping("/delActivity")
    public JSONObject delActivity(@RequestBody JSONObject reqJson){
        return ticketRedeemCodeService.delActivity(reqJson);
    }

    /**
     * 查询兑换码列表
     * @return
     */
    @GetMapping("/codePageList")
    public JSONObject queryCodePageList(HttpServletRequest request, HttpServletResponse response){
        return ticketRedeemCodeService.queryCodePageList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 导出兑换码文件
     * @return
     */
    @GetMapping("/exportCodeFile")
    public JSONObject exportCodeFile(HttpServletRequest request, HttpServletResponse response){
        return ticketRedeemCodeService.exportCodeFile(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 批量删除兑换码
     * @return
     */
    @PostMapping("/delCodes")
    public JSONObject delCodes(@RequestBody JSONObject reqJson){
        return ticketRedeemCodeService.delCodes(reqJson);
    }
}