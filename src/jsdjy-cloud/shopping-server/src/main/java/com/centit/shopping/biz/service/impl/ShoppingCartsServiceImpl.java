package com.centit.shopping.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.biz.service.ShoppingCartsService;
import com.centit.shopping.common.enums.Const;
import com.centit.shopping.dao.*;
import com.centit.shopping.po.*;
import com.centit.shopping.redis.RedisStockService;
import com.centit.shopping.utils.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

@Transactional
@Service
public class ShoppingCartsServiceImpl implements ShoppingCartsService {
    public static final Log log = LogFactory.getLog(ShoppingCartsService.class);

    @Resource
    private ShoppingGoodsDao shoppingGoodsDao;

    @Resource
    private ShoppingGoodsspecpropertyDao shoppingGoodsspecpropertyDao;

    @Resource
    private ShoppingStorecartDao shoppingStorecartDao;

    @Resource
    private ShoppingGoodscartDao shoppingGoodscartDao;

    @Resource
    private ShoppingOrderformDao shoppingOrderformDao;

    @Resource
    private ShoppingTransportDao shoppingTransportDao;

    @Resource
    private ShoppingOrderLogDao shoppingOrderLogDao;

    @Resource
    private ShoppingOrderPayDao shoppingOrderPayDao;

    @Resource
    private ShoppingGoodsInventoryDao shoppingGoodsInventoryDao;

    @Resource
    private ShoppingAddressDao shoppingAddressDao;

    @Resource
    private ShoppingPaymentDao shoppingPaymentDao;

    @Resource
    private ShoppingOrderPaykeyDao shoppingOrderPaykeyDao;

    @Resource
    private ShoppingArtactivityDao shoppingArtactivityDao;

    @Resource
    private ShoppingArtplanDao shoppingArtplanDao;

    @Resource
    private ShoppingArtclassDao shoppingArtclassDao;

    @Resource
    private ShoppingArtactivitySignupinfoDao shoppingArtactivitySignupinfoDao;

    @Resource
    private ShoppingArtplanSignupinfoDao shoppingArtplanSignupinfoDao;

    @Resource
    private ShoppingArtclassSignupinfoDao shoppingArtclassSignupinfoDao;

    @Resource
    private ShoppingCouponUsertempDao shoppingCouponUsertempDao;

    @Resource
    private ShoppingArtinfosDao shoppingArtinfosDao;

    @Resource
    private ShoppingWriteoffDao shoppingWriteoffDao;

    @Resource
    private ShoppingCouponDao shoppingCouponDao;

    @Resource
    private ShoppingArtactivityInventoryDao shoppingArtactivityInventoryDao;

    @Resource
    private ShoppingArtplanInventoryDao shoppingArtplanInventoryDao;

    @Resource
    @Value("${goods.culGoods.firstClassId}")
    private String firstCulGoodsClassId;

    @Value("${goods.integralGoods.firstClassId}")
    private String firstIntegralGoodsClassId;

    @Value("${moneyToIntegralScale}")
    private int moneyToIntegralScale;

    @Value("${offcodeLength}")
    private int offcodeLength;

    public static final String REDIS_KEY = "REDIS_KEY:STOCK:";

    public static final String REDIS_KEY_GOODS = "REDIS_KEY:STOCK:GOODS";

    @Resource
    private RedisStockService redisStockService;

    public JSONObject addCart(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String goodsId = reqJson.getString("goodsId");
            int count = reqJson.getInteger("count").intValue();
            String userId = reqJson.getString("userId");
            int cartType = reqJson.getInteger("cartType").intValue();
            String goodsStoreId = CommonUtil.getSystemStore().getId();
            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("userId", userId);
            reqMap.put("storeId", goodsStoreId);
            reqMap.put("deleteStatus", Character.valueOf('0'));
            List<ShoppingStorecart> shoppingStorecartList = this.shoppingStorecartDao.queryList(reqMap);
            ShoppingStorecart shoppingStorecart = new ShoppingStorecart();
            if (shoppingStorecartList.isEmpty()) {
                shoppingStorecart.setUserId(userId);
                shoppingStorecart.setStoreId(goodsStoreId);
                this.shoppingStorecartDao.insert(shoppingStorecart);
            } else {
                shoppingStorecart = shoppingStorecartList.get(0);
            }
            String scId = shoppingStorecart.getId();
            ShoppingGoodscart shoppingGoodscart = new ShoppingGoodscart();
            reqMap.clear();
            reqMap.put("scId", scId);
            reqMap.put("goodsId", goodsId);
            reqMap.put("deleteStatus", "0");
            reqMap.put("cartType", Integer.valueOf(cartType));
            if (null != reqJson.get("propertys")) {
                String propertys = reqJson.getString("propertys");
                reqMap.put("propertys", propertys);
                shoppingGoodscart.setPropertys(propertys);
                shoppingGoodscart.setSpecInfo(reqJson.getString("specInfo"));
            }
            List<ShoppingGoodscart> shoppingGoodscartList = this.shoppingGoodscartDao.queryCartList(reqMap);
            if (shoppingGoodscartList.isEmpty()) {
                shoppingGoodscart.setScId(scId);
                shoppingGoodscart.setGoodsId(goodsId);
                shoppingGoodscart.setCount(Integer.valueOf(count));
                shoppingGoodscart.setCartType(Integer.valueOf(cartType));
                this.shoppingGoodscartDao.insert(shoppingGoodscart);
            } else {
                shoppingGoodscart = shoppingGoodscartList.get(0);
                shoppingGoodscart.setCount(Integer.valueOf(shoppingGoodscart.getCount().intValue() + count));
                this.shoppingGoodscartDao.update(shoppingGoodscart);
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

    public JSONObject getUserCartInfo(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String userId = reqJson.getString("userId");
            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("userId", userId);
            reqMap.put("storeId", CommonUtil.getSystemStore().getId());
            reqMap.put("deleteStatus", Integer.valueOf(0));
            List<ShoppingStorecart> shoppingStorecartList = this.shoppingStorecartDao.queryList(reqMap);
            for (ShoppingStorecart shoppingStorecart : shoppingStorecartList) {
                reqMap.clear();
                reqMap.put("scId", shoppingStorecart.getId());
                reqMap.put("deleteStatus", Boolean.valueOf(false));
                List<ShoppingGoodscart> shoppingGoodscartList = this.shoppingGoodscartDao.queryCartGoodsList(reqMap);
                List<HashMap<String, Object>> goodsList = new ArrayList<>();
                for (ShoppingGoodscart shoppingGoodscart : shoppingGoodscartList) {
                    HashMap<String, Object> goodsMap = new HashMap<>();
                    goodsMap.put("id", shoppingGoodscart.getId());
                    goodsMap.put("scId", shoppingStorecart.getId());
                    goodsMap.put("cartType", shoppingGoodscart.getCartType());
                    if (shoppingGoodscart.getCartType() == Const.SHOPPING_CUL_CART_TYPE || shoppingGoodscart.getCartType() == Const.SHOPPING_INT_CART_TYPE) {
                        ShoppingGoods shoppingGoods = new ShoppingGoods();
                        shoppingGoods.setId(shoppingGoodscart.getGoodsId());
                        shoppingGoods = this.shoppingGoodsDao.queryDetail(shoppingGoods);
                        if (shoppingGoods.getDeleteStatus().equals("1") || shoppingGoods.getGoodsStatus().equals("1")) {
                            goodsMap.put("isOff", Boolean.valueOf(true));
                        } else {
                            goodsMap.put("isOff", Boolean.valueOf(false));
                        }
                        goodsMap.put("goodsId", shoppingGoods.getId());
                        goodsMap.put("goodsName", shoppingGoods.getGoodsName());
                        goodsMap.put("count", shoppingGoodscart.getCount());
                        goodsMap.put("photoId", shoppingGoods.getGoodsMainPhotoId());
                        goodsMap.put("limitBuy", shoppingGoods.getLimitBuy());
                        if (null != shoppingGoodscart.getPropertys()) {
                            ShoppingGoodsInventory shoppingGoodsInventory = new ShoppingGoodsInventory();
                            shoppingGoodsInventory.setGoodsId(shoppingGoods.getId());
                            shoppingGoodsInventory.setPropertys(shoppingGoodscart.getPropertys());
                            shoppingGoodsInventory = this.shoppingGoodsInventoryDao.queryDetail(shoppingGoodsInventory);
                            goodsMap.put("currentPrice", shoppingGoodsInventory.getPrice());
                            goodsMap.put("specInfo", shoppingGoodscart.getSpecInfo());
                            goodsMap.put("propertys", shoppingGoodscart.getPropertys());
                        } else {
                            goodsMap.put("currentPrice", shoppingGoods.getStorePrice());
                        }
                    } else if (shoppingGoodscart.getCartType() == Const.SHOPPING_ACT_CART_TYPE) {
                        ShoppingArtactivity shoppingArtactivity = new ShoppingArtactivity();
                        shoppingArtactivity.setId(shoppingGoodscart.getGoodsId());
                        shoppingArtactivity = this.shoppingArtactivityDao.queryDetail(shoppingArtactivity);
                        if (shoppingArtactivity.getDeleteStatus().equals("1") || shoppingArtactivity.getActivityStatus().intValue() == 0) {
                            goodsMap.put("isOff", Boolean.valueOf(true));
                        } else {
                            goodsMap.put("isOff", Boolean.valueOf(false));
                        }
                        goodsMap.put("goodsId", shoppingArtactivity.getId());
                        goodsMap.put("goodsName", shoppingArtactivity.getActivityName());
                        goodsMap.put("count", shoppingGoodscart.getCount());
                        goodsMap.put("photoId", shoppingArtactivity.getMainPhotoId());
                        goodsMap.put("limitBuy", shoppingArtactivity.getSignupPerLimit());
                        if (null != shoppingGoodscart.getPropertys()) {
                            ShoppingArtactivityInventory shoppingArtactivityInventory = new ShoppingArtactivityInventory();
                            shoppingArtactivityInventory.setActivityId(shoppingArtactivity.getId());
                            shoppingArtactivityInventory.setPropertys(shoppingGoodscart.getPropertys());
                            shoppingArtactivityInventory = this.shoppingArtactivityInventoryDao.queryDetail(shoppingArtactivityInventory);
                            goodsMap.put("currentPrice", shoppingArtactivityInventory.getPrice());
                            goodsMap.put("specInfo", shoppingGoodscart.getSpecInfo());
                            goodsMap.put("propertys", shoppingGoodscart.getPropertys());
                        } else {
                            goodsMap.put("currentPrice", shoppingArtactivity.getCurrentPrice());
                        }
                    } else if (shoppingGoodscart.getCartType() == Const.SHOPPING_PLAN_CART_TYPE) {
                        ShoppingArtplan shoppingArtplan = new ShoppingArtplan();
                        shoppingArtplan.setId(shoppingGoodscart.getGoodsId());
                        shoppingArtplan = this.shoppingArtplanDao.queryDetail(shoppingArtplan);
                        if (shoppingArtplan.getDeleteStatus().equals("1") || shoppingArtplan.getActivityStatus().intValue() == 0) {
                            goodsMap.put("isOff", Boolean.valueOf(true));
                        } else {
                            goodsMap.put("isOff", Boolean.valueOf(false));
                        }
                        goodsMap.put("goodsId", shoppingArtplan.getId());
                        goodsMap.put("goodsName", shoppingArtplan.getActivityName());
                        goodsMap.put("count", shoppingGoodscart.getCount());
                        goodsMap.put("photoId", shoppingArtplan.getMainPhotoId());
                        goodsMap.put("limitBuy", shoppingArtplan.getSignupPerLimit());
                        if (null != shoppingGoodscart.getPropertys()) {
                            ShoppingArtplanInventory shoppingArtplanInventory = new ShoppingArtplanInventory();
                            shoppingArtplanInventory.setActivityId(shoppingArtplan.getId());
                            shoppingArtplanInventory.setPropertys(shoppingGoodscart.getPropertys());
                            shoppingArtplanInventory = this.shoppingArtplanInventoryDao.queryDetail(shoppingArtplanInventory);
                            goodsMap.put("currentPrice", shoppingArtplanInventory.getPrice());
                            goodsMap.put("specInfo", shoppingGoodscart.getSpecInfo());
                            goodsMap.put("propertys", shoppingGoodscart.getPropertys());
                        } else {
                            goodsMap.put("currentPrice", shoppingArtplan.getCurrentPrice());
                        }
                    } else if (shoppingGoodscart.getCartType() == Const.SHOPPING_CLASS_CART_TYPE) {
                        ShoppingArtclass shoppingArtclass = new ShoppingArtclass();
                        shoppingArtclass.setId(shoppingGoodscart.getGoodsId());
                        shoppingArtclass = this.shoppingArtclassDao.queryDetail(shoppingArtclass);
                        if (shoppingArtclass.getDeleteStatus().equals("1") || shoppingArtclass.getClassStatus().intValue() == 0) {
                            bizDataJson.put("isOff", Boolean.valueOf(true));
                        } else {
                            bizDataJson.put("isOff", Boolean.valueOf(false));
                        }
                        goodsMap.put("goodsId", shoppingArtclass.getId());
                        goodsMap.put("goodsName", shoppingArtclass.getClassName());
                        goodsMap.put("count", shoppingGoodscart.getCount());
                        goodsMap.put("photoId", shoppingArtclass.getMainPhotoId());
                        goodsMap.put("currentPrice", shoppingArtclass.getCurrentPrice());
                    }
                    goodsList.add(goodsMap);
                }
                shoppingStorecart.setGoodsList(goodsList);
            }
            bizDataJson.put("objList", shoppingStorecartList);
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

    public JSONObject updateCartGoodsCount(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingGoodscart shoppingGoodscart = (ShoppingGoodscart)JSON.parseObject(reqJson.toJSONString(), ShoppingGoodscart.class);
            this.shoppingGoodscartDao.update(shoppingGoodscart);
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

    public JSONObject delCartGoods(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String[] ids = reqJson.getString("id").split(",");
            for (int i = 0; i < ids.length; i++) {
                ShoppingGoodscart shoppingGoodscart = new ShoppingGoodscart();
                shoppingGoodscart.setId(ids[i]);
                this.shoppingGoodscartDao.delete(shoppingGoodscart);
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

    public JSONObject checkLimitNum(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "0";
        String retMsg = "操作成功！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String goodsStoreId = CommonUtil.getSystemStore().getId();
            String userId = reqJson.getString("userId");
            JSONArray goodsList = reqJson.getJSONArray("goodsList");
            HashSet<String> transportIds = new HashSet<>();
            Map<String, JSONObject> goodsMap = new HashMap<>();
            for (int i = 0; i < goodsList.size(); i++) {
                JSONObject goodsOne = goodsList.getJSONObject(i);
                String goodsId = goodsOne.getString("goodsId");
                int count = goodsOne.getInteger("count").intValue();
                int cartType = goodsOne.getInteger("cartType").intValue();
                if (null == goodsMap.get(goodsId + cartType)) {
                    JSONObject obj = new JSONObject();
                    obj.put("goodsId", goodsId);
                    obj.put("cartType", Integer.valueOf(cartType));
                    obj.put("count", Integer.valueOf(count));
                    goodsMap.put(goodsId + cartType, obj);
                } else {
                    JSONObject obj = goodsMap.get(goodsId + cartType);
                    int pcount = obj.getInteger("count").intValue();
                    obj.put("count", Integer.valueOf(pcount + count));
                    goodsMap.put(goodsId + cartType, obj);
                }
            }
            for (String key : goodsMap.keySet()) {
                JSONObject goodsOne = goodsMap.get(key);
                String goodsId = goodsOne.getString("goodsId");
                int count = goodsOne.getInteger("count").intValue();
                int cartType = goodsOne.getInteger("cartType").intValue();
                if (cartType == Const.SHOPPING_CUL_CART_TYPE.intValue() || cartType == Const.SHOPPING_INT_CART_TYPE.intValue()) {
                    ShoppingGoods shoppingGoods = new ShoppingGoods();
                    shoppingGoods.setId(goodsId);
                    shoppingGoods = this.shoppingGoodsDao.queryDetail(shoppingGoods);
                    if (shoppingGoods.getGoodsTransfee().intValue() == 0)
                        if (null != shoppingGoods.getTransportId() && (shoppingGoods.getExpressTransFee() == null || shoppingGoods.getExpressTransFee().compareTo(BigDecimal.ZERO) == 0))
                            transportIds.add(shoppingGoods.getTransportId());
                    if (shoppingGoods.getLimitBuy().intValue() == 0) {
                        bizDataJson.put("result", Boolean.valueOf(false));
                        continue;
                    }
                    int limitBuy = shoppingGoods.getLimitBuy().intValue();
                    String scId = CommonUtil.getUserScId(userId);
                    HashMap<String, Object> reqMap = new HashMap<>();
                    reqMap.put("scId", scId);
                    reqMap.put("goodsId", goodsId);
                    reqMap.put("userId", userId);
                    reqMap.put("deleteStatus", "0");
                    List<ShoppingGoodscart> shoppingGoodscartList = this.shoppingGoodscartDao.queryBuyList(reqMap);
                    int cartCount = 0;
                    for (ShoppingGoodscart shoppingGoodscart : shoppingGoodscartList)
                        cartCount += shoppingGoodscart.getCount().intValue();
                    bizDataJson.put("doneCount", Integer.valueOf(cartCount));
                    if (cartCount + count > limitBuy) {
                        bizDataJson.put("goodsId", goodsId);
                        bizDataJson.put("cartType", Integer.valueOf(cartType));
                        bizDataJson.put("limitNum", shoppingGoods.getLimitBuy());
                        String showMsg = "您当前选择结算的" + shoppingGoods.getGoodsName() + "每人限购数量为" + shoppingGoods.getLimitBuy() + ",您已超出此限制！";
                        bizDataJson.put("result", Boolean.valueOf(true));
                        bizDataJson.put("showMsg", showMsg);
                        break;
                    }
                    bizDataJson.put("result", Boolean.valueOf(false));
                    continue;
                }
                if (cartType == Const.SHOPPING_ACT_CART_TYPE.intValue()) {
                    ShoppingArtactivity shoppingArtactivity = new ShoppingArtactivity();
                    shoppingArtactivity.setId(goodsId);
                    shoppingArtactivity = this.shoppingArtactivityDao.queryDetail(shoppingArtactivity);
                    if (shoppingArtactivity.getSignupPerLimit().intValue() == 0) {
                        bizDataJson.put("result", Boolean.valueOf(false));
                        continue;
                    }
                    int limitBuy = shoppingArtactivity.getSignupPerLimit().intValue();
                    String scId = CommonUtil.getUserScId(userId);
                    HashMap<String, Object> reqMap = new HashMap<>();
                    reqMap.put("scId", scId);
                    reqMap.put("goodsId", goodsId);
                    reqMap.put("userId", userId);
                    reqMap.put("deleteStatus", "0");
                    List<ShoppingGoodscart> shoppingGoodscartList = this.shoppingGoodscartDao.queryBuyList(reqMap);
                    int cartCount = 0;
                    for (ShoppingGoodscart shoppingGoodscart : shoppingGoodscartList)
                        cartCount += shoppingGoodscart.getCount().intValue();
                    bizDataJson.put("doneCount", Integer.valueOf(cartCount));
                    if (cartCount + count > limitBuy) {
                        bizDataJson.put("goodsId", goodsId);
                        bizDataJson.put("cartType", Integer.valueOf(cartType));
                        bizDataJson.put("limitNum", shoppingArtactivity.getSignupPerLimit());
                        String showMsg = "您当前选择结算的" + shoppingArtactivity.getActivityName() + "每人限购数量为" + shoppingArtactivity.getSignupPerLimit() + ",您已超出此限制！";
                        bizDataJson.put("result", Boolean.valueOf(true));
                        bizDataJson.put("showMsg", showMsg);
                        break;
                    }
                    bizDataJson.put("result", Boolean.valueOf(false));
                    continue;
                }
                if (cartType == Const.SHOPPING_PLAN_CART_TYPE.intValue()) {
                    ShoppingArtplan shoppingArtplan = new ShoppingArtplan();
                    shoppingArtplan.setId(goodsId);
                    shoppingArtplan = this.shoppingArtplanDao.queryDetail(shoppingArtplan);
                    if (shoppingArtplan.getSignupPerLimit().intValue() == 0) {
                        bizDataJson.put("result", Boolean.valueOf(false));
                        continue;
                    }
                    int limitBuy = shoppingArtplan.getSignupPerLimit().intValue();
                    String scId = CommonUtil.getUserScId(userId);
                    HashMap<String, Object> reqMap = new HashMap<>();
                    reqMap.put("scId", scId);
                    reqMap.put("goodsId", goodsId);
                    reqMap.put("userId", userId);
                    reqMap.put("deleteStatus", "0");
                    List<ShoppingGoodscart> shoppingGoodscartList = this.shoppingGoodscartDao.queryBuyList(reqMap);
                    int cartCount = 0;
                    for (ShoppingGoodscart shoppingGoodscart : shoppingGoodscartList)
                        cartCount += shoppingGoodscart.getCount().intValue();
                    bizDataJson.put("doneCount", Integer.valueOf(cartCount));
                    if (cartCount + count > limitBuy) {
                        bizDataJson.put("goodsId", goodsId);
                        bizDataJson.put("cartType", Integer.valueOf(cartType));
                        bizDataJson.put("limitNum", shoppingArtplan.getSignupPerLimit());
                        String showMsg = "您当前选择结算的商品" + shoppingArtplan.getActivityName() + "每人限购数量为" + shoppingArtplan.getSignupPerLimit() + ",您已超出此限制！";
                        bizDataJson.put("result", Boolean.valueOf(true));
                        bizDataJson.put("showMsg", showMsg);
                        break;
                    }
                    bizDataJson.put("result", Boolean.valueOf(false));
                }
            }
            if (transportIds.size() > 1) {
                String showMsg = "您当前选择结算的文创或积分商品存在不同的运费计算方式，暂不支持合并支付，请重新选择";
                bizDataJson.put("result", Boolean.valueOf(true));
                bizDataJson.put("showMsg", showMsg);
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

    public JSONObject renderMultipleGoodsOrder(JSONObject reqJson, HttpServletRequest request) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "服务器内部错误！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String userId = reqJson.getString("userId");
            String mzUserId = CommonUtil.getMzUserId(userId);
            JSONArray goodsList = reqJson.getJSONArray("goodsList");
            BigDecimal goodsPrice = BigDecimal.ZERO;
            BigDecimal shipPrice = BigDecimal.ZERO;
            List<Map<String, Object>> goodsTransList = new ArrayList<>();
            BigDecimal totalWeight = BigDecimal.ZERO;
            boolean integralOn = false;
            JSONArray goodsArray = new JSONArray();
            for (int i = 0; i < goodsList.size(); i++) {
                JSONObject goodsOne = goodsList.getJSONObject(i);
                String goodsId = goodsOne.getString("goodsId");
                int goodsCount = goodsOne.getInteger("goodsCount").intValue();
                int cartType = goodsOne.getInteger("cartType").intValue();
                JSONObject goodsObj = new JSONObject();
                if (cartType == Const.SHOPPING_CUL_CART_TYPE.intValue() || cartType == Const.SHOPPING_INT_CART_TYPE.intValue()) {
                    ShoppingGoods shoppingGoods = new ShoppingGoods();
                    shoppingGoods.setId(goodsId);
                    shoppingGoods = this.shoppingGoodsDao.queryDetail(shoppingGoods);
                    BigDecimal currentPrice = BigDecimal.ZERO;
                    String specInfo = "";
                    if (null != goodsOne.get("propertys") && !"".equals(goodsOne.get("propertys"))) {
                        ShoppingGoodsInventory shoppingGoodsInventory = new ShoppingGoodsInventory();
                        shoppingGoodsInventory.setGoodsId(shoppingGoods.getId());
                        shoppingGoodsInventory.setPropertys(goodsOne.getString("propertys"));
                        shoppingGoodsInventory = this.shoppingGoodsInventoryDao.queryDetail(shoppingGoodsInventory);
                        currentPrice = shoppingGoodsInventory.getPrice();
                        String[] strs = goodsOne.getString("propertys").split("_");
                        for (int m = 0; m < strs.length; m++) {
                            ShoppingGoodsspecproperty shoppingGoodsspecproperty = new ShoppingGoodsspecproperty();
                            shoppingGoodsspecproperty.setId(strs[m]);
                            shoppingGoodsspecproperty = this.shoppingGoodsspecpropertyDao.queryDetail(shoppingGoodsspecproperty);
                            specInfo = specInfo + shoppingGoodsspecproperty.getValue() + ";";
                        }
                    } else {
                        currentPrice = shoppingGoods.getStorePrice();
                    }
                    goodsObj.put("goodsId", goodsId);
                    goodsObj.put("cartType", Integer.valueOf(cartType));
                    goodsObj.put("goodsName", shoppingGoods.getGoodsName());
                    goodsObj.put("currentPrice", currentPrice);
                    goodsObj.put("goodsCount", Integer.valueOf(goodsCount));
                    goodsObj.put("photoId", shoppingGoods.getGoodsMainPhotoId());
                    if (null != goodsOne.get("propertys")) {
                        goodsObj.put("propertys", goodsOne.get("propertys"));
                        goodsObj.put("specInfo", specInfo);
                    }
                    int useIntegralSet = shoppingGoods.getUseIntegralSet().intValue();
                    int useIntegralValue = shoppingGoods.getUseIntegralValue().intValue();
                    int useBalanceSet = shoppingGoods.getUseBalanceSet().intValue();
                    int useMembershipSet = shoppingGoods.getUseMembershipSet().intValue();
                    goodsObj.put("useIntegralSet", Integer.valueOf(useIntegralSet));
                    goodsObj.put("useIntegralValue", Integer.valueOf(useIntegralValue));
                    goodsObj.put("useBalanceSet", Integer.valueOf(useBalanceSet));
                    goodsObj.put("useMembershipSet", Integer.valueOf(useMembershipSet));
                    goodsObj.put("goodsTransfee", shoppingGoods.getGoodsTransfee());
                    goodsObj.put("selfextractionSet", shoppingGoods.getSelfextractionSet());
                    goodsObj.put("selfextractionAddress", shoppingGoods.getSelfextractionAddress());
                    goodsPrice = goodsPrice.add(currentPrice.multiply(new BigDecimal(goodsCount)).setScale(2, 4));
                    if (shoppingGoods.getGoodsTransfee().intValue() == 0)
                        if (null == goodsOne.get("transport") || "".equals(goodsOne.get("transport")) || goodsOne.get("transport").equals("快递"))
                            if (null != shoppingGoods.getTransportId() && (shoppingGoods.getExpressTransFee() == null || shoppingGoods.getExpressTransFee().compareTo(BigDecimal.ZERO) == 0)) {
                                BigDecimal goodsWeights = shoppingGoods.getGoodsWeight().multiply(new BigDecimal(goodsCount));
                                Map<String, Object> objMap = new HashMap<>();
                                objMap.put("goodsId", goodsId);
                                objMap.put("goodsCount", Integer.valueOf(goodsCount));
                                goodsTransList.add(objMap);
                                totalWeight = totalWeight.add(goodsWeights);
                            } else {
                                shipPrice = shipPrice.add((shoppingGoods.getExpressTransFee() == null) ? BigDecimal.ZERO : shoppingGoods.getExpressTransFee());
                            }
                } else if (cartType == Const.SHOPPING_ACT_CART_TYPE.intValue()) {
                    ShoppingArtactivity shoppingArtactivity = new ShoppingArtactivity();
                    shoppingArtactivity.setId(goodsId);
                    shoppingArtactivity = this.shoppingArtactivityDao.queryDetail(shoppingArtactivity);
                    BigDecimal currentPrice = BigDecimal.ZERO;
                    String specInfo = "";
                    if (null != goodsOne.get("propertys") && !"".equals(goodsOne.get("propertys"))) {
                        ShoppingArtactivityInventory shoppingArtactivityInventory = new ShoppingArtactivityInventory();
                        shoppingArtactivityInventory.setActivityId(shoppingArtactivity.getId());
                        shoppingArtactivityInventory.setPropertys(goodsOne.getString("propertys"));
                        shoppingArtactivityInventory = this.shoppingArtactivityInventoryDao.queryDetail(shoppingArtactivityInventory);
                        currentPrice = shoppingArtactivityInventory.getPrice();
                        String[] strs = goodsOne.getString("propertys").split("_");
                        for (int m = 0; m < strs.length; m++) {
                            ShoppingGoodsspecproperty shoppingGoodsspecproperty = new ShoppingGoodsspecproperty();
                            shoppingGoodsspecproperty.setId(strs[m]);
                            shoppingGoodsspecproperty = this.shoppingGoodsspecpropertyDao.queryDetail(shoppingGoodsspecproperty);
                            specInfo = specInfo + shoppingGoodsspecproperty.getValue() + ";";
                        }
                    } else {
                        currentPrice = shoppingArtactivity.getCurrentPrice();
                    }
                    goodsObj.put("goodsId", goodsId);
                    goodsObj.put("cartType", Integer.valueOf(cartType));
                    goodsObj.put("goodsName", shoppingArtactivity.getActivityName());
                    goodsObj.put("currentPrice", currentPrice);
                    goodsObj.put("goodsCount", Integer.valueOf(goodsCount));
                    goodsObj.put("photoId", shoppingArtactivity.getMainPhotoId());
                    if (null != goodsOne.get("propertys")) {
                        goodsObj.put("propertys", goodsOne.get("propertys"));
                        goodsObj.put("specInfo", specInfo);
                    }
                    int useIntegralSet = shoppingArtactivity.getUseIntegralSet().intValue();
                    int useIntegralValue = shoppingArtactivity.getUseIntegralValue().intValue();
                    int useBalanceSet = shoppingArtactivity.getUseBalanceSet().intValue();
                    int useMembershipSet = shoppingArtactivity.getUseMembershipSet().intValue();
                    goodsObj.put("useIntegralSet", Integer.valueOf(useIntegralSet));
                    goodsObj.put("useIntegralValue", Integer.valueOf(useIntegralValue));
                    goodsObj.put("useBalanceSet", Integer.valueOf(useBalanceSet));
                    goodsObj.put("useMembershipSet", Integer.valueOf(useMembershipSet));
                    HashMap<String, Object> reqMap = new HashMap<>();
                    reqMap.put("activityId", goodsId);
                    List<ShoppingArtinfos> infos = this.shoppingArtinfosDao.queryActivityInfoList(reqMap);
                    goodsObj.put("infos", infos);
                    goodsPrice = goodsPrice.add(currentPrice.multiply(new BigDecimal(goodsCount)).setScale(2, 4));
                } else if (cartType == Const.SHOPPING_PLAN_CART_TYPE.intValue()) {
                    ShoppingArtplan shoppingArtplan = new ShoppingArtplan();
                    shoppingArtplan.setId(goodsId);
                    shoppingArtplan = this.shoppingArtplanDao.queryDetail(shoppingArtplan);
                    BigDecimal currentPrice = BigDecimal.ZERO;
                    String specInfo = "";
                    if (null != goodsOne.get("propertys") && !"".equals(goodsOne.get("propertys"))) {
                        ShoppingArtplanInventory shoppingArtplanInventory = new ShoppingArtplanInventory();
                        shoppingArtplanInventory.setActivityId(shoppingArtplan.getId());
                        shoppingArtplanInventory.setPropertys(goodsOne.getString("propertys"));
                        shoppingArtplanInventory = this.shoppingArtplanInventoryDao.queryDetail(shoppingArtplanInventory);
                        currentPrice = shoppingArtplanInventory.getPrice();
                        String[] strs = goodsOne.getString("propertys").split("_");
                        for (int m = 0; m < strs.length; m++) {
                            ShoppingGoodsspecproperty shoppingGoodsspecproperty = new ShoppingGoodsspecproperty();
                            shoppingGoodsspecproperty.setId(strs[m]);
                            shoppingGoodsspecproperty = this.shoppingGoodsspecpropertyDao.queryDetail(shoppingGoodsspecproperty);
                            specInfo = specInfo + shoppingGoodsspecproperty.getValue() + ";";
                        }
                    } else {
                        currentPrice = shoppingArtplan.getCurrentPrice();
                    }
                    goodsObj.put("goodsId", goodsId);
                    goodsObj.put("cartType", Integer.valueOf(cartType));
                    goodsObj.put("goodsName", shoppingArtplan.getActivityName());
                    goodsObj.put("currentPrice", currentPrice);
                    goodsObj.put("goodsCount", Integer.valueOf(goodsCount));
                    goodsObj.put("photoId", shoppingArtplan.getMainPhotoId());
                    if (null != goodsOne.get("propertys")) {
                        goodsObj.put("propertys", goodsOne.get("propertys"));
                        goodsObj.put("specInfo", specInfo);
                    }
                    int useIntegralSet = shoppingArtplan.getUseIntegralSet().intValue();
                    int useIntegralValue = shoppingArtplan.getUseIntegralValue().intValue();
                    int useBalanceSet = shoppingArtplan.getUseBalanceSet().intValue();
                    int useMembershipSet = shoppingArtplan.getUseMembershipSet().intValue();
                    goodsObj.put("useIntegralSet", Integer.valueOf(useIntegralSet));
                    goodsObj.put("useIntegralValue", Integer.valueOf(useIntegralValue));
                    goodsObj.put("useBalanceSet", Integer.valueOf(useBalanceSet));
                    goodsObj.put("useMembershipSet", Integer.valueOf(useMembershipSet));
                    HashMap<String, Object> reqMap = new HashMap<>();
                    reqMap.put("activityId", goodsId);
                    List<ShoppingArtinfos> infos = this.shoppingArtinfosDao.queryPlanInfoList(reqMap);
                    goodsObj.put("infos", infos);
                    goodsPrice = goodsPrice.add(currentPrice.multiply(new BigDecimal(goodsCount)).setScale(2, 4));
                } else if (cartType == Const.SHOPPING_CLASS_CART_TYPE.intValue()) {
                    ShoppingArtclass shoppingArtclass = new ShoppingArtclass();
                    shoppingArtclass.setId(goodsId);
                    shoppingArtclass = this.shoppingArtclassDao.queryDetail(shoppingArtclass);
                    BigDecimal currentPrice = shoppingArtclass.getCurrentPrice();
                    goodsObj.put("goodsId", goodsId);
                    goodsObj.put("cartType", Integer.valueOf(cartType));
                    goodsObj.put("goodsName", shoppingArtclass.getClassName());
                    goodsObj.put("currentPrice", currentPrice);
                    goodsObj.put("goodsCount", Integer.valueOf(goodsCount));
                    goodsObj.put("photoId", shoppingArtclass.getMainPhotoId());
                    int useIntegralSet = shoppingArtclass.getUseIntegralSet().intValue();
                    int useIntegralValue = shoppingArtclass.getUseIntegralValue().intValue();
                    int useBalanceSet = shoppingArtclass.getUseBalanceSet().intValue();
                    int useMembershipSet = shoppingArtclass.getUseMembershipSet().intValue();
                    goodsObj.put("useIntegralSet", Integer.valueOf(useIntegralSet));
                    goodsObj.put("useIntegralValue", Integer.valueOf(useIntegralValue));
                    goodsObj.put("useBalanceSet", Integer.valueOf(useBalanceSet));
                    goodsObj.put("useMembershipSet", Integer.valueOf(useMembershipSet));
                    HashMap<String, Object> reqMap = new HashMap<>();
                    reqMap.put("classId", goodsId);
                    List<ShoppingArtinfos> infos = this.shoppingArtinfosDao.queryClassInfoList(reqMap);
                    goodsObj.put("infos", infos);
                    goodsPrice = goodsPrice.add(currentPrice.multiply(new BigDecimal(goodsCount)).setScale(2, 4));
                }
                goodsArray.add(goodsObj);
            }
            JSONObject addObj = null;
            if (null == reqJson.get("addressId") || "".equals(reqJson.get("addressId"))) {
                JSONObject addressList = MZService.getUserAddress(mzUserId, 100, 1);
                if (null != addressList) {
                    JSONObject data_list = addressList.getJSONObject("data_list");
                    if (null != data_list.get("user_address_detail_v_o")) {
                        JSONArray addressArray = data_list.getJSONArray("user_address_detail_v_o");
                        for (int j = 0; j < addressArray.size(); j++) {
                            JSONObject addressObj = addressArray.getJSONObject(j);
                            addObj = addressObj;
                            if (addressObj.getBoolean("default_address_boolean").booleanValue())
                                break;
                        }
                    }
                }
            } else {
                String address_id = reqJson.getString("addressId");
                JSONObject addressObj = MZService.getAddressDetail(mzUserId, address_id);
                addObj = addressObj;
            }
            bizDataJson.put("address", addObj);
            if (goodsTransList.size() > 0 && addObj == null) {
                retCode = "1";
                retMsg = "请先至”设置-收货地址“添加个人收货地址！";
            } else {
                if (goodsTransList.size() > 0)
                    shipPrice = shipPrice.add(buildShipPrice(goodsTransList, addObj));
                bizDataJson.put("shipPrice", shipPrice);
                boolean accountState = true;
                JSONObject accountObj = MZService.getAssetinfo(mzUserId);
                if (null != accountObj) {
                    int account_point = (accountObj.get("account_point") == null) ? 0 : accountObj.getInteger("account_point").intValue();
                    int account_money_fen = (accountObj.get("account_money_fen") == null) ? 0 : accountObj.getInteger("account_money_fen").intValue();
                    BigDecimal accountMoney = (new BigDecimal(account_money_fen)).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
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
                if (!accountState) {
                    retCode = "-1";
                    retMsg = "无法获取您的账户积分和余额数据，请稍后再试！";
                } else {
                    BigDecimal totalPrice = goodsPrice.add(shipPrice);
                    BigDecimal payPrice = goodsPrice;
                    BigDecimal totalCashPrice = BigDecimal.ZERO;
                    int fixedIntegalValue = 0;
                    for (int j = 0; j < goodsArray.size(); j++) {
                        JSONObject goodsObj = goodsArray.getJSONObject(j);
                        BigDecimal currentPrice = goodsObj.getBigDecimal("currentPrice");
                        int goodsCount = goodsObj.getInteger("goodsCount").intValue();
                        goodsObj.put("perGoodsPrice", currentPrice.multiply(new BigDecimal(goodsCount)));
                        if (goodsObj.getInteger("useIntegralSet").intValue() == 1) {
                            integralOn = true;
                            int useIntegralValue = goodsObj.getInteger("useIntegralValue").intValue();
                            if (currentPrice.multiply(new BigDecimal(100)).intValue() < useIntegralValue)
                                useIntegralValue = currentPrice.multiply(new BigDecimal(100)).intValue();
                            goodsObj.put("useIntegralValue", Integer.valueOf(useIntegralValue));
                            fixedIntegalValue += useIntegralValue * goodsCount;
                            goodsObj.put("perIntegralValue", Integer.valueOf(useIntegralValue * goodsCount));
                            BigDecimal cashPrice = currentPrice.multiply(new BigDecimal(goodsCount)).subtract((new BigDecimal(useIntegralValue * goodsCount)).divide(new BigDecimal(this.moneyToIntegralScale), 2, RoundingMode.HALF_UP));
                            goodsObj.put("perPayPrice", cashPrice);
                            totalCashPrice = totalCashPrice.add(cashPrice);
                        } else {
                            BigDecimal cashPrice = currentPrice.multiply(new BigDecimal(goodsCount));
                            goodsObj.put("perPayPrice", cashPrice);
                            totalCashPrice = totalCashPrice.add(cashPrice);
                        }
                    }
                    List<String> couponIds = new ArrayList<>();
                    JSONArray couponArray = CommonUtil.getCartsCouppon(userId, goodsArray);
                    for (int k = 0; k < couponArray.size(); k++) {
                        JSONObject obj = couponArray.getJSONObject(k);
                        couponIds.add(obj.getString("id"));
                    }
                    bizDataJson.put("couponList", couponArray);
                    BigDecimal originPrice = payPrice;
                    int originntegalValue = fixedIntegalValue;
                    BigDecimal couponCut = BigDecimal.ZERO;
                    BigDecimal accountCut = BigDecimal.ZERO;
                    BigDecimal integralCut = BigDecimal.ZERO;
                    BigDecimal balanceCut = BigDecimal.ZERO;
                    if (null != reqJson.get("couponId") && !"".equals(reqJson.get("couponId"))) {
                        String couponId = reqJson.getString("couponId");
                        if (couponIds.contains(couponId)) {
                            JSONObject couponDtl = CRMService.getUserCouponDtl(couponId);
                            String right_No = couponDtl.getString("right_No");
                            ShoppingCoupon shoppingCoupon = new ShoppingCoupon();
                            shoppingCoupon.setRight_No(right_No);
                            shoppingCoupon = this.shoppingCouponDao.queryDetail(shoppingCoupon);
                            HashSet<String> goodsSet = new HashSet<>();
                            for (int n = 0; n < couponArray.size(); n++) {
                                JSONObject obj = couponArray.getJSONObject(n);
                                if (shoppingCoupon.getRight_No().equals(obj.getString("right_No"))) {
                                    goodsSet = (HashSet<String>)obj.get("goodsSet");
                                    break;
                                }
                            }
                            if (shoppingCoupon.getRight_Type().equals("coincp")) {
                                BigDecimal couponPrice = payPrice;
                                BigDecimal couponAmount = new BigDecimal(Integer.valueOf(shoppingCoupon.getRight_Content()).intValue());
                                if (couponPrice.compareTo(couponAmount) < 0)
                                    couponAmount = couponPrice;
                                payPrice = payPrice.subtract(couponAmount);
                                if (totalCashPrice.compareTo(BigDecimal.ZERO) == 1) {
                                    BigDecimal totalPerCouponCut = BigDecimal.ZERO;
                                    for (int i1 = 0; i1 < goodsArray.size(); i1++) {
                                        JSONObject goodsObj = goodsArray.getJSONObject(i1);
                                        BigDecimal perPayPrice = goodsObj.getBigDecimal("perPayPrice");
                                        BigDecimal perCouponCut = BigDecimal.ZERO;
                                        if (i1 != goodsArray.size() - 1) {
                                            perCouponCut = couponAmount.multiply(perPayPrice).divide(totalCashPrice, 2, RoundingMode.HALF_UP);
                                            totalPerCouponCut = totalPerCouponCut.add(perCouponCut);
                                        } else {
                                            perCouponCut = couponAmount.subtract(totalPerCouponCut);
                                        }
                                        if (goodsSet.contains(goodsObj.getString("cartType") + "&" + goodsObj.getString("goodsId"))) {
                                            goodsObj.put("perPayPrice", perPayPrice.subtract(perCouponCut));
                                            goodsObj.put("perCouponCut", perCouponCut);
                                        } else {
                                            goodsObj.put("perCouponCut", BigDecimal.ZERO);
                                        }
                                    }
                                    couponCut = couponAmount;
                                }
                            } else {
                                int i1 = 0;
                                BigDecimal bigDecimal = new BigDecimal(shoppingCoupon.getRight_Content());
                                for (int i2 = 0; i2 < goodsArray.size(); i2++) {
                                    JSONObject goodsObj = goodsArray.getJSONObject(i2);
                                    if (goodsSet.contains(goodsObj.getString("cartType") + "&" + goodsObj.getString("goodsId"))) {
                                        BigDecimal perPayPrice = goodsObj.getBigDecimal("perPayPrice");
                                        BigDecimal cprice = perPayPrice.multiply(bigDecimal).setScale(2, 4);
                                        goodsObj.put("perPayPrice", cprice);
                                        BigDecimal perCouponCut = perPayPrice.subtract(cprice);
                                        if (goodsObj.getInteger("useIntegralSet").intValue() == 1) {
                                            int perIntegralValue = goodsObj.getInteger("perIntegralValue").intValue();
                                            int cIntegralValue = (new BigDecimal(perIntegralValue)).multiply(bigDecimal).intValue();
                                            goodsObj.put("perIntegralValue", Integer.valueOf(cIntegralValue));
                                            perCouponCut = perCouponCut.add((new BigDecimal(perIntegralValue - cIntegralValue)).divide(new BigDecimal(this.moneyToIntegralScale), 2, RoundingMode.HALF_UP));
                                            i1 += perIntegralValue - cIntegralValue;
                                        }
                                        goodsObj.put("perCouponCut", perCouponCut);
                                        couponCut = couponCut.add(perCouponCut);
                                    } else {
                                        goodsObj.put("perCouponCut", BigDecimal.ZERO);
                                    }
                                }
                                payPrice = originPrice.subtract(couponCut);
                                fixedIntegalValue = originntegalValue - i1;
                            }
                            originPrice = payPrice;
                            originntegalValue = fixedIntegalValue;
                        }
                    }
                    BigDecimal discount = CommonUtil.getUserMemberShip(userId);
                    int cutIntegral = 0;
                    List<String> ids = new ArrayList<>();
                    int m;
                    for (m = 0; m < goodsArray.size(); m++) {
                        JSONObject goodsObj = goodsArray.getJSONObject(m);
                        if (goodsObj.getInteger("useMembershipSet").intValue() == 1)
                            ids.add(goodsObj.getString("goodsId"));
                    }
                    for (m = 0; m < goodsArray.size(); m++) {
                        JSONObject goodsObj = goodsArray.getJSONObject(m);
                        if (ids.contains(goodsObj.getString("goodsId"))) {
                            BigDecimal perPayPrice = goodsObj.getBigDecimal("perPayPrice");
                            BigDecimal cprice = perPayPrice.multiply(discount).setScale(2, 4);
                            goodsObj.put("perPayPrice", cprice);
                            BigDecimal perAccountCut = perPayPrice.subtract(cprice);
                            if (goodsObj.getInteger("useIntegralSet").intValue() == 1) {
                                int perIntegralValue = goodsObj.getInteger("perIntegralValue").intValue();
                                int cIntegralValue = (new BigDecimal(perIntegralValue)).multiply(discount).intValue();
                                goodsObj.put("perIntegralValue", Integer.valueOf(cIntegralValue));
                                perAccountCut = perAccountCut.add((new BigDecimal(perIntegralValue - cIntegralValue)).divide(new BigDecimal(this.moneyToIntegralScale), 2, RoundingMode.HALF_UP));
                                cutIntegral += perIntegralValue - cIntegralValue;
                            }
                            goodsObj.put("perAccountCut", perAccountCut);
                            accountCut = accountCut.add(perAccountCut);
                        } else {
                            goodsObj.put("perAccountCut", BigDecimal.ZERO);
                        }
                    }
                    payPrice = originPrice.subtract(accountCut);
                    fixedIntegalValue = originntegalValue - cutIntegral;
                    originPrice = payPrice;
                    originntegalValue = fixedIntegalValue;
                    ShoppingPayLimit payLimit = CommonUtil.getPayLimit();
                    bizDataJson.put("pointPayLimit", Integer.valueOf(payLimit.getPointPay()));
                    bizDataJson.put("balancePayLimit", Integer.valueOf(payLimit.getBalancePay()));
                    int accountPoint = (bizDataJson.get("accountPoint") == null) ? 0 : bizDataJson.getInteger("accountPoint").intValue();
                    int pointPay = payLimit.getPointPay();
                    int minIntegralValue = originntegalValue;
                    if (minIntegralValue > accountPoint) {
                        retCode = "1";
                        retMsg = "当前账户积分不够，无法下单！";
                    } else if (minIntegralValue > pointPay) {
                        retCode = "1";
                        retMsg = "积分单次支付限额" + pointPay + ",当前订单已超出该额度，无法下单！";
                    } else {
                        int maxIntegralValue = originntegalValue;
                        BigDecimal tPrice = BigDecimal.ZERO;
                        int tIntegral = 0;
                        if (null != reqJson.get("useIntegral") && reqJson.getInteger("useIntegral").intValue() == 1)
                            for (int i2 = 0; i2 < goodsArray.size(); i2++) {
                                JSONObject goodsObj = goodsArray.getJSONObject(i2);
                                int useIntegralSet = goodsObj.getInteger("useIntegralSet").intValue();
                                BigDecimal perPayPrice = goodsObj.getBigDecimal("perPayPrice");
                                if (useIntegralSet == 2) {
                                    tPrice = tPrice.add(perPayPrice);
                                    int perPayToInt = perPayPrice.multiply(new BigDecimal(this.moneyToIntegralScale)).intValue();
                                    int goodsCount = goodsObj.getInteger("goodsCount").intValue();
                                    int useIntegralValue = goodsObj.getInteger("useIntegralValue").intValue();
                                    int perMaxIntegralValue = (useIntegralValue * goodsCount < perPayToInt) ? (useIntegralValue * goodsCount) : perPayToInt;
                                    tIntegral += perMaxIntegralValue;
                                    goodsObj.put("perMaxIntegralValue", Integer.valueOf(perMaxIntegralValue));
                                    maxIntegralValue += perMaxIntegralValue;
                                }
                            }
                        int pointLimit = accountPoint;
                        if (pointPay > 0)
                            pointLimit = (pointPay > pointLimit) ? pointLimit : pointPay;
                        if (maxIntegralValue > pointLimit)
                            maxIntegralValue = pointLimit;
                        int iNum = 0;
                        for (int n = 0; n < goodsArray.size(); n++) {
                            JSONObject goodsObj = goodsArray.getJSONObject(n);
                            int useIntegralSet = goodsObj.getInteger("useIntegralSet").intValue();
                            if (useIntegralSet == 2)
                                iNum++;
                        }
                        int iCount = 0;
                        int totalPerIntegralCutValue = 0;
                        for (int i1 = 0; i1 < goodsArray.size(); i1++) {
                            JSONObject goodsObj = goodsArray.getJSONObject(i1);
                            int useIntegralSet = goodsObj.getInteger("useIntegralSet").intValue();
                            BigDecimal perPayPrice = goodsObj.getBigDecimal("perPayPrice");
                            if (useIntegralSet == 2) {
                                iCount++;
                                if (tPrice.compareTo(BigDecimal.ZERO) == 1)
                                    if (iCount != iNum) {
                                        int perMaxIntegralValue = goodsObj.getInteger("perMaxIntegralValue").intValue();
                                        int perIntegralCutValue = (new BigDecimal(maxIntegralValue - minIntegralValue)).multiply(new BigDecimal(perMaxIntegralValue)).divide(new BigDecimal(tIntegral), 2, RoundingMode.HALF_UP).intValue();
                                        totalPerIntegralCutValue += perIntegralCutValue;
                                        goodsObj.put("perIntegralCutValue", Integer.valueOf(perIntegralCutValue));
                                        BigDecimal perIntegralCut = (new BigDecimal(perIntegralCutValue)).divide(new BigDecimal(this.moneyToIntegralScale), 2, RoundingMode.HALF_UP);
                                        goodsObj.put("perIntegralCut", perIntegralCut);
                                        goodsObj.put("perPayPrice", perPayPrice.subtract(perIntegralCut));
                                    } else {
                                        int perIntegralCutValue = maxIntegralValue - minIntegralValue - totalPerIntegralCutValue;
                                        goodsObj.put("perIntegralCutValue", Integer.valueOf(perIntegralCutValue));
                                        BigDecimal perIntegralCut = (new BigDecimal(perIntegralCutValue)).divide(new BigDecimal(this.moneyToIntegralScale), 2, RoundingMode.HALF_UP);
                                        goodsObj.put("perIntegralCut", perIntegralCut);
                                        goodsObj.put("perPayPrice", perPayPrice.subtract(perIntegralCut));
                                    }
                            } else if (useIntegralSet == 1) {
                                int perIntegralValue = goodsObj.getInteger("perIntegralValue").intValue();
                                goodsObj.put("perIntegralCutValue", Integer.valueOf(perIntegralValue));
                                BigDecimal perIntegralCut = (new BigDecimal(perIntegralValue)).divide(new BigDecimal(this.moneyToIntegralScale), 2, RoundingMode.HALF_UP);
                                goodsObj.put("perIntegralCut", perIntegralCut);
                            } else {
                                goodsObj.put("perIntegralCutValue", Integer.valueOf(0));
                                goodsObj.put("perIntegralCut", BigDecimal.ZERO);
                            }
                        }
                        bizDataJson.put("minIntegralValue", Integer.valueOf(minIntegralValue));
                        bizDataJson.put("maxIntegralValue", Integer.valueOf(maxIntegralValue));
                        BigDecimal integralAmount = (new BigDecimal(maxIntegralValue)).divide(new BigDecimal(this.moneyToIntegralScale), 2, RoundingMode.HALF_UP).setScale(2, 4);
                        integralCut = integralAmount;
                        payPrice = originPrice.subtract(integralCut);
                        originPrice = payPrice;
                        tPrice = BigDecimal.ZERO;
                        if (null != reqJson.get("useBalance") && reqJson.getInteger("useBalance").intValue() == 1) {
                            BigDecimal deductionBalance = BigDecimal.ZERO;
                            BigDecimal accountMoney = bizDataJson.getBigDecimal("accountMoney");
                            for (int i2 = 0; i2 < goodsArray.size(); i2++) {
                                JSONObject goodsObj = goodsArray.getJSONObject(i2);
                                int useBalanceSet = goodsObj.getInteger("useBalanceSet").intValue();
                                BigDecimal perPayPrice = goodsObj.getBigDecimal("perPayPrice");
                                if (useBalanceSet == 1) {
                                    tPrice = tPrice.add(perPayPrice);
                                    deductionBalance = deductionBalance.add(perPayPrice);
                                }
                            }
                            BigDecimal accountLimit = bizDataJson.getBigDecimal("accountMoney");
                            BigDecimal balancePay = new BigDecimal(payLimit.getBalancePay());
                            if (balancePay.compareTo(BigDecimal.ZERO) > 0 && balancePay.compareTo(accountLimit) < 0)
                                accountLimit = balancePay;
                            if (payPrice.compareTo(accountLimit) == 1)
                                deductionBalance = accountLimit;
                            for (int i3 = 0; i3 < goodsArray.size(); i3++) {
                                JSONObject goodsObj = goodsArray.getJSONObject(i3);
                                int useBalanceSet = goodsObj.getInteger("useBalanceSet").intValue();
                                BigDecimal perPayPrice = goodsObj.getBigDecimal("perPayPrice");
                                if (useBalanceSet == 1) {
                                    if (tPrice.compareTo(BigDecimal.ZERO) == 1) {
                                        BigDecimal cent = perPayPrice.divide(tPrice, 2, RoundingMode.HALF_UP);
                                        BigDecimal perBalanceCut = deductionBalance.multiply(cent);
                                        goodsObj.put("perBalanceCut", perBalanceCut);
                                        goodsObj.put("perPayPrice", perPayPrice.subtract(perBalanceCut));
                                    }
                                } else {
                                    goodsObj.put("perBalanceCut", BigDecimal.ZERO);
                                }
                            }
                            payPrice = payPrice.subtract(deductionBalance);
                            balanceCut = deductionBalance;
                            if (balanceCut.compareTo(accountLimit) == -1) {
                                if (shipPrice.compareTo(accountLimit.subtract(balanceCut)) == -1) {
                                    balanceCut = balanceCut.add(shipPrice);
                                } else {
                                    balanceCut = accountLimit;
                                    payPrice = payPrice.add(shipPrice.subtract(accountLimit.subtract(balanceCut)));
                                }
                            } else {
                                payPrice = payPrice.add(shipPrice);
                            }
                        } else {
                            payPrice = payPrice.add(shipPrice);
                        }
                        bizDataJson.put("goodsInfoList", goodsArray);
                        bizDataJson.put("goodsPrice", goodsPrice);
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
            }
        } catch (Exception e) {
            log.error(e);
            e.printStackTrace();
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    public JSONObject addMultipleOrder(JSONObject reqJson, HttpServletRequest request) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "服务器内部错误！";
        JSONObject bizDataJson = new JSONObject();
        try {
            System.out.println(reqJson);
            String userId = reqJson.getString("userId");
            String mzUserId = CommonUtil.getMzUserId(userId);
            BigDecimal orderTotalPrice = reqJson.getBigDecimal("orderTotalPrice");
            BigDecimal orderShipPrice = reqJson.getBigDecimal("orderShipPrice");
            BigDecimal orderPayPrice = reqJson.getBigDecimal("orderPayPrice");
            int orderUseIntegralValue = (reqJson.get("orderUseIntegralValue") != null) ? reqJson.getInteger("orderUseIntegralValue").intValue() : 0;
            BigDecimal orderDeductionBalancePrice = (reqJson.get("orderDeductionBalancePrice") != null) ? reqJson.getBigDecimal("orderDeductionBalancePrice") : BigDecimal.ZERO;
            JSONArray goodsList = reqJson.getJSONArray("goodsList");
            BigDecimal goodsPrice = BigDecimal.ZERO;
            BigDecimal shipPrice = BigDecimal.ZERO;
            List<Map<String, Object>> goodsTransList = new ArrayList<>();
            Map<String, BigDecimal> weightMap = new HashMap<>();
            BigDecimal totalWeight = BigDecimal.ZERO;
            boolean cartState = false;
            boolean unitPriceCorrect = true;
            boolean inventoryCorrect = true;
            boolean overLimitBuy = false;
            boolean overSignUpTime = false;
            String inventoryName = "";
            JSONArray goodsArray = new JSONArray();
            for (int i = 0; i < goodsList.size(); i++) {
                JSONObject goodsOne = goodsList.getJSONObject(i);
                String goodsId = goodsOne.getString("goodsId");
                int goodsCount = goodsOne.getInteger("goodsCount").intValue();
                int cartType = goodsOne.getInteger("cartType").intValue();
                String cartId = goodsOne.getString("cartId");
                BigDecimal unitPrice = goodsOne.getBigDecimal("unitPrice");
                ShoppingGoodscart goodscart = new ShoppingGoodscart();
                goodscart.setId(goodsOne.getString("cartId"));
                goodscart = this.shoppingGoodscartDao.queryDetail(goodscart);
                if (StringUtil.isNotNull(goodscart.getOfId())) {
                    cartState = true;
                    break;
                }
                JSONObject goodsObj = new JSONObject();
                goodsObj.put("cartId", cartId);
                goodsObj.put("cartType", Integer.valueOf(cartType));
                goodsObj.put("signupInfos", goodsOne.get("signupInfos"));
                if (cartType == Const.SHOPPING_CUL_CART_TYPE.intValue() || cartType == Const.SHOPPING_INT_CART_TYPE.intValue()) {
                    ShoppingGoods shoppingGoods = new ShoppingGoods();
                    shoppingGoods.setId(goodsId);
                    shoppingGoods = this.shoppingGoodsDao.queryDetail(shoppingGoods);
                    BigDecimal currentPrice = BigDecimal.ZERO;
                    String specInfo = "";
                    if (null != goodsOne.get("propertys") && !"".equals(goodsOne.get("propertys"))) {
                        ShoppingGoodsInventory shoppingGoodsInventory = new ShoppingGoodsInventory();
                        shoppingGoodsInventory.setGoodsId(shoppingGoods.getId());
                        shoppingGoodsInventory.setPropertys(goodsOne.getString("propertys"));
                        shoppingGoodsInventory = this.shoppingGoodsInventoryDao.queryDetail(shoppingGoodsInventory);
                        int inventoryCount = shoppingGoodsInventory.getCount().intValue();
                        goodsObj.put("inventoryCount", Integer.valueOf(inventoryCount));
                        currentPrice = shoppingGoodsInventory.getPrice();
                        String[] strs = goodsOne.getString("propertys").split("_");
                        for (int m = 0; m < strs.length; m++) {
                            ShoppingGoodsspecproperty shoppingGoodsspecproperty = new ShoppingGoodsspecproperty();
                            shoppingGoodsspecproperty.setId(strs[m]);
                            shoppingGoodsspecproperty = this.shoppingGoodsspecpropertyDao.queryDetail(shoppingGoodsspecproperty);
                            specInfo = specInfo + shoppingGoodsspecproperty.getValue() + ";";
                        }
                    } else {
                        currentPrice = shoppingGoods.getStorePrice();
                    }
                    if (unitPrice.compareTo(currentPrice) != 0) {
                        unitPriceCorrect = false;
                        break;
                    }
                    goodsObj.put("goodsId", goodsId);
                    goodsObj.put("goodsName", shoppingGoods.getGoodsName());
                    goodsObj.put("currentPrice", currentPrice);
                    goodsObj.put("goodsCount", Integer.valueOf(goodsCount));
                    goodsObj.put("photoId", shoppingGoods.getGoodsMainPhotoId());
                    goodsObj.put("writeOffCount", shoppingGoods.getWriteOffCount());
                    if (null != goodsOne.get("propertys")) {
                        goodsObj.put("propertys", goodsOne.get("propertys"));
                        goodsObj.put("specInfo", specInfo);
                    }
                    int useIntegralSet = shoppingGoods.getUseIntegralSet().intValue();
                    int useIntegralValue = shoppingGoods.getUseIntegralValue().intValue();
                    int useBalanceSet = shoppingGoods.getUseBalanceSet().intValue();
                    int useMembershipSet = shoppingGoods.getUseMembershipSet().intValue();
                    goodsObj.put("useIntegralSet", Integer.valueOf(useIntegralSet));
                    goodsObj.put("useIntegralValue", Integer.valueOf(useIntegralValue));
                    goodsObj.put("useBalanceSet", Integer.valueOf(useBalanceSet));
                    goodsObj.put("useMembershipSet", Integer.valueOf(useMembershipSet));
                    goodsObj.put("goodsTransfee", shoppingGoods.getGoodsTransfee());
                    goodsObj.put("selfextractionSet", shoppingGoods.getSelfextractionSet());
                    goodsObj.put("selfextractionAddress", shoppingGoods.getSelfextractionAddress());
                    goodsPrice = goodsPrice.add(currentPrice.multiply(new BigDecimal(goodsCount)).setScale(2, 4));
                    if (null != goodsOne.get("propertys") && !"".equals(goodsOne.get("propertys"))) {
                        ShoppingGoodsInventory shoppingGoodsInventory = new ShoppingGoodsInventory();
                        shoppingGoodsInventory.setGoodsId(goodsId);
                        shoppingGoodsInventory.setPropertys(goodsOne.getString("propertys"));
                        shoppingGoodsInventory = this.shoppingGoodsInventoryDao.queryDetail(shoppingGoodsInventory);
                        if (null == shoppingGoodsInventory || shoppingGoodsInventory.getCount().intValue() < goodsCount) {
                            inventoryName = shoppingGoods.getGoodsName();
                            inventoryCorrect = false;
                            break;
                        }
                    } else if (shoppingGoods.getGoodsInventory().intValue() < goodsCount) {
                        inventoryName = shoppingGoods.getGoodsName();
                        inventoryCorrect = false;
                        break;
                    }
                    goodsObj.put("transport", goodsOne.get("transport"));
                    if (shoppingGoods.getGoodsTransfee().intValue() == 0)
                        if (null == goodsOne.get("transport") || "".equals(goodsOne.get("transport")) || goodsOne.get("transport").equals("快递"))
                            if (null != shoppingGoods.getTransportId() && (shoppingGoods.getExpressTransFee() == null || shoppingGoods.getExpressTransFee().compareTo(BigDecimal.ZERO) == 0)) {
                                goodsObj.put("transport", "快递");
                                Map<String, Object> objMap = new HashMap<>();
                                objMap.put("goodsId", goodsId);
                                objMap.put("goodsCount", Integer.valueOf(goodsCount));
                                goodsTransList.add(objMap);
                            } else {
                                shipPrice = shipPrice.add((shoppingGoods.getExpressTransFee() == null) ? BigDecimal.ZERO : shoppingGoods.getExpressTransFee());
                            }
                } else if (cartType == Const.SHOPPING_ACT_CART_TYPE.intValue()) {
                    ShoppingArtactivity shoppingArtactivity = new ShoppingArtactivity();
                    shoppingArtactivity.setId(goodsId);
                    shoppingArtactivity = this.shoppingArtactivityDao.queryDetail(shoppingArtactivity);
                    BigDecimal currentPrice = BigDecimal.ZERO;
                    String specInfo = "";
                    if (null != goodsOne.get("propertys") && !"".equals(goodsOne.get("propertys"))) {
                        ShoppingArtactivityInventory shoppingArtactivityInventory = new ShoppingArtactivityInventory();
                        shoppingArtactivityInventory.setActivityId(shoppingArtactivity.getId());
                        shoppingArtactivityInventory.setPropertys(goodsOne.getString("propertys"));
                        shoppingArtactivityInventory = this.shoppingArtactivityInventoryDao.queryDetail(shoppingArtactivityInventory);
                        int inventoryCount = shoppingArtactivityInventory.getCount().intValue();
                        goodsObj.put("inventoryCount", Integer.valueOf(inventoryCount));
                        currentPrice = shoppingArtactivityInventory.getPrice();
                        String[] strs = goodsOne.getString("propertys").split("_");
                        for (int m = 0; m < strs.length; m++) {
                            ShoppingGoodsspecproperty shoppingGoodsspecproperty = new ShoppingGoodsspecproperty();
                            shoppingGoodsspecproperty.setId(strs[m]);
                            shoppingGoodsspecproperty = this.shoppingGoodsspecpropertyDao.queryDetail(shoppingGoodsspecproperty);
                            specInfo = specInfo + shoppingGoodsspecproperty.getValue() + ";";
                        }
                    } else {
                        currentPrice = shoppingArtactivity.getCurrentPrice();
                    }
                    if (unitPrice.compareTo(currentPrice) != 0) {
                        unitPriceCorrect = false;
                        break;
                    }
                    goodsObj.put("goodsId", goodsId);
                    goodsObj.put("goodsName", shoppingArtactivity.getActivityName());
                    goodsObj.put("currentPrice", currentPrice);
                    goodsObj.put("goodsCount", Integer.valueOf(goodsCount));
                    goodsObj.put("photoId", shoppingArtactivity.getMainPhotoId());
                    if (null != goodsOne.get("propertys")) {
                        goodsObj.put("propertys", goodsOne.get("propertys"));
                        goodsObj.put("specInfo", specInfo);
                    }
                    int useIntegralSet = shoppingArtactivity.getUseIntegralSet().intValue();
                    int useIntegralValue = shoppingArtactivity.getUseIntegralValue().intValue();
                    int useBalanceSet = shoppingArtactivity.getUseBalanceSet().intValue();
                    int useMembershipSet = shoppingArtactivity.getUseMembershipSet().intValue();
                    goodsObj.put("useIntegralSet", Integer.valueOf(useIntegralSet));
                    goodsObj.put("useIntegralValue", Integer.valueOf(useIntegralValue));
                    goodsObj.put("useBalanceSet", Integer.valueOf(useBalanceSet));
                    goodsObj.put("useMembershipSet", Integer.valueOf(useMembershipSet));
                    goodsPrice = goodsPrice.add(currentPrice.multiply(new BigDecimal(goodsCount)).setScale(2, 4));
                    if (null != goodsOne.get("propertys") && !"".equals(goodsOne.get("propertys"))) {
                        ShoppingArtactivityInventory shoppingArtactivityInventory = new ShoppingArtactivityInventory();
                        shoppingArtactivityInventory.setActivityId(shoppingArtactivity.getId());
                        shoppingArtactivityInventory.setPropertys(goodsOne.getString("propertys"));
                        shoppingArtactivityInventory = this.shoppingArtactivityInventoryDao.queryDetail(shoppingArtactivityInventory);
                        if (null == shoppingArtactivityInventory || shoppingArtactivityInventory.getCount().intValue() < goodsCount) {
                            inventoryName = shoppingArtactivity.getActivityName();
                            inventoryCorrect = false;
                            break;
                        }
                    } else if (shoppingArtactivity.getSignupTotalLimit().intValue() > 0 && shoppingArtactivity.getLeftnum().intValue() < goodsCount) {
                        inventoryName = shoppingArtactivity.getActivityName();
                        inventoryCorrect = false;
                        break;
                    }


                    if (shoppingArtactivity.getSignupPerLimit().intValue() > 0) {
                        int limitBuy = shoppingArtactivity.getSignupPerLimit().intValue();
                        String scId = CommonUtil.getUserScId(userId);
                        HashMap<String, Object> reqMap = new HashMap<>();
                        reqMap.put("scId", scId);
                        reqMap.put("goodsId", shoppingArtactivity.getId());
                        reqMap.put("userId", userId);
                        reqMap.put("deleteStatus", "0");
                        List<ShoppingGoodscart> shoppingGoodscartList = this.shoppingGoodscartDao.queryBuyList(reqMap);
                        int cartCount = 0;
                        for (ShoppingGoodscart shoppingGoodscart : shoppingGoodscartList)
                            cartCount += shoppingGoodscart.getCount().intValue();
                        bizDataJson.put("doneCount", Integer.valueOf(cartCount));
                        if (cartCount + goodsCount > limitBuy){
                            overLimitBuy = true;
                            break;
                        }
                    }
                    String signupStarttime = shoppingArtactivity.getSignupStarttime();
                    String signupEndtime = shoppingArtactivity.getSignupEndtime();
                    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    if (StringUtil.compareMillisecond(signupStarttime, sf) > 0 || StringUtil.compareMillisecond(signupEndtime, sf) < 0) {
                        overSignUpTime = true;
                        break;
                    }

                } else if (cartType == Const.SHOPPING_PLAN_CART_TYPE.intValue()) {
                    ShoppingArtplan shoppingArtplan = new ShoppingArtplan();
                    shoppingArtplan.setId(goodsId);
                    shoppingArtplan = this.shoppingArtplanDao.queryDetail(shoppingArtplan);
                    BigDecimal currentPrice = BigDecimal.ZERO;
                    String specInfo = "";
                    if (null != goodsOne.get("propertys") && !"".equals(goodsOne.get("propertys"))) {
                        ShoppingArtplanInventory shoppingArtplanInventory = new ShoppingArtplanInventory();
                        shoppingArtplanInventory.setActivityId(shoppingArtplan.getId());
                        shoppingArtplanInventory.setPropertys(goodsOne.getString("propertys"));
                        shoppingArtplanInventory = this.shoppingArtplanInventoryDao.queryDetail(shoppingArtplanInventory);
                        int inventoryCount = shoppingArtplanInventory.getCount().intValue();
                        goodsObj.put("inventoryCount", Integer.valueOf(inventoryCount));
                        currentPrice = shoppingArtplanInventory.getPrice();
                        String[] strs = goodsOne.getString("propertys").split("_");
                        for (int m = 0; m < strs.length; m++) {
                            ShoppingGoodsspecproperty shoppingGoodsspecproperty = new ShoppingGoodsspecproperty();
                            shoppingGoodsspecproperty.setId(strs[m]);
                            shoppingGoodsspecproperty = this.shoppingGoodsspecpropertyDao.queryDetail(shoppingGoodsspecproperty);
                            specInfo = specInfo + shoppingGoodsspecproperty.getValue() + ";";
                        }
                    } else {
                        currentPrice = shoppingArtplan.getCurrentPrice();
                    }
                    if (unitPrice.compareTo(currentPrice) != 0) {
                        unitPriceCorrect = false;
                        break;
                    }
                    goodsObj.put("goodsId", goodsId);
                    goodsObj.put("goodsName", shoppingArtplan.getActivityName());
                    goodsObj.put("currentPrice", currentPrice);
                    goodsObj.put("goodsCount", Integer.valueOf(goodsCount));
                    goodsObj.put("photoId", shoppingArtplan.getMainPhotoId());
                    if (null != goodsOne.get("propertys")) {
                        goodsObj.put("propertys", goodsOne.get("propertys"));
                        goodsObj.put("specInfo", specInfo);
                    }
                    int useIntegralSet = shoppingArtplan.getUseIntegralSet().intValue();
                    int useIntegralValue = shoppingArtplan.getUseIntegralValue().intValue();
                    int useBalanceSet = shoppingArtplan.getUseBalanceSet().intValue();
                    int useMembershipSet = shoppingArtplan.getUseMembershipSet().intValue();
                    goodsObj.put("useIntegralSet", Integer.valueOf(useIntegralSet));
                    goodsObj.put("useIntegralValue", Integer.valueOf(useIntegralValue));
                    goodsObj.put("useBalanceSet", Integer.valueOf(useBalanceSet));
                    goodsObj.put("useMembershipSet", Integer.valueOf(useMembershipSet));
                    goodsPrice = goodsPrice.add(currentPrice.multiply(new BigDecimal(goodsCount)).setScale(2, 4));
                    if (null != goodsOne.get("propertys") && !"".equals(goodsOne.get("propertys"))) {
                        ShoppingArtplanInventory shoppingArtplanInventory = new ShoppingArtplanInventory();
                        shoppingArtplanInventory.setActivityId(shoppingArtplan.getId());
                        shoppingArtplanInventory.setPropertys(goodsOne.getString("propertys"));
                        shoppingArtplanInventory = this.shoppingArtplanInventoryDao.queryDetail(shoppingArtplanInventory);
                        if (null == shoppingArtplanInventory || shoppingArtplanInventory.getCount().intValue() < goodsCount) {
                            inventoryName = shoppingArtplan.getActivityName();
                            inventoryCorrect = false;
                            break;
                        }
                    } else if (shoppingArtplan.getSignupTotalLimit().intValue() > 0 && shoppingArtplan.getLeftnum().intValue() < goodsCount) {
                        inventoryName = shoppingArtplan.getActivityName();
                        inventoryCorrect = false;
                        break;
                    }
                } else if (cartType == Const.SHOPPING_CLASS_CART_TYPE.intValue()) {
                    ShoppingArtclass shoppingArtclass = new ShoppingArtclass();
                    shoppingArtclass.setId(goodsId);
                    shoppingArtclass = this.shoppingArtclassDao.queryDetail(shoppingArtclass);
                    BigDecimal currentPrice = shoppingArtclass.getCurrentPrice();
                    goodsObj.put("goodsId", goodsId);
                    goodsObj.put("goodsName", shoppingArtclass.getClassName());
                    goodsObj.put("currentPrice", currentPrice);
                    goodsObj.put("goodsCount", Integer.valueOf(goodsCount));
                    goodsObj.put("photoId", shoppingArtclass.getMainPhotoId());
                    int useIntegralSet = shoppingArtclass.getUseIntegralSet().intValue();
                    int useIntegralValue = shoppingArtclass.getUseIntegralValue().intValue();
                    int useBalanceSet = shoppingArtclass.getUseBalanceSet().intValue();
                    int useMembershipSet = shoppingArtclass.getUseMembershipSet().intValue();
                    goodsObj.put("useIntegralSet", Integer.valueOf(useIntegralSet));
                    goodsObj.put("useIntegralValue", Integer.valueOf(useIntegralValue));
                    goodsObj.put("useBalanceSet", Integer.valueOf(useBalanceSet));
                    goodsObj.put("useMembershipSet", Integer.valueOf(useMembershipSet));
                    goodsPrice = goodsPrice.add(currentPrice.multiply(new BigDecimal(goodsCount)).setScale(2, 4));
                }
                goodsArray.add(goodsObj);
            }
            if (!inventoryCorrect) {
                retCode = "-1";
                retMsg = inventoryName + "库存不足,请重新确认订单信息！";
            } else if (!unitPriceCorrect) {
                retCode = "-1";
                retMsg = "商品价格发生变化，请重新确认订单信息！";
            } else if (cartState) {
                retCode = "-1";
                retMsg = "您提交的购物车记录不存在，请刷新购物车！";
            } else if (overLimitBuy) {
                retCode = "-1";
                retMsg = "您的单人报名数量已超过限额！";
            } else if (overSignUpTime) {
                retCode = "-1";
                retMsg = "当前时间不可报名！";
            }else {
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
                        accountMoney = (new BigDecimal(account_money_fen)).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
                        bizDataJson.put("accountPoint", Integer.valueOf(account_point));
                        bizDataJson.put("accountMoney", accountMoney);
                    } else {
                        bizDataJson.put("accountPoint", Integer.valueOf(0));
                        bizDataJson.put("accountMoney", Integer.valueOf(0));
                    }
                    ShoppingAssetRule shoppingAssetRule = CommonUtil.getAssetRule();
                    if (null != shoppingAssetRule) {
                        bizDataJson.put("accountPointLimit", Integer.valueOf(shoppingAssetRule.getPointAvoidLimit()));
                        bizDataJson.put("accountMoneyLimit", shoppingAssetRule.getAccountAvoidLimit());
                    }
                }
                if (goodsTransList.size() > 0) {
                    JSONObject addObj = null;
                    if (null != reqJson.get("addressId"))
                        addObj = MZService.getAddressDetail(mzUserId, reqJson.getString("addressId"));
                    shipPrice = shipPrice.add(buildShipPrice(goodsTransList, addObj));
                }
                if (!accountState) {
                    retCode = "-1";
                    retMsg = "无法获取您的账户积分和余额数据，请稍后下单！";
                }
                if (shipPrice.compareTo(orderShipPrice) != 0) {
                    retCode = "-1";
                    retMsg = "运费金额发生变化，请重新确认订单信息！";
                } else if (accountMoney.compareTo(orderDeductionBalancePrice) < 0 || account_point < orderUseIntegralValue) {
                    retCode = "-1";
                    retMsg = "账户积分或余额不足，请重新确认订单信息！";
                } else {
                    BigDecimal couponCut = BigDecimal.ZERO;
                    BigDecimal accountCut = BigDecimal.ZERO;
                    int integralValue = 0;
                    BigDecimal integralCut = BigDecimal.ZERO;
                    BigDecimal balanceCut = BigDecimal.ZERO;
                    BigDecimal totalPrice = goodsPrice.add(shipPrice);
                    BigDecimal payPrice = BigDecimal.ZERO;
                    payPrice = goodsPrice;
                    BigDecimal totalCashPrice = BigDecimal.ZERO;
                    int fixedIntegalValue = 0;
                    for (int j = 0; j < goodsArray.size(); j++) {
                        JSONObject goodsObj = goodsArray.getJSONObject(j);
                        BigDecimal currentPrice = goodsObj.getBigDecimal("currentPrice");
                        int goodsCount = goodsObj.getInteger("goodsCount").intValue();
                        goodsObj.put("perGoodsPrice", currentPrice.multiply(new BigDecimal(goodsCount)));
                        if (goodsObj.getInteger("useIntegralSet").intValue() == 1) {
                            int useIntegralValue = goodsObj.getInteger("useIntegralValue").intValue();
                            if (currentPrice.multiply(new BigDecimal(100)).intValue() < useIntegralValue)
                                useIntegralValue = currentPrice.multiply(new BigDecimal(100)).intValue();
                            goodsObj.put("useIntegralValue", Integer.valueOf(useIntegralValue));
                            fixedIntegalValue += useIntegralValue * goodsCount;
                            goodsObj.put("perIntegralValue", Integer.valueOf(useIntegralValue * goodsCount));
                            BigDecimal cashPrice = currentPrice.multiply(new BigDecimal(goodsCount)).subtract((new BigDecimal(useIntegralValue * goodsCount)).divide(new BigDecimal(this.moneyToIntegralScale), 2, RoundingMode.HALF_UP));
                            goodsObj.put("perPayPrice", cashPrice);
                            totalCashPrice = totalCashPrice.add(cashPrice);
                        } else {
                            BigDecimal cashPrice = currentPrice.multiply(new BigDecimal(goodsCount));
                            goodsObj.put("perPayPrice", cashPrice);
                            totalCashPrice = totalCashPrice.add(cashPrice);
                        }
                    }
                    List<String> couponIds = new ArrayList<>();
                    JSONArray couponArray = CommonUtil.getCartsCouppon(userId, goodsArray);
                    for (int k = 0; k < couponArray.size(); k++) {
                        JSONObject obj = couponArray.getJSONObject(k);
                        couponIds.add(obj.getString("id"));
                    }
                    bizDataJson.put("couponList", couponArray);
                    BigDecimal originPrice = payPrice;
                    int originntegalValue = fixedIntegalValue;
                    if (null != reqJson.get("couponId") && !"".equals(reqJson.get("couponId"))) {
                        String couponId = reqJson.getString("couponId");
                        if (couponIds.contains(couponId)) {
                            JSONObject couponDtl = CRMService.getUserCouponDtl(couponId);
                            String right_No = couponDtl.getString("right_No");
                            ShoppingCoupon shoppingCoupon = new ShoppingCoupon();
                            shoppingCoupon.setRight_No(right_No);
                            shoppingCoupon = this.shoppingCouponDao.queryDetail(shoppingCoupon);
                            HashSet<String> goodsSet = new HashSet<>();
                            for (int n = 0; n < couponArray.size(); n++) {
                                JSONObject obj = couponArray.getJSONObject(n);
                                if (shoppingCoupon.getRight_No().equals(obj.getString("right_No"))) {
                                    goodsSet = (HashSet<String>)obj.get("goodsSet");
                                    break;
                                }
                            }
                            if (shoppingCoupon.getRight_Type().equals("coincp")) {
                                BigDecimal couponPrice = payPrice;
                                BigDecimal couponAmount = new BigDecimal(Integer.valueOf(shoppingCoupon.getRight_Content()).intValue());
                                if (couponPrice.compareTo(couponAmount) < 0)
                                    couponAmount = couponPrice;
                                payPrice = payPrice.subtract(couponAmount);
                                BigDecimal totalPerCouponCut = BigDecimal.ZERO;
                                for (int i1 = 0; i1 < goodsArray.size(); i1++) {
                                    JSONObject goodsObj = goodsArray.getJSONObject(i1);
                                    BigDecimal perPayPrice = goodsObj.getBigDecimal("perPayPrice");
                                    BigDecimal perCouponCut = BigDecimal.ZERO;
                                    if (i1 != goodsArray.size() - 1) {
                                        perCouponCut = couponAmount.multiply(perPayPrice).divide(totalCashPrice, 2, RoundingMode.HALF_UP);
                                        totalPerCouponCut = totalPerCouponCut.add(perCouponCut);
                                    } else {
                                        perCouponCut = couponAmount.subtract(totalPerCouponCut);
                                    }
                                    if (goodsSet.contains(goodsObj.getString("cartType") + "&" + goodsObj.getString("goodsId"))) {
                                        goodsObj.put("perPayPrice", perPayPrice.subtract(perCouponCut));
                                        goodsObj.put("perCouponCut", perCouponCut);
                                    } else {
                                        goodsObj.put("perCouponCut", BigDecimal.ZERO);
                                    }
                                }
                                couponCut = couponAmount;
                            } else {
                                int i1 = 0;
                                BigDecimal bigDecimal = new BigDecimal(shoppingCoupon.getRight_Content());
                                for (int i2 = 0; i2 < goodsArray.size(); i2++) {
                                    JSONObject goodsObj = goodsArray.getJSONObject(i2);
                                    if (goodsSet.contains(goodsObj.getString("cartType") + "&" + goodsObj.getString("goodsId"))) {
                                        BigDecimal perPayPrice = goodsObj.getBigDecimal("perPayPrice");
                                        BigDecimal cprice = perPayPrice.multiply(bigDecimal).setScale(2, 4);
                                        goodsObj.put("perPayPrice", cprice);
                                        BigDecimal perCouponCut = perPayPrice.subtract(cprice);
                                        if (goodsObj.getInteger("useIntegralSet").intValue() == 1) {
                                            int perIntegralValue = goodsObj.getInteger("perIntegralValue").intValue();
                                            int cIntegralValue = (new BigDecimal(perIntegralValue)).multiply(bigDecimal).intValue();
                                            goodsObj.put("perIntegralValue", Integer.valueOf(cIntegralValue));
                                            perCouponCut = perCouponCut.add((new BigDecimal(perIntegralValue - cIntegralValue)).divide(new BigDecimal(this.moneyToIntegralScale), 2, RoundingMode.HALF_UP));
                                            i1 += perIntegralValue - cIntegralValue;
                                        }
                                        goodsObj.put("perCouponCut", perCouponCut);
                                        couponCut = couponCut.add(perCouponCut);
                                    } else {
                                        goodsObj.put("perCouponCut", BigDecimal.ZERO);
                                    }
                                }
                                payPrice = originPrice.subtract(couponCut);
                                fixedIntegalValue = originntegalValue - i1;
                            }
                            originPrice = payPrice;
                            originntegalValue = fixedIntegalValue;
                        }
                    }
                    BigDecimal discount = CommonUtil.getUserMemberShip(userId);
                    int cutIntegral = 0;
                    List<String> ids = new ArrayList<>();
                    int m;
                    for (m = 0; m < goodsArray.size(); m++) {
                        JSONObject goodsObj = goodsArray.getJSONObject(m);
                        if (goodsObj.getInteger("useMembershipSet").intValue() == 1)
                            ids.add(goodsObj.getString("goodsId"));
                    }
                    for (m = 0; m < goodsArray.size(); m++) {
                        JSONObject goodsObj = goodsArray.getJSONObject(m);
                        if (ids.contains(goodsObj.getString("goodsId"))) {
                            BigDecimal perPayPrice = goodsObj.getBigDecimal("perPayPrice");
                            BigDecimal cprice = perPayPrice.multiply(discount).setScale(2, 4);
                            goodsObj.put("perPayPrice", cprice);
                            BigDecimal perAccountCut = perPayPrice.subtract(cprice);
                            if (goodsObj.getInteger("useIntegralSet").intValue() == 1) {
                                int perIntegralValue = goodsObj.getInteger("perIntegralValue").intValue();
                                int cIntegralValue = (new BigDecimal(perIntegralValue)).multiply(discount).intValue();
                                goodsObj.put("perIntegralValue", Integer.valueOf(cIntegralValue));
                                perAccountCut = perAccountCut.add((new BigDecimal(perIntegralValue - cIntegralValue)).divide(new BigDecimal(this.moneyToIntegralScale), 2, RoundingMode.HALF_UP));
                                cutIntegral += perIntegralValue - cIntegralValue;
                            }
                            goodsObj.put("perAccountCut", perAccountCut);
                            accountCut = accountCut.add(perAccountCut);
                        } else {
                            goodsObj.put("perAccountCut", BigDecimal.ZERO);
                        }
                    }
                    payPrice = originPrice.subtract(accountCut);
                    fixedIntegalValue = originntegalValue - cutIntegral;
                    originPrice = payPrice;
                    originntegalValue = fixedIntegalValue;
                    ShoppingPayLimit payLimit = CommonUtil.getPayLimit();
                    int accountPoint = (bizDataJson.get("accountPoint") == null) ? 0 : bizDataJson.getInteger("accountPoint").intValue();
                    int pointPay = payLimit.getPointPay();
                    int minIntegralValue = originntegalValue;
                    if (minIntegralValue > accountPoint) {
                        retCode = "1";
                        retMsg = "当前账户积分不够，无法下单！";
                    } else if (minIntegralValue > pointPay) {
                        retCode = "1";
                        retMsg = "积分单次支付限额" + pointPay + ",当前订单已超出该额度，无法下单！";
                    } else {
                        int maxIntegralValue = originntegalValue;
                        BigDecimal tPrice = BigDecimal.ZERO;
                        int tIntegral = 0;
                        if (null != reqJson.get("useIntegral") && reqJson.getInteger("useIntegral").intValue() == 1)
                            for (int n = 0; n < goodsArray.size(); n++) {
                                JSONObject goodsObj = goodsArray.getJSONObject(n);
                                int useIntegralSet = goodsObj.getInteger("useIntegralSet").intValue();
                                BigDecimal perPayPrice = goodsObj.getBigDecimal("perPayPrice");
                                if (useIntegralSet == 2) {
                                    tPrice = tPrice.add(perPayPrice);
                                    int perPayToInt = perPayPrice.multiply(new BigDecimal(this.moneyToIntegralScale)).intValue();
                                    int goodsCount = goodsObj.getInteger("goodsCount").intValue();
                                    int useIntegralValue = goodsObj.getInteger("useIntegralValue").intValue();
                                    int perMaxIntegralValue = (useIntegralValue * goodsCount < perPayToInt) ? (useIntegralValue * goodsCount) : perPayToInt;
                                    tIntegral += perMaxIntegralValue;
                                    goodsObj.put("perMaxIntegralValue", Integer.valueOf(perMaxIntegralValue));
                                    maxIntegralValue += perMaxIntegralValue;
                                }
                            }
                        if (maxIntegralValue > 0) {
                            int pointLimit = accountPoint;
                            if (pointPay > 0)
                                pointLimit = (pointPay > pointLimit) ? pointLimit : pointPay;
                            if (maxIntegralValue > pointLimit)
                                maxIntegralValue = pointLimit;
                            int iNum = 0;
                            for (int n = 0; n < goodsArray.size(); n++) {
                                JSONObject goodsObj = goodsArray.getJSONObject(n);
                                int useIntegralSet = goodsObj.getInteger("useIntegralSet").intValue();
                                if (useIntegralSet == 2)
                                    iNum++;
                            }
                            int totalPerIntegralCutValue = 0;
                            int iCount = 0;
                            for (int i1 = 0; i1 < goodsArray.size(); i1++) {
                                JSONObject goodsObj = goodsArray.getJSONObject(i1);
                                int useIntegralSet = goodsObj.getInteger("useIntegralSet").intValue();
                                BigDecimal perPayPrice = goodsObj.getBigDecimal("perPayPrice");
                                if (useIntegralSet == 2) {
                                    if (tPrice.compareTo(BigDecimal.ZERO) == 1) {
                                        iCount++;
                                        if (iCount != iNum) {
                                            int perMaxIntegralValue = goodsObj.getInteger("perMaxIntegralValue").intValue();
                                            int perIntegralCutValue = (new BigDecimal(maxIntegralValue - minIntegralValue)).multiply(new BigDecimal(perMaxIntegralValue)).divide(new BigDecimal(tIntegral), 2, RoundingMode.HALF_UP).intValue();
                                            totalPerIntegralCutValue += perIntegralCutValue;
                                            goodsObj.put("perIntegralCutValue", Integer.valueOf(perIntegralCutValue));
                                            BigDecimal perIntegralCut = (new BigDecimal(perIntegralCutValue)).divide(new BigDecimal(this.moneyToIntegralScale), 2, RoundingMode.HALF_UP);
                                            goodsObj.put("perIntegralCut", perIntegralCut);
                                            goodsObj.put("perPayPrice", perPayPrice.subtract(perIntegralCut));
                                        } else {
                                            int perIntegralCutValue = maxIntegralValue - minIntegralValue - totalPerIntegralCutValue;
                                            goodsObj.put("perIntegralCutValue", Integer.valueOf(perIntegralCutValue));
                                            BigDecimal perIntegralCut = (new BigDecimal(perIntegralCutValue)).divide(new BigDecimal(this.moneyToIntegralScale), 2, RoundingMode.HALF_UP);
                                            goodsObj.put("perIntegralCut", perIntegralCut);
                                            goodsObj.put("perPayPrice", perPayPrice.subtract(perIntegralCut));
                                        }
                                    }
                                } else if (useIntegralSet == 1) {
                                    int perIntegralValue = goodsObj.getInteger("perIntegralValue").intValue();
                                    goodsObj.put("perIntegralCutValue", Integer.valueOf(perIntegralValue));
                                    BigDecimal perIntegralCut = (new BigDecimal(perIntegralValue)).divide(new BigDecimal(this.moneyToIntegralScale), 2, RoundingMode.HALF_UP);
                                    goodsObj.put("perIntegralCut", perIntegralCut);
                                } else {
                                    goodsObj.put("perIntegralCutValue", Integer.valueOf(0));
                                    goodsObj.put("perIntegralCut", BigDecimal.ZERO);
                                }
                            }
                            bizDataJson.put("minIntegralValue", Integer.valueOf(minIntegralValue));
                            bizDataJson.put("maxIntegralValue", Integer.valueOf(maxIntegralValue));
                            BigDecimal integralAmount = (new BigDecimal(maxIntegralValue)).divide(new BigDecimal(this.moneyToIntegralScale), 2, RoundingMode.HALF_UP);
                            integralCut = integralAmount;
                            payPrice = originPrice.subtract(integralCut);
                            originPrice = payPrice;
                        }
                        tPrice = BigDecimal.ZERO;
                        if (null != reqJson.get("useBalance") && reqJson.getInteger("useBalance").intValue() == 1) {
                            BigDecimal deductionBalance = BigDecimal.ZERO;
                            for (int n = 0; n < goodsArray.size(); n++) {
                                JSONObject goodsObj = goodsArray.getJSONObject(n);
                                int useBalanceSet = goodsObj.getInteger("useBalanceSet").intValue();
                                BigDecimal perPayPrice = goodsObj.getBigDecimal("perPayPrice");
                                if (useBalanceSet == 1) {
                                    tPrice = tPrice.add(perPayPrice);
                                    deductionBalance = deductionBalance.add(perPayPrice);
                                }
                            }
                            BigDecimal accountLimit = bizDataJson.getBigDecimal("accountMoney");
                            BigDecimal balancePay = new BigDecimal(payLimit.getBalancePay());
                            if (balancePay.compareTo(BigDecimal.ZERO) > 0 && balancePay.compareTo(accountLimit) < 0)
                                accountLimit = balancePay;
                            if (payPrice.compareTo(accountLimit) == 1)
                                deductionBalance = accountLimit;
                            for (int i1 = 0; i1 < goodsArray.size(); i1++) {
                                JSONObject goodsObj = goodsArray.getJSONObject(i1);
                                int useBalanceSet = goodsObj.getInteger("useBalanceSet").intValue();
                                BigDecimal perPayPrice = goodsObj.getBigDecimal("perPayPrice");
                                if (useBalanceSet == 1) {
                                    if (tPrice.compareTo(BigDecimal.ZERO) == 1) {
                                        BigDecimal perBalanceCut = deductionBalance.multiply(perPayPrice).divide(tPrice, 2, RoundingMode.HALF_UP);
                                        goodsObj.put("perBalanceCut", perBalanceCut);
                                        goodsObj.put("perPayPrice", perPayPrice.subtract(perBalanceCut));
                                    }
                                } else {
                                    goodsObj.put("perBalanceCut", BigDecimal.ZERO);
                                }
                            }
                            payPrice = payPrice.subtract(deductionBalance);
                            balanceCut = deductionBalance;
                            if (balanceCut.compareTo(accountLimit) == -1) {
                                if (shipPrice.compareTo(accountLimit.subtract(balanceCut)) == -1) {
                                    balanceCut = balanceCut.add(shipPrice);
                                } else {
                                    balanceCut = accountLimit;
                                    payPrice = payPrice.add(shipPrice.subtract(accountLimit.subtract(balanceCut)));
                                }
                            } else {
                                payPrice = payPrice.add(shipPrice);
                            }
                        } else {
                            payPrice = payPrice.add(shipPrice);
                        }
                        if (totalPrice.compareTo(orderTotalPrice) != 0 || payPrice.compareTo(orderPayPrice) != 0 || maxIntegralValue != orderUseIntegralValue || balanceCut.compareTo(orderDeductionBalancePrice) != 0) {
                            retCode = "-1";
                            retMsg = "订单金额发生变化，请重新确认订单信息！";
                        } else if (minIntegralValue > account_point) {
                            retCode = "-1";
                            retMsg = "当前账户积分不足，请重新确认订单信息！";
                        } else if (!dealGoodsStock(goodsArray)) {
                            retCode = "-1";
                            retMsg = "存在商品数量不足，请重新下单！";
                        } else {
                            ShoppingOrderform orderform = new ShoppingOrderform();
                            String orderId = PayUtil.getOrderNo(Const.SHOPPING_MERGE_ORDER);
                            orderform.setOrderId(orderId);
                            orderform.setOrderType(Const.SHOPPING_MERGE_ORDER_TYPE);
                            orderform.setOrderStatus(Integer.valueOf(10));
                            orderform.setTotalPrice(totalPrice);
                            orderform.setShipPrice(shipPrice);
                            orderform.setPayPrice(payPrice);
                            String transport = reqJson.getString("transport");
                            orderform.setTransport(transport);
                            if (null != reqJson.get("addressId"))
                                orderform.setAddrId(reqJson.getString("addressId"));
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
                            if (null != reqJson.get("useIntegral") && reqJson.getInteger("useIntegral").intValue() == 1) {
                                orderform.setDeductionIntegralPrice(integralCut);
                                orderform.setDeductionIntegral(Integer.valueOf(integralCut.multiply(new BigDecimal(this.moneyToIntegralScale)).intValue()));
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
                            for (int n = 0; n < goodsArray.size(); n++) {
                                JSONObject goodsObj = goodsArray.getJSONObject(n);
                                ShoppingGoodscart goodscart = new ShoppingGoodscart();
                                String goodsId = goodsObj.getString("goodsId");
                                int goodsCount = goodsObj.getInteger("goodsCount").intValue();
                                String cartId = goodsObj.getString("cartId");
                                int cartType = goodsObj.getInteger("cartType").intValue();
                                BigDecimal currentPrice = goodsObj.getBigDecimal("currentPrice");
                                goodscart.setId(cartId);
                                goodscart = this.shoppingGoodscartDao.queryDetail(goodscart);
                                goodscart.setPrice(currentPrice);
                                goodscart.setOfId(orderform.getId());
                                goodscart.setCount(Integer.valueOf(goodsCount));
                                BigDecimal perCouponCut = (goodsObj.get("perCouponCut") == null) ? BigDecimal.ZERO : goodsObj.getBigDecimal("perCouponCut");
                                BigDecimal perAccountCut = (goodsObj.get("perAccountCut") == null) ? BigDecimal.ZERO : goodsObj.getBigDecimal("perAccountCut");
                                int perIntegralCutValue = (goodsObj.get("perIntegralCutValue") == null) ? 0 : goodsObj.getInteger("perIntegralCutValue").intValue();
                                BigDecimal perIntegralCut = (goodsObj.get("perIntegralCut") == null) ? BigDecimal.ZERO : goodsObj.getBigDecimal("perIntegralCut");
                                BigDecimal perBalanceCut = (goodsObj.get("perBalanceCut") == null) ? BigDecimal.ZERO : goodsObj.getBigDecimal("perBalanceCut");
                                BigDecimal perPayPrice = (goodsObj.get("perPayPrice") == null) ? BigDecimal.ZERO : goodsObj.getBigDecimal("perPayPrice");
                                goodscart.setDeductionCouponPrice(perCouponCut);
                                goodscart.setDeductionMemberPrice(perAccountCut);
                                goodscart.setDeductionIntegral(Integer.valueOf(perIntegralCutValue));
                                goodscart.setDeductionIntegralPrice(perIntegralCut);
                                goodscart.setDeductionBalancePrice(perBalanceCut);
                                goodscart.setPayPrice(perPayPrice);
                                String cartTransport = (goodsObj.get("transport") != null) ? goodsObj.getString("transport") : null;
                                goodscart.setTransport(cartTransport);
                                if (cartType == Const.SHOPPING_CUL_CART_TYPE.intValue() || cartType == Const.SHOPPING_INT_CART_TYPE.intValue()) {
                                    if (cartTransport != null && "自提".equals(cartTransport)) {
                                        String gcId = goodscart.getId();
                                        ShoppingWriteoff shoppingWriteoff = new ShoppingWriteoff();
                                        shoppingWriteoff.setGcId(gcId);
                                        shoppingWriteoff.setGoodsCount(goodscart.getCount());
                                        shoppingWriteoff.setOffCode(StringUtil.randomOffCode(this.offcodeLength));
                                        this.shoppingWriteoffDao.insert(shoppingWriteoff);
                                    }
                                    if (weightMap.keySet().contains(goodsId)) {
                                        BigDecimal goodsWeight = weightMap.get(goodsId);
                                        BigDecimal cent = goodsWeight.divide(totalWeight, 2, RoundingMode.HALF_UP);
                                        goodscart.setShipPrice(shipPrice.multiply(cent).setScale(2, 4));
                                    }
                                    if (null != goodsObj.get("propertys") && !"".equals(goodsObj.get("propertys"))) {
                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("goodsId", goodsId);
                                        hashMap.put("propertys", goodsObj.getString("propertys"));
                                        hashMap.put("cutCount", Integer.valueOf(goodsCount));
                                        this.shoppingGoodsInventoryDao.cutInventory(hashMap);
                                    }
                                    HashMap<String, Object> cutMap = new HashMap<>();
                                    cutMap.put("goodsId", goodsId);
                                    cutMap.put("cutInventory", Integer.valueOf(goodsCount));
                                    this.shoppingGoodsDao.cutGoodsInventory(cutMap);
                                } else if (cartType == Const.SHOPPING_ACT_CART_TYPE.intValue()) {
                                    String gcId = goodscart.getId();
                                    ShoppingWriteoff shoppingWriteoff = new ShoppingWriteoff();
                                    shoppingWriteoff.setGcId(gcId);
                                    shoppingWriteoff.setGoodsCount(goodscart.getCount());
                                    shoppingWriteoff.setOffCode(StringUtil.randomOffCode(this.offcodeLength));
                                    this.shoppingWriteoffDao.insert(shoppingWriteoff);
                                    ShoppingArtactivity shoppingArtactivity = new ShoppingArtactivity();
                                    shoppingArtactivity.setId(goodsId);
                                    shoppingArtactivity = this.shoppingArtactivityDao.queryDetail(shoppingArtactivity);
                                    if (null != goodsObj.get("propertys") && !"".equals(goodsObj.get("propertys"))) {
                                        HashMap<String, Object> cutMap = new HashMap<>();
                                        cutMap.put("activityId", shoppingArtactivity.getId());
                                        cutMap.put("propertys", goodsObj.getString("propertys"));
                                        cutMap.put("cutCount", Integer.valueOf(goodsCount));
                                        this.shoppingArtactivityInventoryDao.cutInventory(cutMap);
                                    }
                                    if (shoppingArtactivity.getSignupTotalLimit().intValue() > 0)
                                        shoppingArtactivity.setLeftnum(Integer.valueOf(shoppingArtactivity.getLeftnum().intValue() - goodsCount));
                                    HashMap<String, Object> numMap = new HashMap<>();
                                    numMap.put("id", shoppingArtactivity.getId());
                                    numMap.put("cutnum", Integer.valueOf(goodsCount));
                                    this.shoppingArtactivityDao.updateActivityCutNum(numMap);
                                    if (null != goodsObj.get("signupInfos") && !"".equals(goodsObj.get("signupInfos"))) {
                                        JSONArray infoArray = goodsObj.getJSONArray("signupInfos");
                                        for (int i1 = 0; i1 < infoArray.size(); i1++) {
                                            ShoppingArtactivitySignupinfo shoppingArtactivitySignupinfo = new ShoppingArtactivitySignupinfo();
                                            shoppingArtactivitySignupinfo.setActivityId(goodsId);
                                            shoppingArtactivitySignupinfo.setOfId(orderform.getId());
                                            shoppingArtactivitySignupinfo.setSignupInfo(infoArray.getJSONArray(i1).toString());
                                            this.shoppingArtactivitySignupinfoDao.insert(shoppingArtactivitySignupinfo);
                                        }
                                    }
                                } else if (cartType == Const.SHOPPING_PLAN_CART_TYPE.intValue()) {
                                    String gcId = goodscart.getId();
                                    ShoppingWriteoff shoppingWriteoff = new ShoppingWriteoff();
                                    shoppingWriteoff.setGcId(gcId);
                                    shoppingWriteoff.setGoodsCount(goodscart.getCount());
                                    shoppingWriteoff.setOffCode(StringUtil.randomOffCode(this.offcodeLength));
                                    this.shoppingWriteoffDao.insert(shoppingWriteoff);
                                    ShoppingArtplan shoppingArtplan = new ShoppingArtplan();
                                    shoppingArtplan.setId(goodsId);
                                    shoppingArtplan = this.shoppingArtplanDao.queryDetail(shoppingArtplan);
                                    if (null != goodsObj.get("propertys") && !"".equals(goodsObj.get("propertys"))) {
                                        ShoppingArtplanInventory shoppingArtplanInventory = new ShoppingArtplanInventory();
                                        shoppingArtplanInventory.setActivityId(shoppingArtplan.getId());
                                        shoppingArtplanInventory.setPropertys(goodsObj.getString("propertys"));
                                        shoppingArtplanInventory.setCount(Integer.valueOf(goodsObj.getInteger("inventoryCount").intValue() - goodsCount));
                                        this.shoppingArtplanInventoryDao.update(shoppingArtplanInventory);
                                    }
                                    if (shoppingArtplan.getSignupTotalLimit().intValue() > 0)
                                        shoppingArtplan.setLeftnum(Integer.valueOf(shoppingArtplan.getLeftnum().intValue() - goodsCount));
                                    this.shoppingArtplanDao.updatePlanNum(shoppingArtplan);
                                    if (null != goodsObj.get("signupInfos") && !"".equals(goodsObj.get("signupInfos"))) {
                                        JSONArray infoArray = goodsObj.getJSONArray("signupInfos");
                                        for (int i1 = 0; i1 < infoArray.size(); i1++) {
                                            ShoppingArtplanSignupinfo shoppingArtplanSignupinfo = new ShoppingArtplanSignupinfo();
                                            shoppingArtplanSignupinfo.setActivityId(goodsId);
                                            shoppingArtplanSignupinfo.setOfId(orderform.getId());
                                            shoppingArtplanSignupinfo.setSignupInfo(infoArray.getJSONArray(i1).toString());
                                            this.shoppingArtplanSignupinfoDao.insert(shoppingArtplanSignupinfo);
                                        }
                                    }
                                } else if (cartType == Const.SHOPPING_CLASS_CART_TYPE.intValue()) {
                                    if (null != goodsObj.get("signupInfos") && !"".equals(goodsObj.get("signupInfos"))) {
                                        JSONArray infoArray = goodsObj.getJSONArray("signupInfos");
                                        for (int i1 = 0; i1 < infoArray.size(); i1++) {
                                            ShoppingArtclassSignupinfo shoppingArtclassSignupinfo = new ShoppingArtclassSignupinfo();
                                            shoppingArtclassSignupinfo.setClassId(goodsId);
                                            shoppingArtclassSignupinfo.setOfId(orderform.getId());
                                            shoppingArtclassSignupinfo.setSignupInfo(infoArray.getJSONArray(i1).toString());
                                            this.shoppingArtclassSignupinfoDao.insert(shoppingArtclassSignupinfo);
                                        }
                                    }
                                }
                                this.shoppingGoodscartDao.update(goodscart);
                            }
                            HashMap<String, Object> reqMap = new HashMap<>();
                            reqMap.put("deleteStatus", Integer.valueOf(0));
                            List<ShoppingPayment> payments = this.shoppingPaymentDao.queryList(reqMap);
                            JSONArray payArray = new JSONArray();
                            for (ShoppingPayment shoppingPayment : payments) {
                                JSONObject obj = new JSONObject();
                                obj.put("id", shoppingPayment.getId());
                                obj.put("name", shoppingPayment.getName());
                                payArray.add(obj);
                            }
                            bizDataJson.put("payments", payArray);
                            bizDataJson.put("orderId", orderId);
                            bizDataJson.put("price", payPrice);
                            retCode = "0";
                            retMsg = "操作成功！";
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

    public BigDecimal buildShipPrice(List<Map<String, Object>> goodsTransList, JSONObject addObj) {
        ShoppingTransport shoppingTransport = new ShoppingTransport();
        ShoppingGoods shoppingGoods = new ShoppingGoods();
        shoppingGoods.setId(((Map)goodsTransList.get(0)).get("goodsId").toString());
        shoppingGoods = this.shoppingGoodsDao.queryDetail(shoppingGoods);
        shoppingTransport.setId(shoppingGoods.getTransportId());
        shoppingTransport = this.shoppingTransportDao.queryDetail(shoppingTransport);
        String transInfo = (shoppingTransport.getTransEmsInfo() == null) ? ((shoppingTransport.getTransExpressInfo() == null) ? shoppingTransport.getTransMailInfo() : shoppingTransport.getTransExpressInfo()) : shoppingTransport.getTransEmsInfo();
        BigDecimal shipPrice = BigDecimal.ZERO;
        if (shoppingTransport.getTransType().intValue() == 3) {
            if (addObj == null) {
                shipPrice = null;
            } else {
                String cityCode = addObj.getString("city_code");
                String provinceCode = addObj.getString("province_code");
                BigDecimal areaPrice = this.shoppingTransportDao.queryAreaPrice(cityCode);
                if (areaPrice == null)
                    areaPrice = this.shoppingTransportDao.queryAreaPrice(provinceCode);
                shipPrice = (areaPrice == null) ? new BigDecimal(100) : areaPrice;
            }
        } else if (shoppingTransport.getTransType().intValue() == 1) {
            BigDecimal totalWeight = BigDecimal.ZERO;
            for (Map<String, Object> goodsMap : goodsTransList) {
                ShoppingGoods goods = new ShoppingGoods();
                goods.setId(goodsMap.get("goodsId").toString());
                goods = this.shoppingGoodsDao.queryDetail(goods);
                int goodsCount = Integer.valueOf(goodsMap.get("goodsCount").toString()).intValue();
                BigDecimal goodsWeights = goods.getGoodsWeight().multiply(new BigDecimal(goodsCount));
                totalWeight = totalWeight.add(goodsWeights);
            }
            if (totalWeight.compareTo(BigDecimal.ZERO) == 0)
                return BigDecimal.ZERO;
            if (transInfo != null) {
                JSONArray array = JSONArray.parseArray(transInfo);
                for (int i = 0; i < array.size(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    if (obj.get("city_id").equals("-1")) {
                        int transWeight = obj.getInteger("trans_weight").intValue();
                        BigDecimal transFee = obj.getBigDecimal("trans_fee");
                        int transAddWeight = obj.getInteger("trans_add_weight").intValue();
                        BigDecimal transAddFee = obj.getBigDecimal("trans_add_fee");
                        if (totalWeight.compareTo(new BigDecimal(transWeight)) != 1) {
                            shipPrice = transFee;
                        } else {
                            BigDecimal diffWeight = totalWeight.subtract(new BigDecimal(transWeight));
                            BigDecimal amount = diffWeight.divide(new BigDecimal(transAddWeight), 2, RoundingMode.HALF_UP);
                            shipPrice = transFee.add(transAddFee.multiply(amount).setScale(2, 4));
                        }
                    }
                }
            }
        } else if (shoppingTransport.getTransType().intValue() == 2) {
            BigDecimal totalVolume = BigDecimal.ZERO;
            for (Map<String, Object> goodsMap : goodsTransList) {
                ShoppingGoods goods = new ShoppingGoods();
                goods.setId(goodsMap.get("goodsId").toString());
                goods = this.shoppingGoodsDao.queryDetail(goods);
                int goodsCount = Integer.valueOf(goodsMap.get("goodsCount").toString()).intValue();
                BigDecimal goodsVolumes = goods.getGoodsVolume().multiply(new BigDecimal(goodsCount));
                totalVolume = totalVolume.add(goodsVolumes);
            }
            if (totalVolume.compareTo(BigDecimal.ZERO) == 0)
                return BigDecimal.ZERO;
            if (transInfo != null) {
                JSONArray array = JSONArray.parseArray(transInfo);
                for (int i = 0; i < array.size(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    if (obj.get("city_id").equals("-1")) {
                        int transWeight = obj.getInteger("trans_weight").intValue();
                        BigDecimal transFee = obj.getBigDecimal("trans_fee");
                        int transAddWeight = obj.getInteger("trans_add_weight").intValue();
                        BigDecimal transAddFee = obj.getBigDecimal("trans_add_fee");
                        if (totalVolume.compareTo(new BigDecimal(transWeight)) != 1) {
                            shipPrice = transFee;
                        } else {
                            BigDecimal diffWeight = totalVolume.subtract(new BigDecimal(transWeight));
                            BigDecimal amount = diffWeight.divide(new BigDecimal(transAddWeight), 2, RoundingMode.HALF_UP);
                            shipPrice = transFee.add(transAddFee.multiply(amount).setScale(2, 4));
                        }
                    }
                }
            }
        } else if (shoppingTransport.getTransType().intValue() == 0) {
            int totalCount = 0;
            for (Map<String, Object> goodsMap : goodsTransList)
                totalCount += Integer.valueOf(goodsMap.get("goodsCount").toString()).intValue();
            if (transInfo != null) {
                JSONArray array = JSONArray.parseArray(transInfo);
                for (int i = 0; i < array.size(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    if (obj.get("city_id").equals("-1")) {
                        int transWeight = obj.getInteger("trans_weight").intValue();
                        BigDecimal transFee = obj.getBigDecimal("trans_fee");
                        int transAddWeight = obj.getInteger("trans_add_weight").intValue();
                        BigDecimal transAddFee = obj.getBigDecimal("trans_add_fee");
                        if (totalCount <= transWeight) {
                            shipPrice = transFee;
                        } else {
                            BigDecimal diffWeight = new BigDecimal(totalCount - transWeight);
                            BigDecimal amount = diffWeight.divide(new BigDecimal(transAddWeight), 2, RoundingMode.HALF_UP);
                            shipPrice = transFee.add(transAddFee.multiply(amount).setScale(2, 4));
                        }
                    }
                }
            }
        }
        return shipPrice;
    }

    public boolean dealGoodsStock(JSONArray goodsArray) {
        boolean flag = true;
        for (int i = 0; i < goodsArray.size(); i++) {
            JSONObject goodsObj = goodsArray.getJSONObject(i);
            ShoppingGoodscart goodscart = new ShoppingGoodscart();
            String goodsId = goodsObj.getString("goodsId");
            int goodsCount = goodsObj.getInteger("goodsCount").intValue();
            int cartType = goodsObj.getInteger("cartType").intValue();
            if (cartType == Const.SHOPPING_CUL_CART_TYPE.intValue() || cartType == Const.SHOPPING_INT_CART_TYPE.intValue()) {
                if (null != goodsObj.get("propertys") && !"".equals(goodsObj.get("propertys"))) {
                    String inventory_key = "REDIS_KEY:STOCK:GOODS" + goodsId + goodsObj.get("propertys");
                    String key = "REDIS_KEY:STOCK:GOODS" + goodsId;
                    if (this.redisStockService.updateStock(key, goodsCount) < 0L || this.redisStockService.updateStock(inventory_key, goodsCount) < 0L) {
                        flag = false;
                        break;
                    }
                } else {
                    String key = "REDIS_KEY:STOCK:GOODS" + goodsId;
                    if (this.redisStockService.updateStock(key, goodsCount) < 0L) {
                        flag = false;
                        break;
                    }
                }
            } else if (cartType == Const.SHOPPING_ACT_CART_TYPE.intValue()) {
                if (null != goodsObj.get("propertys") && !"".equals(goodsObj.get("propertys"))) {
                    String inventory_key = "REDIS_KEY:STOCK:" + goodsId + goodsObj.get("propertys");
                    String key = "REDIS_KEY:STOCK:" + goodsId;
                    if (this.redisStockService.updateStock(key, goodsCount) < 0L || this.redisStockService.updateStock(inventory_key, goodsCount) < 0L) {
                        flag = false;
                        break;
                    }
                } else {
                    String key = "REDIS_KEY:STOCK:" + goodsId;
                    if (this.redisStockService.updateStock(key, goodsCount) < 0L) {
                        flag = false;
                        break;
                    }
                }
            }
        }
        return flag;
    }
}
