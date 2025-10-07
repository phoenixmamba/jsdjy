package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingPayLimit;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-10-12
 **/
@Repository
@Mapper
public interface ShoppingPayLimitDao {

    /**
     * 新增
     */
    int insert(ShoppingPayLimit entity);

    /**
     * 更新
     */
    int update(ShoppingPayLimit entity);

    /**
     * 删除
     */
    int delete(ShoppingPayLimit entity);

    /**
     * 查询详情
     */
    ShoppingPayLimit queryDetail(ShoppingPayLimit entity);

    /**
     * 查询列表
     */
    List<ShoppingPayLimit> queryList(HashMap<String, Object> reqMap);

}
