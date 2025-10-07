package com.centit.shopping.dao;

import com.centit.shopping.po.TicketRedeemBatch;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>兑换码创建批次<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2022-07-18
 **/
@Repository
@Mapper
public interface TicketRedeemBatchDao {

    /**
     * 新增
     */
    int insert(TicketRedeemBatch entity);

    /**
     * 更新
     */
    int update(TicketRedeemBatch entity);

    /**
     * 删除
     */
    int delete(TicketRedeemBatch entity);

    /**
     * 查询详情
     */
    TicketRedeemBatch queryDetail(TicketRedeemBatch entity);

    /**
     * 查询列表
     */
    List<TicketRedeemBatch> queryList(HashMap<String, Object> reqMap);
    int queryTotalCount(HashMap<String, Object> reqMap);
}
