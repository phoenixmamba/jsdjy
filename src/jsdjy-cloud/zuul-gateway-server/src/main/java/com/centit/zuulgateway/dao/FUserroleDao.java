package com.centit.zuulgateway.dao;

import com.centit.zuulgateway.po.FUserrole;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>用户角色<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2020-09-16
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

}
