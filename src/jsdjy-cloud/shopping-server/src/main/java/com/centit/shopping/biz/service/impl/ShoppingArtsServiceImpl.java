package com.centit.shopping.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.biz.service.ShoppingArtsService;
import com.centit.shopping.common.enums.Const;
import com.centit.shopping.dao.*;
import com.centit.shopping.po.*;
import com.centit.shopping.redis.RedisStockService;
import com.centit.shopping.utils.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Transactional
@Service
public class ShoppingArtsServiceImpl implements ShoppingArtsService {
    public static final Log log = LogFactory.getLog(ShoppingArtsService.class);

    @Resource
    private ShoppingArtactivityDao shoppingArtactivityDao;

    @Resource
    private ShoppingOrderformDao shoppingOrderformDao;

    @Resource
    private ShoppingOrderLogDao shoppingOrderLogDao;

    @Resource
    private ShoppingOrderPayDao shoppingOrderPayDao;

    @Resource
    private ShoppingGoodscartDao shoppingGoodscartDao;

    @Resource
    private ShoppingPaymentDao shoppingPaymentDao;

    @Resource
    private ShoppingArtinfosDao shoppingArtinfosDao;

    @Resource
    private ShoppingArtactivitySignupinfoDao shoppingArtactivitySignupinfoDao;

    @Resource
    private ShoppingArtclassDao shoppingArtclassDao;

    @Resource
    private ShoppingArtclassSignupinfoDao shoppingArtclassSignupinfoDao;

    @Resource
    private ShoppingArtactivityPhotoDao shoppingArtactivityPhotoDao;

    @Resource
    private ShoppingArtclassPhotoDao shoppingArtclassPhotoDao;

    @Resource
    private ShoppingArtplanDao shoppingArtplanDao;

    @Resource
    private ShoppingArtplanSignupinfoDao shoppingArtplanSignupinfoDao;

    @Resource
    private ShoppingArtplanPhotoDao shoppingArtplanPhotoDao;

    @Resource
    private ShoppingOrderPaykeyDao shoppingOrderPaykeyDao;

    @Resource
    private ShoppingWriteoffDao shoppingWriteoffDao;

    @Resource
    private ShoppingFavoriteDao shoppingFavoriteDao;

    @Resource
    private ShoppingHistoryDao shoppingHistoryDao;

    @Resource
    private ShoppingCouponDao shoppingCouponDao;

    @Resource
    private ShoppingCouponUsertempDao shoppingCouponUsertempDao;

    @Resource
    private ShoppingArtactivityInventoryDao shoppingArtactivityInventoryDao;

    @Resource
    private ShoppingArtactivitySpecDao shoppingArtactivitySpecDao;

    @Resource
    private ShoppingArtplanInventoryDao shoppingArtplanInventoryDao;

    @Resource
    private ShoppingArtplanSpecDao shoppingArtplanSpecDao;

    @Resource
    private ShoppingGoodsspecpropertyDao shoppingGoodsspecpropertyDao;

    @Resource
    private ShoppingGoodsspecificationDao shoppingGoodsspecificationDao;

    @Resource
    private TConcurrencySwitchDao tConcurrencySwitchDao;

    /**
     * Redis 客户端
     */
    @Resource
    private RedisTemplate redisTemplate;

    public static final String REDIS_KEY = "REDIS_KEY:STOCK:";

    public static final String REDIS_KEY_PLAN = "REDIS_KEY:STOCK:PLAN";

    @Resource
    private RedisStockService redisStockService;

    @Value("${moneyToIntegralScale}")
    private int moneyToIntegralScale;

    @Value("${offcodeLength}")
    private int offcodeLength;

    public JSONObject artActivityPageList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            int pageNo = (reqJson.get("pageNo") == null) ? 1 : reqJson.getInteger("pageNo").intValue();
            int pageSize = (reqJson.get("pageSize") == null) ? 10 : reqJson.getInteger("pageSize").intValue();
            HashMap<String, Object> reqMap = (HashMap<String, Object>)JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("startRow", Integer.valueOf((pageNo - 1) * pageSize));
            reqMap.put("pageSize", Integer.valueOf(pageSize));
            reqMap.put("deleteStatus", "0");
            reqMap.put("activityStatus", Integer.valueOf(1));
            reqMap.put("recmmondType", Integer.valueOf(3));
            bizDataJson.put("total", Integer.valueOf(this.shoppingArtactivityDao.queryTotalCount(reqMap)));
            List<ShoppingArtactivity> activityLiat = this.shoppingArtactivityDao.queryList(reqMap);
            JSONArray resArray = new JSONArray();
            for (ShoppingArtactivity shoppingArtactivity : activityLiat) {
                JSONObject resObj = new JSONObject();
                resObj.put("goodsId", shoppingArtactivity.getId());
                resObj.put("goodsName", shoppingArtactivity.getActivityName());
                resObj.put("photoId", shoppingArtactivity.getMainPhotoId());
                resObj.put("goodsPrice", shoppingArtactivity.getCurrentPrice());
                //固定积分需要显示现金价格+固定积分值
                if(shoppingArtactivity.getUseIntegralSet()==1){
                    int integralValue = shoppingArtactivity.getUseIntegralValue();
                    BigDecimal storePrice = shoppingArtactivity.getCurrentPrice();
                    BigDecimal integralAmount = (new BigDecimal(integralValue).divide(new BigDecimal(moneyToIntegralScale))).setScale(2, BigDecimal.ROUND_HALF_UP);
                    BigDecimal restPrice= storePrice.subtract(integralAmount);
                    resObj.put("restPrice", restPrice.compareTo(BigDecimal.ZERO)>=0?restPrice:BigDecimal.ZERO);   //扣除固定积分值的现金价格
                    resObj.put("integralValue", integralValue);  //固定积分值
                }
                resArray.add(resObj);
            }
            bizDataJson.put("objList", resArray);
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

    public JSONObject artPlanPageList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            int pageNo = (reqJson.get("pageNo") == null) ? 1 : reqJson.getInteger("pageNo").intValue();
            int pageSize = (reqJson.get("pageSize") == null) ? 10 : reqJson.getInteger("pageSize").intValue();
            HashMap<String, Object> reqMap = (HashMap<String, Object>)JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("startRow", Integer.valueOf((pageNo - 1) * pageSize));
            reqMap.put("pageSize", Integer.valueOf(pageSize));
            reqMap.put("deleteStatus", "0");
            reqMap.put("activityStatus", Integer.valueOf(1));
            reqMap.put("recmmondType", Integer.valueOf(5));
            bizDataJson.put("total", Integer.valueOf(this.shoppingArtplanDao.queryTotalCount(reqMap)));
            List<ShoppingArtplan> planLiat = this.shoppingArtplanDao.queryList(reqMap);
            JSONArray resArray = new JSONArray();
            for (ShoppingArtplan shoppingArtplan : planLiat) {
                JSONObject resObj = new JSONObject();
                resObj.put("goodsId", shoppingArtplan.getId());
                resObj.put("goodsName", shoppingArtplan.getActivityName());
                resObj.put("photoId", shoppingArtplan.getMainPhotoId());
                resObj.put("goodsPrice", shoppingArtplan.getCurrentPrice());
                //固定积分需要显示现金价格+固定积分值
                if(shoppingArtplan.getUseIntegralSet()==1){
                    int integralValue = shoppingArtplan.getUseIntegralValue();
                    BigDecimal storePrice = shoppingArtplan.getCurrentPrice();
                    BigDecimal integralAmount = (new BigDecimal(integralValue).divide(new BigDecimal(moneyToIntegralScale))).setScale(2, BigDecimal.ROUND_HALF_UP);
                    BigDecimal restPrice= storePrice.subtract(integralAmount);
                    resObj.put("restPrice", restPrice.compareTo(BigDecimal.ZERO)>=0?restPrice:BigDecimal.ZERO);  //扣除固定积分值的现金价格
                    resObj.put("integralValue", integralValue);  //固定积分值
                }
                resObj.put("activityTime", shoppingArtplan.getActivityTime());
                resObj.put("activityLocation", shoppingArtplan.getActivityLocation());
                resArray.add(resObj);
            }
            bizDataJson.put("objList", resArray);
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

    public JSONObject artClassPageList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            int pageNo = (reqJson.get("pageNo") == null) ? 1 : reqJson.getInteger("pageNo").intValue();
            int pageSize = (reqJson.get("pageSize") == null) ? 10 : reqJson.getInteger("pageSize").intValue();
            HashMap<String, Object> reqMap = (HashMap<String, Object>)JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("startRow", Integer.valueOf((pageNo - 1) * pageSize));
            reqMap.put("pageSize", Integer.valueOf(pageSize));
            reqMap.put("deleteStatus", "0");
            reqMap.put("classStatus", Integer.valueOf(1));
            reqMap.put("recmmondType", Integer.valueOf(4));
            bizDataJson.put("total", Integer.valueOf(this.shoppingArtclassDao.queryTotalCount(reqMap)));
            List<ShoppingArtclass> classList = this.shoppingArtclassDao.queryList(reqMap);
            JSONArray resArray = new JSONArray();
            for (ShoppingArtclass shoppingArtclass : classList) {
                JSONObject resObj = new JSONObject();
                resObj.put("goodsId", shoppingArtclass.getId());
                resObj.put("goodsName", shoppingArtclass.getClassName());
                resObj.put("photoId", shoppingArtclass.getMainPhotoId());
                resObj.put("goodsPrice", shoppingArtclass.getCurrentPrice());
                resArray.add(resObj);
            }
            bizDataJson.put("objList", resArray);
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

    public JSONObject artActivityDetail(String activityId, JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingArtactivity shoppingArtactivity = new ShoppingArtactivity();
            shoppingArtactivity.setId(activityId);
            shoppingArtactivity = this.shoppingArtactivityDao.queryDetail(shoppingArtactivity);
            if (shoppingArtactivity.getDeleteStatus().equals("1") || shoppingArtactivity.getActivityStatus().intValue() == 0) {
                bizDataJson.put("isOff", Boolean.valueOf(true));
            } else {
                bizDataJson.put("isOff", Boolean.valueOf(false));
            }
            JSONObject activityDetail = new JSONObject();
            activityDetail.put("activityId", shoppingArtactivity.getId());
            activityDetail.put("activityName", shoppingArtactivity.getActivityName());
            activityDetail.put("photoId", shoppingArtactivity.getMainPhotoId());
            activityDetail.put("signupStarttime", shoppingArtactivity.getSignupStarttime());
            activityDetail.put("signupEndtime", shoppingArtactivity.getSignupEndtime());
            activityDetail.put("activityTime", shoppingArtactivity.getActivityTime());
            activityDetail.put("activityLocation", shoppingArtactivity.getActivityLocation());
            activityDetail.put("signupTotalLimit", shoppingArtactivity.getSignupTotalLimit());
            activityDetail.put("showLeftnum", shoppingArtactivity.getShowLeftnum());
            activityDetail.put("leftnum", shoppingArtactivity.getLeftnum());
            activityDetail.put("signupPerLimit", shoppingArtactivity.getSignupPerLimit());
            activityDetail.put("cancelEnable", shoppingArtactivity.getCancelEnable());
            activityDetail.put("cuttentPrice", shoppingArtactivity.getCurrentPrice());
            activityDetail.put("originPrice", shoppingArtactivity.getOriginPrice());
            activityDetail.put("useIntegralSet", shoppingArtactivity.getUseIntegralSet());
            activityDetail.put("useIntegralValue", shoppingArtactivity.getUseIntegralValue());
            activityDetail.put("useBalanceSet", shoppingArtactivity.getUseBalanceSet());
            activityDetail.put("useMembershipSet", shoppingArtactivity.getUseMembershipSet());
            activityDetail.put("activityNotice", shoppingArtactivity.getActivityNotice());
            activityDetail.put("returnExplain", shoppingArtactivity.getReturnExplain());
            activityDetail.put("details", shoppingArtactivity.getActivityDetails());
            HashMap<String, Object> photoMap = new HashMap<>();
            photoMap.put("activityId", activityId);
            List<ShoppingArtactivityPhoto> photos = this.shoppingArtactivityPhotoDao.queryList(photoMap);
            activityDetail.put("photos", photos);

            JSONArray specArray = new JSONArray();
            List<ShoppingArtactivityInventory> inventoryList = new ArrayList<>();
            HashMap<String, Object> reqMap = new HashMap<>();
            //开关打开时，默认是抢票活动，抢票活动目前不支持规格属性
            if (openConcurrencySwitch()) {
                bizDataJson.put("couponList", new ArrayList());

                activityDetail.put("minPrice", shoppingArtactivity.getCurrentPrice());
                activityDetail.put("maxPrice", shoppingArtactivity.getCurrentPrice());

            } else {
                reqMap.put("goodsId", activityId);
                reqMap.put("goodsType", Integer.valueOf(3));
                List<ShoppingCoupon> couponList = this.shoppingCouponDao.queryArtCouponList(reqMap);
                bizDataJson.put("couponList", couponList);
                if (null != reqJson.get("userId")) {
                    String userId = reqJson.getString("userId");
                    ShoppingHistory shoppingHistory = new ShoppingHistory();
                    shoppingHistory.setUserId(userId);
                    shoppingHistory.setGoodsId(activityId);
                    shoppingHistory.setType(Integer.valueOf(3));
                    this.shoppingHistoryDao.insert(shoppingHistory);
                    reqMap.clear();
                    reqMap.put("type", Integer.valueOf(3));
                    reqMap.put("goodsId", activityId);
                    reqMap.put("userId", userId);
                    reqMap.put("deleteStatus", Character.valueOf('0'));
                    if (!this.shoppingFavoriteDao.queryList(reqMap).isEmpty()) {
                        activityDetail.put("isFav", Boolean.valueOf(true));
                    } else {
                        activityDetail.put("isFav", Boolean.valueOf(false));
                    }
                }

                reqMap = new HashMap<>();
                reqMap.put("activityId", activityId);
                List<BigDecimal> priceList = new ArrayList<>();
                inventoryList = this.shoppingArtactivityInventoryDao.queryList(reqMap);
                HashSet<String> propertyIds = new HashSet<>();
                for (ShoppingArtactivityInventory shoppingArtactivityInventory : inventoryList) {
                    String[] sps = shoppingArtactivityInventory.getPropertys().split("_");
                    for (int m = 0; m < sps.length; m++) {
                        String propertyId = sps[m];
                        propertyIds.add(propertyId);
                    }
                    priceList.add(shoppingArtactivityInventory.getPrice());
                }
                if (priceList.size() == 1) {
                    activityDetail.put("minPrice", shoppingArtactivity.getCurrentPrice());
                    activityDetail.put("maxPrice", shoppingArtactivity.getCurrentPrice());
                } else if (priceList.size() > 1) {
                    Map<String, BigDecimal> resMap = StringUtil.getMaxMin(priceList);
                    activityDetail.put("minPrice", resMap.get("min"));
                    activityDetail.put("maxPrice", resMap.get("max"));
                }
                List<ShoppingGoodsspecification> specs = this.shoppingGoodsspecificationDao.queryActSpecs(reqMap);

                for (ShoppingGoodsspecification shoppingGoodsspecification : specs) {
                    JSONObject obj = new JSONObject();
                    obj.put("specId", shoppingGoodsspecification.getId());
                    obj.put("specName", shoppingGoodsspecification.getName());
                    JSONArray propertyArray = new JSONArray();
                    List<ShoppingGoodsspecproperty> propertys = CommonUtil.getPropertys(shoppingGoodsspecification.getId());
                    for (ShoppingGoodsspecproperty shoppingGoodsspecproperty : propertys) {
                        if (propertyIds.contains(shoppingGoodsspecproperty.getId())) {
                            JSONObject propertyObj = new JSONObject();
                            propertyObj.put("propertyId", shoppingGoodsspecproperty.getId());
                            propertyObj.put("propertyValue", shoppingGoodsspecproperty.getValue());
                            propertyArray.add(propertyObj);
                        }
                    }
                    obj.put("propertys", propertyArray);
                    specArray.add(obj);
                }
            }

            bizDataJson.put("specs", specArray);
            bizDataJson.put("inventoryDetails", inventoryList);
            bizDataJson.put("data", activityDetail);
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

    public JSONObject artPlanDetail(String activityId, JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingArtplan shoppingArtplan = new ShoppingArtplan();
            shoppingArtplan.setId(activityId);
            shoppingArtplan = this.shoppingArtplanDao.queryDetail(shoppingArtplan);
            if (shoppingArtplan.getDeleteStatus().equals("1") || shoppingArtplan.getActivityStatus().intValue() == 0) {
                bizDataJson.put("isOff", Boolean.valueOf(true));
            } else {
                bizDataJson.put("isOff", Boolean.valueOf(false));
            }
            JSONObject activityDetail = new JSONObject();
            activityDetail.put("activityId", shoppingArtplan.getId());
            activityDetail.put("activityName", shoppingArtplan.getActivityName());
            activityDetail.put("photoId", shoppingArtplan.getMainPhotoId());
            activityDetail.put("signupStarttime", shoppingArtplan.getSignupStarttime());
            activityDetail.put("signupEndtime", shoppingArtplan.getSignupEndtime());
            activityDetail.put("activityTime", shoppingArtplan.getActivityTime());
            activityDetail.put("activityLocation", shoppingArtplan.getActivityLocation());
            activityDetail.put("signupTotalLimit", shoppingArtplan.getSignupTotalLimit());
            activityDetail.put("showLeftnum", shoppingArtplan.getShowLeftnum());
            activityDetail.put("leftnum", shoppingArtplan.getLeftnum());
            activityDetail.put("signupPerLimit", shoppingArtplan.getSignupPerLimit());
            activityDetail.put("cancelEnable", shoppingArtplan.getCancelEnable());
            activityDetail.put("cuttentPrice", shoppingArtplan.getCurrentPrice());
            activityDetail.put("originPrice", shoppingArtplan.getOriginPrice());
            activityDetail.put("useIntegralSet", shoppingArtplan.getUseIntegralSet());
            activityDetail.put("useIntegralValue", shoppingArtplan.getUseIntegralValue());
            activityDetail.put("useBalanceSet", shoppingArtplan.getUseBalanceSet());
            activityDetail.put("useMembershipSet", shoppingArtplan.getUseMembershipSet());
            activityDetail.put("activityNotice", shoppingArtplan.getActivityNotice());
            activityDetail.put("returnExplain", shoppingArtplan.getReturnExplain());
            activityDetail.put("details", shoppingArtplan.getActivityDetails());
            HashMap<String, Object> photoMap = new HashMap<>();
            photoMap.put("activityId", activityId);
            List<ShoppingArtplanPhoto> photos = this.shoppingArtplanPhotoDao.queryList(photoMap);
            activityDetail.put("photos", photos);
            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.clear();
            reqMap.put("goodsId", activityId);
            reqMap.put("goodsType", Integer.valueOf(6));
            List<ShoppingCoupon> couponList = this.shoppingCouponDao.queryArtCouponList(reqMap);
            bizDataJson.put("couponList", couponList);
            if (null != reqJson.get("userId")) {
                String userId = reqJson.getString("userId");
                ShoppingHistory shoppingHistory = new ShoppingHistory();
                shoppingHistory.setUserId(userId);
                shoppingHistory.setGoodsId(activityId);
                shoppingHistory.setType(Integer.valueOf(5));
                this.shoppingHistoryDao.insert(shoppingHistory);
                reqMap.clear();
                reqMap.put("type", Integer.valueOf(5));
                reqMap.put("goodsId", activityId);
                reqMap.put("userId", userId);
                reqMap.put("deleteStatus", Character.valueOf('0'));
                if (!this.shoppingFavoriteDao.queryList(reqMap).isEmpty()) {
                    activityDetail.put("isFav", Boolean.valueOf(true));
                } else {
                    activityDetail.put("isFav", Boolean.valueOf(false));
                }
            }
            reqMap = new HashMap<>();
            reqMap.put("activityId", activityId);
            List<BigDecimal> priceList = new ArrayList<>();
            List<ShoppingArtplanInventory> inventoryList = this.shoppingArtplanInventoryDao.queryList(reqMap);
            HashSet<String> propertyIds = new HashSet<>();
            for (ShoppingArtplanInventory shoppingArtplanInventory : inventoryList) {
                String[] sps = shoppingArtplanInventory.getPropertys().split("_");
                for (int m = 0; m < sps.length; m++) {
                    String propertyId = sps[m];
                    propertyIds.add(propertyId);
                }
                priceList.add(shoppingArtplanInventory.getPrice());
            }
            if (priceList.size() == 1) {
                activityDetail.put("minPrice", shoppingArtplan.getCurrentPrice());
                activityDetail.put("maxPrice", shoppingArtplan.getCurrentPrice());
            } else if (priceList.size() > 1) {
                Map<String, BigDecimal> resMap = StringUtil.getMaxMin(priceList);
                activityDetail.put("minPrice", resMap.get("min"));
                activityDetail.put("maxPrice", resMap.get("max"));
            }
            List<ShoppingGoodsspecification> specs = this.shoppingGoodsspecificationDao.queryPlanSpecs(reqMap);
            JSONArray specArray = new JSONArray();
            for (ShoppingGoodsspecification shoppingGoodsspecification : specs) {
                JSONObject obj = new JSONObject();
                obj.put("specId", shoppingGoodsspecification.getId());
                obj.put("specName", shoppingGoodsspecification.getName());
                JSONArray propertyArray = new JSONArray();
                List<ShoppingGoodsspecproperty> propertys = CommonUtil.getPropertys(shoppingGoodsspecification.getId());
                for (ShoppingGoodsspecproperty shoppingGoodsspecproperty : propertys) {
                    if (propertyIds.contains(shoppingGoodsspecproperty.getId())) {
                        JSONObject propertyObj = new JSONObject();
                        propertyObj.put("propertyId", shoppingGoodsspecproperty.getId());
                        propertyObj.put("propertyValue", shoppingGoodsspecproperty.getValue());
                        propertyArray.add(propertyObj);
                    }
                }
                obj.put("propertys", propertyArray);
                specArray.add(obj);
            }
            bizDataJson.put("specs", specArray);
            bizDataJson.put("inventoryDetails", inventoryList);
            bizDataJson.put("data", activityDetail);
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

    public JSONObject artClassDetail(String classId, JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingArtclass shoppingArtclass = new ShoppingArtclass();
            shoppingArtclass.setId(classId);
            shoppingArtclass = this.shoppingArtclassDao.queryDetail(shoppingArtclass);
            if (shoppingArtclass.getDeleteStatus().equals("1") || shoppingArtclass.getClassStatus().intValue() == 0) {
                bizDataJson.put("isOff", Boolean.valueOf(true));
            } else {
                bizDataJson.put("isOff", Boolean.valueOf(false));
            }
            JSONObject classDetail = new JSONObject();
            classDetail.put("classId", shoppingArtclass.getId());
            classDetail.put("className", shoppingArtclass.getClassName());
            classDetail.put("photoId", shoppingArtclass.getMainPhotoId());
            classDetail.put("classRemark", shoppingArtclass.getClassRemark());
            classDetail.put("cuttentPrice", shoppingArtclass.getCurrentPrice());
            classDetail.put("originPrice", shoppingArtclass.getOriginPrice());
            classDetail.put("useIntegralSet", shoppingArtclass.getUseIntegralSet());
            classDetail.put("useIntegralValue", shoppingArtclass.getUseIntegralValue());
            classDetail.put("useBalanceSet", shoppingArtclass.getUseBalanceSet());
            classDetail.put("useMembershipSet", shoppingArtclass.getUseMembershipSet());
            classDetail.put("classNotice", shoppingArtclass.getClassNotice());
            classDetail.put("returnExplain", shoppingArtclass.getReturnExplain());
            classDetail.put("details", shoppingArtclass.getDetails());
            HashMap<String, Object> photoMap = new HashMap<>();
            photoMap.put("classId", classId);
            List<ShoppingArtclassPhoto> photos = this.shoppingArtclassPhotoDao.queryList(photoMap);
            classDetail.put("photos", photos);
            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.clear();
            reqMap.put("goodsId", classId);
            reqMap.put("goodsType", Integer.valueOf(4));
            List<ShoppingCoupon> couponList = this.shoppingCouponDao.queryArtCouponList(reqMap);
            bizDataJson.put("couponList", couponList);
            if (null != reqJson.get("userId")) {
                String userId = reqJson.getString("userId");
                ShoppingHistory shoppingHistory = new ShoppingHistory();
                shoppingHistory.setUserId(userId);
                shoppingHistory.setGoodsId(classId);
                shoppingHistory.setType(Integer.valueOf(4));
                this.shoppingHistoryDao.insert(shoppingHistory);
                reqMap.clear();
                reqMap.put("type", Integer.valueOf(4));
                reqMap.put("goodsId", classId);
                reqMap.put("userId", userId);
                reqMap.put("deleteStatus", Character.valueOf('0'));
                if (!this.shoppingFavoriteDao.queryList(reqMap).isEmpty()) {
                    classDetail.put("isFav", Boolean.valueOf(true));
                } else {
                    classDetail.put("isFav", Boolean.valueOf(false));
                }
            }
            bizDataJson.put("data", classDetail);
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

    public JSONObject checkActivityLimit(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String activityId = reqJson.getString("activityId");
            int count = reqJson.getInteger("count").intValue();
            String userId = reqJson.getString("userId");
            String goodsStoreId = CommonUtil.getSystemStore().getId();
            ShoppingArtactivity shoppingArtactivity = new ShoppingArtactivity();
            shoppingArtactivity.setId(activityId);
            shoppingArtactivity = this.shoppingArtactivityDao.queryDetail(shoppingArtactivity);
            bizDataJson.put("peiLimit", shoppingArtactivity.getSignupPerLimit());
            if (shoppingArtactivity.getSignupPerLimit().intValue() == 0) {
                bizDataJson.put("result", Boolean.valueOf(false));
            } else {
                int limitBuy = shoppingArtactivity.getSignupPerLimit().intValue();
                String scId = CommonUtil.getUserScId(userId);
                HashMap<String, Object> reqMap = new HashMap<>();
                reqMap.put("scId", scId);
                reqMap.put("goodsId", activityId);
                reqMap.put("userId", userId);
                reqMap.put("deleteStatus", "0");
                List<ShoppingGoodscart> shoppingGoodscartList = this.shoppingGoodscartDao.queryBuyList(reqMap);
                int cartCount = 0;
                for (ShoppingGoodscart shoppingGoodscart : shoppingGoodscartList)
                    cartCount += shoppingGoodscart.getCount().intValue();
                bizDataJson.put("doneCount", Integer.valueOf(cartCount));
                if (cartCount + count > limitBuy) {
                    bizDataJson.put("result", Boolean.valueOf(true));
                } else {
                    bizDataJson.put("result", Boolean.valueOf(false));
                }
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

    public JSONObject checkPlanLimit(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String activityId = reqJson.getString("activityId");
            int count = reqJson.getInteger("count").intValue();
            String userId = reqJson.getString("userId");
            String goodsStoreId = CommonUtil.getSystemStore().getId();
            ShoppingArtplan shoppingArtplan = new ShoppingArtplan();
            shoppingArtplan.setId(activityId);
            shoppingArtplan = this.shoppingArtplanDao.queryDetail(shoppingArtplan);
            bizDataJson.put("peiLimit", shoppingArtplan.getSignupPerLimit());
            if (shoppingArtplan.getSignupPerLimit().intValue() == 0) {
                bizDataJson.put("result", Boolean.valueOf(false));
            } else {
                int limitBuy = shoppingArtplan.getSignupPerLimit().intValue();
                String scId = CommonUtil.getUserScId(userId);
                HashMap<String, Object> reqMap = new HashMap<>();
                reqMap.put("scId", scId);
                reqMap.put("goodsId", activityId);
                reqMap.put("userId", userId);
                reqMap.put("deleteStatus", "0");
                List<ShoppingGoodscart> shoppingGoodscartList = this.shoppingGoodscartDao.queryBuyList(reqMap);
                int cartCount = 0;
                for (ShoppingGoodscart shoppingGoodscart : shoppingGoodscartList)
                    cartCount += shoppingGoodscart.getCount().intValue();
                bizDataJson.put("doneCount", Integer.valueOf(cartCount));
                if (cartCount + count > limitBuy) {
                    bizDataJson.put("result", Boolean.valueOf(true));
                } else {
                    bizDataJson.put("result", Boolean.valueOf(false));
                }
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

    public JSONObject renderActivityOrder(JSONObject reqJson, HttpServletRequest request) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "服务器内部错误！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String userId = reqJson.getString("userId");
            String mzUserId = CommonUtil.getMzUserId(userId);
            String activityId = reqJson.getString("activityId");
            int goodsCount = reqJson.getInteger("goodsCount").intValue();
            ShoppingArtactivity shoppingArtactivity = new ShoppingArtactivity();
            shoppingArtactivity.setId(activityId);
            shoppingArtactivity = this.shoppingArtactivityDao.queryDetail(shoppingArtactivity);
            BigDecimal currentPrice = BigDecimal.ZERO;
            String specInfo = "";
            if (null != reqJson.get("propertys") && !"".equals(reqJson.get("propertys"))) {
                ShoppingArtactivityInventory shoppingArtactivityInventory = new ShoppingArtactivityInventory();
                shoppingArtactivityInventory.setActivityId(shoppingArtactivity.getId());
                shoppingArtactivityInventory.setPropertys(reqJson.getString("propertys"));
                shoppingArtactivityInventory = this.shoppingArtactivityInventoryDao.queryDetail(shoppingArtactivityInventory);
                currentPrice = shoppingArtactivityInventory.getPrice();
                String[] strs = reqJson.getString("propertys").split("_");
                for (int j = 0; j < strs.length; j++) {
                    ShoppingGoodsspecproperty shoppingGoodsspecproperty = new ShoppingGoodsspecproperty();
                    shoppingGoodsspecproperty.setId(strs[j]);
                    shoppingGoodsspecproperty = this.shoppingGoodsspecpropertyDao.queryDetail(shoppingGoodsspecproperty);
                    specInfo = specInfo + shoppingGoodsspecproperty.getValue() + ";";
                }
            } else {
                currentPrice = shoppingArtactivity.getCurrentPrice();
            }
            bizDataJson.put("currentPrice", currentPrice);
            JSONArray goodsArray = new JSONArray();
            JSONObject goodsObj = new JSONObject();
            goodsObj.put("activityId", activityId);
            goodsObj.put("activityName", shoppingArtactivity.getActivityName());
            goodsObj.put("currentPrice", currentPrice);
            goodsObj.put("goodsCount", Integer.valueOf(goodsCount));
            goodsObj.put("photoId", shoppingArtactivity.getMainPhotoId());
            if (null != reqJson.get("propertys")) {
                goodsObj.put("propertys", reqJson.get("propertys"));
                goodsObj.put("specInfo", specInfo);
            }
            goodsArray.add(goodsObj);
            bizDataJson.put("activityInfoList", goodsArray);
            BigDecimal goodsPrice = currentPrice.multiply(new BigDecimal(goodsCount)).setScale(2, 4);
            bizDataJson.put("goodsPrice", goodsPrice);
            BigDecimal couponCut = BigDecimal.ZERO;
            BigDecimal accountCut = BigDecimal.ZERO;
            BigDecimal integralCut = BigDecimal.ZERO;
            BigDecimal balanceCut = BigDecimal.ZERO;
            int useIntegralSet = shoppingArtactivity.getUseIntegralSet().intValue();
            int useIntegralValue = shoppingArtactivity.getUseIntegralValue().intValue();
            int useBalanceSet = shoppingArtactivity.getUseBalanceSet().intValue();
            int useMembershipSet = shoppingArtactivity.getUseMembershipSet().intValue();
            bizDataJson.put("useMembershipSet", Integer.valueOf(useMembershipSet));
            bizDataJson.put("useIntegralSet", Integer.valueOf(useIntegralSet));
            bizDataJson.put("useBalanceSet", Integer.valueOf(useBalanceSet));
            BigDecimal totalPrice = currentPrice.multiply(new BigDecimal(goodsCount));
            BigDecimal cashPrice = BigDecimal.ZERO;
            int fixedIntegalValue = 0;
            if (useIntegralSet == 1) {
                if (currentPrice.multiply(new BigDecimal(100)).intValue() < useIntegralValue)
                    useIntegralValue = currentPrice.multiply(new BigDecimal(100)).intValue();
                fixedIntegalValue = useIntegralValue * goodsCount;
                cashPrice = totalPrice.subtract((new BigDecimal(fixedIntegalValue)).divide(new BigDecimal(this.moneyToIntegralScale)).setScale(2, 4));
            }
            BigDecimal payPrice = BigDecimal.ZERO;
            if (useIntegralSet == 1) {
                payPrice = cashPrice.multiply(new BigDecimal(goodsCount));
            } else {
                payPrice = currentPrice.multiply(new BigDecimal(goodsCount));
            }
            List<String> couponIds = new ArrayList<>();
            JSONArray couponArray = CommonUtil.getArtCouppon(activityId, 3, userId, goodsPrice, payPrice);
            for (int i = 0; i < couponArray.size(); i++) {
                JSONObject obj = couponArray.getJSONObject(i);
                couponIds.add(obj.getString("id"));
            }
            bizDataJson.put("couponList", couponArray);
            boolean accountState = true;
            if (useIntegralSet != 0 || useBalanceSet != 0) {
                JSONObject accountObj = MZService.getAssetinfo(mzUserId);
                if (null != accountObj) {
                    int account_point = (accountObj.get("account_point") == null) ? 0 : accountObj.getInteger("account_point").intValue();
                    int account_money_fen = (accountObj.get("account_money_fen") == null) ? 0 : accountObj.getInteger("account_money_fen").intValue();
                    BigDecimal accountMoney = (new BigDecimal(account_money_fen)).divide(new BigDecimal(100)).setScale(2, 4);
                    bizDataJson.put("accountPoint", Integer.valueOf(account_point));
                    bizDataJson.put("accountMoney", accountMoney);
                } else {
                    accountState = false;
                    bizDataJson.put("accountPoint", Integer.valueOf(0));
                    bizDataJson.put("accountMoney", Integer.valueOf(0));
                }
                ShoppingAssetRule shoppingAssetRule = CommonUtil.getAssetRule();
                if (null != shoppingAssetRule) {
                    bizDataJson.put("accountPointLimit", Integer.valueOf(shoppingAssetRule.getPointAvoidLimit()));
                    bizDataJson.put("accountMoneyLimit", shoppingAssetRule.getAccountAvoidLimit());
                }
            }
            String signupStarttime = shoppingArtactivity.getSignupStarttime();
            String signupEndtime = shoppingArtactivity.getSignupEndtime();
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (StringUtil.compareMillisecond(signupStarttime, sf) > 0 || StringUtil.compareMillisecond(signupEndtime, sf) < 0) {
                retCode = "-1";
                retMsg = "当前时间不可报名！";
            } else if (!accountState) {
                retCode = "-1";
                retMsg = "无法获取您的账户积分和余额数据，请稍后再试！";
            } else {
                BigDecimal originPrice = payPrice;
                int originntegalValue = fixedIntegalValue;
                if (null != reqJson.get("couponId") && !"".equals(reqJson.get("couponId")) && couponIds.contains(reqJson.getString("couponId"))) {
                    String couponId = reqJson.getString("couponId");
                    JSONObject couponDtl = CRMService.getUserCouponDtl(couponId);
                    if (null != couponDtl) {
                        String right_No = couponDtl.getString("right_No");
                        ShoppingCoupon shoppingCoupon = new ShoppingCoupon();
                        shoppingCoupon.setRight_No(right_No);
                        shoppingCoupon = this.shoppingCouponDao.queryDetail(shoppingCoupon);
                        if (shoppingCoupon.getRight_Type().equals("coincp")) {
                            int couponAmount = Integer.valueOf(shoppingCoupon.getRight_Content()).intValue();
                            if ((new BigDecimal(couponAmount)).compareTo(payPrice) == 1) {
                                payPrice = BigDecimal.ZERO;
                            } else {
                                payPrice = payPrice.subtract(new BigDecimal(couponAmount));
                            }
                        } else {
                            BigDecimal discount = new BigDecimal(shoppingCoupon.getRight_Content());
                            payPrice = payPrice.multiply(discount).setScale(2, 4);
                            if (useIntegralSet == 1)
                                fixedIntegalValue = (new BigDecimal(fixedIntegalValue)).multiply(discount).intValue();
                        }
                        if (useIntegralSet == 1) {
                            couponCut = originPrice.subtract(payPrice).add((new BigDecimal(originntegalValue - fixedIntegalValue)).divide(new BigDecimal(this.moneyToIntegralScale)).setScale(2, 4));
                        } else {
                            couponCut = originPrice.subtract(payPrice);
                        }
                        originPrice = payPrice;
                        originntegalValue = fixedIntegalValue;
                    }
                }
                if (useMembershipSet == 1) {
                    BigDecimal discount = CommonUtil.getUserMemberShip(userId);
                    payPrice = payPrice.multiply(discount).setScale(2, 4);
                    if (useIntegralSet == 1)
                        fixedIntegalValue = (new BigDecimal(fixedIntegalValue)).multiply(discount).intValue();
                    if (useIntegralSet == 1) {
                        accountCut = originPrice.subtract(payPrice).add((new BigDecimal(originntegalValue - fixedIntegalValue)).divide(new BigDecimal(this.moneyToIntegralScale)).setScale(2, 4));
                    } else {
                        accountCut = originPrice.subtract(payPrice);
                    }
                    originPrice = payPrice;
                    originntegalValue = fixedIntegalValue;
                }
                ShoppingPayLimit payLimit = CommonUtil.getPayLimit();
                bizDataJson.put("pointPayLimit", Integer.valueOf(payLimit.getPointPay()));
                bizDataJson.put("balancePayLimit", Integer.valueOf(payLimit.getBalancePay()));
                int maxIntegralValue = 0;
                int accountPoint = (bizDataJson.get("accountPoint") == null) ? 0 : bizDataJson.getInteger("accountPoint").intValue();
                int pointPay = payLimit.getPointPay();
                if (useIntegralSet == 1 || (null != reqJson.get("useIntegral") && reqJson.getInteger("useIntegral").intValue() == 1)) {
                    if (useIntegralSet == 1) {
                        maxIntegralValue = fixedIntegalValue;
                    } else if (useIntegralSet == 2) {
                        int payPriceToInt = payPrice.multiply(new BigDecimal(this.moneyToIntegralScale)).intValue();
                        maxIntegralValue = (useIntegralValue * goodsCount < payPriceToInt) ? (useIntegralValue * goodsCount) : payPriceToInt;
                        int pointLimit = accountPoint;
                        if (pointPay > 0)
                            pointLimit = (pointPay > pointLimit) ? pointLimit : pointPay;
                        if (maxIntegralValue > pointLimit)
                            maxIntegralValue = pointLimit;
                    }
                    bizDataJson.put("useIntegralValue", Integer.valueOf(maxIntegralValue));
                    BigDecimal integralAmount = (new BigDecimal(maxIntegralValue)).divide(new BigDecimal(this.moneyToIntegralScale)).setScale(2, 4);
                    if (useIntegralSet == 1) {
                        integralCut = integralAmount;
                    } else {
                        payPrice = payPrice.subtract(integralAmount);
                        integralCut = originPrice.subtract(payPrice);
                    }
                    originPrice = payPrice;
                }
                if (maxIntegralValue > accountPoint) {
                    retCode = "1";
                    retMsg = "当前账户积分不够，无法下单！";
                } else if (maxIntegralValue > pointPay) {
                    retCode = "1";
                    retMsg = "积分单次支付限额" + pointPay + ",当前订单已超出额度，无法下单！";
                } else {
                    if (null != reqJson.get("useBalance") && reqJson.getInteger("useBalance").intValue() == 1) {
                        BigDecimal deductionBalance = BigDecimal.ZERO;
                        BigDecimal accountLimit = bizDataJson.getBigDecimal("accountMoney");
                        BigDecimal balancePay = new BigDecimal(payLimit.getBalancePay());
                        if (balancePay.compareTo(BigDecimal.ZERO) > 0 && balancePay.compareTo(accountLimit) < 0)
                            accountLimit = balancePay;
                        if (payPrice.compareTo(accountLimit) == 1) {
                            deductionBalance = accountLimit;
                        } else {
                            deductionBalance = payPrice;
                        }
                        payPrice = payPrice.subtract(deductionBalance);
                        balanceCut = originPrice.subtract(payPrice);
                    }
                    HashMap<String, Object> reqMap = new HashMap<>();
                    reqMap.put("activityId", activityId);
                    List<ShoppingArtinfos> infos = this.shoppingArtinfosDao.queryActivityInfoList(reqMap);
                    bizDataJson.put("infos", infos);
                    bizDataJson.put("totalPrice", totalPrice);
                    bizDataJson.put("payPrice", payPrice);
                    bizDataJson.put("deductionCouponPrice", couponCut);
                    bizDataJson.put("deductionMemberPrice", accountCut);
                    bizDataJson.put("deductionIntegralPrice", integralCut);
                    bizDataJson.put("deductionBalancePrice", balanceCut);
                    retCode = "0";
                    retMsg = "操作成功！";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    public JSONObject renderPlanOrder(JSONObject reqJson, HttpServletRequest request) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "服务器内部错误！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String userId = reqJson.getString("userId");
            String mzUserId = CommonUtil.getMzUserId(userId);
            String activityId = reqJson.getString("activityId");
            int goodsCount = reqJson.getInteger("goodsCount").intValue();
            ShoppingArtplan shoppingArtplan = new ShoppingArtplan();
            shoppingArtplan.setId(activityId);
            shoppingArtplan = this.shoppingArtplanDao.queryDetail(shoppingArtplan);
            BigDecimal currentPrice = BigDecimal.ZERO;
            String specInfo = "";
            if (null != reqJson.get("propertys") && !"".equals(reqJson.get("propertys"))) {
                ShoppingArtplanInventory shoppingArtplanInventory = new ShoppingArtplanInventory();
                shoppingArtplanInventory.setActivityId(shoppingArtplan.getId());
                shoppingArtplanInventory.setPropertys(reqJson.getString("propertys"));
                shoppingArtplanInventory = this.shoppingArtplanInventoryDao.queryDetail(shoppingArtplanInventory);
                currentPrice = shoppingArtplanInventory.getPrice();
                String[] strs = reqJson.getString("propertys").split("_");
                for (int j = 0; j < strs.length; j++) {
                    ShoppingGoodsspecproperty shoppingGoodsspecproperty = new ShoppingGoodsspecproperty();
                    shoppingGoodsspecproperty.setId(strs[j]);
                    shoppingGoodsspecproperty = this.shoppingGoodsspecpropertyDao.queryDetail(shoppingGoodsspecproperty);
                    specInfo = specInfo + shoppingGoodsspecproperty.getValue() + ";";
                }
            } else {
                currentPrice = shoppingArtplan.getCurrentPrice();
            }
            bizDataJson.put("currentPrice", currentPrice);
            JSONArray goodsArray = new JSONArray();
            JSONObject goodsObj = new JSONObject();
            goodsObj.put("activityId", activityId);
            goodsObj.put("activityName", shoppingArtplan.getActivityName());
            goodsObj.put("currentPrice", currentPrice);
            goodsObj.put("goodsCount", Integer.valueOf(goodsCount));
            goodsObj.put("photoId", shoppingArtplan.getMainPhotoId());
            if (null != reqJson.get("propertys")) {
                goodsObj.put("propertys", reqJson.get("propertys"));
                goodsObj.put("specInfo", specInfo);
            }
            goodsArray.add(goodsObj);
            bizDataJson.put("activityInfoList", goodsArray);
            BigDecimal goodsPrice = currentPrice.multiply(new BigDecimal(goodsCount)).setScale(2, 4);
            bizDataJson.put("goodsPrice", goodsPrice);
            BigDecimal couponCut = BigDecimal.ZERO;
            BigDecimal accountCut = BigDecimal.ZERO;
            BigDecimal integralCut = BigDecimal.ZERO;
            BigDecimal balanceCut = BigDecimal.ZERO;
            int useIntegralSet = shoppingArtplan.getUseIntegralSet().intValue();
            int useIntegralValue = shoppingArtplan.getUseIntegralValue().intValue();
            int useBalanceSet = shoppingArtplan.getUseBalanceSet().intValue();
            int useMembershipSet = shoppingArtplan.getUseMembershipSet().intValue();
            bizDataJson.put("useMembershipSet", Integer.valueOf(useMembershipSet));
            bizDataJson.put("useIntegralSet", Integer.valueOf(useIntegralSet));
            bizDataJson.put("useBalanceSet", Integer.valueOf(useBalanceSet));
            BigDecimal totalPrice = currentPrice.multiply(new BigDecimal(goodsCount));
            BigDecimal cashPrice = BigDecimal.ZERO;
            int fixedIntegalValue = 0;
            if (useIntegralSet == 1) {
                if (currentPrice.multiply(new BigDecimal(100)).intValue() < useIntegralValue)
                    useIntegralValue = currentPrice.multiply(new BigDecimal(100)).intValue();
                fixedIntegalValue = useIntegralValue * goodsCount;
                cashPrice = totalPrice.subtract((new BigDecimal(fixedIntegalValue)).divide(new BigDecimal(this.moneyToIntegralScale)).setScale(2, 4));
            }
            BigDecimal payPrice = BigDecimal.ZERO;
            if (useIntegralSet == 1) {
                payPrice = cashPrice.multiply(new BigDecimal(goodsCount));
            } else {
                payPrice = currentPrice.multiply(new BigDecimal(goodsCount));
            }
            List<String> couponIds = new ArrayList<>();
            JSONArray couponArray = CommonUtil.getArtCouppon(activityId, 6, userId, goodsPrice, payPrice);
            for (int i = 0; i < couponArray.size(); i++) {
                JSONObject obj = couponArray.getJSONObject(i);
                couponIds.add(obj.getString("id"));
            }
            bizDataJson.put("couponList", couponArray);
            boolean accountState = true;
            if (useIntegralSet != 0 || useBalanceSet != 0) {
                JSONObject accountObj = MZService.getAssetinfo(mzUserId);
                if (null != accountObj) {
                    int account_point = (accountObj.get("account_point") == null) ? 0 : accountObj.getInteger("account_point").intValue();
                    int account_money_fen = (accountObj.get("account_money_fen") == null) ? 0 : accountObj.getInteger("account_money_fen").intValue();
                    BigDecimal accountMoney = (new BigDecimal(account_money_fen)).divide(new BigDecimal(100)).setScale(2, 4);
                    bizDataJson.put("accountPoint", Integer.valueOf(account_point));
                    bizDataJson.put("accountMoney", accountMoney);
                } else {
                    accountState = false;
                    bizDataJson.put("accountPoint", Integer.valueOf(0));
                    bizDataJson.put("accountMoney", Integer.valueOf(0));
                }
                ShoppingAssetRule shoppingAssetRule = CommonUtil.getAssetRule();
                if (null != shoppingAssetRule) {
                    bizDataJson.put("accountPointLimit", Integer.valueOf(shoppingAssetRule.getPointAvoidLimit()));
                    bizDataJson.put("accountMoneyLimit", shoppingAssetRule.getAccountAvoidLimit());
                }
            }
            if (!accountState) {
                retCode = "-1";
                retMsg = "无法获取您的账户积分和余额数据，请稍后再试！";
            } else {
                BigDecimal originPrice = payPrice;
                int originntegalValue = fixedIntegalValue;
                if (null != reqJson.get("couponId") && !"".equals(reqJson.get("couponId")) && couponIds.contains(reqJson.getString("couponId"))) {
                    String couponId = reqJson.getString("couponId");
                    JSONObject couponDtl = CRMService.getUserCouponDtl(couponId);
                    if (null != couponDtl) {
                        String right_No = couponDtl.getString("right_No");
                        ShoppingCoupon shoppingCoupon = new ShoppingCoupon();
                        shoppingCoupon.setRight_No(right_No);
                        shoppingCoupon = this.shoppingCouponDao.queryDetail(shoppingCoupon);
                        if (shoppingCoupon.getRight_Type().equals("coincp")) {
                            int couponAmount = Integer.valueOf(shoppingCoupon.getRight_Content()).intValue();
                            if ((new BigDecimal(couponAmount)).compareTo(payPrice) == 1) {
                                payPrice = BigDecimal.ZERO;
                            } else {
                                payPrice = payPrice.subtract(new BigDecimal(couponAmount));
                            }
                        } else {
                            BigDecimal discount = new BigDecimal(shoppingCoupon.getRight_Content());
                            payPrice = payPrice.multiply(discount).setScale(2, 4);
                            if (useIntegralSet == 1)
                                fixedIntegalValue = (new BigDecimal(fixedIntegalValue)).multiply(discount).intValue();
                        }
                        if (useIntegralSet == 1) {
                            couponCut = originPrice.subtract(payPrice).add((new BigDecimal(originntegalValue - fixedIntegalValue)).divide(new BigDecimal(this.moneyToIntegralScale)).setScale(2, 4));
                        } else {
                            couponCut = originPrice.subtract(payPrice);
                        }
                        originPrice = payPrice;
                        originntegalValue = fixedIntegalValue;
                    }
                }
                if (useMembershipSet == 1) {
                    BigDecimal discount = CommonUtil.getUserMemberShip(userId);
                    payPrice = payPrice.multiply(discount).setScale(2, 4);
                    if (useIntegralSet == 1)
                        fixedIntegalValue = (new BigDecimal(fixedIntegalValue)).multiply(discount).intValue();
                    if (useIntegralSet == 1) {
                        accountCut = originPrice.subtract(payPrice).add((new BigDecimal(originntegalValue - fixedIntegalValue)).divide(new BigDecimal(this.moneyToIntegralScale)).setScale(2, 4));
                    } else {
                        accountCut = originPrice.subtract(payPrice);
                    }
                    originPrice = payPrice;
                    originntegalValue = fixedIntegalValue;
                }
                ShoppingPayLimit payLimit = CommonUtil.getPayLimit();
                bizDataJson.put("pointPayLimit", Integer.valueOf(payLimit.getPointPay()));
                bizDataJson.put("balancePayLimit", Integer.valueOf(payLimit.getBalancePay()));
                int maxIntegralValue = 0;
                int accountPoint = (bizDataJson.get("accountPoint") == null) ? 0 : bizDataJson.getInteger("accountPoint").intValue();
                int pointPay = payLimit.getPointPay();
                if (useIntegralSet == 1 || (null != reqJson.get("useIntegral") && reqJson.getInteger("useIntegral").intValue() == 1)) {
                    if (useIntegralSet == 1) {
                        maxIntegralValue = fixedIntegalValue;
                    } else if (useIntegralSet == 2) {
                        int payPriceToInt = payPrice.multiply(new BigDecimal(this.moneyToIntegralScale)).intValue();
                        maxIntegralValue = (useIntegralValue * goodsCount < payPriceToInt) ? (useIntegralValue * goodsCount) : payPriceToInt;
                        int pointLimit = accountPoint;
                        if (pointPay > 0)
                            pointLimit = (pointPay > pointLimit) ? pointLimit : pointPay;
                        if (maxIntegralValue > pointLimit)
                            maxIntegralValue = pointLimit;
                    }
                    bizDataJson.put("useIntegralValue", Integer.valueOf(maxIntegralValue));
                    BigDecimal integralAmount = (new BigDecimal(maxIntegralValue)).divide(new BigDecimal(this.moneyToIntegralScale)).setScale(2, 4);
                    if (useIntegralSet == 1) {
                        integralCut = integralAmount;
                    } else {
                        payPrice = payPrice.subtract(integralAmount);
                        integralCut = originPrice.subtract(payPrice);
                    }
                    originPrice = payPrice;
                }
                if (maxIntegralValue > accountPoint) {
                    retCode = "1";
                    retMsg = "当前账户积分不够，无法下单！";
                } else if (maxIntegralValue > pointPay) {
                    retCode = "1";
                    retMsg = "积分单次支付限额" + pointPay + ",当前订单已超出该额度，无法下单！";
                } else {
                    if (null != reqJson.get("useBalance") && reqJson.getInteger("useBalance").intValue() == 1) {
                        BigDecimal deductionBalance = BigDecimal.ZERO;
                        BigDecimal accountLimit = bizDataJson.getBigDecimal("accountMoney");
                        BigDecimal balancePay = new BigDecimal(payLimit.getBalancePay());
                        if (balancePay.compareTo(BigDecimal.ZERO) > 0 && balancePay.compareTo(accountLimit) < 0)
                            accountLimit = balancePay;
                        if (payPrice.compareTo(accountLimit) == 1) {
                            deductionBalance = accountLimit;
                        } else {
                            deductionBalance = payPrice;
                        }
                        payPrice = payPrice.subtract(deductionBalance);
                        balanceCut = originPrice.subtract(payPrice);
                    }
                    HashMap<String, Object> reqMap = new HashMap<>();
                    reqMap.put("activityId", activityId);
                    List<ShoppingArtinfos> infos = this.shoppingArtinfosDao.queryPlanInfoList(reqMap);
                    bizDataJson.put("infos", infos);
                    bizDataJson.put("totalPrice", totalPrice);
                    bizDataJson.put("payPrice", payPrice);
                    bizDataJson.put("deductionCouponPrice", couponCut);
                    bizDataJson.put("deductionMemberPrice", accountCut);
                    bizDataJson.put("deductionIntegralPrice", integralCut);
                    bizDataJson.put("deductionBalancePrice", balanceCut);
                    retCode = "0";
                    retMsg = "操作成功！";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    public JSONObject renderClassOrder(JSONObject reqJson, HttpServletRequest request) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "服务器内部错误！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String userId = reqJson.getString("userId");
            String mzUserId = CommonUtil.getMzUserId(userId);
            String classId = reqJson.getString("classId");
            int goodsCount = reqJson.getInteger("goodsCount").intValue();
            ShoppingArtclass shoppingArtclass = new ShoppingArtclass();
            shoppingArtclass.setId(classId);
            shoppingArtclass = this.shoppingArtclassDao.queryDetail(shoppingArtclass);
            BigDecimal currentPrice = shoppingArtclass.getCurrentPrice();
            bizDataJson.put("currentPrice", currentPrice);
            JSONArray goodsArray = new JSONArray();
            JSONObject goodsObj = new JSONObject();
            goodsObj.put("classId", classId);
            goodsObj.put("className", shoppingArtclass.getClassName());
            goodsObj.put("currentPrice", currentPrice);
            goodsObj.put("goodsCount", Integer.valueOf(goodsCount));
            goodsObj.put("photoId", shoppingArtclass.getMainPhotoId());
            goodsArray.add(goodsObj);
            bizDataJson.put("classInfoList", goodsArray);
            BigDecimal goodsPrice = currentPrice.multiply(new BigDecimal(goodsCount)).setScale(2, 4);
            bizDataJson.put("goodsPrice", goodsPrice);
            int useIntegralSet = shoppingArtclass.getUseIntegralSet().intValue();
            int useIntegralValue = shoppingArtclass.getUseIntegralValue().intValue();
            int useBalanceSet = shoppingArtclass.getUseBalanceSet().intValue();
            int useMembershipSet = shoppingArtclass.getUseMembershipSet().intValue();
            bizDataJson.put("useMembershipSet", Integer.valueOf(useMembershipSet));
            bizDataJson.put("useIntegralSet", Integer.valueOf(useIntegralSet));
            bizDataJson.put("useBalanceSet", Integer.valueOf(useBalanceSet));
            BigDecimal couponCut = BigDecimal.ZERO;
            BigDecimal accountCut = BigDecimal.ZERO;
            BigDecimal integralCut = BigDecimal.ZERO;
            BigDecimal balanceCut = BigDecimal.ZERO;
            BigDecimal totalPrice = currentPrice.multiply(new BigDecimal(goodsCount));
            BigDecimal cashPrice = BigDecimal.ZERO;
            int fixedIntegalValue = 0;
            if (useIntegralSet == 1) {
                if (currentPrice.multiply(new BigDecimal(100)).intValue() < useIntegralValue)
                    useIntegralValue = currentPrice.multiply(new BigDecimal(100)).intValue();
                fixedIntegalValue = useIntegralValue * goodsCount;
                cashPrice = totalPrice.subtract((new BigDecimal(fixedIntegalValue)).divide(new BigDecimal(this.moneyToIntegralScale)).setScale(2, 4));
            }
            BigDecimal payPrice = BigDecimal.ZERO;
            if (useIntegralSet == 1) {
                payPrice = cashPrice.multiply(new BigDecimal(goodsCount));
            } else {
                payPrice = currentPrice.multiply(new BigDecimal(goodsCount));
            }
            List<String> couponIds = new ArrayList<>();
            JSONArray couponArray = CommonUtil.getArtCouppon(classId, 4, userId, goodsPrice, payPrice);
            for (int i = 0; i < couponArray.size(); i++) {
                JSONObject obj = couponArray.getJSONObject(i);
                couponIds.add(obj.getString("id"));
            }
            bizDataJson.put("couponList", couponArray);
            boolean accountState = true;
            if (useIntegralSet != 0 || useBalanceSet != 0) {
                JSONObject accountObj = MZService.getAssetinfo(mzUserId);
                if (null != accountObj) {
                    int account_point = (accountObj.get("account_point") == null) ? 0 : accountObj.getInteger("account_point").intValue();
                    int account_money_fen = (accountObj.get("account_money_fen") == null) ? 0 : accountObj.getInteger("account_money_fen").intValue();
                    BigDecimal accountMoney = (new BigDecimal(account_money_fen)).divide(new BigDecimal(100)).setScale(2, 4);
                    bizDataJson.put("accountPoint", Integer.valueOf(account_point));
                    bizDataJson.put("accountMoney", accountMoney);
                } else {
                    accountState = false;
                    bizDataJson.put("accountPoint", Integer.valueOf(0));
                    bizDataJson.put("accountMoney", Integer.valueOf(0));
                }
                ShoppingAssetRule shoppingAssetRule = CommonUtil.getAssetRule();
                if (null != shoppingAssetRule) {
                    bizDataJson.put("accountPointLimit", Integer.valueOf(shoppingAssetRule.getPointAvoidLimit()));
                    bizDataJson.put("accountMoneyLimit", shoppingAssetRule.getAccountAvoidLimit());
                }
            }
            if (!accountState) {
                retCode = "-1";
                retMsg = "无法获取您的账户积分和余额数据，请稍后再试！";
            } else {
                BigDecimal originPrice = payPrice;
                int originntegalValue = fixedIntegalValue;
                if (null != reqJson.get("couponId") && !"".equals(reqJson.get("couponId")) && couponIds.contains(reqJson.getString("couponId"))) {
                    String couponId = reqJson.getString("couponId");
                    JSONObject couponDtl = CRMService.getUserCouponDtl(couponId);
                    if (null != couponDtl) {
                        String right_No = couponDtl.getString("right_No");
                        ShoppingCoupon shoppingCoupon = new ShoppingCoupon();
                        shoppingCoupon.setRight_No(right_No);
                        shoppingCoupon = this.shoppingCouponDao.queryDetail(shoppingCoupon);
                        if (shoppingCoupon.getRight_Type().equals("coincp")) {
                            int couponAmount = Integer.valueOf(shoppingCoupon.getRight_Content()).intValue();
                            if ((new BigDecimal(couponAmount)).compareTo(payPrice) == 1) {
                                payPrice = BigDecimal.ZERO;
                            } else {
                                payPrice = payPrice.subtract(new BigDecimal(couponAmount));
                            }
                        } else {
                            BigDecimal discount = new BigDecimal(shoppingCoupon.getRight_Content());
                            payPrice = payPrice.multiply(discount).setScale(2, 4);
                            if (useIntegralSet == 1)
                                fixedIntegalValue = (new BigDecimal(fixedIntegalValue)).multiply(discount).intValue();
                        }
                        if (useIntegralSet == 1) {
                            couponCut = originPrice.subtract(payPrice).add((new BigDecimal(originntegalValue - fixedIntegalValue)).divide(new BigDecimal(this.moneyToIntegralScale)).setScale(2, 4));
                        } else {
                            couponCut = originPrice.subtract(payPrice);
                        }
                        originPrice = payPrice;
                        originntegalValue = fixedIntegalValue;
                    }
                }
                if (useMembershipSet == 1) {
                    BigDecimal discount = CommonUtil.getUserMemberShip(userId);
                    payPrice = payPrice.multiply(discount).setScale(2, 4);
                    if (useIntegralSet == 1)
                        fixedIntegalValue = (new BigDecimal(fixedIntegalValue)).multiply(discount).intValue();
                    if (useIntegralSet == 1) {
                        accountCut = originPrice.subtract(payPrice).add((new BigDecimal(originntegalValue - fixedIntegalValue)).divide(new BigDecimal(this.moneyToIntegralScale)).setScale(2, 4));
                    } else {
                        accountCut = originPrice.subtract(payPrice);
                    }
                    originPrice = payPrice;
                    originntegalValue = fixedIntegalValue;
                }
                ShoppingPayLimit payLimit = CommonUtil.getPayLimit();
                bizDataJson.put("pointPayLimit", Integer.valueOf(payLimit.getPointPay()));
                bizDataJson.put("balancePayLimit", Integer.valueOf(payLimit.getBalancePay()));
                int maxIntegralValue = 0;
                int accountPoint = (bizDataJson.get("accountPoint") == null) ? 0 : bizDataJson.getInteger("accountPoint").intValue();
                int pointPay = payLimit.getPointPay();
                if (useIntegralSet == 1 || (null != reqJson.get("useIntegral") && reqJson.getInteger("useIntegral").intValue() == 1)) {
                    if (useIntegralSet == 1) {
                        maxIntegralValue = fixedIntegalValue;
                    } else if (useIntegralSet == 2) {
                        int payPriceToInt = payPrice.multiply(new BigDecimal(this.moneyToIntegralScale)).intValue();
                        maxIntegralValue = (useIntegralValue * goodsCount < payPriceToInt) ? (useIntegralValue * goodsCount) : payPriceToInt;
                        int pointLimit = accountPoint;
                        if (pointPay > 0)
                            pointLimit = (pointPay > pointLimit) ? pointLimit : pointPay;
                        if (maxIntegralValue > pointLimit)
                            maxIntegralValue = pointLimit;
                    }
                    bizDataJson.put("useIntegralValue", Integer.valueOf(maxIntegralValue));
                    BigDecimal integralAmount = (new BigDecimal(maxIntegralValue)).divide(new BigDecimal(this.moneyToIntegralScale)).setScale(2, 4);
                    if (useIntegralSet == 1) {
                        integralCut = integralAmount;
                    } else {
                        payPrice = payPrice.subtract(integralAmount);
                        integralCut = originPrice.subtract(payPrice);
                    }
                    originPrice = payPrice;
                }
                if (maxIntegralValue > accountPoint) {
                    retCode = "1";
                    retMsg = "当前账户积分不够，无法下单！";
                } else if (maxIntegralValue > pointPay) {
                    retCode = "1";
                    retMsg = "积分单次支付限额" + pointPay + ",当前订单已超出该额度，无法下单！";
                } else {
                    if (null != reqJson.get("useBalance") && reqJson.getInteger("useBalance").intValue() == 1) {
                        BigDecimal deductionBalance = BigDecimal.ZERO;
                        BigDecimal accountLimit = bizDataJson.getBigDecimal("accountMoney");
                        BigDecimal balancePay = new BigDecimal(payLimit.getBalancePay());
                        if (balancePay.compareTo(BigDecimal.ZERO) > 0 && balancePay.compareTo(accountLimit) < 0)
                            accountLimit = balancePay;
                        if (payPrice.compareTo(accountLimit) == 1) {
                            deductionBalance = accountLimit;
                        } else {
                            deductionBalance = payPrice;
                        }
                        payPrice = payPrice.subtract(deductionBalance);
                        balanceCut = originPrice.subtract(payPrice);
                    }
                    HashMap<String, Object> reqMap = new HashMap<>();
                    reqMap.put("classId", classId);
                    List<ShoppingArtinfos> infos = this.shoppingArtinfosDao.queryClassInfoList(reqMap);
                    bizDataJson.put("infos", infos);
                    bizDataJson.put("totalPrice", totalPrice);
                    bizDataJson.put("payPrice", payPrice);
                    bizDataJson.put("deductionCouponPrice", couponCut);
                    bizDataJson.put("deductionMemberPrice", accountCut);
                    bizDataJson.put("deductionIntegralPrice", integralCut);
                    bizDataJson.put("deductionBalancePrice", balanceCut);
                    retCode = "0";
                    retMsg = "操作成功！";
                }
            }
        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    public JSONObject addActivityOrder(JSONObject reqJson, HttpServletRequest request) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "服务器内部错误！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String userId = reqJson.getString("userId");
            String mzUserId = CommonUtil.getMzUserId(userId);
            BigDecimal orderTotalPrice = reqJson.getBigDecimal("orderTotalPrice");
            BigDecimal orderPayPrice = reqJson.getBigDecimal("orderPayPrice");
            int orderUseIntegralValue = (reqJson.get("orderUseIntegralValue") == null) ? 0 : reqJson.getInteger("orderUseIntegralValue").intValue();
            BigDecimal orderDeductionBalancePrice = (reqJson.get("orderDeductionBalancePrice") == null) ? BigDecimal.ZERO : reqJson.getBigDecimal("orderDeductionBalancePrice");
            BigDecimal unitPrice = reqJson.getBigDecimal("unitPrice");
            String activityId = reqJson.getString("activityId");
            int goodsCount = reqJson.getInteger("goodsCount").intValue();
            ShoppingArtactivity shoppingArtactivity = new ShoppingArtactivity();
            shoppingArtactivity.setId(activityId);
            shoppingArtactivity = this.shoppingArtactivityDao.queryDetail(shoppingArtactivity);
            BigDecimal currentPrice = BigDecimal.ZERO;
            if (null != reqJson.get("propertys") && !"".equals(reqJson.get("propertys"))) {
                ShoppingArtactivityInventory shoppingArtactivityInventory = new ShoppingArtactivityInventory();
                shoppingArtactivityInventory.setActivityId(shoppingArtactivity.getId());
                shoppingArtactivityInventory.setPropertys(reqJson.getString("propertys"));
                shoppingArtactivityInventory = this.shoppingArtactivityInventoryDao.queryDetail(shoppingArtactivityInventory);
                currentPrice = shoppingArtactivityInventory.getPrice();
            } else {
                currentPrice = shoppingArtactivity.getCurrentPrice();
            }
            boolean flag = true;
            if (!flag || (null != reqJson.get("propertys") && !"".equals(reqJson.get("propertys")))) {
                ShoppingArtactivityInventory shoppingArtactivityInventory = new ShoppingArtactivityInventory();
                shoppingArtactivityInventory.setActivityId(shoppingArtactivity.getId());
                shoppingArtactivityInventory.setPropertys(reqJson.getString("propertys"));
                shoppingArtactivityInventory = this.shoppingArtactivityInventoryDao.queryDetail(shoppingArtactivityInventory);
                if (null == shoppingArtactivityInventory || shoppingArtactivityInventory.getCount().intValue() < goodsCount)
                    flag = false;
            }
            boolean overLimitBuy = false;
            if (shoppingArtactivity.getSignupPerLimit().intValue() > 0) {
                int limitBuy = shoppingArtactivity.getSignupPerLimit().intValue();
                String scId = CommonUtil.getUserScId(userId);
                HashMap<String, Object> reqMap = new HashMap<>();
                reqMap.put("scId", scId);
                reqMap.put("goodsId", activityId);
                reqMap.put("userId", userId);
                reqMap.put("deleteStatus", "0");
                List<ShoppingGoodscart> shoppingGoodscartList = this.shoppingGoodscartDao.queryBuyList(reqMap);
                int cartCount = 0;
                for (ShoppingGoodscart shoppingGoodscart : shoppingGoodscartList)
                    cartCount += shoppingGoodscart.getCount().intValue();
                bizDataJson.put("doneCount", Integer.valueOf(cartCount));
                if (cartCount + goodsCount > limitBuy)
                    overLimitBuy = true;
            }
            String signupStarttime = shoppingArtactivity.getSignupStarttime();
            String signupEndtime = shoppingArtactivity.getSignupEndtime();
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (StringUtil.compareMillisecond(signupStarttime, sf) > 0 || StringUtil.compareMillisecond(signupEndtime, sf) < 0) {
                retCode = "-1";
                retMsg = "当前时间不可报名！";
            } else if (overLimitBuy) {
                retCode = "-1";
                retMsg = "您的单人报名数量已超过限额！";
            } else if (!flag || (shoppingArtactivity.getSignupTotalLimit().intValue() > 0 && shoppingArtactivity.getLeftnum().intValue() < goodsCount)) {
                retCode = "-1";
                retMsg = "剩余报名数量不足！";
            } else if (currentPrice.compareTo(unitPrice) != 0) {
                retCode = "-1";
                retMsg = "商品价格发生变化，请重新确认订单信息！";
            } else {
                boolean cartState = false;
                if (null != reqJson.get("cartId") && !"".equals(reqJson.get("cartId"))) {
                    ShoppingGoodscart goodscart = new ShoppingGoodscart();
                    goodscart.setId(reqJson.getString("cartId"));
                    goodscart = this.shoppingGoodscartDao.queryDetail(goodscart);
                    if (StringUtil.isNotNull(goodscart.getOfId()))
                        cartState = true;
                }
                if (cartState) {
                    retCode = "-1";
                    retMsg = "该购物车记录不存在，请刷新购物车！";
                } else {
                    int account_point = 0;
                    BigDecimal accountMoney = BigDecimal.ZERO;
                    int point_avoid_limit = 0;
                    int account_money_fen = 0;
                    int account_avoid_limit = 0;
                    boolean accountState = true;
                    if (orderUseIntegralValue > 0 || orderDeductionBalancePrice.compareTo(BigDecimal.ZERO) == 1 || (null != reqJson.get("useIntegral") && reqJson.getInteger("useIntegral").intValue() == 1) || (null != reqJson.get("useBalance") && reqJson.getInteger("useBalance").intValue() == 1)) {
                        JSONObject accountObj = MZService.getAssetinfo(mzUserId);
                        if (null != accountObj) {
                            account_point = (accountObj.get("account_point") == null) ? 0 : accountObj.getInteger("account_point").intValue();
                            account_money_fen = (accountObj.get("account_money_fen") == null) ? 0 : accountObj.getInteger("account_money_fen").intValue();
                            accountMoney = (new BigDecimal(account_money_fen)).divide(new BigDecimal(100)).setScale(2, 4);
                            bizDataJson.put("accountPoint", Integer.valueOf(account_point));
                            bizDataJson.put("accountMoney", accountMoney);
                        } else {
                            bizDataJson.put("accountPoint", Integer.valueOf(0));
                            bizDataJson.put("accountMoney", Integer.valueOf(0));
                            accountState = false;
                        }
                        ShoppingAssetRule shoppingAssetRule = CommonUtil.getAssetRule();
                        if (null != shoppingAssetRule) {
                            bizDataJson.put("accountPointLimit", Integer.valueOf(shoppingAssetRule.getPointAvoidLimit()));
                            bizDataJson.put("accountMoneyLimit", shoppingAssetRule.getAccountAvoidLimit());
                        }
                    }
                    if (!accountState) {
                        retCode = "-1";
                        retMsg = "无法获取您的账户积分和余额数据，请稍后下单！";
                    } else if (accountMoney.compareTo(orderDeductionBalancePrice) < 0 || account_point < orderUseIntegralValue) {
                        retCode = "-1";
                        retMsg = "账户积分或余额不足，请重新确认订单信息！";
                    } else if (null != reqJson.get("couponId") && !"".equals(reqJson.get("couponId")) && !CommonUtil.checkPayInfo(reqJson.getString("couponId"))) {
                        retCode = "-1";
                        retMsg = "优惠券异常，请重新确认订单信息！";
                    } else {
                        int useIntegralSet = shoppingArtactivity.getUseIntegralSet().intValue();
                        int useIntegralValue = shoppingArtactivity.getUseIntegralValue().intValue();
                        int useBalanceSet = shoppingArtactivity.getUseBalanceSet().intValue();
                        int useMembershipSet = shoppingArtactivity.getUseMembershipSet().intValue();
                        BigDecimal totalPrice = currentPrice.multiply(new BigDecimal(goodsCount)).setScale(2, 4);
                        BigDecimal cashPrice = BigDecimal.ZERO;
                        int fixedIntegalValue = 0;
                        if (useIntegralSet == 1) {
                            if (currentPrice.multiply(new BigDecimal(100)).intValue() < useIntegralValue)
                                useIntegralValue = currentPrice.multiply(new BigDecimal(100)).intValue();
                            fixedIntegalValue = useIntegralValue * goodsCount;
                            cashPrice = totalPrice.subtract((new BigDecimal(fixedIntegalValue)).divide(new BigDecimal(this.moneyToIntegralScale)).setScale(2, 4));
                        }
                        BigDecimal payPrice = BigDecimal.ZERO;
                        if (useIntegralSet == 1) {
                            payPrice = cashPrice.multiply(new BigDecimal(goodsCount));
                        } else {
                            payPrice = currentPrice.multiply(new BigDecimal(goodsCount));
                        }
                        BigDecimal originPrice = payPrice;
                        int originntegalValue = fixedIntegalValue;
                        BigDecimal couponCut = BigDecimal.ZERO;
                        BigDecimal accountCut = BigDecimal.ZERO;
                        int integralValue = 0;
                        BigDecimal integralCut = BigDecimal.ZERO;
                        BigDecimal balanceCut = BigDecimal.ZERO;
                        List<String> couponIds = new ArrayList<>();
                        JSONArray couponArray = CommonUtil.getArtCouppon(activityId, 3, userId, totalPrice, payPrice);
                        for (int i = 0; i < couponArray.size(); i++) {
                            JSONObject obj = couponArray.getJSONObject(i);
                            couponIds.add(obj.getString("id"));
                        }
                        if (null != reqJson.get("couponId") && !"".equals(reqJson.get("couponId")) && couponIds.contains(reqJson.get("couponId"))) {
                            String couponId = reqJson.getString("couponId");
                            JSONObject couponDtl = CRMService.getUserCouponDtl(couponId);
                            if (null != couponDtl) {
                                String right_No = couponDtl.getString("right_No");
                                ShoppingCoupon shoppingCoupon = new ShoppingCoupon();
                                shoppingCoupon.setRight_No(right_No);
                                shoppingCoupon = this.shoppingCouponDao.queryDetail(shoppingCoupon);
                                if (shoppingCoupon.getRight_Type().equals("coincp")) {
                                    int couponAmount = Integer.valueOf(shoppingCoupon.getRight_Content()).intValue();
                                    if ((new BigDecimal(couponAmount)).compareTo(payPrice) == 1) {
                                        payPrice = BigDecimal.ZERO;
                                    } else {
                                        payPrice = payPrice.subtract(new BigDecimal(couponAmount));
                                    }
                                } else {
                                    BigDecimal discount = new BigDecimal(shoppingCoupon.getRight_Content());
                                    payPrice = payPrice.multiply(discount).setScale(2, 4);
                                    if (useIntegralSet == 1)
                                        fixedIntegalValue = (new BigDecimal(fixedIntegalValue)).multiply(discount).intValue();
                                }
                                if (useIntegralSet == 1) {
                                    couponCut = originPrice.subtract(payPrice).add((new BigDecimal(originntegalValue - fixedIntegalValue)).divide(new BigDecimal(this.moneyToIntegralScale)).setScale(2, 4));
                                } else {
                                    couponCut = originPrice.subtract(payPrice);
                                }
                                originPrice = payPrice;
                                originntegalValue = fixedIntegalValue;
                            }
                        }
                        if (useMembershipSet == 1) {
                            BigDecimal discount = CommonUtil.getUserMemberShip(userId);
                            payPrice = payPrice.multiply(discount).setScale(2, 4);
                            if (useIntegralSet == 1)
                                fixedIntegalValue = (new BigDecimal(fixedIntegalValue)).multiply(discount).intValue();
                            if (useIntegralSet == 1) {
                                accountCut = originPrice.subtract(payPrice).add((new BigDecimal(originntegalValue - fixedIntegalValue)).divide(new BigDecimal(this.moneyToIntegralScale)).setScale(2, 4));
                            } else {
                                accountCut = originPrice.subtract(payPrice);
                            }
                            originPrice = payPrice;
                            originntegalValue = fixedIntegalValue;
                        }
                        ShoppingPayLimit payLimit = CommonUtil.getPayLimit();
                        int maxIntegralValue = 0;
                        int accountPoint = (bizDataJson.get("accountPoint") == null) ? 0 : bizDataJson.getInteger("accountPoint").intValue();
                        int pointPay = payLimit.getPointPay();
                        if (useIntegralSet == 1 || (null != reqJson.get("useIntegral") && reqJson.getInteger("useIntegral").intValue() == 1)) {
                            if (useIntegralSet == 1) {
                                maxIntegralValue = fixedIntegalValue;
                            } else {
                                int payPriceToInt = payPrice.multiply(new BigDecimal(this.moneyToIntegralScale)).intValue();
                                maxIntegralValue = (useIntegralValue * goodsCount < payPriceToInt) ? (useIntegralValue * goodsCount) : payPriceToInt;
                                int pointLimit = accountPoint;
                                if (pointPay > 0)
                                    pointLimit = (pointPay > pointLimit) ? pointLimit : pointPay;
                                if (maxIntegralValue > pointLimit)
                                    maxIntegralValue = pointLimit;
                            }
                            integralValue = maxIntegralValue;
                            BigDecimal integralAmount = (new BigDecimal(maxIntegralValue)).divide(new BigDecimal(this.moneyToIntegralScale)).setScale(2, 4);
                            if (useIntegralSet == 1) {
                                integralCut = integralAmount;
                            } else {
                                payPrice = payPrice.subtract(integralAmount);
                                integralCut = originPrice.subtract(payPrice);
                            }
                            originPrice = payPrice;
                        }
                        if (maxIntegralValue > accountPoint) {
                            retCode = "1";
                            retMsg = "当前账户积分不够，无法下单！";
                        } else if (maxIntegralValue > pointPay) {
                            retCode = "1";
                            retMsg = "积分单次支付限额" + pointPay + ",当前订单已超出该额度，无法下单！";
                        } else {
                            BigDecimal deductionBalance = BigDecimal.ZERO;
                            if (null != reqJson.get("useBalance") && reqJson.getInteger("useBalance").intValue() == 1) {
                                BigDecimal accountLimit = bizDataJson.getBigDecimal("accountMoney");
                                BigDecimal balancePay = new BigDecimal(payLimit.getBalancePay());
                                if (balancePay.compareTo(BigDecimal.ZERO) > 0 && balancePay.compareTo(accountLimit) < 0)
                                    accountLimit = balancePay;
                                if (payPrice.compareTo(accountLimit) == 1) {
                                    deductionBalance = accountLimit;
                                } else {
                                    deductionBalance = payPrice;
                                }
                                payPrice = payPrice.subtract(deductionBalance);
                                balanceCut = deductionBalance;
                            }
                            if (totalPrice.compareTo(orderTotalPrice) != 0 || payPrice.compareTo(orderPayPrice) != 0 || maxIntegralValue != orderUseIntegralValue || deductionBalance.compareTo(orderDeductionBalancePrice) != 0) {
                                retCode = "-1";
                                retMsg = "订单金额发生变化，请重新确认订单信息！";
                            } else if (maxIntegralValue > account_point) {
                                retCode = "-1";
                                retMsg = "当前账户积分不足，请重新确认订单信息！";
                            } else {
                                ShoppingOrderform orderform = new ShoppingOrderform();
                                String orderId = PayUtil.getOrderNo(Const.SHOPPING_ACT_ORDER);
                                orderform.setOrderId(orderId);
                                orderform.setOrderType(Const.SHOPPING_ACT_ORDER_TYPE);
                                orderform.setOrderStatus(Integer.valueOf(10));
                                orderform.setTotalPrice(totalPrice);
                                orderform.setPayPrice(payPrice);
                                orderform.setStoreId(Const.STORE_ID);
                                orderform.setUserId(userId);
                                ShoppingOrderPay shoppingOrderPay = new ShoppingOrderPay();
                                shoppingOrderPay.setUserId(userId);
                                if (payPrice.compareTo(BigDecimal.ZERO) == 1)
                                    shoppingOrderPay.setCashStatus(Integer.valueOf(0));
                                if (null != reqJson.get("couponId") && !"".equals(reqJson.get("couponId"))) {
                                    String couponId = reqJson.getString("couponId");
                                    orderform.setCiId(couponId);
                                    orderform.setDeductionCouponPrice(couponCut);
                                    shoppingOrderPay.setCouponStatus(Integer.valueOf(0));
                                    ShoppingCouponUsertemp shoppingCouponUsertemp = new ShoppingCouponUsertemp();
                                    shoppingCouponUsertemp.setUserId(userId);
                                    shoppingCouponUsertemp.setCouponId(couponId);
                                    this.shoppingCouponUsertempDao.insert(shoppingCouponUsertemp);
                                }
                                ShoppingOrderPaykey shoppingOrderPaykey = new ShoppingOrderPaykey();
                                if (useIntegralSet == 1 || (null != reqJson.get("useIntegral") && reqJson.getInteger("useIntegral").intValue() == 1)) {
                                    orderform.setDeductionIntegralPrice(integralCut);
                                    orderform.setDeductionIntegral(Integer.valueOf(integralValue));
                                    shoppingOrderPay.setIntegralStatus(Integer.valueOf(0));
                                    if (null != reqJson.get("accountPointPayKey") && !"".equals(reqJson.get("accountPointPayKey")))
                                        shoppingOrderPaykey.setAccountPointPayKey(reqJson.getString("accountPointPayKey"));
                                }
                                orderform.setDeductionMemberPrice(accountCut);
                                if (null != reqJson.get("useBalance") && reqJson.getInteger("useBalance").intValue() == 1) {
                                    orderform.setDeductionBalancePrice(balanceCut);
                                    shoppingOrderPay.setBalanceStatus(Integer.valueOf(0));
                                    if (null != reqJson.get("accountMoneyPayKey") && !"".equals(reqJson.get("accountMoneyPayKey")) &&
                                            null != reqJson.get("accountMoneyPayKey"))
                                        shoppingOrderPaykey.setAccountMoneyPayKey(reqJson.getString("accountMoneyPayKey"));
                                }
                                if (!dealActivityStock(reqJson,shoppingArtactivity)) {
                                    retCode = "-1";
                                    retMsg = "可报名数量不足，请稍后再试！";
                                } else {
                                    this.shoppingOrderformDao.insert(orderform);
                                    if (null != shoppingOrderPaykey.getAccountPointPayKey() || null != shoppingOrderPaykey.getAccountMoneyPayKey()) {
                                        shoppingOrderPaykey.setOfId(orderform.getId());
                                        this.shoppingOrderPaykeyDao.insert(shoppingOrderPaykey);
                                    }
                                    shoppingOrderPay.setOfId(orderform.getId());
                                    this.shoppingOrderPayDao.insert(shoppingOrderPay);
                                    ShoppingOrderLog shoppingOrderLog = new ShoppingOrderLog();
                                    shoppingOrderLog.setLogInfo("提交订单");
                                    shoppingOrderLog.setLogUserId(userId);
                                    shoppingOrderLog.setOfId(orderform.getId());
                                    this.shoppingOrderLogDao.insert(shoppingOrderLog);
                                    ShoppingGoodscart goodscart = new ShoppingGoodscart();
                                    if (null != reqJson.get("cartId") && !"".equals(reqJson.get("cartId"))) {
                                        goodscart.setId(reqJson.getString("cartId"));
                                        goodscart = this.shoppingGoodscartDao.queryDetail(goodscart);
                                        goodscart.setDeductionCouponPrice(orderform.getDeductionCouponPrice());
                                        goodscart.setDeductionMemberPrice(orderform.getDeductionMemberPrice());
                                        goodscart.setDeductionIntegral(orderform.getDeductionIntegral());
                                        goodscart.setDeductionIntegralPrice(orderform.getDeductionIntegralPrice());
                                        goodscart.setDeductionBalancePrice(orderform.getDeductionBalancePrice());
                                        goodscart.setPayPrice(orderform.getPayPrice());
                                        goodscart.setPrice(currentPrice);
                                        goodscart.setOfId(orderform.getId());
                                        goodscart.setCount(Integer.valueOf(goodsCount));
                                        this.shoppingGoodscartDao.update(goodscart);
                                    } else {
                                        String scId = CommonUtil.getUserScId(userId);
                                        goodscart.setScId(scId);
                                        goodscart.setGoodsId(activityId);
                                        goodscart.setCount(Integer.valueOf(goodsCount));
                                        goodscart.setCartType(Const.SHOPPING_ACT_CART_TYPE);
                                        if (null != reqJson.get("propertys")) {
                                            goodscart.setSpecInfo(reqJson.getString("specInfo"));
                                            goodscart.setPropertys(reqJson.getString("propertys"));
                                        }
                                        goodscart.setDeductionCouponPrice(orderform.getDeductionCouponPrice());
                                        goodscart.setDeductionMemberPrice(orderform.getDeductionMemberPrice());
                                        goodscart.setDeductionIntegral(orderform.getDeductionIntegral());
                                        goodscart.setDeductionIntegralPrice(orderform.getDeductionIntegralPrice());
                                        goodscart.setDeductionBalancePrice(orderform.getDeductionBalancePrice());
                                        goodscart.setPayPrice(orderform.getPayPrice());
                                        goodscart.setPrice(currentPrice);
                                        goodscart.setOfId(orderform.getId());
                                        this.shoppingGoodscartDao.insert(goodscart);
                                    }
                                    String gcId = goodscart.getId();
                                    ShoppingWriteoff shoppingWriteoff = new ShoppingWriteoff();
                                    shoppingWriteoff.setGcId(gcId);
                                    shoppingWriteoff.setGoodsCount(goodscart.getCount());
                                    shoppingWriteoff.setOffCode(StringUtil.randomOffCode(this.offcodeLength));
                                    this.shoppingWriteoffDao.insert(shoppingWriteoff);
                                    if (null != reqJson.get("propertys") && !"".equals(reqJson.get("propertys"))) {
                                        HashMap<String, Object> cutMap = new HashMap<>();
                                        cutMap.put("activityId", shoppingArtactivity.getId());
                                        cutMap.put("propertys", reqJson.getString("propertys"));
                                        cutMap.put("cutCount", Integer.valueOf(goodsCount));
                                        this.shoppingArtactivityInventoryDao.cutInventory(cutMap);
                                    }
                                    if (shoppingArtactivity.getSignupTotalLimit().intValue() > 0)
                                        shoppingArtactivity.setLeftnum(Integer.valueOf(shoppingArtactivity.getLeftnum().intValue() - goodsCount));
                                    HashMap<String, Object> numMap = new HashMap<>();
                                    numMap.put("id", shoppingArtactivity.getId());
                                    numMap.put("cutnum", Integer.valueOf(goodsCount));
                                    this.shoppingArtactivityDao.updateActivityCutNum(numMap);
                                    if (null != reqJson.get("signupInfos") && !"".equals(reqJson.get("signupInfos"))) {
                                        JSONArray infoArray = reqJson.getJSONArray("signupInfos");
                                        for (int j = 0; j < infoArray.size(); j++) {
                                            ShoppingArtactivitySignupinfo shoppingArtactivitySignupinfo = new ShoppingArtactivitySignupinfo();
                                            shoppingArtactivitySignupinfo.setActivityId(activityId);
                                            shoppingArtactivitySignupinfo.setOfId(orderform.getId());
                                            shoppingArtactivitySignupinfo.setSignupInfo(infoArray.getJSONArray(j).toString());
                                            this.shoppingArtactivitySignupinfoDao.insert(shoppingArtactivitySignupinfo);
                                        }
                                    }
                                    HashMap<String, Object> reqMap = new HashMap<>();
                                    reqMap.put("deleteStatus", Integer.valueOf(0));
                                    List<ShoppingPayment> payments = this.shoppingPaymentDao.queryList(reqMap);
                                    bizDataJson.put("payments", payments);
                                    bizDataJson.put("orderId", orderId);
                                    bizDataJson.put("price", payPrice);
                                    retCode = "0";
                                    retMsg = "操作成功！";
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    public JSONObject addPlanOrder(JSONObject reqJson, HttpServletRequest request) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "服务器内部错误！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String userId = reqJson.getString("userId");
            String mzUserId = CommonUtil.getMzUserId(userId);
            BigDecimal orderTotalPrice = reqJson.getBigDecimal("orderTotalPrice");
            BigDecimal orderPayPrice = reqJson.getBigDecimal("orderPayPrice");
            int orderUseIntegralValue = (reqJson.get("orderUseIntegralValue") == null) ? 0 : reqJson.getInteger("orderUseIntegralValue").intValue();
            BigDecimal orderDeductionBalancePrice = (reqJson.get("orderDeductionBalancePrice") == null) ? BigDecimal.ZERO : reqJson.getBigDecimal("orderDeductionBalancePrice");
            BigDecimal unitPrice = reqJson.getBigDecimal("unitPrice");
            String activityId = reqJson.getString("activityId");
            int goodsCount = reqJson.getInteger("goodsCount").intValue();
            ShoppingArtplan shoppingArtplan = new ShoppingArtplan();
            shoppingArtplan.setId(activityId);
            shoppingArtplan = this.shoppingArtplanDao.queryDetail(shoppingArtplan);
            BigDecimal currentPrice = BigDecimal.ZERO;
            if (null != reqJson.get("propertys") && !"".equals(reqJson.get("propertys"))) {
                ShoppingArtplanInventory shoppingArtplanInventory = new ShoppingArtplanInventory();
                shoppingArtplanInventory.setActivityId(shoppingArtplan.getId());
                shoppingArtplanInventory.setPropertys(reqJson.getString("propertys"));
                shoppingArtplanInventory = this.shoppingArtplanInventoryDao.queryDetail(shoppingArtplanInventory);
                currentPrice = shoppingArtplanInventory.getPrice();
            } else {
                currentPrice = shoppingArtplan.getCurrentPrice();
            }
            boolean flag = true;
            int inventoryCount = 0;
            if (!flag || (null != reqJson.get("propertys") && !"".equals(reqJson.get("propertys")))) {
                ShoppingArtplanInventory shoppingArtplanInventory = new ShoppingArtplanInventory();
                shoppingArtplanInventory.setActivityId(shoppingArtplan.getId());
                shoppingArtplanInventory.setPropertys(reqJson.getString("propertys"));
                shoppingArtplanInventory = this.shoppingArtplanInventoryDao.queryDetail(shoppingArtplanInventory);
                inventoryCount = shoppingArtplanInventory.getCount().intValue();
                if (null == shoppingArtplanInventory || shoppingArtplanInventory.getCount().intValue() < goodsCount)
                    flag = false;
            }
            String signupStarttime = shoppingArtplan.getSignupStarttime();
            String signupEndtime = shoppingArtplan.getSignupEndtime();
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (StringUtil.compareMillisecond(signupStarttime, sf) > 0 || StringUtil.compareMillisecond(signupEndtime, sf) < 0) {
                retCode = "-1";
                retMsg = "当前时间不可报名！";
            } else if (shoppingArtplan.getSignupTotalLimit().intValue() > 0 && shoppingArtplan.getLeftnum().intValue() < goodsCount) {
                retCode = "-1";
                retMsg = "库存不足！";
            } else if (currentPrice.compareTo(unitPrice) != 0) {
                retCode = "-1";
                retMsg = "商品价格发生变化，请重新确认订单信息！";
            } else {
                boolean cartState = false;
                if (null != reqJson.get("cartId") && !"".equals(reqJson.get("cartId"))) {
                    ShoppingGoodscart goodscart = new ShoppingGoodscart();
                    goodscart.setId(reqJson.getString("cartId"));
                    goodscart = this.shoppingGoodscartDao.queryDetail(goodscart);
                    if (StringUtil.isNotNull(goodscart.getOfId()))
                        cartState = true;
                }
                if (cartState) {
                    retCode = "-1";
                    retMsg = "该购物车记录不存在，请刷新购物车！";
                } else {
                    int account_point = 0;
                    BigDecimal accountMoney = BigDecimal.ZERO;
                    int point_avoid_limit = 0;
                    int account_money_fen = 0;
                    int account_avoid_limit = 0;
                    boolean accountState = true;
                    if (orderUseIntegralValue > 0 || orderDeductionBalancePrice.compareTo(BigDecimal.ZERO) == 1 || (null != reqJson.get("useIntegral") && reqJson.getInteger("useIntegral").intValue() == 1) || (null != reqJson.get("useBalance") && reqJson.getInteger("useBalance").intValue() == 1)) {
                        JSONObject accountObj = MZService.getAssetinfo(mzUserId);
                        if (null != accountObj) {
                            account_point = (accountObj.get("account_point") == null) ? 0 : accountObj.getInteger("account_point").intValue();
                            account_money_fen = (accountObj.get("account_money_fen") == null) ? 0 : accountObj.getInteger("account_money_fen").intValue();
                            accountMoney = (new BigDecimal(account_money_fen)).divide(new BigDecimal(100)).setScale(2, 4);
                            bizDataJson.put("accountPoint", Integer.valueOf(account_point));
                            bizDataJson.put("accountMoney", accountMoney);
                        } else {
                            bizDataJson.put("accountPoint", Integer.valueOf(0));
                            bizDataJson.put("accountMoney", Integer.valueOf(0));
                            accountState = false;
                        }
                        ShoppingAssetRule shoppingAssetRule = CommonUtil.getAssetRule();
                        if (null != shoppingAssetRule) {
                            bizDataJson.put("accountPointLimit", Integer.valueOf(shoppingAssetRule.getPointAvoidLimit()));
                            bizDataJson.put("accountMoneyLimit", shoppingAssetRule.getAccountAvoidLimit());
                        }
                    }
                    if (!accountState) {
                        retCode = "-1";
                        retMsg = "无法获取您的账户积分和余额数据，请稍后下单！";
                    } else if (accountMoney.compareTo(orderDeductionBalancePrice) < 0 || account_point < orderUseIntegralValue) {
                        retCode = "-1";
                        retMsg = "账户积分或余额不足，请重新确认订单信息！";
                    } else if (null != reqJson.get("couponId") && !"".equals(reqJson.get("couponId")) && !CommonUtil.checkPayInfo(reqJson.getString("couponId"))) {
                        retCode = "-1";
                        retMsg = "优惠券异常，请重新确认订单信息！";
                    } else {
                        int useIntegralSet = shoppingArtplan.getUseIntegralSet().intValue();
                        int useIntegralValue = shoppingArtplan.getUseIntegralValue().intValue();
                        int useBalanceSet = shoppingArtplan.getUseBalanceSet().intValue();
                        int useMembershipSet = shoppingArtplan.getUseMembershipSet().intValue();
                        BigDecimal totalPrice = currentPrice.multiply(new BigDecimal(goodsCount)).setScale(2, 4);
                        BigDecimal cashPrice = BigDecimal.ZERO;
                        int fixedIntegalValue = 0;
                        if (useIntegralSet == 1) {
                            if (currentPrice.multiply(new BigDecimal(100)).intValue() < useIntegralValue)
                                useIntegralValue = currentPrice.multiply(new BigDecimal(100)).intValue();
                            fixedIntegalValue = useIntegralValue * goodsCount;
                            cashPrice = totalPrice.subtract((new BigDecimal(fixedIntegalValue)).divide(new BigDecimal(this.moneyToIntegralScale)).setScale(2, 4));
                        }
                        BigDecimal payPrice = BigDecimal.ZERO;
                        if (useIntegralSet == 1) {
                            payPrice = cashPrice.multiply(new BigDecimal(goodsCount));
                        } else {
                            payPrice = currentPrice.multiply(new BigDecimal(goodsCount));
                        }
                        BigDecimal originPrice = payPrice;
                        int originntegalValue = fixedIntegalValue;
                        BigDecimal couponCut = BigDecimal.ZERO;
                        BigDecimal accountCut = BigDecimal.ZERO;
                        int integralValue = 0;
                        BigDecimal integralCut = BigDecimal.ZERO;
                        BigDecimal balanceCut = BigDecimal.ZERO;
                        List<String> couponIds = new ArrayList<>();
                        JSONArray couponArray = CommonUtil.getArtCouppon(activityId, 6, userId, totalPrice, payPrice);
                        for (int i = 0; i < couponArray.size(); i++) {
                            JSONObject obj = couponArray.getJSONObject(i);
                            couponIds.add(obj.getString("id"));
                        }
                        if (null != reqJson.get("couponId") && !"".equals(reqJson.get("couponId")) && couponIds.contains(reqJson.get("couponId"))) {
                            String couponId = reqJson.getString("couponId");
                            JSONObject couponDtl = CRMService.getUserCouponDtl(couponId);
                            if (null != couponDtl) {
                                String right_No = couponDtl.getString("right_No");
                                ShoppingCoupon shoppingCoupon = new ShoppingCoupon();
                                shoppingCoupon.setRight_No(right_No);
                                shoppingCoupon = this.shoppingCouponDao.queryDetail(shoppingCoupon);
                                if (shoppingCoupon.getRight_Type().equals("coincp")) {
                                    int couponAmount = Integer.valueOf(shoppingCoupon.getRight_Content()).intValue();
                                    if ((new BigDecimal(couponAmount)).compareTo(payPrice) == 1) {
                                        payPrice = BigDecimal.ZERO;
                                    } else {
                                        payPrice = payPrice.subtract(new BigDecimal(couponAmount));
                                    }
                                } else {
                                    BigDecimal discount = new BigDecimal(shoppingCoupon.getRight_Content());
                                    payPrice = payPrice.multiply(discount).setScale(2, 4);
                                    if (useIntegralSet == 1)
                                        fixedIntegalValue = (new BigDecimal(fixedIntegalValue)).multiply(discount).intValue();
                                }
                                if (useIntegralSet == 1) {
                                    couponCut = originPrice.subtract(payPrice).add((new BigDecimal(originntegalValue - fixedIntegalValue)).divide(new BigDecimal(this.moneyToIntegralScale)).setScale(2, 4));
                                } else {
                                    couponCut = originPrice.subtract(payPrice);
                                }
                                originPrice = payPrice;
                                originntegalValue = fixedIntegalValue;
                            }
                        }
                        if (useMembershipSet == 1) {
                            BigDecimal discount = CommonUtil.getUserMemberShip(userId);
                            payPrice = payPrice.multiply(discount).setScale(2, 4);
                            if (useIntegralSet == 1)
                                fixedIntegalValue = (new BigDecimal(fixedIntegalValue)).multiply(discount).intValue();
                            if (useIntegralSet == 1) {
                                accountCut = originPrice.subtract(payPrice).add((new BigDecimal(originntegalValue - fixedIntegalValue)).divide(new BigDecimal(this.moneyToIntegralScale)).setScale(2, 4));
                            } else {
                                accountCut = originPrice.subtract(payPrice);
                            }
                            originPrice = payPrice;
                            originntegalValue = fixedIntegalValue;
                        }
                        ShoppingPayLimit payLimit = CommonUtil.getPayLimit();
                        int maxIntegralValue = 0;
                        int accountPoint = (bizDataJson.get("accountPoint") == null) ? 0 : bizDataJson.getInteger("accountPoint").intValue();
                        int pointPay = payLimit.getPointPay();
                        if (useIntegralSet == 1 || (null != reqJson.get("useIntegral") && reqJson.getInteger("useIntegral").intValue() == 1)) {
                            if (useIntegralSet == 1) {
                                maxIntegralValue = fixedIntegalValue;
                            } else {
                                int payPriceToInt = payPrice.multiply(new BigDecimal(this.moneyToIntegralScale)).intValue();
                                maxIntegralValue = (useIntegralValue * goodsCount < payPriceToInt) ? (useIntegralValue * goodsCount) : payPriceToInt;
                                int pointLimit = accountPoint;
                                if (pointPay > 0)
                                    pointLimit = (pointPay > pointLimit) ? pointLimit : pointPay;
                                if (maxIntegralValue > pointLimit)
                                    maxIntegralValue = pointLimit;
                            }
                            integralValue = maxIntegralValue;
                            BigDecimal integralAmount = (new BigDecimal(maxIntegralValue)).divide(new BigDecimal(this.moneyToIntegralScale)).setScale(2, 4);
                            if (useIntegralSet == 1) {
                                integralCut = integralAmount;
                            } else {
                                payPrice = payPrice.subtract(integralAmount);
                                integralCut = originPrice.subtract(payPrice);
                            }
                            originPrice = payPrice;
                        }
                        if (maxIntegralValue > accountPoint) {
                            retCode = "1";
                            retMsg = "当前账户积分不够，无法下单！";
                        } else if (maxIntegralValue > pointPay) {
                            retCode = "1";
                            retMsg = "积分单次支付限额" + pointPay + ",当前订单已超出该额度，无法下单！";
                        } else {
                            BigDecimal deductionBalance = BigDecimal.ZERO;
                            if (null != reqJson.get("useBalance") && reqJson.getInteger("useBalance").intValue() == 1) {
                                BigDecimal accountLimit = bizDataJson.getBigDecimal("accountMoney");
                                BigDecimal balancePay = new BigDecimal(payLimit.getBalancePay());
                                if (balancePay.compareTo(BigDecimal.ZERO) > 0 && balancePay.compareTo(accountLimit) < 0)
                                    accountLimit = balancePay;
                                if (payPrice.compareTo(accountLimit) == 1) {
                                    deductionBalance = accountLimit;
                                } else {
                                    deductionBalance = payPrice;
                                }
                                payPrice = payPrice.subtract(deductionBalance);
                                balanceCut = deductionBalance;
                            }
                            if (totalPrice.compareTo(orderTotalPrice) != 0 || payPrice.compareTo(orderPayPrice) != 0 || maxIntegralValue != orderUseIntegralValue || deductionBalance.compareTo(orderDeductionBalancePrice) != 0) {
                                retCode = "-1";
                                retMsg = "订单金额发生变化，请重新确认订单信息！";
                            } else if (maxIntegralValue > account_point) {
                                retCode = "-1";
                                retMsg = "当前账户积分不足，请重新确认订单信息！";
                            } else {
                                ShoppingOrderform orderform = new ShoppingOrderform();
                                String orderId = PayUtil.getOrderNo(Const.SHOPPING_PLAN_ORDER);
                                orderform.setOrderId(orderId);
                                orderform.setOrderType(Const.SHOPPING_PLAN_ORDER_TYPE);
                                orderform.setOrderStatus(Integer.valueOf(10));
                                orderform.setTotalPrice(totalPrice);
                                orderform.setPayPrice(payPrice);
                                orderform.setStoreId(Const.STORE_ID);
                                orderform.setUserId(userId);
                                ShoppingOrderPay shoppingOrderPay = new ShoppingOrderPay();
                                shoppingOrderPay.setUserId(userId);
                                if (payPrice.compareTo(BigDecimal.ZERO) == 1)
                                    shoppingOrderPay.setCashStatus(Integer.valueOf(0));
                                if (null != reqJson.get("couponId") && !"".equals(reqJson.get("couponId"))) {
                                    String couponId = reqJson.getString("couponId");
                                    orderform.setCiId(couponId);
                                    orderform.setDeductionCouponPrice(couponCut);
                                    shoppingOrderPay.setCouponStatus(Integer.valueOf(0));
                                    ShoppingCouponUsertemp shoppingCouponUsertemp = new ShoppingCouponUsertemp();
                                    shoppingCouponUsertemp.setUserId(userId);
                                    shoppingCouponUsertemp.setCouponId(couponId);
                                    this.shoppingCouponUsertempDao.insert(shoppingCouponUsertemp);
                                }
                                ShoppingOrderPaykey shoppingOrderPaykey = new ShoppingOrderPaykey();
                                if (useIntegralSet == 1 || (null != reqJson.get("useIntegral") && reqJson.getInteger("useIntegral").intValue() == 1)) {
                                    orderform.setDeductionIntegralPrice(integralCut);
                                    orderform.setDeductionIntegral(Integer.valueOf(integralValue));
                                    shoppingOrderPay.setIntegralStatus(Integer.valueOf(0));
                                    if (null != reqJson.get("accountPointPayKey") && !"".equals(reqJson.get("accountPointPayKey")))
                                        shoppingOrderPaykey.setAccountPointPayKey(reqJson.getString("accountPointPayKey"));
                                }
                                orderform.setDeductionMemberPrice(accountCut);
                                if (null != reqJson.get("useBalance") && reqJson.getInteger("useBalance").intValue() == 1) {
                                    orderform.setDeductionBalancePrice(balanceCut);
                                    shoppingOrderPay.setBalanceStatus(Integer.valueOf(0));
                                    if (null != reqJson.get("accountMoneyPayKey") && !"".equals(reqJson.get("accountMoneyPayKey")) &&
                                            null != reqJson.get("accountMoneyPayKey"))
                                        shoppingOrderPaykey.setAccountMoneyPayKey(reqJson.getString("accountMoneyPayKey"));
                                }
                                if (!dealPlanStock(reqJson,shoppingArtplan)) {
                                    retCode = "-1";
                                    retMsg = "可报名数量不足，请稍后再试！";
                                }else{
                                    this.shoppingOrderformDao.insert(orderform);
                                    if (null != shoppingOrderPaykey.getAccountPointPayKey() || null != shoppingOrderPaykey.getAccountMoneyPayKey()) {
                                        shoppingOrderPaykey.setOfId(orderform.getId());
                                        this.shoppingOrderPaykeyDao.insert(shoppingOrderPaykey);
                                    }
                                    shoppingOrderPay.setOfId(orderform.getId());
                                    this.shoppingOrderPayDao.insert(shoppingOrderPay);
                                    ShoppingOrderLog shoppingOrderLog = new ShoppingOrderLog();
                                    shoppingOrderLog.setLogInfo("提交订单");
                                    shoppingOrderLog.setLogUserId(userId);
                                    shoppingOrderLog.setOfId(orderform.getId());
                                    this.shoppingOrderLogDao.insert(shoppingOrderLog);
                                    ShoppingGoodscart goodscart = new ShoppingGoodscart();
                                    if (null != reqJson.get("cartId") && !"".equals(reqJson.get("cartId"))) {
                                        goodscart.setId(reqJson.getString("cartId"));
                                        goodscart = this.shoppingGoodscartDao.queryDetail(goodscart);
                                        goodscart.setDeductionCouponPrice(orderform.getDeductionCouponPrice());
                                        goodscart.setDeductionMemberPrice(orderform.getDeductionMemberPrice());
                                        goodscart.setDeductionIntegral(orderform.getDeductionIntegral());
                                        goodscart.setDeductionIntegralPrice(orderform.getDeductionIntegralPrice());
                                        goodscart.setDeductionBalancePrice(orderform.getDeductionBalancePrice());
                                        goodscart.setPayPrice(orderform.getPayPrice());
                                        goodscart.setPrice(currentPrice);
                                        goodscart.setCount(Integer.valueOf(goodsCount));
                                        goodscart.setOfId(orderform.getId());
                                        this.shoppingGoodscartDao.update(goodscart);
                                    } else {
                                        String scId = CommonUtil.getUserScId(userId);
                                        goodscart.setScId(scId);
                                        goodscart.setGoodsId(activityId);
                                        goodscart.setCount(Integer.valueOf(goodsCount));
                                        goodscart.setCartType(Const.SHOPPING_PLAN_CART_TYPE);
                                        if (null != reqJson.get("propertys")) {
                                            goodscart.setSpecInfo(reqJson.getString("specInfo"));
                                            goodscart.setPropertys(reqJson.getString("propertys"));
                                        }
                                        goodscart.setDeductionCouponPrice(orderform.getDeductionCouponPrice());
                                        goodscart.setDeductionMemberPrice(orderform.getDeductionMemberPrice());
                                        goodscart.setDeductionIntegral(orderform.getDeductionIntegral());
                                        goodscart.setDeductionIntegralPrice(orderform.getDeductionIntegralPrice());
                                        goodscart.setDeductionBalancePrice(orderform.getDeductionBalancePrice());
                                        goodscart.setPayPrice(orderform.getPayPrice());
                                        goodscart.setPrice(currentPrice);
                                        goodscart.setOfId(orderform.getId());
                                        this.shoppingGoodscartDao.insert(goodscart);
                                    }
                                    String gcId = goodscart.getId();
                                    ShoppingWriteoff shoppingWriteoff = new ShoppingWriteoff();
                                    shoppingWriteoff.setGcId(gcId);
                                    shoppingWriteoff.setGoodsCount(goodscart.getCount());
                                    shoppingWriteoff.setOffCode(StringUtil.randomOffCode(this.offcodeLength));
                                    this.shoppingWriteoffDao.insert(shoppingWriteoff);
//                                    if (null != reqJson.get("propertys") && !"".equals(reqJson.get("propertys"))) {
//                                        ShoppingArtplanInventory shoppingArtplanInventory = new ShoppingArtplanInventory();
//                                        shoppingArtplanInventory.setActivityId(shoppingArtplan.getId());
//                                        shoppingArtplanInventory.setPropertys(reqJson.getString("propertys"));
//                                        shoppingArtplanInventory.setCount(Integer.valueOf(inventoryCount - goodsCount));
//                                        this.shoppingArtplanInventoryDao.update(shoppingArtplanInventory);
//                                    }
                                    if (null != reqJson.get("propertys") && !"".equals(reqJson.get("propertys"))) {
                                        HashMap<String, Object> cutMap = new HashMap<>();
                                        cutMap.put("activityId", shoppingArtplan.getId());
                                        cutMap.put("propertys", reqJson.getString("propertys"));
                                        cutMap.put("cutCount", Integer.valueOf(goodsCount));
                                        this.shoppingArtplanInventoryDao.cutInventory(cutMap);
                                    }
                                    if (shoppingArtplan.getSignupTotalLimit().intValue() > 0)
                                        shoppingArtplan.setLeftnum(Integer.valueOf(shoppingArtplan.getLeftnum().intValue() - goodsCount));
                                    HashMap<String, Object> numMap = new HashMap<>();
                                    numMap.put("id", shoppingArtplan.getId());
                                    numMap.put("cutnum", Integer.valueOf(goodsCount));
                                    this.shoppingArtplanDao.updatePlanCutNum(numMap);
//                                    if (shoppingArtplan.getSignupTotalLimit().intValue() > 0)
//                                        shoppingArtplan.setLeftnum(Integer.valueOf(shoppingArtplan.getLeftnum().intValue() - goodsCount));
//                                    this.shoppingArtplanDao.updatePlanNum(shoppingArtplan);
                                    if (null != reqJson.get("signupInfos") && !"".equals(reqJson.get("signupInfos"))) {
                                        JSONArray infoArray = reqJson.getJSONArray("signupInfos");
                                        for (int j = 0; j < infoArray.size(); j++) {
                                            ShoppingArtplanSignupinfo shoppingArtplanSignupinfo = new ShoppingArtplanSignupinfo();
                                            shoppingArtplanSignupinfo.setActivityId(activityId);
                                            shoppingArtplanSignupinfo.setOfId(orderform.getId());
                                            shoppingArtplanSignupinfo.setSignupInfo(infoArray.getJSONArray(j).toString());
                                            this.shoppingArtplanSignupinfoDao.insert(shoppingArtplanSignupinfo);
                                        }
                                    }
                                    HashMap<String, Object> reqMap = new HashMap<>();
                                    reqMap.put("deleteStatus", Integer.valueOf(0));
                                    List<ShoppingPayment> payments = this.shoppingPaymentDao.queryList(reqMap);
                                    bizDataJson.put("payments", payments);
                                    bizDataJson.put("orderId", orderId);
                                    bizDataJson.put("price", payPrice);
                                    retCode = "0";
                                    retMsg = "操作成功！";
                                }


                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    public JSONObject addClassOrder(JSONObject reqJson, HttpServletRequest request) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "服务器内部错误！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String userId = reqJson.getString("userId");
            String mzUserId = CommonUtil.getMzUserId(userId);
            BigDecimal orderTotalPrice = reqJson.getBigDecimal("orderTotalPrice");
            BigDecimal orderPayPrice = reqJson.getBigDecimal("orderPayPrice");
            int orderUseIntegralValue = (reqJson.get("orderUseIntegralValue") == null) ? 0 : reqJson.getInteger("orderUseIntegralValue").intValue();
            BigDecimal orderDeductionBalancePrice = (reqJson.get("orderDeductionBalancePrice") == null) ? BigDecimal.ZERO : reqJson.getBigDecimal("orderDeductionBalancePrice");
            BigDecimal unitPrice = reqJson.getBigDecimal("unitPrice");
            String classId = reqJson.getString("classId");
            int goodsCount = reqJson.getInteger("goodsCount").intValue();
            ShoppingArtclass shoppingArtclass = new ShoppingArtclass();
            shoppingArtclass.setId(classId);
            shoppingArtclass = this.shoppingArtclassDao.queryDetail(shoppingArtclass);
            BigDecimal currentPrice = shoppingArtclass.getCurrentPrice();
            if (currentPrice.compareTo(unitPrice) != 0) {
                retCode = "-1";
                retMsg = "价格发生变化，请重新确认订单信息！";
            } else {
                boolean cartState = false;
                if (null != reqJson.get("cartId") && !"".equals(reqJson.get("cartId"))) {
                    ShoppingGoodscart goodscart = new ShoppingGoodscart();
                    goodscart.setId(reqJson.getString("cartId"));
                    goodscart = this.shoppingGoodscartDao.queryDetail(goodscart);
                    if (StringUtil.isNotNull(goodscart.getOfId()))
                        cartState = true;
                }
                if (cartState) {
                    retCode = "-1";
                    retMsg = "该购物车记录不存在，请刷新购物车！";
                } else {
                    int account_point = 0;
                    BigDecimal accountMoney = BigDecimal.ZERO;
                    int point_avoid_limit = 0;
                    int account_money_fen = 0;
                    int account_avoid_limit = 0;
                    boolean accountState = true;
                    if (orderUseIntegralValue > 0 || orderDeductionBalancePrice.compareTo(BigDecimal.ZERO) == 1 || (null != reqJson.get("useIntegral") && reqJson.getInteger("useIntegral").intValue() == 1) || (null != reqJson.get("useBalance") && reqJson.getInteger("useBalance").intValue() == 1)) {
                        JSONObject accountObj = MZService.getAssetinfo(mzUserId);
                        if (null != accountObj) {
                            account_point = (accountObj.get("account_point") == null) ? 0 : accountObj.getInteger("account_point").intValue();
                            account_money_fen = (accountObj.get("account_money_fen") == null) ? 0 : accountObj.getInteger("account_money_fen").intValue();
                            accountMoney = (new BigDecimal(account_money_fen)).divide(new BigDecimal(100)).setScale(2, 4);
                            bizDataJson.put("accountPoint", Integer.valueOf(account_point));
                            bizDataJson.put("accountMoney", accountMoney);
                        } else {
                            accountState = false;
                            bizDataJson.put("accountPoint", Integer.valueOf(0));
                            bizDataJson.put("accountMoney", Integer.valueOf(0));
                        }
                        ShoppingAssetRule shoppingAssetRule = CommonUtil.getAssetRule();
                        if (null != shoppingAssetRule) {
                            bizDataJson.put("accountPointLimit", Integer.valueOf(shoppingAssetRule.getPointAvoidLimit()));
                            bizDataJson.put("accountMoneyLimit", shoppingAssetRule.getAccountAvoidLimit());
                        }
                    }
                    int useIntegralSet = shoppingArtclass.getUseIntegralSet().intValue();
                    int useIntegralValue = shoppingArtclass.getUseIntegralValue().intValue();
                    int useBalanceSet = shoppingArtclass.getUseBalanceSet().intValue();
                    int useMembershipSet = shoppingArtclass.getUseMembershipSet().intValue();
                    if (!accountState) {
                        retCode = "-1";
                        retMsg = "无法获取您的账户积分和余额数据，请稍后下单！";
                    } else if (accountMoney.compareTo(orderDeductionBalancePrice) < 0 || account_point < orderUseIntegralValue) {
                        retCode = "-1";
                        retMsg = "账户积分或余额不足，请重新确认订单信息！";
                    } else {
                        BigDecimal totalPrice = currentPrice.multiply(new BigDecimal(goodsCount));
                        BigDecimal cashPrice = BigDecimal.ZERO;
                        int fixedIntegalValue = 0;
                        if (useIntegralSet == 1) {
                            if (currentPrice.multiply(new BigDecimal(100)).intValue() < useIntegralValue)
                                useIntegralValue = currentPrice.multiply(new BigDecimal(100)).intValue();
                            fixedIntegalValue = useIntegralValue * goodsCount;
                            cashPrice = totalPrice.subtract((new BigDecimal(fixedIntegalValue)).divide(new BigDecimal(this.moneyToIntegralScale)).setScale(2, 4));
                        }
                        BigDecimal payPrice = BigDecimal.ZERO;
                        if (useIntegralSet == 1) {
                            payPrice = cashPrice.multiply(new BigDecimal(goodsCount));
                        } else {
                            payPrice = currentPrice.multiply(new BigDecimal(goodsCount));
                        }
                        BigDecimal originPrice = payPrice;
                        int originntegalValue = fixedIntegalValue;
                        BigDecimal couponCut = BigDecimal.ZERO;
                        BigDecimal accountCut = BigDecimal.ZERO;
                        int integralValue = 0;
                        BigDecimal integralCut = BigDecimal.ZERO;
                        BigDecimal balanceCut = BigDecimal.ZERO;
                        List<String> couponIds = new ArrayList<>();
                        JSONArray couponArray = CommonUtil.getArtCouppon(classId, 4, userId, totalPrice, payPrice);
                        for (int i = 0; i < couponArray.size(); i++) {
                            JSONObject obj = couponArray.getJSONObject(i);
                            couponIds.add(obj.getString("id"));
                        }
                        if (null != reqJson.get("couponId") && !"".equals(reqJson.get("couponId")) && couponIds.contains(reqJson.get("couponId"))) {
                            String couponId = reqJson.getString("couponId");
                            JSONObject couponDtl = CRMService.getUserCouponDtl(couponId);
                            if (null != couponDtl) {
                                String right_No = couponDtl.getString("right_No");
                                ShoppingCoupon shoppingCoupon = new ShoppingCoupon();
                                shoppingCoupon.setRight_No(right_No);
                                shoppingCoupon = this.shoppingCouponDao.queryDetail(shoppingCoupon);
                                if (shoppingCoupon.getRight_Type().equals("coincp")) {
                                    int couponAmount = Integer.valueOf(shoppingCoupon.getRight_Content()).intValue();
                                    if ((new BigDecimal(couponAmount)).compareTo(payPrice) == 1) {
                                        payPrice = BigDecimal.ZERO;
                                    } else {
                                        payPrice = payPrice.subtract(new BigDecimal(couponAmount));
                                    }
                                } else {
                                    BigDecimal discount = new BigDecimal(shoppingCoupon.getRight_Content());
                                    payPrice = payPrice.multiply(discount).setScale(2, 4);
                                    if (useIntegralSet == 1)
                                        fixedIntegalValue = (new BigDecimal(fixedIntegalValue)).multiply(discount).intValue();
                                }
                                if (useIntegralSet == 1) {
                                    couponCut = originPrice.subtract(payPrice).add((new BigDecimal(originntegalValue - fixedIntegalValue)).divide(new BigDecimal(this.moneyToIntegralScale)).setScale(2, 4));
                                } else {
                                    couponCut = originPrice.subtract(payPrice);
                                }
                                originPrice = payPrice;
                                originntegalValue = fixedIntegalValue;
                            }
                        }
                        if (useMembershipSet == 1) {
                            BigDecimal discount = CommonUtil.getUserMemberShip(userId);
                            payPrice = payPrice.multiply(discount).setScale(2, 4);
                            if (useIntegralSet == 1)
                                fixedIntegalValue = (new BigDecimal(fixedIntegalValue)).multiply(discount).intValue();
                            if (useIntegralSet == 1) {
                                accountCut = originPrice.subtract(payPrice).add((new BigDecimal(originntegalValue - fixedIntegalValue)).divide(new BigDecimal(this.moneyToIntegralScale)).setScale(2, 4));
                            } else {
                                accountCut = originPrice.subtract(payPrice);
                            }
                            originPrice = payPrice;
                            originntegalValue = fixedIntegalValue;
                        }
                        ShoppingPayLimit payLimit = CommonUtil.getPayLimit();
                        int maxIntegralValue = 0;
                        int accountPoint = (bizDataJson.get("accountPoint") == null) ? 0 : bizDataJson.getInteger("accountPoint").intValue();
                        int pointPay = payLimit.getPointPay();
                        if (useIntegralSet == 1 || (null != reqJson.get("useIntegral") && reqJson.getInteger("useIntegral").intValue() == 1)) {
                            if (useIntegralSet == 1) {
                                maxIntegralValue = fixedIntegalValue;
                            } else {
                                int payPriceToInt = payPrice.multiply(new BigDecimal(this.moneyToIntegralScale)).intValue();
                                maxIntegralValue = (useIntegralValue * goodsCount < payPriceToInt) ? (useIntegralValue * goodsCount) : payPriceToInt;
                                int pointLimit = accountPoint;
                                if (pointPay > 0)
                                    pointLimit = (pointPay > pointLimit) ? pointLimit : pointPay;
                                if (maxIntegralValue > pointLimit)
                                    maxIntegralValue = pointLimit;
                            }
                            integralValue = maxIntegralValue;
                            BigDecimal integralAmount = (new BigDecimal(maxIntegralValue)).divide(new BigDecimal(this.moneyToIntegralScale)).setScale(2, 4);
                            if (useIntegralSet == 1) {
                                integralCut = integralAmount;
                            } else {
                                payPrice = payPrice.subtract(integralAmount);
                                integralCut = originPrice.subtract(payPrice);
                            }
                            originPrice = payPrice;
                        }
                        if (maxIntegralValue > accountPoint) {
                            retCode = "1";
                            retMsg = "当前账户积分不够，无法下单！";
                        } else if (maxIntegralValue > pointPay) {
                            retCode = "1";
                            retMsg = "积分单次支付限额" + pointPay + ",当前订单已超出该额度，无法下单！";
                        } else {
                            BigDecimal deductionBalance = BigDecimal.ZERO;
                            if (null != reqJson.get("useBalance") && reqJson.getInteger("useBalance").intValue() == 1) {
                                BigDecimal accountLimit = bizDataJson.getBigDecimal("accountMoney");
                                BigDecimal balancePay = new BigDecimal(payLimit.getBalancePay());
                                if (balancePay.compareTo(BigDecimal.ZERO) > 0 && balancePay.compareTo(accountLimit) < 0)
                                    accountLimit = balancePay;
                                if (payPrice.compareTo(accountLimit) == 1) {
                                    deductionBalance = accountLimit;
                                } else {
                                    deductionBalance = payPrice;
                                }
                                payPrice = payPrice.subtract(deductionBalance);
                                balanceCut = deductionBalance;
                            }
                            if (totalPrice.compareTo(orderTotalPrice) != 0 || payPrice.compareTo(orderPayPrice) != 0 || maxIntegralValue != orderUseIntegralValue || deductionBalance.compareTo(orderDeductionBalancePrice) != 0) {
                                retCode = "-1";
                                retMsg = "订单金额发生变化，请重新确认订单信息！";
                            } else if (maxIntegralValue > account_point) {
                                retCode = "-1";
                                retMsg = "当前账户积分不足，请重新确认订单信息！";
                            } else {
                                ShoppingOrderform orderform = new ShoppingOrderform();
                                String orderId = PayUtil.getOrderNo(Const.SHOPPING_CLASS_ORDER);
                                orderform.setOrderId(orderId);
                                orderform.setOrderType(Const.SHOPPING_CLASS_ORDER_TYPE);
                                orderform.setOrderStatus(Integer.valueOf(10));
                                orderform.setTotalPrice(totalPrice);
                                orderform.setPayPrice(payPrice);
                                orderform.setStoreId(Const.STORE_ID);
                                orderform.setUserId(userId);
                                ShoppingOrderPay shoppingOrderPay = new ShoppingOrderPay();
                                shoppingOrderPay.setUserId(userId);
                                if (payPrice.compareTo(BigDecimal.ZERO) == 1)
                                    shoppingOrderPay.setCashStatus(Integer.valueOf(0));
                                if (null != reqJson.get("couponId") && !"".equals(reqJson.get("couponId"))) {
                                    String couponId = reqJson.getString("couponId");
                                    orderform.setCiId(couponId);
                                    orderform.setDeductionCouponPrice(couponCut);
                                    shoppingOrderPay.setCouponStatus(Integer.valueOf(0));
                                    ShoppingCouponUsertemp shoppingCouponUsertemp = new ShoppingCouponUsertemp();
                                    shoppingCouponUsertemp.setUserId(userId);
                                    shoppingCouponUsertemp.setCouponId(couponId);
                                    this.shoppingCouponUsertempDao.insert(shoppingCouponUsertemp);
                                }
                                ShoppingOrderPaykey shoppingOrderPaykey = new ShoppingOrderPaykey();
                                if (useIntegralSet == 1 || (null != reqJson.get("useIntegral") && reqJson.getInteger("useIntegral").intValue() == 1)) {
                                    orderform.setDeductionIntegralPrice(integralCut);
                                    orderform.setDeductionIntegral(Integer.valueOf(integralValue));
                                    shoppingOrderPay.setIntegralStatus(Integer.valueOf(0));
                                    if (null != reqJson.get("accountPointPayKey") && !"".equals(reqJson.get("accountPointPayKey")))
                                        shoppingOrderPaykey.setAccountPointPayKey(reqJson.getString("accountPointPayKey"));
                                }
                                orderform.setDeductionMemberPrice(accountCut);
                                if (null != reqJson.get("useBalance") && reqJson.getInteger("useBalance").intValue() == 1) {
                                    orderform.setDeductionBalancePrice(balanceCut);
                                    shoppingOrderPay.setBalanceStatus(Integer.valueOf(0));
                                    if (null != reqJson.get("accountMoneyPayKey") && !"".equals(reqJson.get("accountMoneyPayKey")))
                                        shoppingOrderPaykey.setAccountMoneyPayKey(reqJson.getString("accountMoneyPayKey"));
                                }
                                this.shoppingOrderformDao.insert(orderform);
                                if (null != shoppingOrderPaykey.getAccountPointPayKey() || null != shoppingOrderPaykey.getAccountMoneyPayKey()) {
                                    shoppingOrderPaykey.setOfId(orderform.getId());
                                    this.shoppingOrderPaykeyDao.insert(shoppingOrderPaykey);
                                }
                                shoppingOrderPay.setOfId(orderform.getId());
                                this.shoppingOrderPayDao.insert(shoppingOrderPay);
                                ShoppingOrderLog shoppingOrderLog = new ShoppingOrderLog();
                                shoppingOrderLog.setLogInfo("提交订单");
                                shoppingOrderLog.setLogUserId(userId);
                                shoppingOrderLog.setOfId(orderform.getId());
                                this.shoppingOrderLogDao.insert(shoppingOrderLog);
                                ShoppingGoodscart goodscart = new ShoppingGoodscart();
                                if (null != reqJson.get("cartId") && !"".equals(reqJson.get("cartId"))) {
                                    goodscart.setId(reqJson.getString("cartId"));
                                    goodscart = this.shoppingGoodscartDao.queryDetail(goodscart);
                                    goodscart.setDeductionCouponPrice(orderform.getDeductionCouponPrice());
                                    goodscart.setDeductionMemberPrice(orderform.getDeductionMemberPrice());
                                    goodscart.setDeductionIntegral(orderform.getDeductionIntegral());
                                    goodscart.setDeductionIntegralPrice(orderform.getDeductionIntegralPrice());
                                    goodscart.setDeductionBalancePrice(orderform.getDeductionBalancePrice());
                                    goodscart.setPayPrice(orderform.getPayPrice());
                                    goodscart.setPrice(currentPrice);
                                    goodscart.setOfId(orderform.getId());
                                    goodscart.setCount(Integer.valueOf(goodsCount));
                                    this.shoppingGoodscartDao.update(goodscart);
                                } else {
                                    String scId = CommonUtil.getUserScId(userId);
                                    goodscart.setScId(scId);
                                    goodscart.setGoodsId(classId);
                                    goodscart.setCount(Integer.valueOf(goodsCount));
                                    goodscart.setCartType(Const.SHOPPING_CLASS_CART_TYPE);
                                    goodscart.setDeductionCouponPrice(orderform.getDeductionCouponPrice());
                                    goodscart.setDeductionMemberPrice(orderform.getDeductionMemberPrice());
                                    goodscart.setDeductionIntegral(orderform.getDeductionIntegral());
                                    goodscart.setDeductionIntegralPrice(orderform.getDeductionIntegralPrice());
                                    goodscart.setDeductionBalancePrice(orderform.getDeductionBalancePrice());
                                    goodscart.setPayPrice(orderform.getPayPrice());
                                    goodscart.setPrice(currentPrice);
                                    goodscart.setOfId(orderform.getId());
                                    this.shoppingGoodscartDao.insert(goodscart);
                                }
                                if (null != reqJson.get("signupInfos") && !"".equals(reqJson.get("signupInfos"))) {
                                    JSONArray infoArray = reqJson.getJSONArray("signupInfos");
                                    for (int j = 0; j < infoArray.size(); j++) {
                                        ShoppingArtclassSignupinfo shoppingArtclassSignupinfo = new ShoppingArtclassSignupinfo();
                                        shoppingArtclassSignupinfo.setClassId(classId);
                                        shoppingArtclassSignupinfo.setOfId(orderform.getId());
                                        shoppingArtclassSignupinfo.setSignupInfo(infoArray.getJSONArray(j).toString());
                                        this.shoppingArtclassSignupinfoDao.insert(shoppingArtclassSignupinfo);
                                    }
                                }
                                HashMap<String, Object> reqMap = new HashMap<>();
                                reqMap.put("deleteStatus", Integer.valueOf(0));
                                List<ShoppingPayment> payments = this.shoppingPaymentDao.queryList(reqMap);
                                bizDataJson.put("payments", payments);
                                bizDataJson.put("orderId", orderId);
                                bizDataJson.put("price", payPrice);
                                retCode = "0";
                                retMsg = "操作成功！";
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    public boolean dealActivityStock(JSONObject reqJson,ShoppingArtactivity shoppingArtactivity) {
        String activityId = reqJson.getString("activityId");
        int goodsCount = reqJson.getInteger("goodsCount").intValue();
        try{
            int limitBuy = shoppingArtactivity.getSignupPerLimit().intValue();
            if(limitBuy==1){
                String userId = reqJson.getString("userId");
                SimpleDateFormat sbf = new SimpleDateFormat("yyyyMMddHHHHmm");
                String dateStr = sbf.format(new Date());
                long ad = redisTemplate.opsForSet().add("REDIS_KEY:STOCK:" + activityId,dateStr+userId);
                if(ad <=0){
                    return false;
                }
            }
        }catch (Exception e){
            return false;
        }

        if (null != reqJson.get("propertys") && !"".equals(reqJson.get("propertys"))) {
            String inventory_key = "REDIS_KEY:STOCK:" + activityId + reqJson.get("propertys");
            String key = "REDIS_KEY:STOCK:" + activityId;
            if (this.redisStockService.updateStock(key, goodsCount) >= 0L && this.redisStockService.updateStock(inventory_key, goodsCount) >= 0L)
                return true;
        } else {
            String key = "REDIS_KEY:STOCK:" + activityId;
            if (this.redisStockService.updateStock(key, goodsCount) >= 0L)
                return true;
        }
        return false;
    }

    public boolean dealPlanStock(JSONObject reqJson,ShoppingArtplan shoppingArtplan) {
        String activityId = reqJson.getString("activityId");
        int goodsCount = reqJson.getInteger("goodsCount").intValue();
        try{
            int limitBuy = shoppingArtplan.getSignupPerLimit().intValue();
            if(limitBuy==1){
                String userId = reqJson.getString("userId");
                SimpleDateFormat sbf = new SimpleDateFormat("yyyyMMddHHHHmm");
                String dateStr = sbf.format(new Date());
                long ad = redisTemplate.opsForSet().add(REDIS_KEY_PLAN + activityId,dateStr+userId);
                if(ad <=0){
                    return false;
                }
            }
        }catch (Exception e){
            return false;
        }

        if (null != reqJson.get("propertys") && !"".equals(reqJson.get("propertys"))) {
            String inventory_key = REDIS_KEY_PLAN + activityId + reqJson.get("propertys");
            String key = REDIS_KEY_PLAN + activityId;
            if (this.redisStockService.updateStock(key, goodsCount) >= 0L && this.redisStockService.updateStock(inventory_key, goodsCount) >= 0L)
                return true;
        } else {
            String key = REDIS_KEY_PLAN + activityId;
            if (this.redisStockService.updateStock(key, goodsCount) >= 0L)
                return true;
        }
        return false;
    }

    public boolean openConcurrencySwitch() {
        try {
            TConcurrencySwitch tConcurrencySwitch = this.tConcurrencySwitchDao.queryDetail(null);
            if (tConcurrencySwitch != null && tConcurrencySwitch.getHomepageApiSwitch().equals("on"))
                return true;
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}
