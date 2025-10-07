package com.centit.mallserver.config;

import com.centit.mallserver.threadPool.ThreadPoolExecutorFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 线程池配置
 * @Date : 2024/12/18 9:46
 **/
@Configuration
public class AsyncConfig {
    @Bean(name = "taskExecutor")
    public ExecutorService taskExecutor() {
        return ThreadPoolExecutorFactory.createThreadPoolExecutor();
    }
}
