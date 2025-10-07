package com.centit.core.service.sys;

import com.centit.core.dao.sys.DatadictionaryDao;

import javax.annotation.Resource;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 系统常用方法
 * @Date : 2024/12/3 11:16
 **/
public class SysService {
    @Resource
    private DatadictionaryDao datadictionaryDao;

    /**
     * 获取字典项对应值
     *
     * @param catalogCode,dataCode
     * @return String 字典值
     */
    public String getCodeValue(String catalogCode,String dataCode) {
        return datadictionaryDao.selectDataValue(catalogCode,dataCode);
    }
}
