package com.centit.mallserver.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-02-25
 **/
@Repository
@Mapper
public interface ShoppingGoodscartDao {

    /**
     * 查询已下单的商品数量
     * @param userId 用户id
     * @param goodsId 商品id
     * @param goodsType 商品类型
     * @return
     */
    int selectHasCount(@Param("userId") String userId, @Param("goodsId") String goodsId, @Param("goodsType") Integer goodsType);

    /**
     * 校验购物车记录是否存在
     * @param cartId 购物车记录id
     * @return
     */
    boolean checkCartStatus(@Param("cartId") String cartId);
}
