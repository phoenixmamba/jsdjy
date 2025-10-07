package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingCouponDirectgrant;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2022-01-26
 **/
@Repository
@Mapper
public interface ShoppingCouponDirectgrantDao {

    /**
     * 新增
     */
    int insert(ShoppingCouponDirectgrant entity);

    /**
     * 更新
     */
    int update(ShoppingCouponDirectgrant entity);

    /**
     * 删除
     */
    int delete(ShoppingCouponDirectgrant entity);

    /**
     * 查询详情
     */
    ShoppingCouponDirectgrant queryDetail(ShoppingCouponDirectgrant entity);

    /**
     * 查询列表
     */
    List<ShoppingCouponDirectgrant> queryList(HashMap<String, Object> reqMap);

}
