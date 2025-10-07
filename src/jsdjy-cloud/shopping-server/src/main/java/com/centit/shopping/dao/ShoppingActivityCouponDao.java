package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingActivityCoupon;
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
public interface ShoppingActivityCouponDao {

    /**
     * 新增
     */
    int insert(ShoppingActivityCoupon entity);

    /**
     * 更新
     */
    int update(ShoppingActivityCoupon entity);

    /**
     * 删除
     */
    int delete(ShoppingActivityCoupon entity);

    /**
     * 查询详情
     */
    ShoppingActivityCoupon queryDetail(ShoppingActivityCoupon entity);

    /**
     * 查询列表
     */
    List<ShoppingActivityCoupon> queryList(HashMap<String, Object> reqMap);

}
