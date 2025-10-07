package com.centit.jobserver.dao;

import com.centit.jobserver.po.MzAssetRule;
import org.apache.ibatis.annotations.Mapper;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 
 * @Date : 2024/11/21 19:55
 **/
@Mapper
public interface MzAssetRuleDao {
    int insert(MzAssetRule record);

    /**
     * 更新限额规则
     * @param record 规则对象
     * @return 返回是否成功
     */
    boolean update(MzAssetRule record);
}