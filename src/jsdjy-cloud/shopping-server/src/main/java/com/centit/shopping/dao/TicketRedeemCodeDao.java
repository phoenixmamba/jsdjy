package com.centit.shopping.dao;

import com.centit.shopping.po.TicketRedeemCode;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>兑换码详细表<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2022-07-18
 **/
@Repository
@Mapper
public interface TicketRedeemCodeDao {

    /**
     * 新增
     */
    int insert(TicketRedeemCode entity);

    /**
     * 更新
     */
    int update(TicketRedeemCode entity);

    /**
     * 删除
     */
    int delete(TicketRedeemCode entity);

    /**
     * 查询详情
     */
    TicketRedeemCode queryDetail(TicketRedeemCode entity);

    /**
     * 查询列表
     */
    List<TicketRedeemCode> queryList(HashMap<String, Object> reqMap);
    int queryTotalCount(HashMap<String, Object> reqMap);

    /**
     * 查询最大编号的兑换码
     */
    TicketRedeemCode queryLargestCode(HashMap<String, Object> reqMap);

    /**
     * 查询最小编号的兑换码
     */
    TicketRedeemCode querySmallestCode(HashMap<String, Object> reqMap);

    int queryFreeCodeCount(HashMap<String, Object> reqMap);

    /**
     * 按批次删除兑换码
     */
    int deleteByBatchId(TicketRedeemCode entity);
    /**
     * 将兑换码与活动绑定
     */
    int bindCode(HashMap<String, Object> reqMap);

    /**
     * 按活动删除兑换码
     */
    int deleteCodeByActivity(HashMap<String, Object> reqMap);

    /**
     * 按绑定记录删除兑换码
     */
    int deleteCodeByBindId(HashMap<String, Object> reqMap);

    /**
     * 批量删除兑换码
     */
    int deleteCodes(HashMap<String, Object> reqMap);

    List<TicketRedeemCode> queryMyCodeList(HashMap<String, Object> reqMap);
    int queryMyCodeTotalCount(HashMap<String, Object> reqMap);
}
