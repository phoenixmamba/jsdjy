package com.centit.mallserver.feign;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.core.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 第三方服务模块接口
 * @Date : 2025/1/3 17:24
 **/
@Component
@FeignClient(value = "third")
public interface FeignThirdService {
    /**
     * 获取会员账户资产信息
     * @param mzUserId 麦座id
     * @return
     */
    @RequestMapping(value = "/mz/getUserAccountInfo",method = RequestMethod.GET)
    Result<JSONObject> getUserAccountInfo(@RequestParam("mzUserId") String mzUserId);

    /**
     * 获取会员收货地址
     * @param mzUserId 麦座id
     * @return
     */
    @RequestMapping(value = "/mz/getUserAddressDetail",method = RequestMethod.GET)
    Result<JSONObject> getUserAddressDetail(@RequestParam("mzUserId") String mzUserId,@RequestParam("addressId") String addressId);

    /**
     * 获取会员收货列表
     * @param mzUserId 麦座id
     * @return
     */
    @RequestMapping(value = "/mz/getUserAddressList",method = RequestMethod.GET)
    Result<JSONObject> getUserAddressList(@RequestParam("mzUserId") String mzUserId,@RequestParam("pageSize") Integer pageSize,@RequestParam("page") Integer page);

    /**
     * 获取优惠券详情
     * @param id 优惠券id
     * @return
     */
    @RequestMapping(value = "/crm/getCouponDtl",method = RequestMethod.GET)
    Result<JSONObject> getCouponDtl(@RequestParam("id") String id);

    /**
     * 获取优惠券列表
     * @param
     * @return
     */
    @RequestMapping(value = "/crm/getCouponList",method = RequestMethod.GET)
    Result<JSONObject> getCouponList();

    /**
     * 获取会员优惠券列表
     * @param userId
     * @param regPhone
     * @param flag
     * @return
     */
    @RequestMapping(value = "/crm/getUserCouponList",method = RequestMethod.GET)
    Result<JSONArray> getUserCouponList(@RequestParam("userId") String userId, @RequestParam("regPhone") String regPhone, @RequestParam("flag") String flag);
}
