package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingTransport;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-02-23
 **/
@Repository
@Mapper
public interface ShoppingTransportDao {

    /**
     * 新增
     */
    int insert(ShoppingTransport entity);

    /**
     * 更新
     */
    int update(ShoppingTransport entity);

    /**
     * 删除
     */
    int delete(ShoppingTransport entity);

    /**
     * 查询详情
     */
    ShoppingTransport queryDetail(ShoppingTransport entity);

    /**
     * 查询列表
     */
    List<ShoppingTransport> queryList(HashMap<String, Object> reqMap);

    /**
     * 列表数量
     */
    int queryTotalCount(HashMap<String, Object> reqMap);

    BigDecimal queryAreaPrice(String areaCode);
}
