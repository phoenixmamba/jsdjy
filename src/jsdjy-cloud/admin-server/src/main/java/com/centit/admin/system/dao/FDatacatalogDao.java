package com.centit.admin.system.dao;

import com.centit.admin.system.po.FDatacatalog;

import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-03-04
 **/
@Repository
@Mapper
public interface FDatacatalogDao {

    /**
     * 新增
     */
    int insert(FDatacatalog entity);

    /**
     * 更新
     */
    int update(FDatacatalog entity);

    /**
     * 删除
     */
    int delete(FDatacatalog entity);

    /**
     * 查询详情
     */
    FDatacatalog queryDetail(FDatacatalog entity);

    /**
     * 查询列表
     */
    List<FDatacatalog> queryList(HashMap<String, Object> reqMap);

    /**
     * 查询分页列表
     */
    List<FDatacatalog> queryDictionaryPageList(HashMap<String, Object> reqMap);

    /**
     * 查询分页列表
     */
    int queryDictionaryPageListCount(HashMap<String, Object> reqMap);
}
