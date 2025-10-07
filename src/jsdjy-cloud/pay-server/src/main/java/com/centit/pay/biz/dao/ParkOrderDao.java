package com.centit.pay.biz.dao;

import com.centit.pay.biz.po.ParkOrder;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-03-10
 **/
@Repository
@Mapper
public interface ParkOrderDao {

    /**
     * 新增
     */
    int insert(ParkOrder entity);

    /**
     * 更新
     */
    int update(ParkOrder entity);

    /**
     * 删除
     */
    int delete(ParkOrder entity);

    /**
     * 查询详情
     */
    ParkOrder queryDetail(ParkOrder entity);

    /**
     * 查询列表
     */
    List<ParkOrder> queryList(HashMap<String, Object> reqMap);

}
