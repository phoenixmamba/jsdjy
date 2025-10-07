package com.centit.zuulgateway.dao;

import java.util.HashMap;
import java.util.List;

import com.centit.zuulgateway.po.TStLogin;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-05-06
 **/
@Repository
@Mapper
public interface TStLoginDao {

    /**
     * 新增
     */
    int insert(TStLogin entity);

    /**
     * 更新
     */
    int update(TStLogin entity);

    /**
     * 删除
     */
    int delete(TStLogin entity);

    /**
     * 查询详情
     */
    TStLogin queryDetail(TStLogin entity);

    /**
     * 查询列表
     */
    List<TStLogin> queryList(HashMap<String, Object> reqMap);

}
