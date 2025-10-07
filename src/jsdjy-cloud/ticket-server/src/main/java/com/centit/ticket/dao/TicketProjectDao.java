package com.centit.ticket.dao;

import com.centit.ticket.po.TicketProject;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-04-08
 **/
@Repository
@Mapper
public interface TicketProjectDao {

    /**
     * 新增
     */
    int insert(TicketProject entity);

    /**
     * 更新
     */
    int update(TicketProject entity);

    /**
     * 删除
     */
    int delete(TicketProject entity);

    /**
     * 查询详情
     */
    TicketProject queryDetail(TicketProject entity);

    /**
     * 查询列表
     */
    List<TicketProject> queryList(HashMap<String, Object> reqMap);

    /**
     * 更新
     */
    int updateProjectSaleState(List<String> ids);

    int updateALlProjectSaleState(List<String> ids);

    List<TicketProject> queryUserProjectList(HashMap<String, Object> reqMap);

    List<String> queryIds(HashMap<String, Object> reqMap);
}
