package com.centit.admin.shopping.dao;

import com.centit.admin.shopping.po.ShoppingGoodsclass;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
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
     * 新增
     */
    int insert(ShoppingGoodsclass entity);

    /**
     * 更新
     */
    int update(ShoppingGoodsclass entity);

    /**
     * 删除
     */
    int delete(ShoppingGoodsclass entity);

    /**
     * 查询详情
     */
    ShoppingGoodsclass queryDetail(ShoppingGoodsclass entity);

    /**
     * 查询列表
     */
    List<ShoppingGoodsclass> queryList(HashMap<String, Object> reqMap);

    List<ShoppingGoodsclass> queryPageList(HashMap<String, Object> reqMap);

    /**
     * 查询数量
     */
    int queryTotalCount(HashMap<String, Object> reqMap);

    /**
     * 更新所有子分类类型信息
     */
    int updateChildClassType(ShoppingGoodsclass entity);
}
