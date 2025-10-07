package com.centit.shopping.dao;

import com.centit.shopping.po.TicketRedeemActivityProject;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>兑换码活动与项目关联表<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2022-07-19
 **/
@Repository
@Mapper
public interface TicketRedeemActivityProjectDao {

    /**
     * 新增
     */
    int insert(TicketRedeemActivityProject entity);

    /**
     * 更新
     */
    int update(TicketRedeemActivityProject entity);

    /**
     * 删除
     */
    int delete(TicketRedeemActivityProject entity);

    /**
     * 查询详情
     */
    TicketRedeemActivityProject queryDetail(TicketRedeemActivityProject entity);

    /**
     * 查询列表
     */
    List<TicketRedeemActivityProject> queryList(HashMap<String, Object> reqMap);

    int deleteByProject(TicketRedeemActivityProject entity);

    int deleteByActivity(TicketRedeemActivityProject entity);
}
