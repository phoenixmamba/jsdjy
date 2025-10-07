package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingArtactivityInventory;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2022-05-10
 **/
@Repository
@Mapper
public interface ShoppingArtactivityInventoryDao {

    /**
     * 新增
     */
    int insert(ShoppingArtactivityInventory entity);

    /**
     * 更新
     */
    int update(ShoppingArtactivityInventory entity);

    int cutInventory(HashMap<String, Object> reqMap);
    int addInventory(HashMap<String, Object> reqMap);

    /**
     * 删除
     */
    int delete(ShoppingArtactivityInventory entity);

    /**
     * 查询详情
     */
    ShoppingArtactivityInventory queryDetail(ShoppingArtactivityInventory entity);

    /**
     * 查询列表
     */
    List<ShoppingArtactivityInventory> queryList(HashMap<String, Object> reqMap);

}
