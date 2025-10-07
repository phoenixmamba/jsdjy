package com.centit.thirdserver.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/11/25 11:19
 **/
@ConfigurationProperties(prefix = "crm")
@Component
@Data
public class CrmConfig {
    private  String systemName;
    private  String host;
    private Integer authExpireMinutes;
}
