package com.centit.shopping.biz.utils;

import com.centit.shopping.dao.*;
import com.centit.shopping.utils.StringUtil;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2023/8/30 16:57
 **/
@Component
public class DBServiceImpl implements DBService{
    @Resource
    private ShoppingGoodsInventoryDao shoppingGoodsInventoryDao;
    @Resource
    private ShoppingGoodsDao shoppingGoodsDao;
    @Resource
    private ShoppingArtactivityInventoryDao shoppingArtactivityInventoryDao;
    @Resource
    private ShoppingArtactivityDao shoppingArtactivityDao;
    @Resource
    private ShoppingArtplanInventoryDao shoppingArtplanInventoryDao;
    @Resource
    private ShoppingArtplanDao shoppingArtplanDao;

    /**
     * 数据库扣减商城商品库存
     * @param goodsId 商品id
     * @param propertys 规格
     * @param cutCount 扣减数量
     */
    @Transactional
    public void cutDBGoodsInventory(String goodsId, String propertys, int cutCount){
        HashMap<String, Object> cutMap = new HashMap<>();
        if(StringUtil.isNotNull(propertys)){
            cutMap.put("goodsId",goodsId);
            cutMap.put("propertys",propertys);
            cutMap.put("cutCount",cutCount);
            shoppingGoodsInventoryDao.cutInventory(cutMap);
        }
        cutMap = new HashMap<>();
        cutMap.put("goodsId",goodsId);
        cutMap.put("cutInventory",cutCount);
        shoppingGoodsDao.cutGoodsInventory(cutMap);
    }

    /**
     * 数据库增加商城商品库存
     * @param goodsId  商品id
     * @param propertys  规格
     * @param addCount  增加数量
     */
    @Transactional
    public void addDBGoodsInventory(String goodsId, String propertys, int addCount){
        HashMap<String, Object> addMap = new HashMap<>();
        if(StringUtil.isNotNull(propertys)){
            addMap.put("goodsId",goodsId);
            addMap.put("propertys",propertys);
            addMap.put("addCount",addCount);
            shoppingGoodsInventoryDao.addInventory(addMap);
        }
        addMap = new HashMap<>();
        addMap.put("goodsId",goodsId);
        addMap.put("addInventory",addCount);
        shoppingGoodsDao.addGoodsInventory(addMap);
    }

    /**
     * 数据库扣减会员活动库存
     * @param actId  活动Id
     * @param propertys  规格
     * @param cutCount  扣减数量
     */
    @Transactional
    public void cutDBActInventory(String actId, String propertys, int cutCount){
        HashMap<String, Object> cutMap = new HashMap<>();
        if(StringUtil.isNotNull(propertys)){
            cutMap.put("activityId",actId);
            cutMap.put("propertys",propertys);
            cutMap.put("cutCount",cutCount);
            shoppingArtactivityInventoryDao.cutInventory(cutMap);
        }
        cutMap = new HashMap<>();
        cutMap.put("id",actId);
        cutMap.put("cutnum",cutCount);
        shoppingArtactivityDao.updateActivityCutNum(cutMap);
    }

    /**
     * 数据库增加会员活动库存
     * @param actId  活动id
     * @param propertys  规格
     * @param addCount  增加数量
     */
    @Transactional
    public void addDBActInventory(String actId, String propertys, int addCount){
        HashMap<String, Object> addMap = new HashMap<>();
        if(StringUtil.isNotNull(propertys)){
            addMap.put("activityId",actId);
            addMap.put("propertys",propertys);
            addMap.put("addCount",addCount);
            shoppingArtactivityInventoryDao.addInventory(addMap);
        }
        addMap = new HashMap<>();
        addMap.put("id",actId);
        addMap.put("addnum",addCount);
        shoppingArtactivityDao.updateActivityAddNum(addMap);
    }

    /**
     * 数据库扣减爱艺计划库存
     * @param actId  活动Id
     * @param propertys  规格
     * @param cutCount  扣减数量
     */
    @Transactional
    public void cutDBPlanInventory(String actId, String propertys, int cutCount){
        HashMap<String, Object> cutMap = new HashMap<>();
        if(StringUtil.isNotNull(propertys)){
            cutMap.put("activityId",actId);
            cutMap.put("propertys",propertys);
            cutMap.put("cutCount",cutCount);
            shoppingArtplanInventoryDao.cutInventory(cutMap);
        }
        cutMap = new HashMap<>();
        cutMap.put("id",actId);
        cutMap.put("cutnum",cutCount);
        shoppingArtplanDao.updatePlanCutNum(cutMap);
    }

    /**
     * 数据库增加爱艺计划库存
     * @param actId  活动id
     * @param propertys  规格
     * @param addCount  增加数量
     */
    @Transactional
    public void addDBPlanInventory(String actId, String propertys, int addCount){
        HashMap<String, Object> addMap = new HashMap<>();
        if(StringUtil.isNotNull(propertys)){
            addMap.put("activityId",actId);
            addMap.put("propertys",propertys);
            addMap.put("addCount",addCount);
            shoppingArtplanInventoryDao.addInventory(addMap);
        }
        addMap = new HashMap<>();
        addMap.put("id",actId);
        addMap.put("addnum",addCount);
        shoppingArtplanDao.updatePlanAddNum(addMap);
    }
}
