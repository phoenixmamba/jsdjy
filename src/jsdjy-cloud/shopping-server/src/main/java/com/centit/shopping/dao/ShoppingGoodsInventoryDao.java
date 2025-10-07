package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingGoodsInventory;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-03-10
 **/
@Repository
@Mapper
public interface ShoppingGoodsInventoryDao {

    /**
     * 新增
     */
    int insert(ShoppingGoodsInventory entity);

    /**
     * 更新
     */
    int update(ShoppingGoodsInventory entity);

    int cutInventory(HashMap<String, Object> reqMap);
    int addInventory(HashMap<String, Object> reqMap);

    /**
     * 删除
     */
    int delete(ShoppingGoodsInventory entity);

    /**
     * 查询详情
     */
    ShoppingGoodsInventory queryDetail(ShoppingGoodsInventory entity);

    /**
     * 查询列表
     */
    List<ShoppingGoodsInventory> queryList(HashMap<String, Object> reqMap);

}
