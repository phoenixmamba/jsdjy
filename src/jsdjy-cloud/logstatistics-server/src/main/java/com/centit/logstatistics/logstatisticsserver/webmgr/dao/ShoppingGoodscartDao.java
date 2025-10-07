package com.centit.logstatistics.logstatisticsserver.webmgr.dao;

import com.centit.logstatistics.logstatisticsserver.webmgr.po.ShoppingGoodscart;
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
public interface ShoppingGoodscartDao {

    /**
     * 查询列表
     */
    List<ShoppingGoodscart> queryList(HashMap<String, Object> reqMap);

    int queryGoodsCount(HashMap<String, Object> reqMap);
}
