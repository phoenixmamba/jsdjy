package com.centit.ticket.dao;

import com.centit.ticket.po.TicketRealname;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-04-20
 **/
@Repository
@Mapper
public interface TicketRealnameDao {

    /**
     * 新增
     */
    int insert(TicketRealname entity);

    /**
     * 更新
     */
    int update(TicketRealname entity);

    /**
     * 删除
     */
    int delete(TicketRealname entity);

    /**
     * 查询详情
     */
    TicketRealname queryDetail(TicketRealname entity);

    /**
     * 查询列表
     */
    List<TicketRealname> queryList(HashMap<String, Object> reqMap);

}
