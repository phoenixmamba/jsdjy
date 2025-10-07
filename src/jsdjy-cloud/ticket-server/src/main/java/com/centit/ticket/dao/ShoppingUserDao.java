package com.centit.ticket.dao;

import com.centit.ticket.po.ShoppingUser;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-02-25
 **/
@Repository
@Mapper
public interface ShoppingUserDao {

    /**
     * 新增
     */
    int insert(ShoppingUser entity);

    /**
     * 更新
     */
    int update(ShoppingUser entity);

    /**
     * 删除
     */
    int delete(ShoppingUser entity);

    /**
     * 查询详情
     */
    ShoppingUser queryDetail(ShoppingUser entity);

    /**
     * 查询列表
     */
    List<ShoppingUser> queryList(HashMap<String, Object> reqMap);

    /**
     * 查询列表
     */
    List<ShoppingUser> queryPushUserList(HashMap<String, Object> reqMap);

    List<ShoppingUser> queryProjectUserList(HashMap<String, Object> reqMap);

}
