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
import com.centit.shopping.webmgr.service.ArtplanService;
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
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 
 * Author: cuijian
 * Create Data: 2021/12/14
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ArtplanServiceImpl implements ArtplanService {
    public static final Log LOGGER = LogFactory.getLog(ArtplanService.class);
    private static final String url ="http://api.jsmsxx.com:8030/service/httpService/httpInterface.do";

    @Resource
    private ShoppingArtplanDao artplanDao;

    @Resource
    private ShoppingArtplanPhotoDao artplanPhotoDao;

    @Resource
    private ShoppingArtplanInfoDao artplanInfoDao;

    @Resource
    private ShoppingArtinfosDao artinfosDao;

    @Resource
    private ShoppingArtplanSignupinfoDao shoppingArtplanSignupinfoDao;

    @Resource
    private ShoppingArtinfosDao shoppingArtinfosDao;


    @Resource
    private ShoppingUserDao shoppingUserDao;
    @Resource
    private ShoppingArtplanPushDao shoppingArtplanPushDao;

    @Resource
    private ShoppingArtplanInventoryDao shoppingArtplanInventoryDao;
    @Resource
    private ShoppingArtplanSpecDao shoppingArtplanSpecDao;
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

    @Value("${aetplanSpecTypeId}")
    private String aetplanSpecTypeId;

    /**
     * 配置库存Redis缓存Key前缀
     */
    public static final String REDIS_KEY_PLAN = "REDIS_KEY:STOCK:PLAN";
    @Resource
    private RedisStockService redisStockService;

    @Override
    public JSONObject getArtplanList(JSONObject reqJson) {
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
            List list = artplanDao.queryList(reqMap);
            int total = artplanDao.queryTotalCount(reqMap);
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
    public JSONObject addArtplan(JSONObject param) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingArtplan shoppingArtplan = JSONObject.parseObject(param.toJSONString(), ShoppingArtplan.class);
            //插入活动信息
            artplanDao.insert(shoppingArtplan);
            String key = REDIS_KEY_PLAN + shoppingArtplan.getId();
            redisStockService.initStock(key,shoppingArtplan.getLeftnum());

            JSONArray photos = param.getJSONArray("photos");
            if (CollectionUtils.isNotEmpty(photos)) {
                List<ShoppingArtplanPhoto> shoppingArtplanPhotos = JSONObject.parseArray(photos.toJSONString(), ShoppingArtplanPhoto.class);
                //图片插入
                for (ShoppingArtplanPhoto shoppingArtplanPhoto : shoppingArtplanPhotos) {
                    shoppingArtplanPhoto.setActivityId(shoppingArtplan.getId());
                    artplanPhotoDao.insert(shoppingArtplanPhoto);
                }
            }

            //规格库存
            if(null !=param.get("inventoryDetails")){
                JSONArray inventoryDetailsArray = param.getJSONArray("inventoryDetails");
//                shoppingGoods.setGoodsInventoryDetail(inventoryDetailsArray.toString());
                for(int i=0;i<inventoryDetailsArray.size();i++){
                    JSONObject obj = inventoryDetailsArray.getJSONObject(i);
                    ShoppingArtplanInventory shoppingArtplanInventory = new ShoppingArtplanInventory();
                    shoppingArtplanInventory.setActivityId(shoppingArtplan.getId());
                    shoppingArtplanInventory.setPropertys(obj.getString("id"));
                    shoppingArtplanInventory.setPrice(new BigDecimal(obj.getString("price")));
                    shoppingArtplanInventory.setCount(Integer.valueOf(obj.getString("count")));

                    key = REDIS_KEY_PLAN + shoppingArtplan.getId()+shoppingArtplanInventory.getPropertys();
                    redisStockService.initStock(key,shoppingArtplanInventory.getCount());

                    shoppingArtplanInventoryDao.insert(shoppingArtplanInventory);
                }
            }

            //规格信息
            if(null !=param.get("specs")){
                JSONArray specsArray = param.getJSONArray("specs");
                for(int i=0;i<specsArray.size();i++){
                    ShoppingArtplanSpec shoppingArtplanSpec = JSON.parseObject(specsArray.getJSONObject(i).toJSONString(), ShoppingArtplanSpec.class);
                    shoppingArtplanSpec.setActivityId(shoppingArtplan.getId());
                    shoppingArtplanSpecDao.insert(shoppingArtplanSpec);
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
    public JSONObject modifyArtplan(JSONObject param) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingArtplan shoppingArtplan = JSONObject.parseObject(param.toJSONString(), ShoppingArtplan.class);

            String key = REDIS_KEY_PLAN + shoppingArtplan.getId();
            redisStockService.initStock(key,shoppingArtplan.getLeftnum());

            JSONArray photos = param.getJSONArray("photos");
            if (CollectionUtils.isNotEmpty(photos)) {
                ShoppingArtplanPhoto entity = new ShoppingArtplanPhoto();
                entity.setActivityId(shoppingArtplan.getId());
                artplanPhotoDao.delete(entity);
                List<ShoppingArtplanPhoto> shoppingArtplanPhotos = JSONObject.parseArray(photos.toJSONString(), ShoppingArtplanPhoto.class);
                //图片插入
                for (ShoppingArtplanPhoto shoppingArtplanPhoto : shoppingArtplanPhotos) {
                    shoppingArtplanPhoto.setActivityId(shoppingArtplan.getId());
                    artplanPhotoDao.insert(shoppingArtplanPhoto);
                }
            }

            //规格库存
            if(null !=param.get("inventoryDetails")){
                JSONArray inventoryDetailsArray = param.getJSONArray("inventoryDetails");
                shoppingArtplan.setActivityInventoryDetail(inventoryDetailsArray.toString());
                //先删除已有配置
                ShoppingArtplanInventory goodsInventory = new ShoppingArtplanInventory();
                goodsInventory.setActivityId(shoppingArtplan.getId());
                shoppingArtplanInventoryDao.delete(goodsInventory);
                for(int i=0;i<inventoryDetailsArray.size();i++){
                    JSONObject obj = inventoryDetailsArray.getJSONObject(i);
                    ShoppingArtplanInventory shoppingArtplanInventory = new ShoppingArtplanInventory();
                    shoppingArtplanInventory.setActivityId(shoppingArtplan.getId());
                    shoppingArtplanInventory.setPropertys(obj.getString("id"));
                    shoppingArtplanInventory.setPrice(new BigDecimal(obj.getString("price")));
                    shoppingArtplanInventory.setCount(Integer.valueOf(obj.getString("count")));

                    key = REDIS_KEY_PLAN + shoppingArtplan.getId()+shoppingArtplanInventory.getPropertys();
                    redisStockService.initStock(key,shoppingArtplanInventory.getCount());

                    shoppingArtplanInventoryDao.insert(shoppingArtplanInventory);
                }
            }

            //规格信息
            //先删除以保存的规格信息
            ShoppingArtplanSpec spec =new ShoppingArtplanSpec();
            spec.setActivityId(shoppingArtplan.getId());
            shoppingArtplanSpecDao.delete(spec);
            if(null !=param.get("specs")){
                JSONArray specsArray = param.getJSONArray("specs");
                for(int i=0;i<specsArray.size();i++){
                    ShoppingArtplanSpec shoppingGoodsSpec = JSON.parseObject(specsArray.getJSONObject(i).toJSONString(), ShoppingArtplanSpec.class);
                    shoppingGoodsSpec.setActivityId(shoppingArtplan.getId());
                    shoppingArtplanSpecDao.insert(shoppingGoodsSpec);
                }
            }
            //插入活动信息
            artplanDao.update(shoppingArtplan);
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
    public JSONObject delArtplan(JSONObject param) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingArtplan shoppingArtplan = JSONObject.parseObject(param.toJSONString(), ShoppingArtplan.class);
            shoppingArtplan.setDeleteStatus("1");
            artplanDao.update(shoppingArtplan);
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
    public JSONObject removeArtplan(String id) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingArtplan shoppingArtplan = new ShoppingArtplan();
            shoppingArtplan.setId(id);
            artplanDao.delete(shoppingArtplan);
            ShoppingArtplanPhoto shoppingArtplanPhoto = new ShoppingArtplanPhoto();
            shoppingArtplanPhoto.setActivityId(id);
            artplanPhotoDao.delete(shoppingArtplanPhoto);
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
            ShoppingArtplanInfo shoppingArtplanInfo = JSONObject.parseObject(param.toJSONString(), ShoppingArtplanInfo.class);
            artplanInfoDao.deleteByArtplanId(shoppingArtplanInfo);
            List<ShoppingArtplanInfo> artplanInfos = JSONObject.parseArray(param.getJSONArray("artplanInfos").toJSONString(), ShoppingArtplanInfo.class);

            for (ShoppingArtplanInfo artplanInfo : artplanInfos) {
                artplanInfo.setActivityId(shoppingArtplanInfo.getActivityId());
                artplanInfoDao.insert(artplanInfo);
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
            List<ShoppingArtplanInfo> artplanInfos = artplanInfoDao.queryList(reqMap);
            bizDataJson.put("objList", artplanInfos);
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
    public JSONObject queryArtplanById(String id) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingArtplan shoppingArtplan = new ShoppingArtplan();
            shoppingArtplan.setId(id);
            shoppingArtplan = artplanDao.queryDetail(shoppingArtplan);
            HashMap<String, Object> reqMap = new HashMap();
            reqMap.put("activityId", id);
            List<ShoppingArtplanPhoto> shoppingArtplanPhotos = artplanPhotoDao.queryList(reqMap);
            shoppingArtplan.setPhotos(shoppingArtplanPhotos);
            String key = REDIS_KEY_PLAN + shoppingArtplan.getId();
            if(redisStockService.checkGoods(key)){
                shoppingArtplan.setLeftnum(redisStockService.currentStock(key));
            }
            bizDataJson.put("objList", shoppingArtplan);

            //商品规格信息
            reqMap =new HashMap<>();
            reqMap.put("activityId",id);
            JSONArray inventoryDetailArray = new JSONArray();
            HashSet<String> propertyIds = new HashSet<>();
            List<ShoppingArtplanInventory> inventoryList= shoppingArtplanInventoryDao.queryList(reqMap);
            if(!inventoryList.isEmpty()){
                for(int i=0;i<inventoryList.size();i++){
                    ShoppingArtplanInventory shoppingArtplanInventory = inventoryList.get(i);

                    JSONObject obj = new JSONObject();
                    obj.put("id",shoppingArtplanInventory.getPropertys());
                    key = REDIS_KEY_PLAN + shoppingArtplan.getId()+shoppingArtplanInventory.getPropertys();
                    if(redisStockService.checkGoods(key)){
                        obj.put("count",redisStockService.currentStock(key));
                    }else{
                        obj.put("count",shoppingArtplanInventory.getCount().toString());
                    }
                    obj.put("price",shoppingArtplanInventory.getPrice().toString());
                    String[] sps= shoppingArtplanInventory.getPropertys().split("_");
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
            List<ShoppingGoodsspecification> specs =shoppingGoodsspecificationDao.queryPlanSpecs(reqMap);
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
    public JSONObject queryArtplanSignupList(JSONObject reqJson) {
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
            List<ShoppingArtplanSignupinfo> signupinfos=shoppingArtplanSignupinfoDao.querySignupInfos(reqMap);
            JSONArray objList = new JSONArray();
            for(ShoppingArtplanSignupinfo signupinfo:signupinfos){
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
            bizDataJson.put("total", shoppingArtplanSignupinfoDao.querySignupInfoCount(reqMap));

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
    public JSONObject queryArtplanSignupDetail(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {

            String id = reqJson.getString("id");
            ShoppingArtplanSignupinfo shoppingArtplanSignupinfo = new ShoppingArtplanSignupinfo();
            shoppingArtplanSignupinfo.setId(id);
            shoppingArtplanSignupinfo = shoppingArtplanSignupinfoDao.queryDetail(shoppingArtplanSignupinfo);
            String infoStr=shoppingArtplanSignupinfo.getSignupInfo();
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
    public JSONObject putArtplan(JSONObject param) {
        JSONObject retJson = new JSONObject();
        String retCode = "0";
        String retMsg = "操作成功！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String[] ids = param.getString("ids").split(",");
            for (int i = 0; i < ids.length; i++) {
                ShoppingArtplan shoppingArtplan = new ShoppingArtplan();
                shoppingArtplan.setId(ids[i]);
                shoppingArtplan.setActivityStatus(param.getInteger("activityStatus"));
                artplanDao.update(shoppingArtplan);
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
    public JSONObject exportArtplanSignupList(JSONObject reqJson, HttpServletResponse response) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();

        try {
            TExportFile tExportFile = new TExportFile();
            String fileId = String.valueOf(System.currentTimeMillis());
            tExportFile.setId(fileId);
            tExportFile.setDataType("兑换码");
            tExportFileDao.insert(tExportFile);

            ExecutorService fixPool = Executors.newFixedThreadPool(1);
            fixPool.execute(new Runnable() {
                @Override
                public void run() {
                    HashMap reqMap = JSONObject.parseObject(reqJson.toJSONString(), HashMap.class);
                    //查询已报名信息
                    List<ShoppingArtplanSignupinfo> signupinfos=shoppingArtplanSignupinfoDao.querySignupInfos(reqMap);
                    JSONArray objList = new JSONArray();
                    for(ShoppingArtplanSignupinfo signupinfo:signupinfos){
                        String infoStr=signupinfo.getSignupInfo();
                        JSONArray infoArray = JSONArray.parseArray(infoStr);
                        JSONObject obj =new JSONObject();
                        for(int i=0;i<infoArray.size();i++){
                            JSONObject infoObj = infoArray.getJSONObject(i);
                            obj.put(infoObj.getString("inforName"),infoObj.get("inforValue"));
                        }
                        objList.add(obj);
                    }

                    //报名需要填写的信息项
                    reqMap.clear();
                    reqMap.put("activityId", reqJson.getString("activityId"));
                    List<ShoppingArtinfos> infos = shoppingArtinfosDao.queryPlanInfoList(reqMap);
                    String[] rowsName =new String[infos.size()];
                    Integer[] rowsType =new Integer[infos.size()];
                    for(int i=0;i< infos.size();i++){
                        ShoppingArtinfos shoppingArtinfos = infos.get(i);
                        rowsName[i]=shoppingArtinfos.getInforName();
                        rowsType[i]=shoppingArtinfos.getInfoType();
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
                                        imgValue=imgValue+fileDownloadUrl+imgIds[m]+";";
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
     * 新增推送
     */
    @Override
    public JSONObject pushMsg(JSONObject param) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingArtplanPush shoppingArtplanPush = JSONObject.parseObject(param.toJSONString(), ShoppingArtplanPush.class);
            if(shoppingArtplanPush.getPushType()==1){  //立即推送
                if(shoppingArtplanPush.getPushRange()==1){    //所有下单用户
                    //查询该活动所有下单用户的手机号
                    HashMap<String, Object> reqMap = new HashMap();
                    reqMap.put("activityId",shoppingArtplanPush.getActivityId());
                    List<ShoppingUser> users = shoppingUserDao.queryArtplanUsers(reqMap);
                    List<String> mobiles = new ArrayList<>();
                    String mobileStr = "";
                    for(ShoppingUser user:users){
                        mobiles.add(user.getMobile());
                        mobileStr=mobileStr+user.getMobile()+",";
                        if(mobiles.size()==100){
                            sendMsg(mobileStr,shoppingArtplanPush.getPushContent());
                            mobiles.clear();
                            mobileStr = "";
                        }
                    }
                    if(mobiles.size()>0){
                        sendMsg(mobileStr,shoppingArtplanPush.getPushContent());
                    }
                    shoppingArtplanPush.setDoneTime(StringUtil.nowTimeString());
                    shoppingArtplanPush.setPushStatus(2);
                }else{                 //指定用户
                    String mobileStrs = shoppingArtplanPush.getPushMobiles();
                    String[] strs = mobileStrs.split(",");

                    if(strs.length>100){
                        shoppingArtplanPush.setPushStatus(-1);
                    }else{
                        if(sendMsg(mobileStrs,shoppingArtplanPush.getPushContent())){
                            shoppingArtplanPush.setDoneTime(StringUtil.nowTimeString());
                            shoppingArtplanPush.setPushStatus(2);
                        }else{
                            shoppingArtplanPush.setPushStatus(-1);
                        }
                    }
                }
            }else{    //定时推送
                shoppingArtplanPush.setPushStatus(1);
            }
            shoppingArtplanPushDao.insert(shoppingArtplanPush);
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
    public JSONObject queryArtplanPushList(JSONObject reqJson) {
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
            List<ShoppingArtplanPush> objList=shoppingArtplanPushDao.queryList(reqMap);

            bizDataJson.put("objList", objList);
            bizDataJson.put("total", shoppingArtplanPushDao.queryListCount(reqMap));

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
    public JSONObject cancelArtplanPush(JSONObject param) {
        JSONObject retJson = new JSONObject();
        String retCode = "0";
        String retMsg = "操作成功！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String id = param.getString("id");
            ShoppingArtplanPush shoppingArtplanPush = new ShoppingArtplanPush();
            shoppingArtplanPush.setId(id);
            shoppingArtplanPush.setPushStatus(0);
            shoppingArtplanPushDao.update(shoppingArtplanPush);
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
     * 查询爱艺计划可用的规格参数
     */
    @Override
    public JSONObject queryPlanDefaultSpecification(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            HashMap<String, Object> reqMap =new HashMap<>();
            reqMap.put("typeId",aetplanSpecTypeId);
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
