package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingIntegralTotal;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-09-28
 **/
@Repository
@Mapper
public interface ShoppingIntegralTotalDao {

    /**
     * 新增
     */
    int insert(ShoppingIntegralTotal entity);

    /**
     * 更新
     */
    int update(ShoppingIntegralTotal entity);

    /**
     * 删除
     */
    int delete(ShoppingIntegralTotal entity);

    /**
     * 查询详情
     */
    ShoppingIntegralTotal queryDetail(ShoppingIntegralTotal entity);

    /**
     * 查询列表
     */
    List<ShoppingIntegralTotal> queryList(HashMap<String, Object> reqMap);

}
