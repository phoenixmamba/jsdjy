package com.centit.zuulgateway.dao;

import com.centit.zuulgateway.po.TDmEquipment;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;

/**
 * <p>终端设备信息<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2020-05-27
 **/
@Repository
@Mapper
public interface TDmEquipmentDao {



    /**
     * 查询数量ByProperties
     */
    int queryCountByProperties(HashMap<String, Object> reqMap);



}
