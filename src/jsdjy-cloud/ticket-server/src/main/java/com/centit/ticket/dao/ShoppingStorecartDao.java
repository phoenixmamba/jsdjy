package com.centit.ticket.dao;

import com.centit.ticket.po.ShoppingStorecart;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-03-06
 **/
@Repository
@Mapper
public interface ShoppingStorecartDao {

    /**
     * 新增
     */
    int insert(ShoppingStorecart entity);

    /**
     * 更新
     */
    int update(ShoppingStorecart entity);

    /**
     * 删除
     */
    int delete(ShoppingStorecart entity);

    /**
     * 查询详情
     */
    ShoppingStorecart queryDetail(ShoppingStorecart entity);

    /**
     * 查询列表
     */
    List<ShoppingStorecart> queryList(HashMap<String, Object> reqMap);

}
