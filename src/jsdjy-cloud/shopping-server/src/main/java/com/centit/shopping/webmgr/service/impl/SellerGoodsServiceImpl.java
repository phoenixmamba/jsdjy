package com.centit.shopping.webmgr.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.dao.*;
import com.centit.shopping.po.*;
import com.centit.shopping.redis.RedisStockService;
import com.centit.shopping.utils.CommonUtil;
import com.centit.shopping.webmgr.service.SellerGoodsService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务实现类
 * @Date : 2021-02-21
 **/
@Transactional
@Service
public class SellerGoodsServiceImpl implements SellerGoodsService {
    public static final Log log = LogFactory.getLog(SellerGoodsService.class);

    @Resource
    private ShoppingGoodsDao shoppingGoodsDao;
    @Resource
    private ShoppingGoodsclassDao shoppingGoodsclassDao;
    @Resource
    private ShoppingGoodsSpecDao shoppingGoodsSpecDao;
    @Resource
    private ShoppingGoodsspecificationDao shoppingGoodsspecificationDao;
    @Resource
    private ShoppingGoodsspecpropertyDao shoppingGoodsspecpropertyDao;
    @Resource
    private ShoppingGoodsPhotoDao shoppingGoodsPhotoDao;
    @Resource
    private ShoppingGoodstypeSpecDao shoppingGoodstypeSpecDao;

    @Resource
    private ShoppingGoodsInventoryDao shoppingGoodsInventoryDao;
    @Resource
    private ShoppingGoodscartDao shoppingGoodscartDao;

    /**
     * 配置库存Redis缓存Key前缀
     */
    public static final String REDIS_KEY_GOODS = "REDIS_KEY:STOCK:GOODS";
//    public static final String REDIS_KEY_GOODS = "GOODS";
    @Resource
    private RedisStockService redisStockService;

    /**
     * 查询商户商品列表
     */
    @Override
    public JSONObject queryPageList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            int pageNo =reqJson.get("pageNo")==null?1:reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize")==null?10:reqJson.getInteger("pageSize");
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("startRow", (pageNo-1)*pageSize);
            reqMap.put("pageSize", pageSize);

            reqMap.put("deleteStatus","0");
            //所属商户，默认官方商户
            reqMap.put("goodsStoreId", CommonUtil.getSystemStore().getId());

            bizDataJson.put("total",shoppingGoodsDao.queryTotalCount(reqMap));
            List<ShoppingGoods> goodsList = shoppingGoodsDao.queryList(reqMap);
            for(ShoppingGoods shoppingGoods:goodsList){
                reqMap.clear();
                reqMap.put("goodsId",shoppingGoods.getId());
                shoppingGoods.setSoldCount(shoppingGoodscartDao.queryGoodsSoldCount(reqMap));
            }
            bizDataJson.put("objList",goodsList);

            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 校验商品名称是否已存在
     */
    @Override
    public JSONObject checkGoodsName(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {

            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("deleteStatus","0");
            int count = shoppingGoodsDao.checkGoodsName(reqMap);
            if(count>0){
                bizDataJson.put("res",true);
            }else{
                bizDataJson.put("res",false);
            }

            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * @Description 上/下架商品
     **/
    @Override
    public JSONObject putGoods(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "0";
        String retMsg = "操作成功！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String id=reqJson.getString("id");
            String[] ids = id.split(",");
            for(int i=0;i<ids.length;i++){
                ShoppingGoods shoppingGoods = new ShoppingGoods();
                shoppingGoods.setId(ids[i]);
                shoppingGoods.setGoodsStatus(reqJson.getString("goodsStatus"));
                shoppingGoodsDao.updateStatus(shoppingGoods);
            }

        } catch (Exception e) {
            retCode = "1";
            retMsg = "操作失败！";
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * @Description 删除商品
     **/
    @Override
    public JSONObject delGoods(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "0";
        String retMsg = "操作成功！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingGoods shoppingGoods = JSON.parseObject(reqJson.toJSONString(), ShoppingGoods.class);
            shoppingGoods.setDeleteStatus("1");
            shoppingGoodsDao.update(shoppingGoods);
        } catch (Exception e) {
            retCode = "1";
            retMsg = "操作失败！";
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 查询商品分类列表
     */
    @Override
    public JSONObject queryGoodsClass(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("deleteStatus","0");
            bizDataJson.put("objList",shoppingGoodsclassDao.queryPageList(reqMap));

            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 获取商品详情
     */
    @Override
    public JSONObject goodsDetail(String goodsId) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingGoods shoppingGoods = new ShoppingGoods();
            shoppingGoods.setId(goodsId);
            //查询商品主体信息
            shoppingGoods = shoppingGoodsDao.queryDetail(shoppingGoods);
            String key = REDIS_KEY_GOODS + goodsId;
            if(redisStockService.checkGoods(key)){
                shoppingGoods.setGoodsInventory(redisStockService.currentStock(key));
            }else{
                redisStockService.initStock(key,shoppingGoods.getGoodsInventory());
            }

            //商品规格信息
            HashMap<String, Object> reqMap =new HashMap<>();
            reqMap.put("goodsId",goodsId);
//            List<ShoppingGoodsspecification> specs =shoppingGoodsspecificationDao.queryGoodsSpecs(reqMap);
//            String goodsInventoryDetail= shoppingGoods.getGoodsInventoryDetail();
            JSONArray inventoryDetailArray = new JSONArray();
            HashSet<String> propertyIds = new HashSet<>();
            List<ShoppingGoodsInventory> inventoryList= shoppingGoodsInventoryDao.queryList(reqMap);
            if(!inventoryList.isEmpty()){
                for(int i=0;i<inventoryList.size();i++){
                    ShoppingGoodsInventory shoppingGoodsInventory = inventoryList.get(i);

                    JSONObject obj = new JSONObject();
                    obj.put("id",shoppingGoodsInventory.getPropertys());
//                    obj.put("count",shoppingGoodsInventory.getCount().toString());
                    key = REDIS_KEY_GOODS + goodsId+shoppingGoodsInventory.getPropertys();
                    if(redisStockService.checkGoods(key)){
                        obj.put("count",redisStockService.currentStock(key));
                    }else{
                        obj.put("count",shoppingGoodsInventory.getCount().toString());
                        redisStockService.initStock(key,shoppingGoodsInventory.getCount());
                    }
                    obj.put("price",shoppingGoodsInventory.getPrice().toString());
                    String[] sps= shoppingGoodsInventory.getPropertys().split("_");
                    String valueStr="";
                    for(int m=0;m<sps.length;m++){
                        String propertyId = sps[m];
                        propertyIds.add(propertyId);
                        ShoppingGoodsspecproperty goodsspecproperty = new ShoppingGoodsspecproperty();
                        goodsspecproperty.setId(propertyId);
                        goodsspecproperty = shoppingGoodsspecpropertyDao.queryDetail(goodsspecproperty);
                        valueStr+=goodsspecproperty.getValue()+"_";
                        obj.put("value",valueStr);
                    }
                    inventoryDetailArray.add(obj);
                }
            }

            String gcId=shoppingGoods.getGcId();
            ShoppingGoodsclass shoppingGoodsclass = new ShoppingGoodsclass();
            shoppingGoodsclass.setId(gcId);
            shoppingGoodsclass = shoppingGoodsclassDao.queryDetail(shoppingGoodsclass);
            while((shoppingGoodsclass.getGoodstypeId()==null||"".equals(shoppingGoodsclass.getGoodstypeId()))&&shoppingGoodsclass.getParentId()!=null){
                shoppingGoodsclass.setId(shoppingGoodsclass.getParentId());
                shoppingGoodsclass = shoppingGoodsclassDao.queryDetail(shoppingGoodsclass);
            }
            if(shoppingGoodsclass.getGoodstypeId()!=null&&!"".equals(shoppingGoodsclass.getGoodstypeId())){
                reqMap.clear();
                reqMap.put("typeId",shoppingGoodsclass.getGoodstypeId());
                List<ShoppingGoodsspecification> specs =shoppingGoodsspecificationDao.queryTypeSpecs(reqMap);
                for(ShoppingGoodsspecification shoppingGoodsspecification:specs){
                    List<ShoppingGoodsspecproperty> propertys = CommonUtil.getPropertys(shoppingGoodsspecification.getId());
                    shoppingGoodsspecification.setPropertys(propertys);
                }

                for(ShoppingGoodsspecification shoppingGoodsspecification:specs){
                    List<ShoppingGoodsspecproperty> propertys = CommonUtil.getPropertys(shoppingGoodsspecification.getId());
                    for(ShoppingGoodsspecproperty shoppingGoodsspecproperty:propertys){
                        if(propertyIds.contains(shoppingGoodsspecproperty.getId())){
                            shoppingGoodsspecproperty.setHasChosen(true);
                        }
                    }
                    shoppingGoodsspecification.setPropertys(propertys);
                }
                bizDataJson.put("specs",specs);  //规格信息
            }



            reqMap.clear();
            reqMap.put("goodsId",goodsId);
            List<ShoppingGoodsPhoto> photos = shoppingGoodsPhotoDao.queryList(reqMap);

            bizDataJson.put("detail",shoppingGoods);  //商品信息

            bizDataJson.put("inventoryDetails",inventoryDetailArray);  //规格库存
            bizDataJson.put("photos",photos);  //商品图片


            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 查询商品初始规格配置参数
     */
    @Override
    public JSONObject queryDefaultSpecification(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String gcId=reqJson.getString("gcId");
            ShoppingGoodsclass shoppingGoodsclass = new ShoppingGoodsclass();
            shoppingGoodsclass.setId(gcId);
            shoppingGoodsclass = shoppingGoodsclassDao.queryDetail(shoppingGoodsclass);
            while((shoppingGoodsclass.getGoodstypeId()==null||"".equals(shoppingGoodsclass.getGoodstypeId()))&&shoppingGoodsclass.getParentId()!=null){
                shoppingGoodsclass.setId(shoppingGoodsclass.getParentId());
                shoppingGoodsclass = shoppingGoodsclassDao.queryDetail(shoppingGoodsclass);
            }
            if(shoppingGoodsclass.getGoodstypeId()!=null&&!"".equals(shoppingGoodsclass.getGoodstypeId())){
                HashMap<String, Object> reqMap =new HashMap<>();
                reqMap.put("typeId",shoppingGoodsclass.getGoodstypeId());
                List<ShoppingGoodsspecification> specs =shoppingGoodsspecificationDao.queryTypeSpecs(reqMap);
                for(ShoppingGoodsspecification shoppingGoodsspecification:specs){
                    List<ShoppingGoodsspecproperty> propertys = CommonUtil.getPropertys(shoppingGoodsspecification.getId());
                    shoppingGoodsspecification.setPropertys(propertys);
                }
                bizDataJson.put("specs",specs);
            }

            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * @Description 发布新商品
     **/
    @Override
    public JSONObject addGoods(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "0";
        String retMsg = "操作成功！";
        JSONObject bizDataJson = new JSONObject();
        try {
            //商品信息
            ShoppingGoods shoppingGoods = JSON.parseObject(reqJson.getJSONObject("detail").toJSONString(), ShoppingGoods.class);
            shoppingGoods.setGoodsStoreId(CommonUtil.getSystemStore().getId());

            String addUser = reqJson.getString("addUser");
            shoppingGoods.setAddUser(addUser);
            shoppingGoodsDao.insert(shoppingGoods);
            //库存
            String key = REDIS_KEY_GOODS + shoppingGoods.getId();
            redisStockService.initStock(key,shoppingGoods.getGoodsInventory());
            //规格库存
            if(null !=reqJson.get("inventoryDetails")){
                JSONArray inventoryDetailsArray = reqJson.getJSONArray("inventoryDetails");
//                shoppingGoods.setGoodsInventoryDetail(inventoryDetailsArray.toString());
                for(int i=0;i<inventoryDetailsArray.size();i++){
                    JSONObject obj = inventoryDetailsArray.getJSONObject(i);
                    ShoppingGoodsInventory shoppingGoodsInventory = new ShoppingGoodsInventory();
                    shoppingGoodsInventory.setGoodsId(shoppingGoods.getId());
                    shoppingGoodsInventory.setPropertys(obj.getString("id"));
                    shoppingGoodsInventory.setPrice(new BigDecimal(obj.getString("price")));
                    shoppingGoodsInventory.setCount(Integer.valueOf(obj.getString("count")));

//                    key = REDIS_KEY_GOODS + shoppingGoods.getId()+shoppingGoodsInventory.getPropertys();
                    key = REDIS_KEY_GOODS + shoppingGoods.getId()+":"+shoppingGoodsInventory.getPropertys();
                    redisStockService.initStock(key,shoppingGoodsInventory.getCount());

                    shoppingGoodsInventoryDao.insert(shoppingGoodsInventory);
                }
            }

            //规格信息
            if(null !=reqJson.get("specs")){
                JSONArray specsArray = reqJson.getJSONArray("specs");
                for(int i=0;i<specsArray.size();i++){
                    ShoppingGoodsSpec shoppingGoodsSpec = JSON.parseObject(specsArray.getJSONObject(i).toJSONString(), ShoppingGoodsSpec.class);
                    shoppingGoodsSpec.setGoodsId(shoppingGoods.getId());
                    shoppingGoodsSpecDao.insert(shoppingGoodsSpec);
                }
            }

            //商品图片
            if(null !=reqJson.get("photos")){
                JSONArray photoArray = reqJson.getJSONArray("photos");
                for(int i=0;i<photoArray.size();i++){
                    ShoppingGoodsPhoto shoppingGoodsPhoto = JSON.parseObject(photoArray.getJSONObject(i).toJSONString(), ShoppingGoodsPhoto.class);
                    shoppingGoodsPhoto.setGoodsId(shoppingGoods.getId());
                    shoppingGoodsPhotoDao.insert(shoppingGoodsPhoto);
                }
            }

            bizDataJson.put("id",shoppingGoods.getId());

        } catch (Exception e) {
            retCode = "1";
            retMsg = "操作失败！";
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * @Description 编辑商品
     **/
    @Override
    public JSONObject editGoods(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "0";
        String retMsg = "操作成功！";
        JSONObject bizDataJson = new JSONObject();
        try {
            //商品信息
            ShoppingGoods shoppingGoods = JSON.parseObject(reqJson.getJSONObject("detail").toJSONString(), ShoppingGoods.class);
            //规格库存
            if(null !=reqJson.get("inventoryDetails")){
                JSONArray inventoryDetailsArray = reqJson.getJSONArray("inventoryDetails");
                shoppingGoods.setGoodsInventoryDetail(inventoryDetailsArray.toString());
                //先删除已有配置
                ShoppingGoodsInventory goodsInventory = new ShoppingGoodsInventory();
                goodsInventory.setGoodsId(shoppingGoods.getId());
                shoppingGoodsInventoryDao.delete(goodsInventory);
                for(int i=0;i<inventoryDetailsArray.size();i++){
                    JSONObject obj = inventoryDetailsArray.getJSONObject(i);
                    ShoppingGoodsInventory shoppingGoodsInventory = new ShoppingGoodsInventory();
                    shoppingGoodsInventory.setGoodsId(shoppingGoods.getId());
                    shoppingGoodsInventory.setPropertys(obj.getString("id"));
                    shoppingGoodsInventory.setPrice(new BigDecimal(obj.getString("price")));
                    shoppingGoodsInventory.setCount(Integer.valueOf(obj.getString("count")));

//                    String key = REDIS_KEY_GOODS + shoppingGoods.getId()+shoppingGoodsInventory.getPropertys();
                    String key = REDIS_KEY_GOODS + shoppingGoods.getId()+":"+shoppingGoodsInventory.getPropertys();
                    redisStockService.initStock(key,shoppingGoodsInventory.getCount());

                    shoppingGoodsInventoryDao.insert(shoppingGoodsInventory);
                }
            }
            shoppingGoodsDao.update(shoppingGoods);
            //库存
            String key = REDIS_KEY_GOODS + shoppingGoods.getId();
            redisStockService.initStock(key,shoppingGoods.getGoodsInventory());

            //规格信息
            //先删除以保存的规格信息
            ShoppingGoodsSpec spec =new ShoppingGoodsSpec();
            spec.setGoodsId(shoppingGoods.getId());
            shoppingGoodsSpecDao.delete(spec);
            if(null !=reqJson.get("specs")){
                JSONArray specsArray = reqJson.getJSONArray("specs");
                for(int i=0;i<specsArray.size();i++){
                    ShoppingGoodsSpec shoppingGoodsSpec = JSON.parseObject(specsArray.getJSONObject(i).toJSONString(), ShoppingGoodsSpec.class);
                    shoppingGoodsSpec.setGoodsId(shoppingGoods.getId());
                    shoppingGoodsSpecDao.insert(shoppingGoodsSpec);
                }
            }

            //商品图片
            ShoppingGoodsPhoto photo = new ShoppingGoodsPhoto();
            photo.setGoodsId(shoppingGoods.getId());
            shoppingGoodsPhotoDao.delete(photo);
            if(null !=reqJson.get("photos")){
                JSONArray photoArray = reqJson.getJSONArray("photos");
                for(int i=0;i<photoArray.size();i++){
                    ShoppingGoodsPhoto shoppingGoodsPhoto = JSON.parseObject(photoArray.getJSONObject(i).toJSONString(), ShoppingGoodsPhoto.class);
                    shoppingGoodsPhoto.setGoodsId(shoppingGoods.getId());
                    shoppingGoodsPhotoDao.insert(shoppingGoodsPhoto);
                }
            }



        } catch (Exception e) {
            retCode = "1";
            retMsg = "操作失败！";
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }
}
