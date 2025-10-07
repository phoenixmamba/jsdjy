package com.centit.shopping.dao;

import com.centit.shopping.po.TicketRedeemProjectWatchingNotice;
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
public interface TicketRedeemProjectWatchingNoticeDao {

    /**
     * 新增
     */
    int insert(TicketRedeemProjectWatchingNotice entity);

    /**
     * 更新
     */
    int update(TicketRedeemProjectWatchingNotice entity);

    /**
     * 删除
     */
    int delete(TicketRedeemProjectWatchingNotice entity);

    /**
     * 查询详情
     */
    TicketRedeemProjectWatchingNotice queryDetail(TicketRedeemProjectWatchingNotice entity);

    /**
     * 查询列表
     */
    List<TicketRedeemProjectWatchingNotice> queryList(HashMap<String, Object> reqMap);

}
