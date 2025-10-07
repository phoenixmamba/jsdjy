package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingGoodscart;
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
public interface ShoppingGoodscartDao {

    /**
     * 新增
     */
    int insert(ShoppingGoodscart entity);

    /**
     * 更新
     */
    int update(ShoppingGoodscart entity);

    /**
     * 删除
     */
    int delete(ShoppingGoodscart entity);

    /**
     * 查询详情
     */
    ShoppingGoodscart queryDetail(ShoppingGoodscart entity);

    /**
     * 查询列表
     */
    List<ShoppingGoodscart> queryList(HashMap<String, Object> reqMap);

    List<ShoppingGoodscart> queryBuyList(HashMap<String, Object> reqMap);

    List<ShoppingGoodscart> queryCartGoodsList(HashMap<String, Object> reqMap);

    List<ShoppingGoodscart> queryCartList(HashMap<String, Object> reqMap);

    int queryGoodsSoldCount(HashMap<String, Object> reqMap);

    List<HashMap<String, Object>> queryDoneGoodsList(HashMap<String, Object> reqMap);

    int queryDoneGoodsListCount(HashMap<String, Object> reqMap);

    List<HashMap<String, Object>> queryDoneActivityList(HashMap<String, Object> reqMap);

    int queryDoneActivityListCount(HashMap<String, Object> reqMap);

    List<HashMap<String, Object>> queryDonePlanList(HashMap<String, Object> reqMap);

    int queryDonePlanListCount(HashMap<String, Object> reqMap);

    int queryGoodsCount(HashMap<String, Object> reqMap);
}
