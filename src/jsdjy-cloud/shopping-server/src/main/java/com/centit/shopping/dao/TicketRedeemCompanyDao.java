package com.centit.shopping.dao;

import com.centit.shopping.po.TicketRedeemCompany;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>发卡单位<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2022-07-18
 **/
@Repository
@Mapper
public interface TicketRedeemCompanyDao {

    /**
     * 新增
     */
    int insert(TicketRedeemCompany entity);

    /**
     * 更新
     */
    int update(TicketRedeemCompany entity);

    /**
     * 删除
     */
    int delete(TicketRedeemCompany entity);

    /**
     * 查询详情
     */
    TicketRedeemCompany queryDetail(TicketRedeemCompany entity);

    /**
     * 查询列表
     */
    List<TicketRedeemCompany> queryList(HashMap<String, Object> reqMap);
    int queryTotalCount(HashMap<String, Object> reqMap);

}
