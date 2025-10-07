package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingArtinfos;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-03-19
 **/
@Repository
@Mapper
public interface ShoppingArtinfosDao {

    /**
     * 新增
     */
    int insert(ShoppingArtinfos entity);

    /**
     * 更新
     */
    int update(ShoppingArtinfos entity);

    /**
     * 删除
     */
    int delete(ShoppingArtinfos entity);

    /**
     * 查询详情
     */
    ShoppingArtinfos queryDetail(ShoppingArtinfos entity);

    /**
     * 查询列表
     */
    List<ShoppingArtinfos> queryList(HashMap<String, Object> reqMap);

    List<ShoppingArtinfos> queryActivityInfoList(HashMap<String, Object> reqMap);

    List<ShoppingArtinfos> queryPlanInfoList(HashMap<String, Object> reqMap);

    List<ShoppingArtinfos> queryClassInfoList(HashMap<String, Object> reqMap);

}
