package com.centit.ticket.biz.controller;

import com.alibaba.fastjson.JSONObject;
import com.centit.ticket.biz.service.AddressService;
import com.centit.ticket.biz.service.TicketProjectService;
import com.centit.ticket.utils.RequestParametersUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>会员地址管理<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 前端控制器
 * @Date : 2021-04-08
 **/
@RestController
@RequestMapping("/address")
public class AddressController {

    @Resource
    private AddressService addressService;

    /**
     * 收货地址列表
     * @return
     */
    @GetMapping("/addressList")
    public JSONObject queryAddressList(HttpServletRequest request, HttpServletResponse response){
        return addressService.queryAddressList(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 收货地址详情
     * @return
     */
    @GetMapping("/addressDetail")
    public JSONObject queryAddressDetail(HttpServletRequest request, HttpServletResponse response){
        return addressService.queryAddressDetail(RequestParametersUtil.getRequestParametersRetJson(request));
    }

    /**
     * 设置默认地址
     * @return
     */
    @PostMapping("/setDefaultDetail")
    public JSONObject setDefaultDetail(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return addressService.setDefaultDetail(reqJson);
    }

    /**
     * 新增收货地址
     * @return
     */
    @PostMapping("/addAddress")
    public JSONObject addAddress(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return addressService.addAddress(reqJson);
    }

    /**
     * 修改收货地址
     * @return
     */
    @PostMapping("/editAddress")
    public JSONObject editAddress(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return addressService.editAddress(reqJson);
    }

    /**
     * 删除收货地址
     * @return
     */
    @PostMapping("/removeAddress")
    public JSONObject removeAddress(@RequestBody JSONObject reqJson,HttpServletRequest request){
        return addressService.removeAddress(reqJson);
    }

    /**
     * 获取省/市/区编码
     * @return
     */
    @GetMapping("/queryAreaCode")
    public JSONObject queryAreaCode(HttpServletRequest request, HttpServletResponse response){
        return addressService.queryAreaCode(RequestParametersUtil.getRequestParametersRetJson(request));
    }
}