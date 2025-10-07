package com.centit.ticket.dao;

import com.centit.ticket.po.TicketProjectGoods;
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
public interface TicketProjectGoodsDao {

    /**
     * 新增
     */
    int insert(TicketProjectGoods entity);

    /**
     * 更新
     */
    int update(TicketProjectGoods entity);

    /**
     * 删除
     */
    int delete(TicketProjectGoods entity);

    /**
     * 查询详情
     */
    TicketProjectGoods queryDetail(TicketProjectGoods entity);

    /**
     * 查询列表
     */
    List<TicketProjectGoods> queryList(HashMap<String, Object> reqMap);

}
