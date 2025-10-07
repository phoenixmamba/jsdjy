package com.centit.core.dao.sys;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-03-04
 **/
@Repository
@Mapper
public interface DatadictionaryDao {

    /**
     * 查询字典值
     * @param catalogCode 字典id
     * @param dataCode 值id
     * @return String 字典值
     */
    String selectDataValue(@Param(value = "catalogCode") String catalogCode, @Param(value = "dataCode") String dataCode);

}
