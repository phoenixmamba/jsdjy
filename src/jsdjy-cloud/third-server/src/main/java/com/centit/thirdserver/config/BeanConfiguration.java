package com.centit.thirdserver.config;

import com.taobao.api.DefaultTaobaoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/11/26 15:50
 **/
@Configuration
public class BeanConfiguration {
    @Resource
    private MzConfig mzConfig;

    @Bean
    public DefaultTaobaoClient defaultTaobaoClient() {
        return new DefaultTaobaoClient(mzConfig.getClient().getUrl(), mzConfig.getClient().getAppKey(), mzConfig.getClient().getAppSecret());
    }
}
