package com.centit.admin.system.dao;

import com.centit.admin.system.po.FUserrole;

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
public interface FUserroleDao {

    /**
     * 新增
     */
    int insert(FUserrole entity);

    /**
     * 更新
     */
    int update(FUserrole entity);

    /**
     * 删除
     */
    int delete(FUserrole entity);

    /**
     * 查询详情
     */
    FUserrole queryDetail(FUserrole entity);

    /**
     * 查询列表
     */
    List<FUserrole> queryList(HashMap<String, Object> reqMap);

    /**
     * 查询分页列表
     */
    List<FUserrole> queryPageList(HashMap<String, Object> reqMap);

    /**
     * 查询分页列表
     */
    int queryPageListCount(HashMap<String, Object> reqMap);

}
