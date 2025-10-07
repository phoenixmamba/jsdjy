package com.centit.shopping.dao;

import com.centit.shopping.po.TOnOndemandhistory;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-04-21
 **/
@Repository
@Mapper
public interface TOnOndemandhistoryDao {

    /**
     * 新增
     */
    int insert(TOnOndemandhistory entity);

    /**
     * 更新
     */
    int update(TOnOndemandhistory entity);

    /**
     * 删除
     */
    int delete(TOnOndemandhistory entity);

    /**
     * 查询详情
     */
    TOnOndemandhistory queryDetail(TOnOndemandhistory entity);

    /**
     * 查询列表
     */
    List<TOnOndemandhistory> queryList(HashMap<String, Object> reqMap);

}
