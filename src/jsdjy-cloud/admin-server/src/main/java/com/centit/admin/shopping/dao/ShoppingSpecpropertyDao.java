package com.centit.admin.shopping.dao;

import com.centit.admin.shopping.po.ShoppingSpecproperty;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-02-22
 **/
@Repository
@Mapper
public interface ShoppingSpecpropertyDao {

    /**
     * 新增
     */
    int insert(ShoppingSpecproperty entity);

    /**
     * 更新
     */
    int update(ShoppingSpecproperty entity);

    /**
     * 删除
     */
    int delete(ShoppingSpecproperty entity);

    /**
     * 查询详情
     */
    ShoppingSpecproperty queryDetail(ShoppingSpecproperty entity);

    /**
     * 查询列表
     */
    List<ShoppingSpecproperty> queryList(HashMap<String, Object> reqMap);

}
