package com.centit.ticket.dao;

import com.centit.ticket.po.TicketHistory;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-05-15
 **/
@Repository
@Mapper
public interface TicketHistoryDao {

    /**
     * 新增
     */
    int insert(TicketHistory entity);

    /**
     * 更新
     */
    int update(TicketHistory entity);

    /**
     * 删除
     */
    int delete(TicketHistory entity);

    /**
     * 查询详情
     */
    TicketHistory queryDetail(TicketHistory entity);

    /**
     * 查询列表
     */
    List<TicketHistory> queryList(HashMap<String, Object> reqMap);

    /**
     * 查询我的历史足迹列表
     */
    List<HashMap<String, Object>> queryMyHistoryList(HashMap<String, Object> reqMap);

    int queryMyHistoryCount(HashMap<String, Object> reqMap);

}
