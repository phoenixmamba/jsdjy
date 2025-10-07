package com.centit.admin.system.dao;

import com.centit.admin.system.po.FOptdef;

import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-03-03
 **/
@Repository
@Mapper
public interface FOptdefDao {

    /**
     * 新增
     */
    int insert(FOptdef entity);

    /**
     * 更新
     */
    int update(FOptdef entity);

    /**
     * 删除
     */
    int delete(FOptdef entity);

    /**
     * 查询详情
     */
    FOptdef queryDetail(FOptdef entity);

    /**
     * 查询列表
     */
    List<FOptdef> queryList(HashMap<String, Object> reqMap);

}
