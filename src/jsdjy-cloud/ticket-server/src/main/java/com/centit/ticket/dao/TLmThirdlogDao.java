package com.centit.ticket.dao;

import com.centit.ticket.po.TLmThirdlog;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-07-05
 **/
@Repository
@Mapper
public interface TLmThirdlogDao {

    /**
     * 新增
     */
    int insert(TLmThirdlog entity);

    /**
     * 更新
     */
    int update(TLmThirdlog entity);

    /**
     * 删除
     */
    int delete(TLmThirdlog entity);

    /**
     * 查询详情
     */
    TLmThirdlog queryDetail(TLmThirdlog entity);

    /**
     * 查询列表
     */
    List<TLmThirdlog> queryList(HashMap<String, Object> reqMap);

}
