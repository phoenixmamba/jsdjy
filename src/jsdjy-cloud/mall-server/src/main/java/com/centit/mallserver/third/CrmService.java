package com.centit.mallserver.third;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.core.exp.ThirdApiException;
import com.centit.core.result.Result;
import com.centit.core.result.ResultCodeEnum;
import com.centit.mallserver.feign.FeignThirdService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Crm接口调用服务类
 * @Date : 2024/11/25 11:18
 **/
@Slf4j
@Component
public class CrmService {

    @Resource
    private FeignThirdService feignThirdService;

    /**
     * 获取优惠券详情
     * @return JSONObject
     */
    public JSONObject getCouponDtl(String id) {
        Result<JSONObject> result =feignThirdService.getCouponDtl(id);
        if(result.getRetCode().equals("0")){
            return result.getBizData();
        }else{
            throw new ThirdApiException(ResultCodeEnum.THIRD_API_ERROR,"获取优惠券详情失败");
        }
    }

    /**
     * 获取会员优惠券列表
     * @return JSONObject
     */
    public JSONArray getUserCouponList(String userId,String regPhone,String flag) {
        Result<JSONArray> result =feignThirdService.getUserCouponList(userId,regPhone,flag);
        if(result.getRetCode().equals("0")){
            return  result.getBizData();
        }else{
            throw new ThirdApiException(ResultCodeEnum.THIRD_API_ERROR,"获取会员优惠券列表失败");
        }
    }

    /**
     * 获取优惠券列表
     * @return JSONObject
     */
    public JSONObject getCouponList() {
        Result<JSONObject> result =feignThirdService.getCouponList();
        if(result.getRetCode().equals("0")){
            return result.getBizData();
        }else{
            throw new ThirdApiException(ResultCodeEnum.THIRD_API_ERROR,"获取优惠券列表失败");
        }
    }


}
