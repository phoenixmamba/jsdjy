package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingUser;
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

    ShoppingUser queryDetailByMobile(ShoppingUser entity);

    /**
     * 查询列表
     */
    List<ShoppingUser> queryList(HashMap<String, Object> reqMap);

    List<ShoppingUser> queryGoodsUserList(HashMap<String, Object> reqMap);

    List<ShoppingUser> queryClassUserList(HashMap<String, Object> reqMap);

    int queryClassUserCount(HashMap<String, Object> reqMap);

    List<ShoppingUser> queryClassExtraUserList(HashMap<String, Object> reqMap);

    int queryClassExtraUserCount(HashMap<String, Object> reqMap);

    List<ShoppingUser> queryClassToSelectUserList(HashMap<String, Object> reqMap);

    int queryClassToSelectUserCount(HashMap<String, Object> reqMap);

    List<ShoppingUser> queryRegUsers(HashMap<String, Object> reqMap);

    List<ShoppingUser> queryArtactivityUsers(HashMap<String, Object> reqMap);

    List<ShoppingUser> queryArtplanUsers(HashMap<String, Object> reqMap);
}
