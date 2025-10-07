package com.centit.order.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-02-22
 **/
@Repository
@Mapper
public interface ShoppingGoodsDao {

    /**
     * 查询商品是否下架
     * @param id 商品id
     * @return
     */
    boolean checkGoodsStatus(String id);

    /**
     * 查询商品库存
     * @param id 商品id
     * @return 库存值
     */
    int selectGoodsStock(String id);

    int selectInventoryStock(@Param("goodsId") String goodsId,@Param("propertys") String propertys);

    int cutGoodsStock(@Param("cutStock") int cutStock, @Param("goodsId") String goodsId);

    int cutInventoryStock(@Param("cutStock") int cutStock, @Param("goodsId") String goodsId,@Param("propertys") String propertys);
}
