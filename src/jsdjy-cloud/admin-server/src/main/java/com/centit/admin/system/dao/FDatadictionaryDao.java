package com.centit.admin.system.dao;

import com.centit.admin.system.po.FDatadictionary;

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
public interface FDatadictionaryDao {

    /**
     * 新增
     */
    int insert(FDatadictionary entity);

    /**
     * 更新
     */
    int update(FDatadictionary entity);

    /**
     * 删除
     */
    int delete(FDatadictionary entity);

    /**
     * 查询详情
     */
    FDatadictionary queryDetail(FDatadictionary entity);

    /**
     * 查询列表
     */
    List<FDatadictionary> queryList(HashMap<String, Object> reqMap);

}
