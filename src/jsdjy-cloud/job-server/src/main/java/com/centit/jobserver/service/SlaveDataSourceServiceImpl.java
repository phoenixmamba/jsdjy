package com.centit.jobserver.service;

import com.centit.jobserver.config.db.DS;
import com.centit.jobserver.dao.ArtPushDao;
import com.centit.jobserver.dao.EarlywarningDao;
import com.centit.jobserver.dao.HomeGoodsDao;
import com.centit.jobserver.po.ArtPushPo;
import com.centit.jobserver.po.EarlywarningPo;
import com.centit.jobserver.po.HomeGoods;
import com.centit.jobserver.po.ShoppingRecommendPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/11/28 15:57
 **/
@Slf4j
@Service
public class SlaveDataSourceServiceImpl implements SlaveDataSourceService{
    @Resource
    private ArtPushDao artPushDao;

    @Resource
    private EarlywarningDao earlywarningDao;
    @Resource
    private HomeGoodsDao homeGoodsDao;

    @Override
    @DS("slaveDataSource")
    public List<ArtPushPo> selectToPushActivitys(HashMap<String, Object> reqMap) {
        return artPushDao.selectToPushActivitys(reqMap);
    }

    @Override
    @DS("slaveDataSource")
    public List<ArtPushPo> selectToPushPlans(HashMap<String, Object> reqMap) {
        return artPushDao.selectToPushPlans(reqMap);
    }

    @Override
    @DS("slaveDataSource")
    public List<String> selectArtactivityUserMobiles(String activityId,Integer cartType) {
        return artPushDao.selectArtactivityUserMobiles(activityId,cartType);
    }

    @Override
    @DS("slaveDataSource")
    public List<EarlywarningPo> selectList(HashMap<String,Object> reqMap){
        return earlywarningDao.selectList(reqMap);
    }

    @Override
    @DS("slaveDataSource")
    public List<HomeGoods> queryAllList(HashMap<String, Object> reqMap) {
        return homeGoodsDao.queryAllList(reqMap);
    }

    @Override
    @DS("slaveDataSource")
    public List<ShoppingRecommendPo> queryRecommondList(HashMap<String, Object> reqMap) {
        return homeGoodsDao.queryRecommondList(reqMap);
    }

    @Override
    @DS("slaveDataSource")
    public List<HomeGoods> queryHotList(HashMap<String, Object> reqMap) {
        return homeGoodsDao.queryHotList(reqMap);
    }
}
