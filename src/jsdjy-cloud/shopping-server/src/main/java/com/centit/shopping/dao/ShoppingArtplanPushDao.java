package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingArtactivityPush;
import com.centit.shopping.po.ShoppingArtplanPush;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2022-03-10
 **/
@Repository
@Mapper
public interface ShoppingArtplanPushDao {

    /**
     * 新增
     */
    int insert(ShoppingArtplanPush entity);

    /**
     * 更新
     */
    int update(ShoppingArtplanPush entity);

    /**
     * 删除
     */
    int delete(ShoppingArtplanPush entity);

    /**
     * 查询详情
     */
    ShoppingArtplanPush queryDetail(ShoppingArtplanPush entity);

    /**
     * 查询列表
     */
    List<ShoppingArtplanPush> queryList(HashMap<String, Object> reqMap);

    int queryListCount(HashMap<String, Object> reqMap);

    List<ShoppingArtplanPush> queryToDoPushList(HashMap<String, Object> reqMap);
}
