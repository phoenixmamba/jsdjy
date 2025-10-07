package com.centit.shopping.dao;

import com.centit.shopping.po.TicketRedeemActivityBind;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>活动绑定兑换码记录<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2022-07-19
 **/
@Repository
@Mapper
public interface TicketRedeemActivityBindDao {

    /**
     * 新增
     */
    int insert(TicketRedeemActivityBind entity);

    /**
     * 更新
     */
    int update(TicketRedeemActivityBind entity);

    /**
     * 删除
     */
    int delete(TicketRedeemActivityBind entity);

    /**
     * 查询详情
     */
    TicketRedeemActivityBind queryDetail(TicketRedeemActivityBind entity);

    /**
     * 查询列表
     */
    List<TicketRedeemActivityBind> queryList(HashMap<String, Object> reqMap);
    int queryTotalCount(HashMap<String, Object> reqMap);
}
