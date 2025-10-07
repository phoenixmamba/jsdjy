package com.centit.logstatistics.logstatisticsserver.webmgr.dao;

import com.centit.logstatistics.logstatisticsserver.webmgr.po.ShoppingArtactivity;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p> <p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description :  Dao接口
 * @Date : 2021-06-10
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

}
