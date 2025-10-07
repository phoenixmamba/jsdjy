package com.centit.jobserver.config.db;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/11/28 14:54
 **/
@Configuration
public class DynamicDataSourceConfig {
    @Value("${spring.datasource.type}")
    private Class<? extends DataSource> dataSourceType;
    @Value("${spring.datasource.druid-pool.initial-size}")
    private int initialSize;
    @Value("${spring.datasource.druid-pool.max-active}")
    private int maxActive;
    @Value("${spring.datasource.druid-pool.max-wait}")
    private int maxWait;
    @Value("${spring.datasource.druid-pool.min-idle}")
    private int minIdle;


    @Bean("masterDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.primary")
    public DataSource masterDataSource() {
        DruidDataSource druidDataSource = (DruidDataSource) DataSourceBuilder.create().type(dataSourceType).build();
        druidPoolConfig(druidDataSource);
        return druidDataSource;
    }


    @Bean("slaveDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.slave")
    public DataSource slaveDataSource() {
        DruidDataSource druidDataSource = (DruidDataSource) DataSourceBuilder.create().type(dataSourceType).build();
        druidPoolConfig(druidDataSource);
        return druidDataSource;
    }

    private void druidPoolConfig(DruidDataSource druidDataSource) {
        druidDataSource.setMaxActive(maxActive);
        druidDataSource.setMaxWait(maxWait);
        druidDataSource.setInitialSize(initialSize);
        druidDataSource.setMinIdle(minIdle);
    }

    @Primary
    @Bean("dataSource")
    @DependsOn({"masterDataSource", "slaveDataSource"})
    public DataSource dataSource(@Qualifier("masterDataSource") DataSource masterDataSource, @Qualifier("slaveDataSource") DataSource slaveDataSource) {
        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put("masterDataSource", masterDataSource);
        dataSourceMap.put("slaveDataSource", slaveDataSource);
        DynamicDataSourceContextHolder.dataSourceNames.add("masterDataSource");
        DynamicDataSourceContextHolder.dataSourceNames.add("slaveDataSource");
        // 设置动态数据源
        return new DynamicDataSource(masterDataSource, dataSourceMap);
    }
}
