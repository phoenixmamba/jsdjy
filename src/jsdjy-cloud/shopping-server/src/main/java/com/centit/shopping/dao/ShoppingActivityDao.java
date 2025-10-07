package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingActivity;
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
public interface ShoppingActivityDao {

    /**
     * 新增
     */
    int insert(ShoppingActivity entity);

    /**
     * 更新
     */
    int update(ShoppingActivity entity);

    /**
     * 删除
     */
    int delete(ShoppingActivity entity);

    /**
     * 查询详情
     */
    ShoppingActivity queryDetail(ShoppingActivity entity);

    /**
     * 查询列表
     */
    List<ShoppingActivity> queryList(HashMap<String, Object> reqMap);

    int queryTotalCount(HashMap<String, Object> reqMap);

}
