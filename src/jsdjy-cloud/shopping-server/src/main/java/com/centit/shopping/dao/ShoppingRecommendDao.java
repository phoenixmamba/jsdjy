package com.centit.shopping.dao;

import com.centit.shopping.po.HomeGoods;
import com.centit.shopping.po.ShoppingRecommend;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-04-26
 **/
@Repository
@Mapper
public interface ShoppingRecommendDao {

    /**
     * 新增
     */
    int insert(ShoppingRecommend entity);

    /**
     * 更新
     */
    int update(ShoppingRecommend entity);

    /**
     * 删除
     */
    int delete(ShoppingRecommend entity);

    /**
     * 查询详情
     */
    ShoppingRecommend queryDetail(ShoppingRecommend entity);

    /**
     * 查询列表
     */
    List<ShoppingRecommend> queryList(HashMap<String, Object> reqMap);

    /**
     * 查询所有商城商品
     */
    List<HomeGoods> queryAllList(HashMap<String, Object> reqMap);

    /**
     * 查询用户已购买过的商品的同类商品
     */
    List<HomeGoods> queryBuyList(HashMap<String, Object> reqMap);

    /**
     * 查询热销商品
     */
    List<HomeGoods> queryHotList(HashMap<String, Object> reqMap);

    List<ShoppingRecommend> queryListDetail(HashMap<String, Object> reqMap);
}
