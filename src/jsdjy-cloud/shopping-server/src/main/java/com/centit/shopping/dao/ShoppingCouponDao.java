package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingCoupon;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

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
     * 新增
     */
    int insert(ShoppingCoupon entity);

    /**
     * 更新
     */
    int update(ShoppingCoupon entity);

    /**
     * 删除
     */
    int delete(ShoppingCoupon entity);

    /**
     * 查询详情
     */
    ShoppingCoupon queryDetail(ShoppingCoupon entity);

    /**
     * 查询列表
     */
    List<ShoppingCoupon> queryList(HashMap<String, Object> reqMap);

    int queryTotalCount(HashMap<String, Object> reqMap);

    int updateCouponState(List<String> ids);

    int deleteAllCoupon(HashMap<String, Object> reqMap);

    List<ShoppingCoupon> queryGoodsCouponList(HashMap<String, Object> reqMap);

    List<ShoppingCoupon> queryArtCouponList(HashMap<String, Object> reqMap);

    List<ShoppingCoupon> queryActivityCoupon(HashMap<String, Object> reqMap);

    List<ShoppingCoupon> queryBirthCoupon(HashMap<String, Object> reqMap);

    List<ShoppingCoupon> queryNewCoupon(HashMap<String, Object> reqMap);

    List<ShoppingCoupon> queryUserGoodsCouponList(HashMap<String, Object> reqMap);

    List<ShoppingCoupon> queryUserArtCouponList(HashMap<String, Object> reqMap);

    List<ShoppingCoupon> queryUserVideoCouponList(HashMap<String, Object> reqMap);

    List<ShoppingCoupon> queryDirectgrantCoupon(HashMap<String, Object> reqMap);

    int queryDirectgrantCouponTotalCount(HashMap<String, Object> reqMap);

    List<ShoppingCoupon> queryWriteOffCoupon(HashMap<String, Object> reqMap);

    int updateWriteOffCount(ShoppingCoupon entity);

}
