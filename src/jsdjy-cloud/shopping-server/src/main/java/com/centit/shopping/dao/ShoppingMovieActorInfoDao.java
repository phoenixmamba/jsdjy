package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingMovieActorInfo;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>演职人员<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-04-13
 **/
@Repository
@Mapper
public interface ShoppingMovieActorInfoDao {

    /**
     * 新增
     */
    int insert(ShoppingMovieActorInfo entity);

    /**
     * 更新
     */
    int update(ShoppingMovieActorInfo entity);

    /**
     * 删除
     */
    int delete(ShoppingMovieActorInfo entity);

    /**
     * 查询详情
     */
    ShoppingMovieActorInfo queryDetail(ShoppingMovieActorInfo entity);

    /**
     * 查询列表
     */
    List<ShoppingMovieActorInfo> queryList(HashMap<String, Object> reqMap);

    int deleteByMovieId(String id);
}
