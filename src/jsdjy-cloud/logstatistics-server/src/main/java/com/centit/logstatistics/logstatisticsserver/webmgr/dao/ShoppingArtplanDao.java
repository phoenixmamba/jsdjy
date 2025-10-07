package com.centit.logstatistics.logstatisticsserver.webmgr.dao;

import com.centit.logstatistics.logstatisticsserver.webmgr.po.ShoppingArtplan;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p> <p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description :  Dao接口
 * @Date : 2021-12-24
 **/
@Repository
@Mapper
public interface ShoppingArtplanDao {

    /**
     * 新增
     */
    int insert(ShoppingArtplan entity);

    /**
     * 更新
     */
    int update(ShoppingArtplan entity);

    /**
     * 删除
     */
    int delete(ShoppingArtplan entity);

    /**
     * 查询详情
     */
    ShoppingArtplan queryDetail(ShoppingArtplan entity);

    /**
     * 查询列表
     */
    List<ShoppingArtplan> queryList(HashMap<String, Object> reqMap);

}
