package com.centit.logstatistics.logstatisticsserver.webmgr.dao;

import com.centit.logstatistics.logstatisticsserver.webmgr.po.ShoppingSysconfig;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * <p> <p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description :  Dao接口
 * @Date : 2022-08-08
 **/
@Repository
@Mapper
public interface ShoppingSysconfigDao {

    /**
     * 查询列表
     */
    List<ShoppingSysconfig> queryList(HashMap<String, Object> reqMap);

}
