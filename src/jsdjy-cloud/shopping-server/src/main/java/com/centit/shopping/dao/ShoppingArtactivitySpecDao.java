package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingArtactivitySpec;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2022-05-10
 **/
@Repository
@Mapper
public interface ShoppingArtactivitySpecDao {

    /**
     * 新增
     */
    int insert(ShoppingArtactivitySpec entity);

    /**
     * 更新
     */
    int update(ShoppingArtactivitySpec entity);

    /**
     * 删除
     */
    int delete(ShoppingArtactivitySpec entity);

    /**
     * 查询详情
     */
    ShoppingArtactivitySpec queryDetail(ShoppingArtactivitySpec entity);

    /**
     * 查询列表
     */
    List<ShoppingArtactivitySpec> queryList(HashMap<String, Object> reqMap);

}
