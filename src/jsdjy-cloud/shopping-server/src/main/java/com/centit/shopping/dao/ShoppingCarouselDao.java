package com.centit.shopping.dao;

import com.centit.shopping.po.ShoppingCarousel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.centit.shopping.po.ShoppingGoods;
import com.centit.shopping.po.TicketProject;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-04-19
 **/
@Repository
@Mapper
public interface ShoppingCarouselDao {

    /**
     * 新增
     */
    int insert(ShoppingCarousel entity);

    /**
     * 更新
     */
    int update(ShoppingCarousel entity);

    /**
     * 删除
     */
    int delete(ShoppingCarousel entity);

    /**
     * 查询详情
     */
    ShoppingCarousel queryDetail(ShoppingCarousel entity);

    /**
     * 查询列表
     */
    List<ShoppingCarousel> queryList(HashMap<String, Object> reqMap);

    List<Map> queryAllTicket(HashMap<String, Object> reqMap);

    int queryCount(HashMap reqMap);

    List<Map> queryClassGoodsList(HashMap<String, Object> reqMap);
}
