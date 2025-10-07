package com.centit.ticket.dao;

import com.centit.ticket.po.TicketClassPhoto;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-05-11
 **/
@Repository
@Mapper
public interface TicketClassPhotoDao {

    /**
     * 新增
     */
    int insert(TicketClassPhoto entity);

    /**
     * 更新
     */
    int update(TicketClassPhoto entity);

    /**
     * 删除
     */
    int delete(TicketClassPhoto entity);

    /**
     * 查询详情
     */
    TicketClassPhoto queryDetail(TicketClassPhoto entity);

    /**
     * 查询列表
     */
    List<TicketClassPhoto> queryList(HashMap<String, Object> reqMap);

}
