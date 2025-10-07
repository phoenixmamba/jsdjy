package com.centit.pay.biz.dao;

import com.centit.pay.biz.po.ShoppingCouponRecord;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-12-20
 **/
@Repository
@Mapper
public interface ShoppingCouponRecordDao {

    /**
     * 新增
     */
    int insert(ShoppingCouponRecord entity);

    /**
     * 更新
     */
    int update(ShoppingCouponRecord entity);

    /**
     * 删除
     */
    int delete(ShoppingCouponRecord entity);

    /**
     * 查询详情
     */
    ShoppingCouponRecord queryDetail(ShoppingCouponRecord entity);

    /**
     * 查询列表
     */
    List<ShoppingCouponRecord> queryList(HashMap<String, Object> reqMap);

}
