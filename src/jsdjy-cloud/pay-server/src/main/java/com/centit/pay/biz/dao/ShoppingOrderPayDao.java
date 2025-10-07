package com.centit.pay.biz.dao;

import com.centit.pay.biz.po.ShoppingOrderPay;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-03-10
 **/
@Repository
@Mapper
public interface ShoppingOrderPayDao {

    /**
     * 新增
     */
    int insert(ShoppingOrderPay entity);

    /**
     * 更新
     */
    int update(ShoppingOrderPay entity);

    /**
     * 删除
     */
    int delete(ShoppingOrderPay entity);

    /**
     * 查询详情
     */
    ShoppingOrderPay queryDetail(ShoppingOrderPay entity);

    /**
     * 查询列表
     */
    List<ShoppingOrderPay> queryList(HashMap<String, Object> reqMap);

    int setCashPayStatusFinish(String ofId,String outTradeNo);
}
