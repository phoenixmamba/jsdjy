package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingEarlyWarningAdmin;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-05-31
 **/
@Repository
@Mapper
public interface ShoppingEarlyWarningAdminDao {

    /**
     * 新增
     */
    int insert(ShoppingEarlyWarningAdmin entity);

    /**
     * 更新
     */
    int update(ShoppingEarlyWarningAdmin entity);

    /**
     * 删除
     */
    int delete(ShoppingEarlyWarningAdmin entity);

    /**
     * 查询详情
     */
    ShoppingEarlyWarningAdmin queryDetail(ShoppingEarlyWarningAdmin entity);

    /**
     * 查询列表
     */
    List<ShoppingEarlyWarningAdmin> queryList(HashMap<String, Object> reqMap);

}
