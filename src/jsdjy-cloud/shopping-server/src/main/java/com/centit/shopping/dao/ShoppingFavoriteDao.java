package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingFavorite;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-05-14
 **/
@Repository
@Mapper
public interface ShoppingFavoriteDao {

    /**
     * 新增
     */
    int insert(ShoppingFavorite entity);

    /**
     * 更新
     */
    int update(ShoppingFavorite entity);

    /**
     * 删除
     */
    int delete(ShoppingFavorite entity);

    /**
     * 查询详情
     */
    ShoppingFavorite queryDetail(ShoppingFavorite entity);

    /**
     * 查询列表
     */
    List<ShoppingFavorite> queryList(HashMap<String, Object> reqMap);

    int queryTotalCount(HashMap<String, Object> reqMap);

    /**
     * 取消收藏
     */
    int cancelFav(ShoppingFavorite entity);

}
