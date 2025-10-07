package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingGoods;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-02-21
 **/
@Repository
@Mapper
public interface ShoppingGoodsDao {

    /**
     * 新增
     */
    int insert(ShoppingGoods entity);

    /**
     * 更新
     */
    int update(ShoppingGoods entity);

    /**
     * 删除
     */
    int delete(ShoppingGoods entity);

    /**
     * 查询详情
     */
    ShoppingGoods queryDetail(ShoppingGoods entity);

    /**
     * 查询列表
     */
    List<ShoppingGoods> queryList(HashMap<String, Object> reqMap);

    /**
     * 查询列表数量
     */
    int queryTotalCount(HashMap<String, Object> reqMap);

    /**
     * 查询列表
     */
    List<ShoppingGoods> queryClassGoodsList(HashMap<String, Object> reqMap);

    /**
     * 查询列表数量
     */
    int queryClassGoodsTotalCount(HashMap<String, Object> reqMap);

    /**
     * 更新商品库存
     */
    int updateGoodsInventory(ShoppingGoods entity);

    int cutGoodsInventory(HashMap<String, Object> reqMap);
    int addGoodsInventory(HashMap<String, Object> reqMap);

    int checkGoodsName(HashMap<String, Object> reqMap);

    int updateStatus(ShoppingGoods entity);
}
