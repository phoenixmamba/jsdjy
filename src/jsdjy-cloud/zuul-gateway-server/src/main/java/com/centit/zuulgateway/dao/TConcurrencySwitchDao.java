package com.centit.zuulgateway.dao;

import com.centit.zuulgateway.po.TConcurrencySwitch;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2023-06-05
 **/
@Repository
@Mapper
public interface TConcurrencySwitchDao {

    /**
     * 新增
     */
    int insert(TConcurrencySwitch entity);

    /**
     * 更新
     */
    int update(TConcurrencySwitch entity);

    /**
     * 删除
     */
    int delete(TConcurrencySwitch entity);

    /**
     * 查询详情
     */
    TConcurrencySwitch queryDetail(TConcurrencySwitch entity);

    /**
     * 查询列表
     */
    List<TConcurrencySwitch> queryList(HashMap<String, Object> reqMap);

}
