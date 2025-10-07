package com.centit.shopping.dao;

import com.centit.shopping.po.TOnOndemand;
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
public interface TOnOndemandDao {

    /**
     * 新增
     */
    int insert(TOnOndemand entity);

    /**
     * 更新
     */
    int update(TOnOndemand entity);

    /**
     * 删除
     */
    int delete(TOnOndemand entity);

    /**
     * 查询详情
     */
    TOnOndemand queryDetail(TOnOndemand entity);

    /**
     * 查询列表
     */
    List<TOnOndemand> queryList(HashMap<String, Object> reqMap);

}
