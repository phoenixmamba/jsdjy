package com.centit.ticket.dao;

import com.centit.ticket.po.CrmAuthorization;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-05-18
 **/
@Repository
@Mapper
public interface CrmAuthorizationDao {

    /**
     * 新增
     */
    int insert(CrmAuthorization entity);

    /**
     * 更新
     */
    int update(CrmAuthorization entity);

    /**
     * 删除
     */
    int delete(CrmAuthorization entity);

    /**
     * 查询详情
     */
    CrmAuthorization queryDetail(CrmAuthorization entity);

    /**
     * 查询列表
     */
    List<CrmAuthorization> queryList(HashMap<String, Object> reqMap);

}
