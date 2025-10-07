package com.centit.mallserver.dao;

import com.centit.mallserver.po.ShoppingAssetRulePo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-10-19
 **/
@Repository
@Mapper
public interface ShoppingAssetRuleDao {

    /**
     * 查询详情
     */
    ShoppingAssetRulePo selectDetail();

}
