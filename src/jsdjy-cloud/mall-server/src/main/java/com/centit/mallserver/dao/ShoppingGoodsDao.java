package com.centit.mallserver.dao;

import com.centit.mallserver.po.ShoppingGoodsPo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

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
     * 查询指定分类下的商品列表
     */
    List<ShoppingGoodsPo> selectClassGoodsList(String parentId);

    /**
     * 查询商品详情
     * @param id 商品Id
     * @return
     */
    ShoppingGoodsPo selectGoodsDetail(String id);

    /**
     * 查询商品库存
     * @param id 商品id
     * @return 库存值
     */
    int selectGoodsStock(String id);
}
