package com.centit.pay.biz.dao;

import com.centit.pay.biz.po.ShoppingCouponUsertemp;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-06-03
 **/
@Repository
@Mapper
public interface ShoppingCouponUsertempDao {

    /**
     * 新增
     */
    int insert(ShoppingCouponUsertemp entity);

    /**
     * 更新
     */
    int update(ShoppingCouponUsertemp entity);

    /**
     * 删除
     */
    int delete(ShoppingCouponUsertemp entity);

    /**
     * 查询详情
     */
    ShoppingCouponUsertemp queryDetail(ShoppingCouponUsertemp entity);

    /**
     * 查询列表
     */
    List<ShoppingCouponUsertemp> queryList(HashMap<String, Object> reqMap);

}
