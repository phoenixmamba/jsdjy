package com.centit.ticket.dao;

import com.centit.ticket.po.TicketProjectGuide;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-04-29
 **/
@Repository
@Mapper
public interface TicketProjectGuideDao {

    /**
     * 新增
     */
    int insert(TicketProjectGuide entity);

    /**
     * 更新
     */
    int update(TicketProjectGuide entity);

    /**
     * 删除
     */
    int delete(TicketProjectGuide entity);

    /**
     * 查询详情
     */
    TicketProjectGuide queryDetail(TicketProjectGuide entity);

    /**
     * 查询列表
     */
    List<TicketProjectGuide> queryList(HashMap<String, Object> reqMap);

}
