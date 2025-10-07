package com.centit.logstatistics.logstatisticsserver.webmgr.dao;

import com.centit.logstatistics.logstatisticsserver.webmgr.po.ShoppingOrderPay;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p> <p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description :  Dao接口
 * @Date : 2021-06-08
 **/
@Repository
@Mapper
public interface ShoppingOrderPayDao {

    /**
     * 查询列表
     */
    List<ShoppingOrderPay> queryList(HashMap<String, Object> reqMap);

    /**
     * 查询详情
     */
    ShoppingOrderPay queryDetail(ShoppingOrderPay entity);
}
