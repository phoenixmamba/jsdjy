package com.centit.pay.biz.dao;

import com.centit.pay.biz.po.ShoppingOrderform;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-03-09
 **/
@Repository
@Mapper
public interface ShoppingOrderformDao {

    /**
     * 新增
     */
    int insert(ShoppingOrderform entity);

    /**
     * 更新
     */
    int update(ShoppingOrderform entity);

    /**
     * 删除
     */
    int delete(ShoppingOrderform entity);

    /**
     * 查询详情
     */
    ShoppingOrderform queryDetail(ShoppingOrderform entity);

    ShoppingOrderform queryDetailByOrderId(String orderId);

    /**
     * 查询列表
     */
    List<ShoppingOrderform> queryList(HashMap<String, Object> reqMap);

    int updateStatusWithMsg(ShoppingOrderform entity);
}
