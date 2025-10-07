package com.centit.zuulgateway.dao;

import com.alibaba.fastjson.JSONObject;
import com.centit.zuulgateway.po.TLmOperlog;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * <p> <p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description :  Dao接口
 * @Date : 2020-05-28
 **/
@Repository
@Mapper
public interface TLmOperlogDao {

    /**
     * 查询列表
     */
    List<TLmOperlog> queryList(HashMap<String, Object> reqMap);

    /**
     * 新增日志
     */
    int insert(TLmOperlog entity);
}
