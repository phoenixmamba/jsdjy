package com.centit.admin.system.dao;

import com.centit.admin.system.po.FUnitrole;

import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-03-03
 **/
@Repository
@Mapper
public interface FUnitroleDao {

    /**
     * 新增
     */
    int insert(FUnitrole entity);

    /**
     * 更新
     */
    int update(FUnitrole entity);

    /**
     * 删除
     */
    int delete(FUnitrole entity);

    /**
     * 查询详情
     */
    FUnitrole queryDetail(FUnitrole entity);

    /**
     * 查询列表
     */
    List<FUnitrole> queryList(HashMap<String, Object> reqMap);


    /**
     * 查询分页列表
     */
    List<FUnitrole> queryPageList(HashMap<String, Object> reqMap);

    /**
     * 查询分页列表数量
     */
    int queryPageListCount(HashMap<String, Object> reqMap);
}
