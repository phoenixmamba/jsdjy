package com.centit.ticket.dao;

import com.centit.ticket.po.TicketAreacode;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-04-19
 **/
@Repository
@Mapper
public interface TicketAreacodeDao {

    /**
     * 新增
     */
    int insert(TicketAreacode entity);

    /**
     * 查询列表
     */
    List<TicketAreacode> queryList(HashMap<String, Object> reqMap);

}
