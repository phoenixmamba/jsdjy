package com.centit.thirdserver.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 短信平台相关配置
 * @Date : 2025/1/3 9:49
 **/
@ConfigurationProperties(prefix = "sms")
@Component
@Data
public class SmsConfig {
    private String url;
    private String method;
    private String username;
    private String password;
    private String veryCode;
}
