package com.centit.ticket.dao;

import com.centit.ticket.po.ShoppingAssetRule;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

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
     * 新增
     */
    int insert(ShoppingAssetRule entity);

    /**
     * 更新
     */
    int update(ShoppingAssetRule entity);

    /**
     * 删除
     */
    int delete(ShoppingAssetRule entity);

    /**
     * 查询详情
     */
    ShoppingAssetRule queryDetail(ShoppingAssetRule entity);

    /**
     * 查询列表
     */
    List<ShoppingAssetRule> queryList(HashMap<String, Object> reqMap);

}
