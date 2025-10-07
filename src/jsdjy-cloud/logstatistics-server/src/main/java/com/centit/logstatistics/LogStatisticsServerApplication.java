package com.centit.logstatistics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableEurekaClient
@RefreshScope
@EnableDiscoveryClient
public class LogStatisticsServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogStatisticsServerApplication.class, args);
    }

}
