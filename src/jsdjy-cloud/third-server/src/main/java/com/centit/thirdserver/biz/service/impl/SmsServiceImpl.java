package com.centit.thirdserver.biz.service.impl;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.centit.core.dto.SmsEntity;
import com.centit.core.exp.ThirdApiException;
import com.centit.core.result.Result;
import com.centit.core.result.ResultCodeEnum;
import com.centit.thirdserver.biz.service.SmsService;
import com.centit.thirdserver.config.SmsConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2025/1/3 9:50
 **/
@Slf4j
@Service
public class SmsServiceImpl implements SmsService {
    @Resource
    private SmsConfig smsConfig;

    private final static String MSG_TYPE = "2";
    private final static String RT = "json";
    private final static String RESPONSE_PARAM_STATUS = "status";
    private final static String RESPONSE_PARAM_SUCCESS = "0";

    @Override
    public Result sendTemplateSMS(SmsEntity smsEntity) {
        Map<String, String> params = new HashMap<>(16);
        params.put("method", smsConfig.getMethod());
        params.put("username", smsConfig.getUsername());
        params.put("password", smsConfig.getPassword());
        params.put("veryCode", smsConfig.getVeryCode());
        params.put("tempid", smsEntity.getTempId());
        params.put("content", smsEntity.getContent());
        params.put("msgtype", MSG_TYPE);
        params.put("mobile", smsEntity.getMobile());
        params.put("rt", RT);
        log.info("单条短信推送请求报文：{}", params);
        String result = null;
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        try {
            result = HttpUtil.createPost(smsConfig.getUrl()).body(sb.toString()).execute().body();
            log.info("单条短信推送返回报文：{}", result);
            JSONObject resObj = JSONObject.parseObject(result);
            if(!resObj.get(RESPONSE_PARAM_STATUS).equals(RESPONSE_PARAM_SUCCESS)){
                throw new ThirdApiException(ResultCodeEnum.SMS_FAILED);
            }
        } catch (Exception e) {
            log.error("单条短信推送失败，推送报文：{},返回报文：{}",params,result,e);
            throw new ThirdApiException(ResultCodeEnum.SMS_FAILED);
        }
        return Result.defaultSuccess();
    }
}
