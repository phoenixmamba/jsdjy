package com.centit.order.dao;

import com.centit.order.po.ShoppingOrderPayPo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/12/30 17:04
 **/
@Repository
@Mapper
public interface ShoppingOrderPayDao {
    /**
     * 新增
     */
    int insert(ShoppingOrderPayPo entity);
}
