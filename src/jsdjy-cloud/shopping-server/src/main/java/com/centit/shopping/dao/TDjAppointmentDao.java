package com.centit.shopping.dao;

import com.centit.shopping.po.TDjAppointment;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-07-14
 **/
@Repository
@Mapper
public interface TDjAppointmentDao {

    /**
     * 新增
     */
    int insert(TDjAppointment entity);

    /**
     * 更新
     */
    int update(TDjAppointment entity);

    /**
     * 删除
     */
    int delete(TDjAppointment entity);

    /**
     * 查询详情
     */
    TDjAppointment queryDetail(TDjAppointment entity);

    /**
     * 查询列表
     */
    List<TDjAppointment> queryList(HashMap<String, Object> reqMap);

    int queryTotalCount(HashMap<String, Object> reqMap);

}
