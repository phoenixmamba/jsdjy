package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingArtclass;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-03-24
 **/
@Repository
@Mapper
public interface ShoppingArtclassDao {

    /**
     * 新增
     */
    int insert(ShoppingArtclass entity);

    /**
     * 更新
     */
    int update(ShoppingArtclass entity);

    /**
     * 删除
     */
    int delete(ShoppingArtclass entity);

    /**
     * 查询详情
     */
    ShoppingArtclass queryDetail(ShoppingArtclass entity);

    /**
     * 查询列表
     */
    List<ShoppingArtclass> queryList(HashMap<String, Object> reqMap);

    int queryTotalCount(HashMap<String, Object> reqMap);

    List<Map> queryAllArtclass(HashMap<String, Object> reqMap);

    /**
     * 查询可以开课的艺教课程列表
     */
    List<ShoppingArtclass> queryLiveAbleClassList(HashMap<String, Object> reqMap);
}
