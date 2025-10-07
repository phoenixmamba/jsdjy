package com.centit.admin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/11/21 18:49
 **/
@ConfigurationProperties(prefix = "system")
@Component
@Data
public class SystemConfig {
    private String initalPwdFUser;
    private int loginExpireTime;
    private int loginErrorTimes;
    private int loginErrorExpireMinutes;
    private int verificationCodeLength;
    private String verificationCodeBaseString;

}
