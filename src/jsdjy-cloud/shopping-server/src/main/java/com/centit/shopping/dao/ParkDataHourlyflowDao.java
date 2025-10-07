package com.centit.shopping.dao;

import com.centit.shopping.po.ParkDataHourlyflow;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * <p>车场每小时的车流数<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2023-10-12
 **/
@Repository
@Mapper
public interface ParkDataHourlyflowDao {

    /**
     * 新增
     */
    int insert(ParkDataHourlyflow entity);

    /**
     * 更新
     */
    int update(ParkDataHourlyflow entity);

    /**
     * 删除
     */
    int delete(ParkDataHourlyflow entity);

    /**
     * 查询详情
     */
    ParkDataHourlyflow queryDetail(ParkDataHourlyflow entity);

    /**
     * 查询列表
     */
    List<ParkDataHourlyflow> queryList(HashMap<String, Object> reqMap);

    int mergeInto(ParkDataHourlyflow entity);
}
