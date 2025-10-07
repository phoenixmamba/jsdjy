package com.centit.logstatistics.logstatisticsserver.webmgr.dao;

import java.util.HashMap;
import java.util.List;

import com.centit.logstatistics.logstatisticsserver.webmgr.po.TLmOperlog;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>接口运行日志<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-05-06
 **/
@Repository
@Mapper
public interface TLmOperlogDao {

    /**
     * 新增
     */
    int insert(TLmOperlog entity);

    /**
     * 更新
     */
    int update(TLmOperlog entity);

    /**
     * 删除
     */
    int delete(TLmOperlog entity);

    /**
     * 查询详情
     */
    TLmOperlog queryDetail(TLmOperlog entity);

    /**
     * 查询列表
     */
    List<TLmOperlog> queryList(HashMap<String, Object> reqMap);

    int queryListTotal(HashMap<String, Object> reqMap);
}
