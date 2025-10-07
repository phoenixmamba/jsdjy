package com.centit.shopping.dao;

import com.centit.shopping.po.TicketRedeemEvent;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2022-07-19
 **/
@Repository
@Mapper
public interface TicketRedeemEventDao {

    /**
     * 新增
     */
    int insert(TicketRedeemEvent entity);

    /**
     * 更新
     */
    int update(TicketRedeemEvent entity);

    /**
     * 删除
     */
    int delete(TicketRedeemEvent entity);

    /**
     * 查询详情
     */
    TicketRedeemEvent queryDetail(TicketRedeemEvent entity);

    /**
     * 查询列表
     */
    List<TicketRedeemEvent> queryList(HashMap<String, Object> reqMap);

    List<TicketRedeemEvent> queryPageList(HashMap<String, Object> reqMap);

    int queryTotalCount(HashMap<String, Object> reqMap);

    List<TicketRedeemEvent> queryProjectEvents(HashMap<String, Object> reqMap);

}
