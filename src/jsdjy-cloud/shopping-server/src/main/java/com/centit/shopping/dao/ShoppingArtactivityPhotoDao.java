package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingArtactivityPhoto;
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
public interface ShoppingArtactivityPhotoDao {

    /**
     * 新增
     */
    int insert(ShoppingArtactivityPhoto entity);

    /**
     * 更新
     */
    int update(ShoppingArtactivityPhoto entity);

    /**
     * 删除
     */
    int delete(ShoppingArtactivityPhoto entity);

    /**
     * 查询详情
     */
    ShoppingArtactivityPhoto queryDetail(ShoppingArtactivityPhoto entity);

    /**
     * 查询列表
     */
    List<ShoppingArtactivityPhoto> queryList(HashMap<String, Object> reqMap);

}
