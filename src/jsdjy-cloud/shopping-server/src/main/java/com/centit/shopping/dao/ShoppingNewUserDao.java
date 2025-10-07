package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingNewUser;
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
public interface ShoppingNewUserDao {

    /**
     * 新增
     */
    int insert(ShoppingNewUser entity);

    /**
     * 更新
     */
    int update(ShoppingNewUser entity);

    /**
     * 删除
     */
    int delete(ShoppingNewUser entity);

    /**
     * 查询详情
     */
    ShoppingNewUser queryDetail(ShoppingNewUser entity);

    /**
     * 查询列表
     */
    List<ShoppingNewUser> queryList(HashMap<String, Object> reqMap);

}
