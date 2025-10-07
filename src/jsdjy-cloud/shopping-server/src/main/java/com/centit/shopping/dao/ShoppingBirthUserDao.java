package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingBirthUser;
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
public interface ShoppingBirthUserDao {

    /**
     * 新增
     */
    int insert(ShoppingBirthUser entity);

    /**
     * 更新
     */
    int update(ShoppingBirthUser entity);

    /**
     * 删除
     */
    int delete(ShoppingBirthUser entity);

    /**
     * 查询详情
     */
    ShoppingBirthUser queryDetail(ShoppingBirthUser entity);

    /**
     * 查询列表
     */
    List<ShoppingBirthUser> queryList(HashMap<String, Object> reqMap);

}
