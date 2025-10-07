package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingArtclassExtra;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-08-17
 **/
@Repository
@Mapper
public interface ShoppingArtclassExtraDao {

    /**
     * 新增
     */
    int insert(ShoppingArtclassExtra entity);

    /**
     * 更新
     */
    int update(ShoppingArtclassExtra entity);

    /**
     * 删除
     */
    int delete(ShoppingArtclassExtra entity);

    /**
     * 查询详情
     */
    ShoppingArtclassExtra queryDetail(ShoppingArtclassExtra entity);

    /**
     * 查询列表
     */
    List<ShoppingArtclassExtra> queryList(HashMap<String, Object> reqMap);

}
