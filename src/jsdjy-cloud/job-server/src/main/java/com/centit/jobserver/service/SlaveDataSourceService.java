package com.centit.jobserver.service;

import com.centit.jobserver.po.ArtPushPo;
import com.centit.jobserver.po.EarlywarningPo;
import com.centit.jobserver.po.HomeGoods;
import com.centit.jobserver.po.ShoppingRecommendPo;

import java.util.HashMap;
import java.util.List;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/11/28 15:55
 **/
public interface SlaveDataSourceService {
    List<ArtPushPo> selectToPushActivitys(HashMap<String, Object> reqMap);

    List<ArtPushPo> selectToPushPlans(HashMap<String, Object> reqMap);

    List<String> selectArtactivityUserMobiles(String activityId,Integer cartType);

    List<EarlywarningPo> selectList(HashMap<String,Object> reqMap);

    List<HomeGoods> queryAllList(HashMap<String,Object> reqMap);

    List<ShoppingRecommendPo> queryRecommondList(HashMap<String,Object> reqMap);

    List<HomeGoods> queryHotList(HashMap<String,Object> reqMap);
}
