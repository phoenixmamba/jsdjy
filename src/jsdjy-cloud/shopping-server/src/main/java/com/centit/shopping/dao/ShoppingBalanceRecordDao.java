package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingBalanceRecord;
import java.util.HashMap;
import java.util.List;

import com.centit.shopping.po.ShoppingIntegralRecord;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2022-01-25
 **/
@Repository
@Mapper
public interface ShoppingBalanceRecordDao {

    /**
     * 新增
     */
    int insert(ShoppingBalanceRecord entity);

    /**
     * 更新
     */
    int update(ShoppingBalanceRecord entity);

    /**
     * 删除
     */
    int delete(ShoppingBalanceRecord entity);

    /**
     * 查询详情
     */
    ShoppingBalanceRecord queryDetail(ShoppingBalanceRecord entity);

    /**
     * 查询列表
     */
    List<ShoppingBalanceRecord> queryList(HashMap<String, Object> reqMap);

    int updateBalanceStatus(ShoppingBalanceRecord entity);
}
