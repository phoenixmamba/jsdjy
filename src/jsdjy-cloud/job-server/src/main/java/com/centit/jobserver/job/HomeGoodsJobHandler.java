package com.centit.jobserver.job;

import com.centit.jobserver.dao.HomeGoodsDao;
import com.centit.jobserver.po.HomeGoods;
import com.centit.jobserver.po.ShoppingRecommendPo;
import com.centit.jobserver.service.SlaveDataSourceService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/11/21 21:31
 **/
@Slf4j
@Component
public class HomeGoodsJobHandler {

    @Resource
    private HomeGoodsDao homeGoodsDao;
    @Resource
    private SlaveDataSourceService slaveDataSourceService;

    private final static String CULTURAL_TOP_CLASS_ID = "1";
    private final static String INTEGRAL_TOP_CLASS_ID = "2";
    private final static int HOT_GOODS_LIMIT = 10;
    private final static String DEFAULT_USERID = "default";
    private final static int GOODS_WEIGHT_RECOMMEND = 50;
    private final static int GOODS_WEIGHT_HOT = 20;

    @XxlJob("homeGoodsJobHandler")
    @Transactional(rollbackFor = Exception.class)
    public ReturnT<String> homeGoodsJobHandler() {
        log.info("定时任务-默认推荐商品更新开始执行...");
        try{
            List<HomeGoods> allGoods = fetchAllGoods();
            //查询后台推荐商品
            List<ShoppingRecommendPo> recommendGoods = slaveDataSourceService.queryRecommondList(null);
            //查询热门商品
            List<HomeGoods> hotGoods = fetchHotGoods();
            //处理权重
            buildWeight(allGoods, recommendGoods, hotGoods);
            homeGoodsDao.deleteByUser(DEFAULT_USERID);
            homeGoodsDao.insertHomeGoods(allGoods);
        }catch (Exception e){
            log.error("定时任务-默认推荐商品更新发送异常：",e);
            throw e;
        }
        log.info("定时任务-默认推荐商品更新执行完成");
        return ReturnT.SUCCESS;
    }

    private List<HomeGoods> fetchAllGoods() {
        HashMap<String, Object> reqMap = new HashMap<>(2);
        reqMap.put("culturalTopClassId", CULTURAL_TOP_CLASS_ID);
        reqMap.put("integralTopClassId", INTEGRAL_TOP_CLASS_ID);
        return slaveDataSourceService.queryAllList(reqMap);
    }

    private List<HomeGoods> fetchHotGoods() {
        HashMap<String, Object> reqMap = new HashMap<>(1);
        reqMap.put("limit", HOT_GOODS_LIMIT);
        return slaveDataSourceService.queryHotList(reqMap);
    }

    public void buildWeight(List<HomeGoods> allGoods, List<ShoppingRecommendPo> recommendGoods, List<HomeGoods> hotGoods) {
        Map<String,Boolean> recommendGoodIds = recommendGoods.stream().collect(Collectors.toMap(rec->rec.getGoodsId() + "_" + rec.getGoodsType(), k->Boolean.TRUE,(e,r) -> r));
        Map<String,Boolean> hotGoodIds = hotGoods.stream().collect(Collectors.toMap(hot->hot.getGoodsId() + "_" + hot.getType(), k->Boolean.TRUE,(e, r) ->r));

        //计算权重
        allGoods.forEach(goods -> {
            goods.setUserId(DEFAULT_USERID);
            String goodsIdAndType = goods.getGoodsId() + "_" + goods.getType();
            if (recommendGoodIds.containsKey(goodsIdAndType)) {
                goods.addWeight(GOODS_WEIGHT_RECOMMEND);
            }
            if (hotGoodIds.containsKey(goodsIdAndType)) {
                goods.addWeight(GOODS_WEIGHT_HOT);
            }
        });
    }
}
