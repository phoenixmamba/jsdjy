package com.centit.shopping.dao;

import com.centit.shopping.po.TicketRedeemWatching;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>兑换项目观看人信息表<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2022-07-18
 **/
@Repository
@Mapper
public interface TicketRedeemWatchingDao {

    /**
     * 新增
     */
    int insert(TicketRedeemWatching entity);

    /**
     * 更新
     */
    int update(TicketRedeemWatching entity);

    /**
     * 删除
     */
    int delete(TicketRedeemWatching entity);

    /**
     * 查询详情
     */
    TicketRedeemWatching queryDetail(TicketRedeemWatching entity);

    /**
     * 查询列表
     */
    List<TicketRedeemWatching> queryList(HashMap<String, Object> reqMap);

}
