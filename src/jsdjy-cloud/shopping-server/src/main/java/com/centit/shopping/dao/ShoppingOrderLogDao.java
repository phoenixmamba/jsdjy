package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingOrderLog;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-02-25
 **/
@Repository
@Mapper
public interface ShoppingOrderLogDao {

    /**
     * 新增
     */
    int insert(ShoppingOrderLog entity);

    /**
     * 更新
     */
    int update(ShoppingOrderLog entity);

    /**
     * 删除
     */
    int delete(ShoppingOrderLog entity);

    /**
     * 查询详情
     */
    ShoppingOrderLog queryDetail(ShoppingOrderLog entity);

    /**
     * 查询列表
     */
    List<ShoppingOrderLog> queryList(HashMap<String, Object> reqMap);

}
