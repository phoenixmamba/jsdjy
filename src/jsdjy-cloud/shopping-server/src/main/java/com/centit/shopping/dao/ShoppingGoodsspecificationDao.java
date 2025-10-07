package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingGoodsspecification;
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
public interface ShoppingGoodsspecificationDao {

    /**
     * 新增
     */
    int insert(ShoppingGoodsspecification entity);

    /**
     * 更新
     */
    int update(ShoppingGoodsspecification entity);

    /**
     * 删除
     */
    int delete(ShoppingGoodsspecification entity);

    /**
     * 查询详情
     */
    ShoppingGoodsspecification queryDetail(ShoppingGoodsspecification entity);

    /**
     * 查询列表
     */
    List<ShoppingGoodsspecification> queryList(HashMap<String, Object> reqMap);

    /**
     * 查询数量
     */
    int queryTotalCount(HashMap<String, Object> reqMap);

    /**
     * 查询商品关联的规格分类
     */
    List<ShoppingGoodsspecification> queryGoodsSpecs(HashMap<String, Object> reqMap);

    /**
     * 查询商品类型关联的规格
     */
    List<ShoppingGoodsspecification> queryTypeSpecs(HashMap<String, Object> reqMap);

    /**
     * 查询活动关联的规格
     */
    List<ShoppingGoodsspecification> queryActSpecs(HashMap<String, Object> reqMap);

    /**
     * 查询爱艺计划关联的规格
     */
    List<ShoppingGoodsspecification> queryPlanSpecs(HashMap<String, Object> reqMap);
}
