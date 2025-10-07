package com.centit.zuulgateway.hystrix;

import com.alibaba.fastjson.JSONObject;
import com.centit.zuulgateway.feign.LogstatisticsServerFeignClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Feign Hystrix断路器
 */
@Component
public class LogstatisticsServerHystrix implements LogstatisticsServerFeignClient {
    public static final Log log = LogFactory.getLog(LogstatisticsServerFeignClient.class);

    /**
     * 推送消息
     */
    @ResponseBody
    @Override
    public JSONObject addOperLog(JSONObject reqJson) {
        log.error("Fegin请求[操作日志记录]超时,接口为：operLog/addOperLog");

        JSONObject timeOutRetJson = new JSONObject();
        timeOutRetJson.put("retCode", "10");
        timeOutRetJson.put("retMsg", "Feign请求超时，进入Hystrix断路器");
        timeOutRetJson.put("method", "pushMsg");
        return timeOutRetJson;
    }


}
