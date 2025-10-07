package com.centit.thirdserver.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2025/9/2 16:44
 **/
@Configuration
@EnableAsync
@Slf4j
public class LogAsyncConfig {
    @Bean("logExecutor")
    public Executor logExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("LogAsync-");
        // 日志保存失败时，直接丢弃（后续可以记录到本地文件）
        executor.setRejectedExecutionHandler((r, e) ->
                log.error("日志保存任务被拒绝，队列已满")
        );
        executor.initialize();
        return executor;
    }
}
