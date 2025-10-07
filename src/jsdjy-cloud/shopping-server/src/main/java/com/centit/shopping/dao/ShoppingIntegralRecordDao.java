package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingIntegralRecord;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-07-07
 **/
@Repository
@Mapper
public interface ShoppingIntegralRecordDao {

    /**
     * 新增
     */
    int insert(ShoppingIntegralRecord entity);

    /**
     * 更新
     */
    int update(ShoppingIntegralRecord entity);

    /**
     * 删除
     */
    int delete(ShoppingIntegralRecord entity);

    /**
     * 查询详情
     */
    ShoppingIntegralRecord queryDetail(ShoppingIntegralRecord entity);

    /**
     * 查询列表
     */
    List<ShoppingIntegralRecord> queryList(HashMap<String, Object> reqMap);

    int queryDailySum(HashMap<String, Object> reqMap);

    int updateIntegralStatus(ShoppingIntegralRecord entity);
}
