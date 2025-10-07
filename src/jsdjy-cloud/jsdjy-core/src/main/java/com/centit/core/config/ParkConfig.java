package com.centit.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/11/25 11:19
 **/
@ConfigurationProperties(prefix = "park")
@Component
@Data
public class ParkConfig {
    private  String appId;
    private  String appSecret;
    private String parkId;
    private String host;
    private String version;
}
