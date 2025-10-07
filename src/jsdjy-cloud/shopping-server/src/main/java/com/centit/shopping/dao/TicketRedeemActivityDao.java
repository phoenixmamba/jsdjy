package com.centit.shopping.dao;

import com.centit.shopping.po.TicketRedeemActivity;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>兑换码活动<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2022-07-19
 **/
@Repository
@Mapper
public interface TicketRedeemActivityDao {

    /**
     * 新增
     */
    int insert(TicketRedeemActivity entity);

    /**
     * 更新
     */
    int update(TicketRedeemActivity entity);

    /**
     * 删除
     */
    int delete(TicketRedeemActivity entity);

    /**
     * 查询详情
     */
    TicketRedeemActivity queryDetail(TicketRedeemActivity entity);

    /**
     * 查询列表
     */
    List<TicketRedeemActivity> queryList(HashMap<String, Object> reqMap);
    int queryTotalCount(HashMap<String, Object> reqMap);

}
