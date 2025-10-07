package com.centit.zuulgateway.dao;

import com.centit.zuulgateway.po.TRoleuser;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>移动端角色-用户关联<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2020-10-14
 **/
@Repository
@Mapper
public interface TRoleuserDao {

    /**
     * 新增
     */
    int insert(TRoleuser entity);

    /**
     * 更新
     */
    int update(TRoleuser entity);

    /**
     * 删除
     */
    int delete(TRoleuser entity);

    /**
     * 查询详情
     */
    TRoleuser queryDetail(TRoleuser entity);

    /**
     * 查询列表
     */
    List<TRoleuser> queryList(HashMap<String, Object> reqMap);

}
