package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingActivityUser;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-05-27
 **/
@Repository
@Mapper
public interface ShoppingActivityUserDao {

    /**
     * 新增
     */
    int insert(ShoppingActivityUser entity);

    /**
     * 更新
     */
    int update(ShoppingActivityUser entity);

    /**
     * 删除
     */
    int delete(ShoppingActivityUser entity);

    /**
     * 查询详情
     */
    ShoppingActivityUser queryDetail(ShoppingActivityUser entity);

    /**
     * 查询列表
     */
    List<ShoppingActivityUser> queryList(HashMap<String, Object> reqMap);

}
