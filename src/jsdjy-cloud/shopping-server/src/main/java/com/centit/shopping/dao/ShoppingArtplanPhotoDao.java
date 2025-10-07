package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingArtplanPhoto;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-12-14
 **/
@Repository
@Mapper
public interface ShoppingArtplanPhotoDao {

    /**
     * 新增
     */
    int insert(ShoppingArtplanPhoto entity);

    /**
     * 更新
     */
    int update(ShoppingArtplanPhoto entity);

    /**
     * 删除
     */
    int delete(ShoppingArtplanPhoto entity);

    /**
     * 查询详情
     */
    ShoppingArtplanPhoto queryDetail(ShoppingArtplanPhoto entity);

    /**
     * 查询列表
     */
    List<ShoppingArtplanPhoto> queryList(HashMap<String, Object> reqMap);

}
