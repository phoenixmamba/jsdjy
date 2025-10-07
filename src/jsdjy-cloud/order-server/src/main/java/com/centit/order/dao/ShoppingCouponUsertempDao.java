package com.centit.order.dao;

import com.centit.order.po.ShoppingCouponUsertempPo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/12/30 16:38
 **/
@Repository
@Mapper
public interface ShoppingCouponUsertempDao {
    /**
     * 新增
     */
    int insert(ShoppingCouponUsertempPo entity);
}
