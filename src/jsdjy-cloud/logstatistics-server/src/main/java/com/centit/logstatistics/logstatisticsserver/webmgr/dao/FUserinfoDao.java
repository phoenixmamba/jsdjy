package com.centit.logstatistics.logstatisticsserver.webmgr.dao;

import com.centit.logstatistics.logstatisticsserver.webmgr.po.FUserinfo;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p> <p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description :  Dao接口
 * @Date : 2021-08-23
 **/
@Repository
@Mapper
public interface FUserinfoDao {

    /**
     * 查询列表
     */
    List<FUserinfo> queryList(HashMap<String, Object> reqMap);

    /**
     * 查询详情
     */
    FUserinfo queryDetail(FUserinfo entity);

}
