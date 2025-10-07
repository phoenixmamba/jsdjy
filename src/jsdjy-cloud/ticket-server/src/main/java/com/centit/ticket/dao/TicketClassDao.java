package com.centit.ticket.dao;

import com.centit.ticket.po.TicketClass;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-04-08
 **/
@Repository
@Mapper
public interface TicketClassDao {

    /**
     * 新增
     */
    int insert(TicketClass entity);

    /**
     * 更新
     */
    int update(TicketClass entity);

    /**
     * 删除
     */
    int delete(TicketClass entity);

    /**
     * 查询详情
     */
    TicketClass queryDetail(TicketClass entity);

    /**
     * 查询详情
     */
    TicketClass queryAiYiHuoDong(TicketClass entity);

    /**
     * 查询列表
     */
    List<TicketClass> queryList(HashMap<String, Object> reqMap);

}
