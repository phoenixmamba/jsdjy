package com.centit.shopping.dao;

import com.centit.shopping.po.TicketCouponExchangeRecord;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2022-07-04
 **/
@Repository
@Mapper
public interface TicketCouponExchangeRecordDao {

    /**
     * 新增
     */
    int insert(TicketCouponExchangeRecord entity);

    /**
     * 更新
     */
    int update(TicketCouponExchangeRecord entity);

    /**
     * 删除
     */
    int delete(TicketCouponExchangeRecord entity);

    /**
     * 查询详情
     */
    TicketCouponExchangeRecord queryDetail(TicketCouponExchangeRecord entity);

    /**
     * 查询列表
     */
    List<TicketCouponExchangeRecord> queryList(HashMap<String, Object> reqMap);

    int queryTotalCount(HashMap<String, Object> reqMap);
}
