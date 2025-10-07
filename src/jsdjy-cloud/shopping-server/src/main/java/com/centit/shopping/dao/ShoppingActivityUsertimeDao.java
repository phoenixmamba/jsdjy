package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingActivityUsertime;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-05-28
 **/
@Repository
@Mapper
public interface ShoppingActivityUsertimeDao {

    /**
     * 新增
     */
    int insert(ShoppingActivityUsertime entity);

    /**
     * 更新
     */
    int update(ShoppingActivityUsertime entity);

    /**
     * 删除
     */
    int delete(ShoppingActivityUsertime entity);

    /**
     * 查询详情
     */
    ShoppingActivityUsertime queryDetail(ShoppingActivityUsertime entity);

    /**
     * 查询列表
     */
    List<ShoppingActivityUsertime> queryList(HashMap<String, Object> reqMap);

}
