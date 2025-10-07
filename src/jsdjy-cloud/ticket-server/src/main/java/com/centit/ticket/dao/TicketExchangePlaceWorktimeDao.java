package com.centit.ticket.dao;

import com.centit.ticket.po.TicketExchangePlaceWorktime;
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
public interface TicketExchangePlaceWorktimeDao {

    /**
     * 新增
     */
    int insert(TicketExchangePlaceWorktime entity);

    /**
     * 更新
     */
    int update(TicketExchangePlaceWorktime entity);

    /**
     * 删除
     */
    int delete(TicketExchangePlaceWorktime entity);

    /**
     * 查询详情
     */
    TicketExchangePlaceWorktime queryDetail(TicketExchangePlaceWorktime entity);

    /**
     * 查询列表
     */
    List<TicketExchangePlaceWorktime> queryList(HashMap<String, Object> reqMap);

}
