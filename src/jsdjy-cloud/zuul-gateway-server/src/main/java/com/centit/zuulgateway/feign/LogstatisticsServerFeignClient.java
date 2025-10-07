package com.centit.zuulgateway.feign;

import com.alibaba.fastjson.JSONObject;
import com.centit.zuulgateway.hystrix.LogstatisticsServerHystrix;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;


/**
 * name为服务提供者向注册中心注册的实例名
 * fallback为断路器
 * 地址为服务提供者对外暴露的地址
 */
@FeignClient(value = "logstatistics", fallback = LogstatisticsServerHystrix.class)
public interface LogstatisticsServerFeignClient {

    /**
     * 操作日志记录
     */
    @PostMapping("/tLmOperlog/addOperLog")
    JSONObject addOperLog(JSONObject reqJson);


}
