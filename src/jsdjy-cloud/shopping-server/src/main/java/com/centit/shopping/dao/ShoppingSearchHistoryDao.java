package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingSearchHistory;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-05-24
 **/
@Repository
@Mapper
public interface ShoppingSearchHistoryDao {

    /**
     * 新增
     */
    int insert(ShoppingSearchHistory entity);

    /**
     * 更新
     */
    int update(ShoppingSearchHistory entity);

    /**
     * 删除
     */
    int delete(ShoppingSearchHistory entity);

    /**
     * 查询详情
     */
    ShoppingSearchHistory queryDetail(ShoppingSearchHistory entity);

    /**
     * 查询列表
     */
    List<ShoppingSearchHistory> queryList(HashMap<String, Object> reqMap);

    List<HashMap<String, Object>> queryHotWords(HashMap<String, Object> reqMap);

}
