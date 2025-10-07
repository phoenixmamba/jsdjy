package com.centit.ticket.dao;

import com.centit.ticket.po.TicketEvent;
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
public interface TicketEventDao {

    /**
     * 新增
     */
    int insert(TicketEvent entity);

    /**
     * 更新
     */
    int update(TicketEvent entity);

    /**
     * 删除
     */
    int delete(TicketEvent entity);

    /**
     * 查询详情
     */
    TicketEvent queryDetail(TicketEvent entity);

    /**
     * 查询列表
     */
    List<TicketEvent> queryList(HashMap<String, Object> reqMap);

    /**
     * 更新
     */
    int updateEventSaleState(HashMap<String, Object> reqMap);

    int updateAllEventSaleState(HashMap<String, Object> reqMap);

    List<TicketEvent> queryPageList(HashMap<String, Object> reqMap);

    int queryTotalCount(HashMap<String, Object> reqMap);

    List<TicketEvent> queryProjectEvents(HashMap<String, Object> reqMap);

    List<TicketEvent> queryClassEvents(HashMap<String, Object> reqMap);

    List<TicketEvent> queryToPushEvents(HashMap<String, Object> reqMap);
}
