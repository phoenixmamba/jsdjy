package com.centit.thirdserver.biz.service;

import com.centit.core.dto.SmsEntity;
import com.centit.core.result.Result;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2025/1/3 9:50
 **/
public interface SmsService {
    /**
     * 模板短信推送
     * @param smsEntity 短信推送信息
     */
    Result sendTemplateSMS(SmsEntity smsEntity);
}
