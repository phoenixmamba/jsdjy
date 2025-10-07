package com.centit.core.service.third;


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
 * @Description : 麦座接口调用服务类
 * @Date : 2024/11/25 11:18
 **/
@Slf4j
@Component
public class MzService {

    @Resource
    private FeignThirdService feignThirdService;

    /**
     * 获取会员账户资产信息
     * @return JSONObject
     */
    public JSONObject getUserAccountInfo(String mzUserId) {
        Result<JSONObject> result =feignThirdService.getUserAccountInfo(mzUserId);
        if(result.getRetCode().equals("0")){
            return result.getBizData();
        }else{
            throw new ThirdApiException(ResultCodeEnum.THIRD_API_ERROR,"获取会员账户资产信息失败");
        }
    }

    /**
     * 获取会员收货地址详情
     * @return JSONObject
     */
    public JSONObject getUserAddressDetail(String mzUserId,String addressId) {
        Result result =feignThirdService.getUserAddressDetail(mzUserId,addressId);
        if(result.getRetCode().equals("0")){
            return (JSONObject) result.getBizData();
        }else{
            throw new ThirdApiException(ResultCodeEnum.THIRD_API_ERROR,"获取会员收货地址详情失败");
        }
    }

    /**
     * 获取会员收货地址列表
     * @return JSONObject
     */
    public JSONObject getUserAddressList(String mzUserId,int pageSize,int page) {
        Result<JSONObject> result =feignThirdService.getUserAddressList(mzUserId,pageSize,page);
        if(result.getRetCode().equals("0")){
            return  result.getBizData();
        }else{
            throw new ThirdApiException(ResultCodeEnum.THIRD_API_ERROR,"获取会员收货地址列表失败");
        }
    }
}
