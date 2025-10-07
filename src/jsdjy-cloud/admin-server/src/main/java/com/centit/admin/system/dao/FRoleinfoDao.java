package com.centit.admin.system.dao;

import com.centit.admin.system.po.FRoleinfo;

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
public interface FRoleinfoDao {

    /**
     * 新增
     */
    int insert(FRoleinfo entity);

    /**
     * 更新
     */
    int update(FRoleinfo entity);

    /**
     * 删除
     */
    int delete(FRoleinfo entity);

    /**
     * 查询详情
     */
    FRoleinfo queryDetail(FRoleinfo entity);

    /**
     * 查询列表
     */
    List<FRoleinfo> queryList(HashMap<String, Object> reqMap);

    /**
     * 查询分页列表
     */
    List<FRoleinfo> queryRolePageList(HashMap<String, Object> reqMap);

    /**
     * 查询分页列表
     */
    int queryRolePageListCount(HashMap<String, Object> reqMap);


    /**
     * 查询赋予我的角色分页列表
     */
    List<FRoleinfo> queryToMeRolePageList(HashMap<String, Object> reqMap);

    /**
     * 查询赋予我的角色页列表数量
     */
    int queryToMeRolePageListCount(HashMap<String, Object> reqMap);

    /**
     * 角色名校验
     */
    List<FRoleinfo> checkName(HashMap<String, Object> reqMap);
}
