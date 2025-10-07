package com.centit.jobserver.config.db;

import java.util.ArrayList;
import java.util.List;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/11/28 15:15
 **/
public class DynamicDataSourceContextHolder {
    private static final ThreadLocal<String> DATASOURCE_CONTEXT_HOLDER = new ThreadLocal<>();
    /**
     * 管理全部数据源
     */
    public static List<String> dataSourceNames = new ArrayList<>();

    /**
     * 判断是否存在指定数据源
     */
    public static boolean containsDataSource(String dataSourceName) {
        return dataSourceNames.contains(dataSourceName);
    }
    /**
     * 设置当前数据源
     */
    public static void setDataSource(String dataSourceName) {
        DATASOURCE_CONTEXT_HOLDER.set(dataSourceName);
    }
    /**
     * 获取当前数据源
     */
    public static String getDataSource() {
        return DATASOURCE_CONTEXT_HOLDER.get();
    }
    /**
     * 清除数据源
     */
    public static void clear() {
        DATASOURCE_CONTEXT_HOLDER.remove();
    }
}
