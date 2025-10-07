package com.centit.shopping.biz.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.biz.service.TicketHolderService;
import com.centit.shopping.utils.RequestParametersUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>票夹<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 前端控制器
 * @Date : 2021-02-25
 **/
@RestController
@RequestMapping("/ticketHolder")
public class TicketHolderController {

    @Resource
    private TicketHolderService ticketHolderService;

    /**
     * 查询票夹演出票列表
     * @return
     */
    @GetMapping("/ticketList")
    public JSONObject queryTicketList(HttpServletRequest request){
        return ticketHolderService.queryTicketList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

//    /**
//     * 获取演出票凭证
//     * @return
//     */
//    @GetMapping("/ticketDetail/{ofId}")
//    public JSONObject ticketDetail(@PathVariable String ofId, HttpServletRequest request){
//        return ticketHolderService.ticketDetail(ofId);
//    }

    /**
     * 获取演出票凭证
     * @return
     */
    @GetMapping("/ticketDetail/{ofId}")
    public JSONObject ticketDetail(@PathVariable String ofId, HttpServletRequest request){
        return ticketHolderService.ticketDetail(RequestParametersUtil.getRequestParametersRetJson(request),ofId);
    }

    /**
     * 查询待核销商品列表
     * @return
     */
    @GetMapping("/goodsList")
    public JSONObject queryGoodsList(HttpServletRequest request){
        return ticketHolderService.queryGoodsList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 获取待核销商品详情
     * @return
     */
    @GetMapping("/goodsWriteOffDetail/{gcId}")
    public JSONObject goodsWriteOffDetail(@PathVariable String gcId, HttpServletRequest request){
        return ticketHolderService.goodsWriteOffDetail(gcId);
    }

    /**
     * 查询票夹艺教活动列表
     * @return
     */
    @GetMapping("/activityList")
    public JSONObject queryActivityList(HttpServletRequest request){
        return ticketHolderService.queryActivityList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 获取待核销活动详情
     * @return
     */
    @GetMapping("/actWriteOffDetail/{gcId}")
    public JSONObject actWriteOffDetail(@PathVariable String gcId, HttpServletRequest request){
        return ticketHolderService.actWriteOffDetail(gcId);
    }

    /**
     * 查询票夹爱艺计划列表
     * @return
     */
    @GetMapping("/planList")
    public JSONObject queryPlanList(HttpServletRequest request){
        return ticketHolderService.queryPlanList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 获取待核销爱艺计划详情
     * @return
     */
    @GetMapping("/planWriteOffDetail/{gcId}")
    public JSONObject planWriteOffDetail(@PathVariable String gcId, HttpServletRequest request){
        return ticketHolderService.planWriteOffDetail(gcId);
    }

    /**
     * 获取待核销详情
     * @return
     */
    @GetMapping("/toWriteOffDetail/{offCode}")
    public JSONObject toWriteOffDetail(@PathVariable String offCode, HttpServletRequest request){
        return ticketHolderService.toWriteOffDetail(RequestParametersUtil.getRequestParametersRetJson(request),offCode);
    }

    /**
     * 核销
     * @return
     */
    @PostMapping("/writeOff")
    public JSONObject writeOff(@RequestBody JSONObject reqJson){
        return ticketHolderService.writeOff(reqJson);
    }

    /**
     * 查询我的可核销商品列表
     * @return
     */
    @GetMapping("/myWriteOffGoodsList")
    public JSONObject queryMyWriteOffGoodsList(HttpServletRequest request){
        return ticketHolderService.queryMyWriteOffGoodsList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 查询我的可核销商品详情
     * @return
     */
    @GetMapping("/myWriteOffGoodsDetail")
    public JSONObject queryMyWriteOffGoodsDetail(HttpServletRequest request){
        return ticketHolderService.queryMyWriteOffGoodsDetail(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 查询我的可核销艺术活动列表
     * @return
     */
    @GetMapping("/myWriteOffActivityList")
    public JSONObject queryMyWriteOffActivityList(HttpServletRequest request){
        return ticketHolderService.queryMyWriteOffActivityList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 查询我的可核销艺术活动详情
     * @return
     */
    @GetMapping("/myWriteOffActivityDetail")
    public JSONObject queryMyWriteOffActivityDetail(HttpServletRequest request){
        return ticketHolderService.queryMyWriteOffActivityDetail(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 查询我的可核销爱艺计划列表
     * @return
     */
    @GetMapping("/myWriteOffPlanList")
    public JSONObject queryMyWriteOffPlanList(HttpServletRequest request){
        return ticketHolderService.queryMyWriteOffPlanList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 查询我的可核销爱艺计划详情
     * @return
     */
    @GetMapping("/myWriteOffPlanDetail")
    public JSONObject queryMyWriteOffPlanDetail(HttpServletRequest request){
        return ticketHolderService.queryMyWriteOffPlanDetail(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 查询我的可核销优惠券列表
     * @return
     */
    @GetMapping("/myWriteOffCouponList")
    public JSONObject queryMyWriteOffCouponList(HttpServletRequest request){
        return ticketHolderService.queryMyWriteOffCouponList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 查询我的优惠券核销详情
     * @return
     */
    @GetMapping("/myWriteOffCouponDetail")
    public JSONObject queryMyWriteOffCouponDetail(HttpServletRequest request){
        return ticketHolderService.queryMyWriteOffCouponDetail(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 查询我的可核销演出项目兑换列表
     * @return
     */
    @GetMapping("/myWriteOffRedeemProjectList")
    public JSONObject queryMyWriteOffRedeemProjectList(HttpServletRequest request){
        return ticketHolderService.queryMyWriteOffRedeemProjectList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 查询我的可核销演出兑换详情
     * @return
     */
    @GetMapping("/myWriteOffRedeemProjectDetail")
    public JSONObject queryMyWriteOffRedeemProjectDetail(HttpServletRequest request){
        return ticketHolderService.queryMyWriteOffRedeemProjectDetail(RequestParametersUtil.getRequestParametersRetJson(request));
    }
}

