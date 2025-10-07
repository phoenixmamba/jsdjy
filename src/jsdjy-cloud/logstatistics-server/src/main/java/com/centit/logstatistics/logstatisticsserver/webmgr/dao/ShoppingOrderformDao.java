package com.centit.logstatistics.logstatisticsserver.webmgr.dao;

import com.centit.logstatistics.logstatisticsserver.webmgr.po.ShoppingOrderform;
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
public interface ShoppingOrderformDao {

    /**
     * 查询详情
     */
    ShoppingOrderform queryDetail(ShoppingOrderform entity);

    /**
     * 查询列表
     */
    List<ShoppingOrderform> queryList(HashMap<String, Object> reqMap);

    int queryTotalCount(HashMap<String, Object> reqMap);

    HashMap<String, Object> querySumList(HashMap<String, Object> reqMap);

    List<HashMap<String, Object>> queryMoneyList(HashMap<String, Object> reqMap);

    /**
     * 查询充值列表
     */
    List<ShoppingOrderform> queryRechargeList(HashMap<String, Object> reqMap);

    int queryRechargeTotalCount(HashMap<String, Object> reqMap);

    HashMap<String, Object> queryRechargeSumList(HashMap<String, Object> reqMap);

    List<ShoppingOrderform> queryExceptionList(HashMap<String, Object> reqMap);

}
