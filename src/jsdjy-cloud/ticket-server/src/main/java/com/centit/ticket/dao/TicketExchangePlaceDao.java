package com.centit.ticket.dao;

import com.centit.ticket.po.TicketExchangePlace;
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
public interface TicketExchangePlaceDao {

    /**
     * 新增
     */
    int insert(TicketExchangePlace entity);

    /**
     * 更新
     */
    int update(TicketExchangePlace entity);

    /**
     * 删除
     */
    int delete(TicketExchangePlace entity);

    /**
     * 查询详情
     */
    TicketExchangePlace queryDetail(TicketExchangePlace entity);

    /**
     * 查询列表
     */
    List<TicketExchangePlace> queryList(HashMap<String, Object> reqMap);

}
