package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingArtclassLiveset;
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
public interface ShoppingArtclassLivesetDao {

    /**
     * 新增
     */
    int insert(ShoppingArtclassLiveset entity);

    /**
     * 更新
     */
    int update(ShoppingArtclassLiveset entity);

    /**
     * 删除
     */
    int delete(ShoppingArtclassLiveset entity);

    /**
     * 查询详情
     */
    ShoppingArtclassLiveset queryDetail(ShoppingArtclassLiveset entity);

    /**
     * 查询列表
     */
    List<ShoppingArtclassLiveset> queryList(HashMap<String, Object> reqMap);

    /**
     * 下课
     */
    int closeClass(ShoppingArtclassLiveset entity);

}
