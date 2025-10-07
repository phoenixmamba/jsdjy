package com.centit.shopping.dao;

import com.centit.shopping.po.TInvoiceTicket;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-11-08
 **/
@Repository
@Mapper
public interface TInvoiceTicketDao {

    /**
     * 新增
     */
    int insert(TInvoiceTicket entity);

    /**
     * 更新
     */
    int update(TInvoiceTicket entity);

    /**
     * 删除
     */
    int delete(TInvoiceTicket entity);

    /**
     * 查询详情
     */
    TInvoiceTicket queryDetail(TInvoiceTicket entity);

    /**
     * 查询列表
     */
    List<TInvoiceTicket> queryList(HashMap<String, Object> reqMap);

    int queryInvoiceOrderCount(HashMap<String, Object> reqMap);

    List<TInvoiceTicket> queryUserInvoiceTicketList(HashMap<String, Object> reqMap);
}
