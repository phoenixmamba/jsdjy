package com.centit.pay.biz.dao;

import com.centit.pay.biz.po.ShoppingPayment;
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
public interface ShoppingPaymentDao {

    /**
     * 新增
     */
    int insert(ShoppingPayment entity);

    /**
     * 更新
     */
    int update(ShoppingPayment entity);

    /**
     * 删除
     */
    int delete(ShoppingPayment entity);

    /**
     * 查询详情
     */
    ShoppingPayment queryDetail(ShoppingPayment entity);

    ShoppingPayment queryDetailById(String id);

    /**
     * 查询列表
     */
    List<ShoppingPayment> queryList(HashMap<String, Object> reqMap);

}
