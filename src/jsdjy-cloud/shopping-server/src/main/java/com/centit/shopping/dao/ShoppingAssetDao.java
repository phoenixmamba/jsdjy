package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingAsset;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-05-20
 **/
@Repository
@Mapper
public interface ShoppingAssetDao {

    /**
     * 新增
     */
    int insert(ShoppingAsset entity);

    /**
     * 更新
     */
    int update(ShoppingAsset entity);

    /**
     * 删除
     */
    int delete(ShoppingAsset entity);

    /**
     * 查询详情
     */
    ShoppingAsset queryDetail(ShoppingAsset entity);

    /**
     * 查询列表
     */
    List<ShoppingAsset> queryList(HashMap<String, Object> reqMap);

}
