package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingGoodscart;
import com.centit.shopping.po.ShoppingOrderform;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-02-25
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

    ShoppingOrderform queryDetailByOrderId(ShoppingOrderform entity);


    /**
     * 查询列表
     */
    List<ShoppingOrderform> queryList(HashMap<String, Object> reqMap);

    /**
     * 查询数量
     */
    int queryTotalCount(HashMap<String, Object> reqMap);

    /**
     * 取消订单
     */
    int cancelOrder(ShoppingOrderform entity);

    int confimOrder(ShoppingOrderform entity);

    List<HashMap<String, Object>> queryRecList(HashMap<String, Object> reqMap);

    /**
     * 查询指定商品订单列表
     */
    List<ShoppingOrderform> queryGoodsOrderList(HashMap<String, Object> reqMap);

    int queryGoodsOrderTotalCount(HashMap<String, Object> reqMap);

    /**
     * 查询指定艺术活动订单列表
     */
    List<ShoppingOrderform> queryActivityOrderList(HashMap<String, Object> reqMap);

    int queryActivityOrderTotalCount(HashMap<String, Object> reqMap);

    List<ShoppingOrderform> queryPlanOrderList(HashMap<String, Object> reqMap);

    int queryPlanOrderTotalCount(HashMap<String, Object> reqMap);

    List<ShoppingOrderform> queryArtClassList(HashMap<String, Object> reqMap);

    HashMap<String, Object> querySumList(HashMap<String, Object> reqMap);
}
