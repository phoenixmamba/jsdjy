package com.centit.mallserver.dao;

import com.centit.mallserver.po.ShoppingMembershipPo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-06-04
 **/
@Repository
@Mapper
public interface ShoppingMembershipDao {

    /**
     * 查询详情
     * @return
     */
    ShoppingMembershipPo selectDetail();

}
