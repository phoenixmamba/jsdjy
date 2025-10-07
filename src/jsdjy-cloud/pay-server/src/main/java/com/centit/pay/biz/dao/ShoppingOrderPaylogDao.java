package com.centit.pay.biz.dao;

import com.centit.pay.biz.po.ShoppingOrderPaylog;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-03-23
 **/
@Repository
@Mapper
public interface ShoppingOrderPaylogDao {

    /**
     * 新增
     */
    int insert(ShoppingOrderPaylog entity);

    /**
     * 更新
     */
    int update(ShoppingOrderPaylog entity);

    /**
     * 删除
     */
    int delete(ShoppingOrderPaylog entity);

    /**
     * 查询详情
     */
    ShoppingOrderPaylog queryDetail(ShoppingOrderPaylog entity);

    /**
     * 查询列表
     */
    List<ShoppingOrderPaylog> queryList(HashMap<String, Object> reqMap);

    ShoppingOrderPaylog queryDetailById(String id);

}
