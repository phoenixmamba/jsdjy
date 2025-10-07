package com.centit.jobserver.dao;

import com.centit.jobserver.po.HomeGoods;
import com.centit.jobserver.po.ShoppingRecommendPo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : Dao接口
 * @Date : 2021-04-26
 **/
@Repository
@Mapper
public interface HomeGoodsDao {
    List<HomeGoods> queryAllList(HashMap<String,Object> reqMap);

    List<ShoppingRecommendPo> queryRecommondList(HashMap<String,Object> reqMap);

    List<HomeGoods> queryHotList(HashMap<String,Object> reqMap);

    int  deleteByUser(String userId);

    int insertHomeGoods(List<HomeGoods> homeGoodsList);
}
