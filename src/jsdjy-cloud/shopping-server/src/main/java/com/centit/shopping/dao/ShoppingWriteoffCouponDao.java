package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingWriteoffCoupon;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2022-03-15
 **/
@Repository
@Mapper
public interface ShoppingWriteoffCouponDao {

    /**
     * 新增
     */
    int insert(ShoppingWriteoffCoupon entity);

    /**
     * 更新
     */
    int update(ShoppingWriteoffCoupon entity);

    /**
     * 删除
     */
    int delete(ShoppingWriteoffCoupon entity);

    /**
     * 查询详情
     */
    ShoppingWriteoffCoupon queryDetail(ShoppingWriteoffCoupon entity);

    /**
     * 查询列表
     */
    List<ShoppingWriteoffCoupon> queryList(HashMap<String, Object> reqMap);

    List<ShoppingWriteoffCoupon> queryWriteOffList(HashMap<String, Object> reqMap);

    int queryWriteOffCount(HashMap<String, Object> reqMap);

}
