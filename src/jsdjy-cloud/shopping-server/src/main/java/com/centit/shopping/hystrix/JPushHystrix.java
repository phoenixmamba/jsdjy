package com.centit.shopping.hystrix;

import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.feigin.JPushFeignClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Feign Hystrix断路器
 */
@Component
public class JPushHystrix implements JPushFeignClient {
    public static final Log log = LogFactory.getLog(JPushFeignClient.class);

    /**
     * 推送消息
     */
    @ResponseBody
    @Override
    public JSONObject pushMsg(JSONObject reqJson) {
        log.error("Fegin请求[推送消息]超时,接口为：capability/jpush/pushMsg");

        JSONObject timeOutRetJson = new JSONObject();
        timeOutRetJson.put("retCode", "10");
        timeOutRetJson.put("retMsg", "Feign请求超时，进入Hystrix断路器");
        timeOutRetJson.put("method", "pushMsg");
        return timeOutRetJson;
    }

    /**
     * 推送消息
     */
    @ResponseBody
    @Override
    public JSONObject pushMsgAll(JSONObject reqJson) {
        log.error("Fegin请求[推送消息]超时,接口为：capability/jpush/pushMsgAll");

        JSONObject timeOutRetJson = new JSONObject();
        timeOutRetJson.put("retCode", "10");
        timeOutRetJson.put("retMsg", "Feign请求超时，进入Hystrix断路器");
        timeOutRetJson.put("method", "pushMsg");
        return timeOutRetJson;
    }

}
