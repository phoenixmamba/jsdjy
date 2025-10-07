package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingArtplanSpec;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2022-05-11
 **/
@Repository
@Mapper
public interface ShoppingArtplanSpecDao {

    /**
     * 新增
     */
    int insert(ShoppingArtplanSpec entity);

    /**
     * 更新
     */
    int update(ShoppingArtplanSpec entity);

    /**
     * 删除
     */
    int delete(ShoppingArtplanSpec entity);

    /**
     * 查询详情
     */
    ShoppingArtplanSpec queryDetail(ShoppingArtplanSpec entity);

    /**
     * 查询列表
     */
    List<ShoppingArtplanSpec> queryList(HashMap<String, Object> reqMap);

}
