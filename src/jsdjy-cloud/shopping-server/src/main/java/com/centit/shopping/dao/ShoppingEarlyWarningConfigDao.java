package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingEarlyWarningConfig;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
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
public interface ShoppingEarlyWarningConfigDao {

    /**
     * 新增
     */
    int insert(ShoppingEarlyWarningConfig entity);

    /**
     * 更新
     */
    int update(ShoppingEarlyWarningConfig entity);

    /**
     * 删除
     */
    int delete(ShoppingEarlyWarningConfig entity);

    /**
     * 查询详情
     */
    ShoppingEarlyWarningConfig queryDetail(ShoppingEarlyWarningConfig entity);

    /**
     * 查询列表
     */
    List<ShoppingEarlyWarningConfig> queryList(HashMap<String, Object> reqMap);

    ShoppingEarlyWarningConfig queryDetailByIP(@Param("id") String id, @Param("server")String server);
}
