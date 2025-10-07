package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingHistory;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-05-15
 **/
@Repository
@Mapper
public interface ShoppingHistoryDao {

    /**
     * 新增
     */
    int insert(ShoppingHistory entity);

    /**
     * 更新
     */
    int update(ShoppingHistory entity);

    /**
     * 删除
     */
    int delete(ShoppingHistory entity);

    /**
     * 查询详情
     */
    ShoppingHistory queryDetail(ShoppingHistory entity);

    /**
     * 查询列表
     */
    List<ShoppingHistory> queryList(HashMap<String, Object> reqMap);

    /**
     * 查询我的历史足迹列表
     */
    List<HashMap<String, Object>> queryMyHistoryList(HashMap<String, Object> reqMap);

    int queryMyHistoryCount(HashMap<String, Object> reqMap);

}
