package com.centit.ticket.dao;

import com.centit.ticket.po.TicketFavorite;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-05-14
 **/
@Repository
@Mapper
public interface TicketFavoriteDao {

    /**
     * 新增
     */
    int insert(TicketFavorite entity);

    /**
     * 更新
     */
    int update(TicketFavorite entity);

    /**
     * 删除
     */
    int delete(TicketFavorite entity);

    /**
     * 查询详情
     */
    TicketFavorite queryDetail(TicketFavorite entity);

    /**
     * 查询列表
     */
    List<TicketFavorite> queryList(HashMap<String, Object> reqMap);

    int queryTotalCount(HashMap<String, Object> reqMap);

    /**
     * 取消收藏
     */
    int cancelFav(TicketFavorite entity);

}
