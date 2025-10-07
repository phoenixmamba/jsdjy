package com.centit.order.dao;

import com.centit.order.po.ShoppingWriteoffPo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2025/1/2 9:30
 **/
@Repository
@Mapper
public interface ShoppingWriteoffDao {
    /**
     * 新增
     */
    int insert(ShoppingWriteoffPo entity);
}
