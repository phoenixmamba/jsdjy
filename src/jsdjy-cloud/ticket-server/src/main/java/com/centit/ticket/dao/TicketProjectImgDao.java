package com.centit.ticket.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2023-12-20
 **/
@Repository
@Mapper
public interface TicketProjectImgDao {

    /**
     * 查询列表
     */
    String queryImgId(String projectImgUrl);

}
