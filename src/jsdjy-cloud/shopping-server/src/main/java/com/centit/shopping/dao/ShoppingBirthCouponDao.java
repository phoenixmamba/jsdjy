package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingBirthCoupon;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-05-27
 **/
@Repository
@Mapper
public interface ShoppingBirthCouponDao {

    /**
     * 新增
     */
    int insert(ShoppingBirthCoupon entity);

    /**
     * 更新
     */
    int update(ShoppingBirthCoupon entity);

    /**
     * 删除
     */
    int delete(ShoppingBirthCoupon entity);

    /**
     * 查询详情
     */
    ShoppingBirthCoupon queryDetail(ShoppingBirthCoupon entity);

    /**
     * 查询列表
     */
    List<ShoppingBirthCoupon> queryList(HashMap<String, Object> reqMap);

}
