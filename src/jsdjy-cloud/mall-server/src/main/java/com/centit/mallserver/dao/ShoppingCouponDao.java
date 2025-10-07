package com.centit.mallserver.dao;

import com.centit.mallserver.po.ShoppingCouponPo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-05-25
 **/
@Repository
@Mapper
public interface ShoppingCouponDao {

    /**
     * 查询详情
     */
    ShoppingCouponPo selectDetail(ShoppingCouponPo entity);

    List<ShoppingCouponPo> selectUserGoodsCouponList(HashMap<String, Object> reqMap);


}
