package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingRefund;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-05-07
 **/
@Repository
@Mapper
public interface ShoppingRefundDao {

    /**
     * 新增
     */
    int insert(ShoppingRefund entity);

    /**
     * 更新
     */
    int update(ShoppingRefund entity);

    /**
     * 删除
     */
    int delete(ShoppingRefund entity);

    /**
     * 查询详情
     */
    ShoppingRefund queryDetail(ShoppingRefund entity);

    /**
     * 查询列表
     */
    List<ShoppingRefund> queryList(HashMap<String, Object> reqMap);

    int queryTotalCount(HashMap<String, Object> reqMap);

    List<ShoppingRefund> queryUserRefundList(HashMap<String, Object> reqMap);

    int queryUserRefundCount(HashMap<String, Object> reqMap);

}
