package com.centit.thirdserver.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/11/21 21:53
 **/
@ConfigurationProperties(prefix = "mz")
@Component
@Data
public class MzConfig {
    private String secret;
    private Client client = new Client();
    private Pay pay = new Pay();

    /**
     * 麦座连接信息
     */
    @Data
    public static class Client{
        private String url;
        private String appKey;
        private String appSecret;
    }

    /**
     * 支付渠道号
     */
    @Data
    public static class Pay{
        private String wx;
        private String ali;
    }

}
