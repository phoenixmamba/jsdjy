package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingArtactivityPush;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2022-03-09
 **/
@Repository
@Mapper
public interface ShoppingArtactivityPushDao {

    /**
     * 新增
     */
    int insert(ShoppingArtactivityPush entity);

    /**
     * 更新
     */
    int update(ShoppingArtactivityPush entity);

    /**
     * 删除
     */
    int delete(ShoppingArtactivityPush entity);

    /**
     * 查询详情
     */
    ShoppingArtactivityPush queryDetail(ShoppingArtactivityPush entity);

    /**
     * 查询列表
     */
    List<ShoppingArtactivityPush> queryList(HashMap<String, Object> reqMap);

    int queryListCount(HashMap<String, Object> reqMap);

    List<ShoppingArtactivityPush> queryToDoPushList(HashMap<String, Object> reqMap);
}
