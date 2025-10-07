package com.centit.order.dao;

import com.centit.order.po.ShoppingOrderPaykeyPo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/12/30 17:01
 **/
@Repository
@Mapper
public interface ShoppingOrderPaykeyDao {
    /**
     * 新增
     */
    int insert(ShoppingOrderPaykeyPo entity);
}
