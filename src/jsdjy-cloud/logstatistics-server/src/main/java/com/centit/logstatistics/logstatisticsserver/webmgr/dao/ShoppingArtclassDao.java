package com.centit.logstatistics.logstatisticsserver.webmgr.dao;

import com.centit.logstatistics.logstatisticsserver.webmgr.po.ShoppingArtclass;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p> <p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 艺术课程 Dao接口
 * @Date : 2021-06-10
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

}
