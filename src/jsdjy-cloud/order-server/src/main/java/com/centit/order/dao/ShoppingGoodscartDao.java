package com.centit.order.dao;

import com.centit.order.po.ShoppingGoodscartPo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/12/30 17:11
 **/
@Repository
@Mapper
public interface ShoppingGoodscartDao {
    /**
     * 新增
     */
    int insert(ShoppingGoodscartPo entity);

    /**
     * 更新
     */
    int update(ShoppingGoodscartPo entity);

    /**
     * 删除
     */
    int delete(ShoppingGoodscartPo entity);

    /**
     * 查询详情
     */
    ShoppingGoodscartPo queryDetail(ShoppingGoodscartPo entity);
}
