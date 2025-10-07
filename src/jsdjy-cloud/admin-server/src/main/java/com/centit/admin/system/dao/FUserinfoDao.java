package com.centit.admin.system.dao;

import com.centit.admin.system.po.FUserinfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-03-03
 **/
@Repository
@Mapper
public interface FUserinfoDao {

    /**
     * 新增
     */
    int insert(FUserinfo entity);

    /**
     * 更新
     */
    int update(FUserinfo entity);

    /**
     * 删除
     */
    int delete(FUserinfo entity);

    /**
     * 查询详情
     */
    FUserinfo queryDetail(String userCode);

    /**
     * 查询列表
     */
    List<FUserinfo> queryList(HashMap<String, Object> reqMap);

    /**
     * 根据loginName查询列表
     */
    FUserinfo queryUserByLoginName(String loginName);

    /**
     * 查询分页列表
     */
    List<FUserinfo> queryPageList(HashMap<String, Object> reqMap);

    /**
     * 查询分页列表数量
     */
    int queryPageListCount(HashMap<String, Object> reqMap);

    /**
     * 查询部门用户列表
     */
    List<FUserinfo> getDeptUserList(HashMap<String, Object> reqMap);

    List<FUserinfo> checkLoginName(HashMap<String, Object> reqMap);

}
