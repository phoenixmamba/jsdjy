package com.centit.shopping.dao;

import com.centit.shopping.po.TicketRedeemProject;
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
public interface TicketRedeemProjectDao {

    /**
     * 新增
     */
    int insert(TicketRedeemProject entity);

    /**
     * 更新
     */
    int update(TicketRedeemProject entity);

    /**
     * 删除
     */
    int delete(TicketRedeemProject entity);

    /**
     * 查询详情
     */
    TicketRedeemProject queryDetail(TicketRedeemProject entity);

    /**
     * 查询列表
     */
    List<TicketRedeemProject> queryList(HashMap<String, Object> reqMap);

    int queryTotalCount(HashMap<String, Object> reqMap);
}
