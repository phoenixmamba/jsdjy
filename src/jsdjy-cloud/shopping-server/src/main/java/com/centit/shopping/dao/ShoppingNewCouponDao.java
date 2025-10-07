package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingNewCoupon;
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
public interface ShoppingNewCouponDao {

    /**
     * 新增
     */
    int insert(ShoppingNewCoupon entity);

    /**
     * 更新
     */
    int update(ShoppingNewCoupon entity);

    /**
     * 删除
     */
    int delete(ShoppingNewCoupon entity);

    /**
     * 查询详情
     */
    ShoppingNewCoupon queryDetail(ShoppingNewCoupon entity);

    /**
     * 查询列表
     */
    List<ShoppingNewCoupon> queryList(HashMap<String, Object> reqMap);

}
