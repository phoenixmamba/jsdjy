package com.centit.file.webmgr.dao;

import com.centit.file.webmgr.po.TicketProjectImg;
import java.util.HashMap;
import java.util.List;
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
     * 新增
     */
    int insert(TicketProjectImg entity);

    /**
     * 更新
     */
    int update(TicketProjectImg entity);

    /**
     * 删除
     */
    int delete(TicketProjectImg entity);

    /**
     * 查询详情
     */
    TicketProjectImg queryDetail(TicketProjectImg entity);

    /**
     * 查询列表
     */
    List<TicketProjectImg> queryList(HashMap<String, Object> reqMap);

}
