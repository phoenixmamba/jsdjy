package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingCouponGrant;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2022-01-12
 **/
@Repository
@Mapper
public interface ShoppingCouponGrantDao {

    /**
     * 新增
     */
    int insert(ShoppingCouponGrant entity);

    /**
     * 更新
     */
    int update(ShoppingCouponGrant entity);

    /**
     * 删除
     */
    int delete(ShoppingCouponGrant entity);

    /**
     * 查询详情
     */
    ShoppingCouponGrant queryDetail(ShoppingCouponGrant entity);

    /**
     * 查询列表
     */
    List<ShoppingCouponGrant> queryList(HashMap<String, Object> reqMap);

    int queryListCount(HashMap<String, Object> reqMap);

}
