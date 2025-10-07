package com.centit.mallserver.redis.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.centit.core.consts.RedisConst;
import com.centit.core.consts.StoreConst;
import com.centit.core.enums.SellTypeEnum;
import com.centit.mallserver.dao.GoodsPhotoDao;
import com.centit.mallserver.dao.GoodsSpecInventoryDao;
import com.centit.mallserver.dao.ShoppingGoodsDao;
import com.centit.mallserver.dao.ShoppingSpecificationDao;
import com.centit.mallserver.model.CulturalGoodsDetail;
import com.centit.mallserver.po.GoodsPhotoPo;
import com.centit.mallserver.po.GoodsSpecInventoryPo;
import com.centit.mallserver.po.ShoppingGoodsPo;
import com.centit.mallserver.po.ShoppingSpecificationPo;
import com.centit.mallserver.redis.service.RedisDataService;
import com.centit.mallserver.threadPool.ThreadPoolExecutorFactory;
import com.centit.mallserver.model.CulturalGoodsInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 商品redis锁服务
 * @Date : 2024/12/13 16:09
 **/
@Slf4j
@Service
public class GoodsRedisLockManager extends BaseRedisLockManager {
    @Resource
    private RedisDataService redissonRedisDataService;
    @Resource
    private ShoppingGoodsDao goodsInfoDao;

    @Resource
    private GoodsSpecInventoryDao specInventoryDao;
    @Resource
    private GoodsPhotoDao goodsPhotoDao;

    @Resource
    private ShoppingSpecificationDao shoppingSpecificationDao;

    public Integer getGoodsStock(String goodsId) {
        Integer goodsStock = redissonRedisDataService.queryInt(RedisConst.KEY_STOCK_GOODS + goodsId);
        if (goodsStock == null) {
            String batchNo = RedisConst.KEY_STOCK_GOODS + goodsId;
            return executeWithLockAndReturn(batchNo, () -> {
                int stock = goodsInfoDao.selectGoodsStock(goodsId);
                redissonRedisDataService.setIntWithExpireTime(batchNo, stock, RedisConst.EXPIRE_MINUTES_STOCK_GOODS);
                return stock;
            });
        }
        return goodsStock;
    }

    /**
     * 获取商品详情
     * @param goodsId 商品id
     * @param userId 用户id
     * @return 商品信息
     */
    public CulturalGoodsInfo getGoodsInfo(String goodsId, String userId){
        CulturalGoodsInfo goodsInfoVo;
        String infoStr = redissonRedisDataService.queryStringInfo(RedisConst.KEY_INFO_GOODS +goodsId);
        if(StringUtils.isNotBlank(infoStr)){
            try{
                goodsInfoVo = JSONObject.parseObject(infoStr, CulturalGoodsInfo.class);
            }catch (Exception e){
                log.error("序列化从redis中查询出的商品信息异常，商品信息：{},异常信息：",infoStr,e);
                goodsInfoVo =updateGoodsInfo(goodsId);
            }
        }else {
            goodsInfoVo =updateGoodsInfo(goodsId);
        }
        CulturalGoodsDetail detail= goodsInfoVo.getDetail();
        detail.setGoodsInventory(getGoodsStock(goodsId));
        goodsInfoVo.setDetail(detail);
        //保存浏览历史到redis
        Optional.ofNullable(userId).ifPresent(uid->{
            addGoodsHistory(goodsId, SellTypeEnum.CULTURAL.getGoodsType(),uid);
        });
        return goodsInfoVo;
    }

    /**
     * 从数据库获取商品信息并更新到redis
     * @param goodsId
     * @return
     */
    public CulturalGoodsInfo updateGoodsInfo(String goodsId){
        CulturalGoodsInfo goodsInfoVo = new CulturalGoodsInfo();
        //查询商品主体信息
        ShoppingGoodsPo shoppingGoodsPo = goodsInfoDao.selectGoodsDetail(goodsId);
        if(StoreConst.GOODS_SALE_STATUS_OFF.equals(shoppingGoodsPo.getGoodsStatus())||StoreConst.GOODS_DELETE_STATUS_ON.equals(shoppingGoodsPo.getDeleteStatus())){
            goodsInfoVo.setIsOff(true);
        }else {
            goodsInfoVo.setIsOff(false);
        }
        CulturalGoodsDetail goodsDetail = new CulturalGoodsDetail(shoppingGoodsPo);
        //查询规格信息
        List<GoodsSpecInventoryPo> inventoryList = specInventoryDao.selectGoodsInventorys(goodsId);
        if(!inventoryList.isEmpty()){
            //计算最大和最小价格
            Optional<GoodsSpecInventoryPo> maxGoodsSpecPrice = inventoryList.stream().max(Comparator.comparing(GoodsSpecInventoryPo::getPrice));
            Optional<GoodsSpecInventoryPo> minGoodsSpecPrice = inventoryList.stream().min(Comparator.comparing(GoodsSpecInventoryPo::getPrice));
            goodsDetail.setMinPrice(minGoodsSpecPrice.get().getPrice());
            goodsDetail.setMaxPrice(maxGoodsSpecPrice.get().getPrice());
        }
        goodsInfoVo.setDetail(goodsDetail);
        goodsInfoVo.setInventoryDetails(inventoryList);
        //查询规格属性
        List<ShoppingSpecificationPo> specs = shoppingSpecificationDao.queryList(goodsId);
        goodsInfoVo.setSpecs(specs);
        //查询商品图片
        List<GoodsPhotoPo> photos = goodsPhotoDao.selectGoodsPhotos(goodsId);
        goodsInfoVo.setPhotos(photos);
        //异步更新数据到redis
        CompletableFuture.runAsync(()->{
            String infoStr = JSON.toJSONString(goodsInfoVo);
            redissonRedisDataService.setStringWithExpireTime(RedisConst.KEY_INFO_GOODS +goodsId,infoStr,RedisConst.EXPIRE_MINUTES_INFO_GOODS);
        }, ThreadPoolExecutorFactory.createThreadPoolExecutor());
        return goodsInfoVo;
    }

    /**
     * 异步保存浏览历史记录至redis
     * @param goodsId 商品id
     * @param goodsType 商品类型
     * @param userId 用户id
     */
    @Async
    public void addGoodsHistory(String goodsId,int goodsType,String userId){
        String key=RedisConst.KEY_HISTORY_MALL +userId;
        String value=goodsType+"_"+goodsId;
        long score=System.currentTimeMillis();
        redissonRedisDataService.addSortedSetValue(key,value,score);
    }
}
