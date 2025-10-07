package com.centit.ticket.dao;

import com.centit.ticket.po.TicketRemind;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-06-21
 **/
@Repository
@Mapper
public interface TicketRemindDao {

    /**
     * 新增
     */
    int insert(TicketRemind entity);

    /**
     * 更新
     */
    int update(TicketRemind entity);

    /**
     * 删除
     */
    int delete(TicketRemind entity);

    /**
     * 查询详情
     */
    TicketRemind queryDetail(TicketRemind entity);

    /**
     * 查询列表
     */
    List<TicketRemind> queryList(HashMap<String, Object> reqMap);

}
