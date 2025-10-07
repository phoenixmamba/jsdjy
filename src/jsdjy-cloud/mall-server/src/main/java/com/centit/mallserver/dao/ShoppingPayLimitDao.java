package com.centit.mallserver.dao;

import com.centit.mallserver.po.ShoppingPayLimitPo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/12/23 14:49
 **/
@Mapper
@Repository
public interface ShoppingPayLimitDao {
    /**
     * 查询详情
     */
    ShoppingPayLimitPo selectDetail();
}
