package com.centit.ticket.dao;

import com.centit.ticket.po.TicketProjectSponsor;
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
public interface TicketProjectSponsorDao {

    /**
     * 新增
     */
    int insert(TicketProjectSponsor entity);

    /**
     * 更新
     */
    int update(TicketProjectSponsor entity);

    /**
     * 删除
     */
    int delete(TicketProjectSponsor entity);

    /**
     * 查询详情
     */
    TicketProjectSponsor queryDetail(TicketProjectSponsor entity);

    /**
     * 查询列表
     */
    List<TicketProjectSponsor> queryList(HashMap<String, Object> reqMap);

}
