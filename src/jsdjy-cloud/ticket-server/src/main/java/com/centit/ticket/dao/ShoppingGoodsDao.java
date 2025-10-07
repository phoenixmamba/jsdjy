package com.centit.ticket.dao;

import com.centit.ticket.po.ShoppingGoods;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-04-29
 **/
@Repository
@Mapper
public interface ShoppingGoodsDao {

    /**
     * 新增
     */
    int insert(ShoppingGoods entity);

    /**
     * 更新
     */
    int update(ShoppingGoods entity);

    /**
     * 删除
     */
    int delete(ShoppingGoods entity);

    /**
     * 查询详情
     */
    ShoppingGoods queryDetail(ShoppingGoods entity);

    /**
     * 查询列表
     */
    List<ShoppingGoods> queryList(HashMap<String, Object> reqMap);

}
