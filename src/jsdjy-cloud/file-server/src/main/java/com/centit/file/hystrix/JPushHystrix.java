//package com.centit.park.hystrix;
//
//import com.alibaba.fastjson.JSONObject;
//import com.centit.park.feign.JPushFeignClient;
//import CommonInit;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.springframework.stereotype.Component;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//
///**
// * Feign Hystrix断路器
// */
//@Component
//public class JPushHystrix implements JPushFeignClient {
//    public static final Log log = LogFactory.getLog(JPushFeignClient.class);
//
//    /**
//     * 推送消息
//     */
//    @ResponseBody
//    @Override
//    public JSONObject pushMsg(JSONObject reqJson) {
//        log.error("Fegin请求[推送消息]超时,接口为：capability/jpush/pushMsg");
//
//        JSONObject timeOutRetJson = new JSONObject();
//        timeOutRetJson.put("retCode", CommonInit.static_retCode10);
//        timeOutRetJson.put("retMsg", CommonInit.static_retMsg10);
//        timeOutRetJson.put("method", "pushMsg");
//        return timeOutRetJson;
//    }
//
//
//}
