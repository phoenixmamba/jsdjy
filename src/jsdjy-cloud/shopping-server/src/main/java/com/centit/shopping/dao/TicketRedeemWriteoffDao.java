package com.centit.shopping.dao;

import com.centit.shopping.po.TicketRedeemWriteoff;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2022-07-22
 **/
@Repository
@Mapper
public interface TicketRedeemWriteoffDao {

    /**
     * 新增
     */
    int insert(TicketRedeemWriteoff entity);

    /**
     * 更新
     */
    int update(TicketRedeemWriteoff entity);

    /**
     * 删除
     */
    int delete(TicketRedeemWriteoff entity);

    /**
     * 查询详情
     */
    TicketRedeemWriteoff queryDetail(TicketRedeemWriteoff entity);

    /**
     * 查询列表
     */
    List<TicketRedeemWriteoff> queryList(HashMap<String, Object> reqMap);

}
