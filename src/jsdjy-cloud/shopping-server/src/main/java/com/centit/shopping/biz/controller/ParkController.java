package com.centit.shopping.biz.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.biz.service.ParkService;
import com.centit.shopping.biz.service.ShoppingArtsService;
import com.centit.shopping.utils.RequestParametersUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>停车缴费<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 前端控制器
 * @Date : 2021-03-19
 **/
@RestController
@RequestMapping("/park")
public class ParkController {

    @Resource
    private ParkService parkService;

    /**
     * 获取用户默认车牌号
     * @return
     */
    @GetMapping("/queryDefaultPlateNo")
    public JSONObject queryDefaultPlateNo(HttpServletRequest request, HttpServletResponse response){
        return parkService.queryDefaultPlateNo(RequestParametersUtil.getRequestParametersRetJson(request));
    }


    /**
     * 账单查询/费用查询
     * @return
     */
    @GetMapping("/getParkingPaymentInfo")
    public JSONObject getParkingPaymentInfo(HttpServletRequest request, HttpServletResponse response){
        return parkService.getParkingPaymentInfo(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 订单页面渲染
     * @return
     */
    @PostMapping("/renderParkOrder")
    public JSONObject renderParkOrder(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return parkService.renderParkOrder(reqJson,request);
    }

    /**
     * 下单
     * @return
     */
    @PostMapping("/addParkOrder")
    public JSONObject addParkOrder(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return parkService.addParkOrder(reqJson,request);
    }

    /**
     * 查询车牌号列表
     * @return
     */
    @GetMapping("/plateNoList")
    public JSONObject queryPlateNoList(HttpServletRequest request, HttpServletResponse response){
        return parkService.queryPlateNoList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 查询车牌号列表
     * @return
     */
    @PostMapping("/setDefaultPlateNo")
    public JSONObject setDefaultPlateNo(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return parkService.setDefaultPlateNo(reqJson);
    }

    /**
     * 新增车牌号
     * @return
     */
    @PostMapping("/addPlateNo")
    public JSONObject addPlateNo(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return parkService.addPlateNo(reqJson);
    }

    /**
     * 编辑车牌号
     * @return
     */
    @PostMapping("/editPlateNo")
    public JSONObject editPlateNo(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return parkService.editPlateNo(reqJson);
    }

    /**
     * 删除车牌号
     * @return
     */
    @PostMapping("/delPlateNo")
    public JSONObject delPlateNo(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return parkService.delPlateNo(reqJson);
    }
}