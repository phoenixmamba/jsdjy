//package com.centit.park.feign;
//
//import com.alibaba.fastjson.JSONObject;
//import com.centit.park.hystrix.JPushHystrix;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.PostMapping;
//
//
///**
// * name为服务提供者向注册中心注册的实例名
// * fallback为断路器
// * 地址为服务提供者对外暴露的地址
// */
////@FeignClient("capability/jpush")
//@FeignClient(value = "capability/jpush", fallback = JPushHystrix.class)
//public interface JPushFeignClient {
//
//    /**
//     * 推送消息
//     */
//    @PostMapping("/pushMsg")
//    JSONObject pushMsg(JSONObject reqJson);
//
//
//
//
//
//
//}
