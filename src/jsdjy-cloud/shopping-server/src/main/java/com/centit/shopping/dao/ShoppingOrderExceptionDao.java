package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingOrderException;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-06-10
 **/
@Repository
@Mapper
public interface ShoppingOrderExceptionDao {

    /**
     * 新增
     */
    int insert(ShoppingOrderException entity);

    /**
     * 更新
     */
    int update(ShoppingOrderException entity);

    /**
     * 删除
     */
    int delete(ShoppingOrderException entity);

    /**
     * 查询详情
     */
    ShoppingOrderException queryDetail(ShoppingOrderException entity);

    /**
     * 查询列表
     */
    List<ShoppingOrderException> queryList(HashMap<String, Object> reqMap);

}
