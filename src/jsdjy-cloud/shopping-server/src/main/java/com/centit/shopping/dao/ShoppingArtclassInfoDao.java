package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingArtclassInfo;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-03-24
 **/
@Repository
@Mapper
public interface ShoppingArtclassInfoDao {

    /**
     * 新增
     */
    int insert(ShoppingArtclassInfo entity);

    /**
     * 更新
     */
    int update(ShoppingArtclassInfo entity);

    /**
     * 删除
     */
    int delete(ShoppingArtclassInfo entity);

    /**
     * 查询详情
     */
    ShoppingArtclassInfo queryDetail(ShoppingArtclassInfo entity);

    /**
     * 查询列表
     */
    List<ShoppingArtclassInfo> queryList(HashMap<String, Object> reqMap);

    int deleteByArtclassId(ShoppingArtclassInfo shoppingArtclassInfo);
}
