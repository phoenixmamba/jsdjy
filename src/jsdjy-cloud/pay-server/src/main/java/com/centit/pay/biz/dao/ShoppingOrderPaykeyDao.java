package com.centit.pay.biz.dao;

import com.centit.pay.biz.po.ShoppingOrderPaykey;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-03-25
 **/
@Repository
@Mapper
public interface ShoppingOrderPaykeyDao {

    /**
     * 新增
     */
    int insert(ShoppingOrderPaykey entity);

    /**
     * 更新
     */
    int update(ShoppingOrderPaykey entity);

    /**
     * 删除
     */
    int delete(ShoppingOrderPaykey entity);

    /**
     * 查询详情
     */
    ShoppingOrderPaykey queryDetail(ShoppingOrderPaykey entity);

    /**
     * 查询列表
     */
    List<ShoppingOrderPaykey> queryList(HashMap<String, Object> reqMap);

}
