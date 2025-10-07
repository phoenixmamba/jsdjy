package com.centit.shopping.dao;

import java.util.HashMap;
import java.util.List;

import com.centit.shopping.po.HomeGoods;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-04-27
 **/
@Repository
@Mapper
public interface ShoppingHomeGoodsDao {

    /**
     * 删除
     */
    int delete(String userId);

    /**
     * 查询列表
     */
    List<HomeGoods> queryList(HashMap<String, Object> reqMap);

    int queryTotalCount(HashMap<String, Object> reqMap);

    int insertHomeGoods(List<HomeGoods> homeGoodsList);
}
