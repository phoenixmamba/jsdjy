package com.centit.shopping.webmgr.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.dao.*;
import com.centit.shopping.feigin.JPushFeignClient;
import com.centit.shopping.po.*;
import com.centit.shopping.redis.RedisStockService;
import com.centit.shopping.utils.CommonUtil;
import com.centit.shopping.utils.ExportExcel;
import com.centit.shopping.utils.HttpSendUtil;
import com.centit.shopping.utils.StringUtil;
import com.centit.shopping.webmgr.service.AdminGoodsManageService;
import com.centit.shopping.webmgr.service.ArtactivityService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * company: www.abc.com
 * Author: 苏依林
 * Create Data: 2021/4/8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ArtactivityServiceImpl implements ArtactivityService {
    public static final Log LOGGER = LogFactory.getLog(AdminGoodsManageService.class);
    private static final String url ="http://api.jsmsxx.com:8030/service/httpService/httpInterface.do";

    @Resource
    private ShoppingArtactivityDao artactivityDao;

    @Resource
    private ShoppingArtactivityPhotoDao artactivityPhotoDao;

    @Resource
    private ShoppingArtactivityInfoDao artactivityInfoDao;

    @Resource
    private ShoppingArtinfosDao artinfosDao;

    @Resource
    private ShoppingArtactivitySignupinfoDao shoppingArtactivitySignupinfoDao;

    @Resource
    private ShoppingArtinfosDao shoppingArtinfosDao;

    @Resource
    private ShoppingUserDao shoppingUserDao;
    @Resource
    private ShoppingArtactivityPushDao shoppingArtactivityPushDao;

    @Resource
    private ShoppingArtactivityInventoryDao shoppingArtactivityInventoryDao;
    @Resource
    private ShoppingArtactivitySpecDao shoppingArtactivitySpecDao;
    @Resource
    private ShoppingGoodsspecpropertyDao shoppingGoodsspecpropertyDao;
    @Resource
    private ShoppingGoodsspecificationDao shoppingGoodsspecificationDao;
    @Resource
    private TExportFileDao tExportFileDao;

    @Resource
    private JPushFeignClient jPushFeignClient;

    @Value("${fileDownloadUrl}")
    private String fileDownloadUrl;

    @Value("${artactivitySpecTypeId}")
    private String artactivitySpecTypeId;

    /**
     * 配置库存Redis缓存Key前缀
     */
    public static final String REDIS_KEY = "REDIS_KEY:STOCK:";
    @Resource
    private RedisStockService redisStockService;

    @Override
    public JSONObject getArtactivityList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            int pageNo = reqJson.get("pageNo") == null ? 1 : reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize") == null ? 10 : reqJson.getInteger("pageSize");
            HashMap reqMap = JSONObject.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("startRow", (pageNo - 1) * pageSize);
            reqMap.put("pageSize", pageSize);
            reqMap.put("deleteStatus", "0");
            List list = artactivityDao.queryList(reqMap);
            int total = artactivityDao.queryTotalCount(reqMap);
            bizDataJson.put("objList", list);
            bizDataJson.put("total", total);
            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            LOGGER.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    @Override
    public JSONObject addArtactivity(JSONObject param) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingArtactivity shoppingArtactivity = JSONObject.parseObject(param.toJSONString(), ShoppingArtactivity.class);
            //插入活动信息
            artactivityDao.insert(shoppingArtactivity);
            String key = REDIS_KEY + shoppingArtactivity.getId();
            redisStockService.initStock(key,shoppingArtactivity.getLeftnum());

            JSONArray photos = param.getJSONArray("photos");
            if (CollectionUtils.isNotEmpty(photos)) {
                List<ShoppingArtactivityPhoto> shoppingArtactivityPhotos = JSONObject.parseArray(photos.toJSONString(), ShoppingArtactivityPhoto.class);
                //图片插入
                for (ShoppingArtactivityPhoto shoppingArtactivityPhoto : shoppingArtactivityPhotos) {
                    shoppingArtactivityPhoto.setActivityId(shoppingArtactivity.getId());
                    artactivityPhotoDao.insert(shoppingArtactivityPhoto);
                }
            }

            //规格库存
            if(null !=param.get("inventoryDetails")){
                JSONArray inventoryDetailsArray = param.getJSONArray("inventoryDetails");
//                shoppingGoods.setGoodsInventoryDetail(inventoryDetailsArray.toString());
                for(int i=0;i<inventoryDetailsArray.size();i++){
                    JSONObject obj = inventoryDetailsArray.getJSONObject(i);
                    ShoppingArtactivityInventory shoppingArtactivityInventory = new ShoppingArtactivityInventory();
                    shoppingArtactivityInventory.setActivityId(shoppingArtactivity.getId());
                    shoppingArtactivityInventory.setPropertys(obj.getString("id"));
                    shoppingArtactivityInventory.setPrice(new BigDecimal(obj.getString("price")));
                    shoppingArtactivityInventory.setCount(Integer.valueOf(obj.getString("count")));

                    key = REDIS_KEY + shoppingArtactivity.getId()+shoppingArtactivityInventory.getPropertys();
                    redisStockService.initStock(key,shoppingArtactivityInventory.getCount());

                    shoppingArtactivityInventoryDao.insert(shoppingArtactivityInventory);
                }
            }

            //规格信息
            if(null !=param.get("specs")){
                JSONArray specsArray = param.getJSONArray("specs");
                for(int i=0;i<specsArray.size();i++){
                    ShoppingArtactivitySpec shoppingArtactivitySpec = JSON.parseObject(specsArray.getJSONObject(i).toJSONString(), ShoppingArtactivitySpec.class);
                    shoppingArtactivitySpec.setActivityId(shoppingArtactivity.getId());
                    shoppingArtactivitySpecDao.insert(shoppingArtactivitySpec);
                }
            }

            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            LOGGER.error(e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    @Override
    public JSONObject modifyArtactivity(JSONObject param) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingArtactivity shoppingArtactivity = JSONObject.parseObject(param.toJSONString(), ShoppingArtactivity.class);
            String key = REDIS_KEY + shoppingArtactivity.getId();
            redisStockService.initStock(key,shoppingArtactivity.getLeftnum());
            JSONArray photos = param.getJSONArray("photos");
            if (CollectionUtils.isNotEmpty(photos)) {
                ShoppingArtactivityPhoto entity = new ShoppingArtactivityPhoto();
                entity.setActivityId(shoppingArtactivity.getId());
                artactivityPhotoDao.delete(entity);
                List<ShoppingArtactivityPhoto> shoppingArtactivityPhotos = JSONObject.parseArray(photos.toJSONString(), ShoppingArtactivityPhoto.class);
                //图片插入
                for (ShoppingArtactivityPhoto shoppingArtactivityPhoto : shoppingArtactivityPhotos) {
                    shoppingArtactivityPhoto.setActivityId(shoppingArtactivity.getId());
                    artactivityPhotoDao.insert(shoppingArtactivityPhoto);
                }
            }

            //规格库存
            if(null !=param.get("inventoryDetails")){
                JSONArray inventoryDetailsArray = param.getJSONArray("inventoryDetails");
                shoppingArtactivity.setActivityInventoryDetail(inventoryDetailsArray.toString());
                //先删除已有配置
                ShoppingArtactivityInventory goodsInventory = new ShoppingArtactivityInventory();
                goodsInventory.setActivityId(shoppingArtactivity.getId());
                shoppingArtactivityInventoryDao.delete(goodsInventory);
                for(int i=0;i<inventoryDetailsArray.size();i++){
                    JSONObject obj = inventoryDetailsArray.getJSONObject(i);
                    ShoppingArtactivityInventory shoppingArtactivityInventory = new ShoppingArtactivityInventory();
                    shoppingArtactivityInventory.setActivityId(shoppingArtactivity.getId());
                    shoppingArtactivityInventory.setPropertys(obj.getString("id"));
                    shoppingArtactivityInventory.setPrice(new BigDecimal(obj.getString("price")));
                    shoppingArtactivityInventory.setCount(Integer.valueOf(obj.getString("count")));

                    key = REDIS_KEY + shoppingArtactivity.getId()+shoppingArtactivityInventory.getPropertys();
                    redisStockService.initStock(key,shoppingArtactivityInventory.getCount());

                    shoppingArtactivityInventoryDao.insert(shoppingArtactivityInventory);
                }
            }

            //规格信息
            //先删除以保存的规格信息
            ShoppingArtactivitySpec spec =new ShoppingArtactivitySpec();
            spec.setActivityId(shoppingArtactivity.getId());
            shoppingArtactivitySpecDao.delete(spec);
            if(null !=param.get("specs")){
                JSONArray specsArray = param.getJSONArray("specs");
                for(int i=0;i<specsArray.size();i++){
                    ShoppingArtactivitySpec shoppingGoodsSpec = JSON.parseObject(specsArray.getJSONObject(i).toJSONString(), ShoppingArtactivitySpec.class);
                    shoppingGoodsSpec.setActivityId(shoppingArtactivity.getId());
                    shoppingArtactivitySpecDao.insert(shoppingGoodsSpec);
                }
            }

            //插入活动信息
            artactivityDao.update(shoppingArtactivity);
            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOGGER.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    @Override
    public JSONObject delArtactivity(JSONObject param) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingArtactivity shoppingArtactivity = JSONObject.parseObject(param.toJSONString(), ShoppingArtactivity.class);
            shoppingArtactivity.setDeleteStatus("1");

            artactivityDao.update(shoppingArtactivity);
            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOGGER.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    @Override
    public JSONObject removeArtactivity(String id) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingArtactivity shoppingArtactivity = new ShoppingArtactivity();
            shoppingArtactivity.setId(id);
            artactivityDao.delete(shoppingArtactivity);
            ShoppingArtactivityPhoto shoppingArtactivityPhoto = new ShoppingArtactivityPhoto();
            shoppingArtactivityPhoto.setActivityId(id);
            artactivityPhotoDao.delete(shoppingArtactivityPhoto);
            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOGGER.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    @Override
    public JSONObject modifySignInfo(JSONObject param) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingArtactivityInfo shoppingArtactivityInfo = JSONObject.parseObject(param.toJSONString(), ShoppingArtactivityInfo.class);
            artactivityInfoDao.deleteByArtactivityId(shoppingArtactivityInfo);
            List<ShoppingArtactivityInfo> artactivityInfos = JSONObject.parseArray(param.getJSONArray("artactivityInfos").toJSONString(), ShoppingArtactivityInfo.class);

            for (ShoppingArtactivityInfo artactivityInfo : artactivityInfos) {
                artactivityInfo.setActivityId(shoppingArtactivityInfo.getActivityId());
                artactivityInfoDao.insert(artactivityInfo);
            }
            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOGGER.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    @Override
    public JSONObject querySignInfo(String id) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            HashMap<String, Object> reqMap = new HashMap();
            reqMap.put("activityId", id);
            List<ShoppingArtactivityInfo> artactivityInfos = artactivityInfoDao.queryList(reqMap);
            bizDataJson.put("objList", artactivityInfos);
            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOGGER.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    @Override
    public JSONObject queryArtInfos(JSONObject requestParametersRetJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            List<ShoppingArtinfos> shoppingArtinfos = artinfosDao.queryList(null);
            bizDataJson.put("objList", shoppingArtinfos);
            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOGGER.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    @Override
    public JSONObject queryArtactivityById(String id) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingArtactivity shoppingArtactivity = new ShoppingArtactivity();
            shoppingArtactivity.setId(id);
            shoppingArtactivity = artactivityDao.queryDetail(shoppingArtactivity);
            HashMap<String, Object> reqMap = new HashMap();
            reqMap.put("activityId", id);
            List<ShoppingArtactivityPhoto> shoppingArtactivityPhotos = artactivityPhotoDao.queryList(reqMap);
            shoppingArtactivity.setPhotos(shoppingArtactivityPhotos);
            String key = REDIS_KEY + shoppingArtactivity.getId();
            if(redisStockService.checkGoods(key)){
                shoppingArtactivity.setLeftnum(redisStockService.currentStock(key));
            }
            bizDataJson.put("objList", shoppingArtactivity);

            //商品规格信息
            reqMap =new HashMap<>();
            reqMap.put("activityId",id);
            JSONArray inventoryDetailArray = new JSONArray();
            HashSet<String> propertyIds = new HashSet<>();
            List<ShoppingArtactivityInventory> inventoryList= shoppingArtactivityInventoryDao.queryList(reqMap);
            if(!inventoryList.isEmpty()){
                for(int i=0;i<inventoryList.size();i++){
                    ShoppingArtactivityInventory shoppingArtactivityInventory = inventoryList.get(i);

                    JSONObject obj = new JSONObject();
                    obj.put("id",shoppingArtactivityInventory.getPropertys());
                    key = REDIS_KEY + shoppingArtactivity.getId()+shoppingArtactivityInventory.getPropertys();
                    if(redisStockService.checkGoods(key)){
                        obj.put("count",redisStockService.currentStock(key));
                    }else{
                        obj.put("count",shoppingArtactivityInventory.getCount().toString());
                    }
                    obj.put("price",shoppingArtactivityInventory.getPrice().toString());
                    String[] sps= shoppingArtactivityInventory.getPropertys().split("_");
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
            bizDataJson.put("inventoryDetails",inventoryDetailArray);  //规格库存
            reqMap.clear();
            reqMap.put("activityId",id);
            List<ShoppingGoodsspecification> specs =shoppingGoodsspecificationDao.queryActSpecs(reqMap);
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


            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            LOGGER.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    @Override
    public JSONObject queryArtactivitySignupList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            int pageNo = reqJson.get("pageNo") == null ? 1 : reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize") == null ? 10 : reqJson.getInteger("pageSize");
            HashMap reqMap = JSONObject.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("startRow", (pageNo - 1) * pageSize);
            reqMap.put("pageSize", pageSize);
            //查询已报名信息
            List<ShoppingArtactivitySignupinfo> signupinfos=shoppingArtactivitySignupinfoDao.querySignupInfos(reqMap);
            JSONArray objList = new JSONArray();
            for(ShoppingArtactivitySignupinfo signupinfo:signupinfos){
                String infoStr=signupinfo.getSignupInfo();
                JSONArray infoArray = JSONArray.parseArray(infoStr);
                JSONObject obj =new JSONObject();
                obj.put("id",signupinfo.getId());
                obj.put("signupTime",signupinfo.getSignupTime());
                for(int i=0;i<infoArray.size();i++){
                    JSONObject infoObj = infoArray.getJSONObject(i);
                    if(infoObj.getString("inforName").equals("姓名")){
                        obj.put("name",infoObj.get("inforValue"));
                        break;
                    }

                }
                objList.add(obj);
            }
            bizDataJson.put("objList", objList);
            bizDataJson.put("total", shoppingArtactivitySignupinfoDao.querySignupInfoCount(reqMap));

            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            LOGGER.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 获取报名详情
     */
    @Override
    public JSONObject queryArtactivitySignupDetail(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {

            String id = reqJson.getString("id");
            ShoppingArtactivitySignupinfo shoppingArtactivitySignupinfo = new ShoppingArtactivitySignupinfo();
            shoppingArtactivitySignupinfo.setId(id);
            shoppingArtactivitySignupinfo = shoppingArtactivitySignupinfoDao.queryDetail(shoppingArtactivitySignupinfo);
            String infoStr=shoppingArtactivitySignupinfo.getSignupInfo();
            JSONArray infoArray = JSONArray.parseArray(infoStr);
            bizDataJson.put("objList", infoArray);

            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            LOGGER.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    @Override
    public JSONObject putArtactivity(JSONObject param) {
        JSONObject retJson = new JSONObject();
        String retCode = "0";
        String retMsg = "操作成功！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String[] ids = param.getString("ids").split(",");
            for (int i = 0; i < ids.length; i++) {
                ShoppingArtactivity shoppingArtactivity = new ShoppingArtactivity();
                shoppingArtactivity.setId(ids[i]);
                shoppingArtactivity.setActivityStatus(param.getInteger("activityStatus"));
                artactivityDao.update(shoppingArtactivity);
            }

        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            retCode = "1";
            retMsg = "操作失败！";
            LOGGER.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 导出报名信息列表
     */
    @Override
    public JSONObject exportArtactivitySignupList(JSONObject reqJson, HttpServletResponse response) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            TExportFile tExportFile = new TExportFile();
            String fileId = String.valueOf(System.currentTimeMillis());
            tExportFile.setId(fileId);
            tExportFile.setDataType("报名信息");
            tExportFileDao.insert(tExportFile);

            ExecutorService fixPool = Executors.newFixedThreadPool(1);
            fixPool.execute(new Runnable() {
                @Override
                public void run() {
                    HashMap reqMap = JSONObject.parseObject(reqJson.toJSONString(), HashMap.class);
                    //查询已报名信息
                    List<ShoppingArtactivitySignupinfo> signupinfos=shoppingArtactivitySignupinfoDao.querySignupInfos(reqMap);
                    JSONArray objList = new JSONArray();
                    boolean hasId =false;
                    for(ShoppingArtactivitySignupinfo signupinfo:signupinfos){
                        String infoStr=signupinfo.getSignupInfo();
                        JSONArray infoArray = JSONArray.parseArray(infoStr);
                        JSONObject obj =new JSONObject();
                        obj.put("用户手机号",signupinfo.getMobile());
                        obj.put("活动名称",signupinfo.getActivityName());
                        obj.put("下单时间",signupinfo.getAddTime());
                        obj.put("订单号",signupinfo.getOrderId());
                        obj.put("交易流水号",signupinfo.getTradeNo());
                        for(int i=0;i<infoArray.size();i++){
                            JSONObject infoObj = infoArray.getJSONObject(i);
                            obj.put(infoObj.getString("inforName"),infoObj.get("inforValue"));
                            if(infoObj.getString("inforName").contains("身份证")){
                                hasId = true;
                                String idCard = infoObj.getString("inforValue");
                                String sex =  judgeGender(idCard);   //性别
                                obj.put("性别",sex);
                                int age = countAge(idCard);  //年龄
                                obj.put("年龄",String.valueOf(age));
                                String province = getProvince(idCard);   //省份
                                obj.put("省份",province);
                            }
                        }
                        objList.add(obj);
                    }
                    //报名需要填写的信息项
                    int extraCount = hasId?8:5;
                    reqMap.clear();
                    reqMap.put("activityId", reqJson.getString("activityId"));
                    List<ShoppingArtinfos> infos = shoppingArtinfosDao.queryActivityInfoList(reqMap);
                    String[] rowsName =new String[infos.size()+extraCount];
                    Integer[] rowsType =new Integer[infos.size()+extraCount];

                    rowsName[0]="用户手机号";
                    rowsType[0]=1;
                    rowsName[1]="活动名称";
                    rowsType[1]=1;
                    rowsName[2]="下单时间";
                    rowsType[2]=1;
                    rowsName[3]="订单号";
                    rowsType[3]=1;
                    rowsName[4]="交易流水号";
                    rowsType[4]=1;
                    if(hasId){
                        rowsName[5]="性别";
                        rowsType[5]=1;
                        rowsName[6]="年龄";
                        rowsType[6]=1;
                        rowsName[7]="省份";
                        rowsType[7]=1;
                    }
                    for(int i=0;i< infos.size();i++){
                        ShoppingArtinfos shoppingArtinfos = infos.get(i);
                        rowsName[i+extraCount]=shoppingArtinfos.getInforName();
                        rowsType[i+extraCount]=shoppingArtinfos.getInfoType();
                    }

                    String sumStr ="报名数据";
                    // 导出表的标题
                    String title =sumStr;

                    List<Object[]> dataList = new ArrayList<Object[]>();
                    for(int i=0;i<objList.size();i++){
                        JSONObject dataObj = objList.getJSONObject(i);
                        Object[] obj = new Object[rowsName.length];
                        for(int j=0;j<rowsName.length;j++){
                            if(null!=dataObj.get(rowsName[j])){
                                if(rowsType[j]==1){ //文本类型
                                    obj[j] = dataObj.getString(rowsName[j]);
                                }else if(rowsType[j]==2){  //图片类型
                                    String strs = dataObj.getString(rowsName[j]);
                                    String[] imgIds = strs.split(";");
                                    String imgValue="";
                                    for(int m=0;m<imgIds.length;m++){
                                        if(m==imgIds.length-1){
                                            imgValue=imgValue+fileDownloadUrl+imgIds[m];
                                        }else{
                                            imgValue=imgValue+fileDownloadUrl+imgIds[m]+System.getProperty("line.separator");
                                        }

                                    }
                                    obj[j] =imgValue;
                                }
                            }else{
                                obj[j] = "";
                            }
                        }

                        dataList.add(obj);
                    }

                    String fileName = fileId + ".xls";
                    ShoppingSysconfig config = CommonUtil.getSysConfig();
                    String uploadFilePath = config.getUploadFilePath();
                    File file = new File(uploadFilePath + File.separator + "exportFile" +
                            File.separator + fileName);
                    try {
                        OutputStream out = new FileOutputStream(file);
                        ExportExcel ex = new ExportExcel(title, rowsName, dataList);
                        ex.export(out);

                        tExportFile.setFileName(fileName);
                        tExportFile.setFinishTime(StringUtil.nowTimeString());
                        tExportFile.setTaskStatus(1);  //已完成

                        tExportFileDao.update(tExportFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                        tExportFile.setTaskStatus(-1);
                        tExportFileDao.update(tExportFile);
                    }
                }
            });
            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            e.printStackTrace();
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 导出报名信息列表
     */
    @Override
    public void exportArtactivitySignup(JSONObject reqJson, HttpServletResponse response) {
        List<HashMap<String, Object>> dataList = shoppingArtactivitySignupinfoDao.querySignupInfosTest(null);
        List<List<String>> llList = new ArrayList<>();
        for(HashMap<String, Object> mapObj:dataList){
            List<String> resList = new ArrayList<>();

            String mobile = mapObj.get("mobile").toString();
            resList.add(mobile);
            String orderid = mapObj.get("orderid").toString();
            resList.add(orderid);
            String infos = mapObj.get("infos").toString();
            JSONArray infoarray = JSONArray.parseArray(infos);
            for(int i=0;i<infoarray.size();i++){
                JSONObject obj = infoarray.getJSONObject(i);
                String inforValue =obj.get("inforValue").toString();
                if(obj.getInteger("infoType")==2){
                    inforValue="https://app.jsartcentre.org/fileserver/common/downloadFile/"+inforValue;
                }
                String inforName =obj.get("inforName").toString();
                resList.add(inforValue);
                if(inforName.equals("身份证号")){
                    String judgeGender =judgeGender(inforValue);
                    resList.add(judgeGender);
                    String countAge =String.valueOf(countAge(inforValue));
                    resList.add(countAge);
                    if(inforValue.startsWith("32")){
                        resList.add("省内");
                    }else{
                        resList.add("省外");
                    }
                }

            }
            llList.add(resList);
        }
        for(int i=0;i<13;i++){
            for(List<String> aList:llList){
                System.out.println(aList.get(i));
            }
            System.out.println("====================================");
        }

    }

    /**
     * 根据身份证号判断性别
     * @param idNumber
     * @return
     */
    public static String judgeGender(String idNumber) throws IllegalArgumentException{
        System.out.println(idNumber.length());
        if(idNumber.length() != 18 && idNumber.length() != 15){
            return "身份证错误";
        }
        int gender = 0;
        if(idNumber.length() == 18){
            //如果身份证号18位，取身份证号倒数第二位
            char c = idNumber.charAt(idNumber.length() - 2);
            gender = Integer.parseInt(String.valueOf(c));
        }else{
            //如果身份证号15位，取身份证号最后一位
            char c = idNumber.charAt(idNumber.length() - 1);
            gender = Integer.parseInt(String.valueOf(c));
        }
        if(gender % 2 == 1){
            return "男";
        }else{
            return "女";
        }
    }

    /**
     * 根据身份证的号码算出当前身份证持有者的年龄
     *
     * @return
     */
    public static int countAge(String idNumber) {
        if(idNumber.length() != 18 && idNumber.length() != 15){
            return -1;
        }
        String year;
        String yue;
        String day;
        if(idNumber.length() == 18){
            year = idNumber.substring(6).substring(0, 4);// 得到年份
            yue = idNumber.substring(10).substring(0, 2);// 得到月份
            day = idNumber.substring(12).substring(0,2);//得到日
        }else{
            year = "19" + idNumber.substring(6, 8);// 年份
            yue = idNumber.substring(8, 10);// 月份
            day = idNumber.substring(10, 12);//日
        }
        Date date = new Date();// 得到当前的系统时间
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String fyear = format.format(date).substring(0, 4);// 当前年份
        String fyue = format.format(date).substring(5, 7);// 月份
        String fday=format.format(date).substring(8,10);//
        int age = 0;
        if(Integer.parseInt(yue) == Integer.parseInt(fyue)){//如果月份相同
            if(Integer.parseInt(day) <= Integer.parseInt(fday)){//说明已经过了生日或者今天是生日
                age = Integer.parseInt(fyear) - Integer.parseInt(year);
            } else {
                age = Integer.parseInt(fyear) - Integer.parseInt(year) - 1;
            }
        }else{

            if(Integer.parseInt(yue) < Integer.parseInt(fyue)){
                //如果当前月份大于出生月份
                age = Integer.parseInt(fyear) - Integer.parseInt(year);
            }else{
                //如果当前月份小于出生月份,说明生日还没过
                age = Integer.parseInt(fyear) - Integer.parseInt(year) - 1;
            }
        }
        System.out.println("age = " + age);
        return age;
    }

    public String getProvince(String idNumber) {
        try{
            String[] a = { "11", "12", "13", "14", "15", "21", "22", "23", "31", "32", "33", "34", "35", "36", "37", "41",
                    "42", "43", "44", "45", "46", "50", "51", "52", "53", "54", "61", "62", "63", "64", "65", "71", "81",
                    "82" };

            String[] b = { "北京市", "天津市", "河北省", "山西省", "内蒙古自治区", "辽宁省", "吉林省", "黑龙江省", "上海市", " 江苏省", "浙江省", "安徽省", "福建省",
                    " 江西省", "山东省", " 河南省", "湖北省", " 湖南省", "广东省", " 广西壮族自治区", "海南省", "重庆市", "四川省", "贵州省", "云南省", " 西藏自治区",
                    "陕西省", "甘肃省", "青海省", "宁夏回族自治区", "新疆维吾尔自治区", "台湾省", "香港特别行政区", "澳门特别行政区" };       //将省份全部放进数组b;
            String pos = (idNumber.substring(0, 2));      //id.substring(0, 2)获取身份证的前两位；
            int i;
            for( i=0;i<a.length;i++){
                if(pos.equals(a[i])){
                    break;
                }
            }
            return b[i];  //获取b数组中的省份信息且输出省份;
        }catch (Exception e){
            return "身份证错误";
        }

    }

    /**
     * 新增推送
     */
    @Override
    public JSONObject pushMsg(JSONObject param) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingArtactivityPush shoppingArtactivityPush = JSONObject.parseObject(param.toJSONString(), ShoppingArtactivityPush.class);
            if(shoppingArtactivityPush.getPushType()==1){  //立即推送
                if(shoppingArtactivityPush.getPushRange()==1){    //所有下单用户
                    //查询该活动所有下单用户的手机号
                    HashMap reqMap = new HashMap();
                    reqMap.put("activityId",shoppingArtactivityPush.getActivityId());
                    List<ShoppingUser> users = shoppingUserDao.queryArtactivityUsers(reqMap);
                    List<String> mobiles = new ArrayList<>();
                    String mobileStr = "";
                    for(ShoppingUser user:users){
                        mobiles.add(user.getMobile());
                        mobileStr=mobileStr+user.getMobile()+",";
                        if(mobiles.size()==100){
                            sendMsg(mobileStr,shoppingArtactivityPush.getPushContent());
                            mobiles.clear();
                            mobileStr = "";
                        }
                    }
                    if(mobiles.size()>0){
                        sendMsg(mobileStr,shoppingArtactivityPush.getPushContent());
                    }
                    shoppingArtactivityPush.setDoneTime(StringUtil.nowTimeString());
                    shoppingArtactivityPush.setPushStatus(2);
                }else{                 //指定用户
                    String mobileStrs = shoppingArtactivityPush.getPushMobiles();
                    String[] strs = mobileStrs.split(",");

                    if(strs.length>100){
                        shoppingArtactivityPush.setPushStatus(-1);
                    }else{
                        if(sendMsg(mobileStrs,shoppingArtactivityPush.getPushContent())){
                            shoppingArtactivityPush.setDoneTime(StringUtil.nowTimeString());
                            shoppingArtactivityPush.setPushStatus(2);
                        }else{
                            shoppingArtactivityPush.setPushStatus(-1);
                        }
                    }
                }
            }else{    //定时推送
                shoppingArtactivityPush.setPushStatus(1);
            }
            shoppingArtactivityPushDao.insert(shoppingArtactivityPush);
            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOGGER.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    public boolean sendMsg(String mobile,String content){
        Map<String, String> params=new HashMap<>();
        params.put("method", "sendUtf8Msg");
        params.put("username", "JSM4140009");
        params.put("password", "xr185whg");
        params.put("veryCode", "maoyvrgxx9h8");
        params.put("tempid", "JSM41400-0095");
        params.put("content", "@1@="+content);
        params.put("msgtype", "2");
        params.put("mobile", mobile);
        params.put("rt", "json");
//            params.put("code", "utf-8");
        JSONObject retJson = HttpSendUtil.sendMsg(url, params);
        System.out.println(retJson);
        if(retJson.get("status")!=null&&retJson.get("status").equals("0")){
            return true;
        }else{
            return false;
        }

    }

    /**
     * 获取所有推送记录
     */
    @Override
    public JSONObject queryArtactivityPushList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            int pageNo = reqJson.get("pageNo") == null ? 1 : reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize") == null ? 10 : reqJson.getInteger("pageSize");
            HashMap reqMap = JSONObject.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("startRow", (pageNo - 1) * pageSize);
            reqMap.put("pageSize", pageSize);
            //查询已报名信息
            List<ShoppingArtactivityPush> objList=shoppingArtactivityPushDao.queryList(reqMap);

            bizDataJson.put("objList", objList);
            bizDataJson.put("total", shoppingArtactivityPushDao.queryListCount(reqMap));

            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            LOGGER.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 取消未执行的定时推送任务
     */
    @Override
    public JSONObject cancelArtactivityPush(JSONObject param) {
        JSONObject retJson = new JSONObject();
        String retCode = "0";
        String retMsg = "操作成功！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String id = param.getString("id");
            ShoppingArtactivityPush shoppingArtactivityPush = new ShoppingArtactivityPush();
            shoppingArtactivityPush.setId(id);
            shoppingArtactivityPush.setPushStatus(0);
            shoppingArtactivityPushDao.update(shoppingArtactivityPush);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            retCode = "1";
            retMsg = "操作失败！";
            LOGGER.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 查询活动可用的规格参数
     */
    @Override
    public JSONObject queryActDefaultSpecification(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            HashMap<String, Object> reqMap =new HashMap<>();
            reqMap.put("typeId",artactivitySpecTypeId);
            List<ShoppingGoodsspecification> specs =shoppingGoodsspecificationDao.queryTypeSpecs(reqMap);
            for(ShoppingGoodsspecification shoppingGoodsspecification:specs){
                List<ShoppingGoodsspecproperty> propertys = CommonUtil.getPropertys(shoppingGoodsspecification.getId());
                shoppingGoodsspecification.setPropertys(propertys);
            }
            bizDataJson.put("specs",specs);

            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            LOGGER.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }
}
