package com.centit.ticket.dao;

import com.centit.ticket.po.TicketVenue;
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
public interface TicketVenueDao {

    /**
     * 新增
     */
    int insert(TicketVenue entity);

    /**
     * 更新
     */
    int update(TicketVenue entity);

    /**
     * 删除
     */
    int delete(TicketVenue entity);

    /**
     * 查询详情
     */
    TicketVenue queryDetail(TicketVenue entity);

    /**
     * 查询列表
     */
    List<TicketVenue> queryList(HashMap<String, Object> reqMap);

}
