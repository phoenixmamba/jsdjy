package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingCouponDirectgrantRecord;
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
public interface ShoppingCouponDirectgrantRecordDao {

    /**
     * 新增
     */
    int insert(ShoppingCouponDirectgrantRecord entity);

    /**
     * 更新
     */
    int update(ShoppingCouponDirectgrantRecord entity);

    /**
     * 删除
     */
    int delete(ShoppingCouponDirectgrantRecord entity);

    /**
     * 查询详情
     */
    ShoppingCouponDirectgrantRecord queryDetail(ShoppingCouponDirectgrantRecord entity);

    /**
     * 查询列表
     */
    List<ShoppingCouponDirectgrantRecord> queryList(HashMap<String, Object> reqMap);

}
