package com.centit.jobserver.config.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/11/28 15:17
 **/
@Slf4j
public class DynamicDataSource extends AbstractRoutingDataSource {

    public DynamicDataSource(DataSource defaultTargetDataSource, Map<Object, Object> targetDataSources) {
        super.setDefaultTargetDataSource(defaultTargetDataSource);
        super.setTargetDataSources(targetDataSources);
    }

    @Override
    public Object determineCurrentLookupKey() {
        Object dataSourceKey = DynamicDataSourceContextHolder.getDataSource();
        return DynamicDataSourceContextHolder.getDataSource();
    }
}
