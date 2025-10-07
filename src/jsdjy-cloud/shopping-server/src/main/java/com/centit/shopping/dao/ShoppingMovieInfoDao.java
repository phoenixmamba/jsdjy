package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingMovieActorInfo;
import com.centit.shopping.po.ShoppingMovieInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.centit.shopping.po.ShoppingMoviePhoto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 *
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-04-13
 **/
@Repository
@Mapper
public interface ShoppingMovieInfoDao {

    /**
     * 新增
     */
    int insert(ShoppingMovieInfo entity);

    /**
     * 更新
     */
    int update(ShoppingMovieInfo entity);

    /**
     * 删除
     */
    int delete(ShoppingMovieInfo entity);

    /**
     * 查询详情
     */
    ShoppingMovieInfo queryDetail(ShoppingMovieInfo entity);

    /**
     * 查询列表
     */
    List<ShoppingMovieInfo> queryList(HashMap<String, Object> reqMap);

    List<ShoppingMovieActorInfo> queryActorInfosByMovieId(String id);

    List<ShoppingMoviePhoto> queryPhotosByMovieId(String id);


    int queryCount(HashMap reqParam);

    List<Map> queryAllMovie(HashMap<String, Object> reqMap);
}
