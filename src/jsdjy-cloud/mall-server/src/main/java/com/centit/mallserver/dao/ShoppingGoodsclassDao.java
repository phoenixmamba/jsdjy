package com.centit.mallserver.dao;

import com.centit.mallserver.model.ShoppingGoodsclass;
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
public interface ShoppingGoodsclassDao {

    /**
     * 查询列表
     */
    List<ShoppingGoodsclass> queryChildList(String parentId);

    /**
     * 查询列表
     */
    List<ShoppingGoodsclass> queryAllList();

}
