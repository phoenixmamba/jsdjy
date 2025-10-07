package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingArtclassPhoto;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-03-25
 **/
@Repository
@Mapper
public interface ShoppingArtclassPhotoDao {

    /**
     * 新增
     */
    int insert(ShoppingArtclassPhoto entity);

    /**
     * 更新
     */
    int update(ShoppingArtclassPhoto entity);

    /**
     * 删除
     */
    int delete(ShoppingArtclassPhoto entity);

    /**
     * 查询详情
     */
    ShoppingArtclassPhoto queryDetail(ShoppingArtclassPhoto entity);

    /**
     * 查询列表
     */
    List<ShoppingArtclassPhoto> queryList(HashMap<String, Object> reqMap);

}
