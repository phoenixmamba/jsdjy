package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingIntegralSet;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-09-28
 **/
@Repository
@Mapper
public interface ShoppingIntegralSetDao {

    /**
     * 新增
     */
    int insert(ShoppingIntegralSet entity);

    /**
     * 更新
     */
    int update(ShoppingIntegralSet entity);

    /**
     * 删除
     */
    int delete(ShoppingIntegralSet entity);

    /**
     * 查询详情
     */
    ShoppingIntegralSet queryDetail(ShoppingIntegralSet entity);

    /**
     * 查询列表
     */
    List<ShoppingIntegralSet> queryList(HashMap<String, Object> reqMap);

    List<ShoppingIntegralSet> checkName(HashMap<String, Object> reqMap);

}
