package com.centit.ticket.dao;

import com.centit.ticket.po.TicketEventPrice;
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
public interface TicketEventPriceDao {

    /**
     * 新增
     */
    int insert(TicketEventPrice entity);

    /**
     * 更新
     */
    int update(TicketEventPrice entity);

    /**
     * 删除
     */
    int delete(TicketEventPrice entity);

    /**
     * 查询详情
     */
    TicketEventPrice queryDetail(TicketEventPrice entity);

    /**
     * 查询列表
     */
    List<TicketEventPrice> queryList(HashMap<String, Object> reqMap);

}
