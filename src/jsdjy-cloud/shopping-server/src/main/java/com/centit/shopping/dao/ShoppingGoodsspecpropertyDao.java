package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingGoodsspecproperty;
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
public interface ShoppingGoodsspecpropertyDao {

    /**
     * 新增
     */
    int insert(ShoppingGoodsspecproperty entity);

    /**
     * 更新
     */
    int update(ShoppingGoodsspecproperty entity);

    /**
     * 删除
     */
    int delete(ShoppingGoodsspecproperty entity);

    /**
     * 查询详情
     */
    ShoppingGoodsspecproperty queryDetail(ShoppingGoodsspecproperty entity);

    /**
     * 查询列表
     */
    List<ShoppingGoodsspecproperty> queryList(HashMap<String, Object> reqMap);

}
