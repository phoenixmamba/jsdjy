package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingGoodsSpec;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
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
public interface ShoppingGoodsSpecDao {

    /**
     * 新增
     */
    int insert(ShoppingGoodsSpec entity);

    /**
     * 更新
     */
    int update(ShoppingGoodsSpec entity);

    /**
     * 删除
     */
    int delete(ShoppingGoodsSpec entity);

    /**
     * 查询详情
     */
    ShoppingGoodsSpec queryDetail(ShoppingGoodsSpec entity);

    /**
     * 查询列表
     */
    List<ShoppingGoodsSpec> queryList(HashMap<String, Object> reqMap);

}
