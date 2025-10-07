package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingPayment;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-03-16
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

    /**
     * 查询列表
     */
    List<ShoppingPayment> queryList(HashMap<String, Object> reqMap);

}
