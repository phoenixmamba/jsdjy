package com.centit.admin.system.dao;

import com.centit.admin.system.po.FRolepower;

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
public interface FRolepowerDao {

    /**
     * 新增
     */
    int insert(FRolepower entity);

    /**
     * 更新
     */
    int update(FRolepower entity);

    /**
     * 删除
     */
    int delete(FRolepower entity);

    /**
     * 查询详情
     */
    FRolepower queryDetail(FRolepower entity);

    /**
     * 查询列表
     */
    List<FRolepower> queryList(HashMap<String, Object> reqMap);

}
