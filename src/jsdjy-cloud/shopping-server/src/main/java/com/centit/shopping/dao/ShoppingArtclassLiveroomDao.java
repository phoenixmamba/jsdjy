package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingArtclassLiveroom;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-07-29
 **/
@Repository
@Mapper
public interface ShoppingArtclassLiveroomDao {

    /**
     * 新增
     */
    int insert(ShoppingArtclassLiveroom entity);

    /**
     * 更新
     */
    int update(ShoppingArtclassLiveroom entity);

    /**
     * 删除
     */
    int delete(ShoppingArtclassLiveroom entity);

    /**
     * 查询详情
     */
    ShoppingArtclassLiveroom queryDetail(ShoppingArtclassLiveroom entity);

    /**
     * 查询列表
     */
    List<ShoppingArtclassLiveroom> queryList(HashMap<String, Object> reqMap);

}
