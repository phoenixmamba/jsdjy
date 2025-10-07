package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingArtplanInventory;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2022-05-11
 **/
@Repository
@Mapper
public interface ShoppingArtplanInventoryDao {

    /**
     * 新增
     */
    int insert(ShoppingArtplanInventory entity);

    /**
     * 更新
     */
    int update(ShoppingArtplanInventory entity);

    /**
     * 删除
     */
    int delete(ShoppingArtplanInventory entity);

    /**
     * 查询详情
     */
    ShoppingArtplanInventory queryDetail(ShoppingArtplanInventory entity);

    /**
     * 查询列表
     */
    List<ShoppingArtplanInventory> queryList(HashMap<String, Object> reqMap);

    int cutInventory(HashMap<String, Object> reqMap);
    int addInventory(HashMap<String, Object> reqMap);


}
