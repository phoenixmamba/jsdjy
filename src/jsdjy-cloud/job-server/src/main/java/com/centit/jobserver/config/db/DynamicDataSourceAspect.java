package com.centit.jobserver.config.db;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/11/28 15:29
 **/
@Component
@Aspect
@Slf4j
public class DynamicDataSourceAspect {
    private static final String DEFAULT_DATASOURCE = "masterDataSource";

    @Pointcut("@annotation(com.centit.jobserver.config.db.DS)")
    public void dsPointCut() {}

    @Before("dsPointCut() && @annotation(ds)")
    public void changeDataSource(JoinPoint joinPoint, DS ds) {
        String dataSourceName = ds.value();
        if (DynamicDataSourceContextHolder.containsDataSource(dataSourceName)) {
            DynamicDataSourceContextHolder.setDataSource(dataSourceName);
            log.info("切换到数据源：{}", dataSourceName);
        } else {
            log.error("数据源不存在：{}", dataSourceName);
        }
    }
    @After("@annotation(ds)")
    public void clearDataSource(JoinPoint joinPoint, DS ds) {
        DynamicDataSourceContextHolder.clear();
    }
}
