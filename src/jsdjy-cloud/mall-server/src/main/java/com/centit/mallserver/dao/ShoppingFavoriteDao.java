package com.centit.mallserver.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-05-14
 **/
@Repository
@Mapper
public interface ShoppingFavoriteDao {



    /**
     * 查询列表
     */
    Boolean selectIsFav( @Param("goodsId") String goodsId, @Param("goodsType") Integer goodsType, @Param("userId") String userId);


}
