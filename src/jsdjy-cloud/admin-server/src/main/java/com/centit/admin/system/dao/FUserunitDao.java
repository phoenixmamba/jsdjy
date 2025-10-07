package com.centit.admin.system.dao;

import com.centit.admin.system.po.FUserunit;
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
public interface FUserunitDao {

    /**
     * 新增
     */
    int insert(FUserunit entity);

    /**
     * 更新
     */
    int update(FUserunit entity);

    /**
     * 删除
     */
    int delete(FUserunit entity);

    /**
     * 查询详情
     */
    FUserunit queryDetail(FUserunit entity);

    /**
     * 查询列表
     */
    List<FUserunit> queryList(HashMap<String, Object> reqMap);

    /**
     * 查询列表
     */
    List<FUserunit> queryUnitUserPageList(HashMap<String, Object> reqMap);

    /**
     * 查询部门下用户数量
     */
    int queryUnitUserPageListCount(HashMap<String, Object> reqMap);

}
