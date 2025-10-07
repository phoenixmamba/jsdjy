package com.centit.logstatistics.logstatisticsserver.webmgr.dao;

import com.centit.logstatistics.logstatisticsserver.webmgr.po.ParkOrder;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p> <p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description :  Dao接口
 * @Date : 2021-06-10
 **/
@Repository
@Mapper
public interface ParkOrderDao {

    /**
     * 查询列表
     */
    List<ParkOrder> queryList(HashMap<String, Object> reqMap);

    /**
     * 查询详情
     */
    ParkOrder queryDetail(ParkOrder entity);

}
