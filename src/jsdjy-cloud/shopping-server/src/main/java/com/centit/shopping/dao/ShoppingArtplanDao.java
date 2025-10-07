package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingArtplan;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-12-14
 **/
@Repository
@Mapper
public interface ShoppingArtplanDao {

    /**
     * 新增
     */
    int insert(ShoppingArtplan entity);

    /**
     * 更新
     */
    int update(ShoppingArtplan entity);

    /**
     * 删除
     */
    int delete(ShoppingArtplan entity);

    /**
     * 查询详情
     */
    ShoppingArtplan queryDetail(ShoppingArtplan entity);

    /**
     * 查询列表
     */
    List<ShoppingArtplan> queryList(HashMap<String, Object> reqMap);


    int queryTotalCount(HashMap<String, Object> reqMap);

    /**
     * 更新爱艺计划报名剩余数浪
     */
    int updatePlanNum(ShoppingArtplan entity);

    List<Map> queryAllArtplan(HashMap<String, Object> reqMap);

    /**
     * 更新爱艺计划报名剩余数浪
     */
    int updatePlanCutNum(HashMap<String, Object> reqMap);
    int updatePlanAddNum(HashMap<String, Object> reqMap);

}
