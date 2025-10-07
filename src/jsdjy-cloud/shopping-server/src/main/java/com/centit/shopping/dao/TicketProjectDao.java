package com.centit.shopping.dao;

import com.centit.shopping.po.TicketProject;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-04-17
 **/
@Repository
@Mapper
public interface TicketProjectDao {

    /**
     * 新增
     */
    int insert(TicketProject entity);

    /**
     * 更新
     */
    int update(TicketProject entity);

    /**
     * 删除
     */
    int delete(TicketProject entity);

    /**
     * 查询详情
     */
    TicketProject queryDetail(TicketProject entity);

    /**
     * 查询列表
     */
    List<TicketProject> queryList(HashMap<String, Object> reqMap);

    int queryListCount(HashMap<String, Object> reqMap);

    List<TicketProject> queryInvoiceProjectList(HashMap<String, Object> reqMap);

}
