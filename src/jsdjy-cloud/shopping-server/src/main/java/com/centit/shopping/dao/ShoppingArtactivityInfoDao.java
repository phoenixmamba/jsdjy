package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingArtactivityInfo;
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
public interface ShoppingArtactivityInfoDao {

    /**
     * 新增
     */
    int insert(ShoppingArtactivityInfo entity);

    /**
     * 更新
     */
    int update(ShoppingArtactivityInfo entity);

    /**
     * 删除
     */
    int delete(ShoppingArtactivityInfo entity);

    /**
     * 查询详情
     */
    ShoppingArtactivityInfo queryDetail(ShoppingArtactivityInfo entity);

    /**
     * 查询列表
     */
    List<ShoppingArtactivityInfo> queryList(HashMap<String, Object> reqMap);

    int deleteByArtactivityId(ShoppingArtactivityInfo shoppingArtactivityInfo);
}
