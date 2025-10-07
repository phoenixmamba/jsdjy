package com.centit.admin.shopping.dao;

import com.centit.admin.shopping.po.ShoppingSpecification;
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
public interface ShoppingSpecificationDao {

    /**
     * 新增
     */
    int insert(ShoppingSpecification entity);

    /**
     * 更新
     */
    int update(ShoppingSpecification entity);

    /**
     * 删除
     */
    int delete(ShoppingSpecification entity);

    /**
     * 查询详情
     */
    ShoppingSpecification queryDetail(ShoppingSpecification entity);

    /**
     * 查询列表
     */
    List<ShoppingSpecification> queryList(HashMap<String, Object> reqMap);

    /**
     * 查询数量
     */
    int queryTotalCount(HashMap<String, Object> reqMap);

    /**
     * 查询商品关联的规格分类
     */
    List<ShoppingSpecification> queryGoodsSpecs(HashMap<String, Object> reqMap);

    /**
     * 查询商品类型关联的规格
     */
    List<ShoppingSpecification> queryTypeSpecs(String typeId);

    /**
     * 查询活动关联的规格
     */
    List<ShoppingSpecification> queryActSpecs(HashMap<String, Object> reqMap);

    /**
     * 查询爱艺计划关联的规格
     */
    List<ShoppingSpecification> queryPlanSpecs(HashMap<String, Object> reqMap);
}
