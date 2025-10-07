package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingArtactivity;
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
 * @Date : 2021-03-19
 **/
@Repository
@Mapper
public interface ShoppingArtactivityDao {

    /**
     * 新增
     */
    int insert(ShoppingArtactivity entity);

    /**
     * 更新
     */
    int update(ShoppingArtactivity entity);

    /**
     * 删除
     */
    int delete(ShoppingArtactivity entity);

    /**
     * 查询详情
     */
    ShoppingArtactivity queryDetail(ShoppingArtactivity entity);

    /**
     * 查询列表
     */
    List<ShoppingArtactivity> queryList(HashMap<String, Object> reqMap);

    int queryTotalCount(HashMap<String, Object> reqMap);

    /**
     * 更新艺教活动报名剩余数浪
     */
    int updateActivityCutNum(HashMap<String, Object> reqMap);
    int updateActivityAddNum(HashMap<String, Object> reqMap);

    int updateActivityNum(ShoppingArtactivity entity);

    List<Map> queryAllArtactivity(HashMap<String, Object> reqMap);
}
