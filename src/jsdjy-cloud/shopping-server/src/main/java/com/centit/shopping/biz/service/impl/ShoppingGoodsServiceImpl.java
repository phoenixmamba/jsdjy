package com.centit.shopping.biz.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.biz.service.ShoppingGoodsService;
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
import java.util.*;

/**
 * <p><p>
 *
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务实现类
 * @Date : 2021-02-24
 **/
@Transactional
@Service
public class ShoppingGoodsServiceImpl implements ShoppingGoodsService {
    public static final Log log = LogFactory.getLog(ShoppingGoodsService.class);

    @Resource
    private ShoppingGoodsDao shoppingGoodsDao;
    @Resource
    private ShoppingGoodsclassDao shoppingGoodsclassDao;
    @Resource
    private ShoppingGoodsspecificationDao shoppingGoodsspecificationDao;
    @Resource
    private ShoppingGoodsspecpropertyDao shoppingGoodsspecpropertyDao;
    @Resource
    private ShoppingGoodsPhotoDao shoppingGoodsPhotoDao;
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
    private ShoppingEvaluateDao shoppingEvaluateDao;

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

    @Value("${goods.culGoods.firstClassId}")
    private String firstCulGoodsClassId;

    @Value("${goods.integralGoods.firstClassId}")
    private String firstIntegralGoodsClassId;

    @Value("${moneyToIntegralScale}")
    private int moneyToIntegralScale;

    @Value("${offcodeLength}")
    private int offcodeLength;

    @Value("${otherGoodsTypeId}")
    private String otherGoodsTypeId;

    /**
     * 配置库存Redis缓存Key前缀
     */
    public static final String REDIS_KEY_GOODS = "REDIS_KEY:STOCK:GOODS";
    @Resource
    private RedisStockService redisStockService;

    /**
     * 获取文创商品分类
     */
    @Override
    public JSONObject testCoupon(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String userId = reqJson.getString("userId");
            String goodsId = reqJson.getString("goodsId");
            BigDecimal price = reqJson.getBigDecimal("price");
//            bizDataJson.put("objList", CommonUtil.getGoodsCouppon(goodsId,1,userId,price));
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
     * 获取文创商品分类
     */
    @Override
    public JSONObject culGoodsClass(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            HashMap<String, Object> reqMap = new HashMap<>();
            if (null != reqJson.get("parentId") && !"".equals(reqJson.getString("parentId"))) {
                reqMap.put("parentId", reqJson.getString("parentId"));
            } else {
                reqMap.put("parentId", firstCulGoodsClassId);
            }

            reqMap.put("deleteStatus", "0");
            bizDataJson.put("objList", shoppingGoodsclassDao.queryList(reqMap));
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
     * 获取积分商城商品分类
     */
    @Override
    public JSONObject integralGoodsClass(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            HashMap<String, Object> reqMap = new HashMap<>();
            if (null != reqJson.get("parentId") && !"".equals(reqJson.getString("parentId"))) {
                reqMap.put("parentId", reqJson.getString("parentId"));
            } else {
                reqMap.put("parentId", firstIntegralGoodsClassId);
            }

            reqMap.put("deleteStatus", "0");
            bizDataJson.put("objList", shoppingGoodsclassDao.queryList(reqMap));
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
     * 获取文创商品分类树
     */
    @Override
    public JSONObject culGoodsClassTree(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("deleteStatus", "0");
            List<ShoppingGoodsclass> list = shoppingGoodsclassDao.queryList(reqMap);
            JSONArray menu = new JSONArray();

            menuTree(list, menu, firstCulGoodsClassId);

            bizDataJson.put("objList", menu);

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
     * 获取积分商品分类树
     */
    @Override
    public JSONObject integralGoodsClassTree(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("deleteStatus", "0");
            List<ShoppingGoodsclass> list = shoppingGoodsclassDao.queryList(reqMap);
            JSONArray menu = new JSONArray();

            menuTree(list, menu, firstIntegralGoodsClassId);

            bizDataJson.put("objList", menu);

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

    private void menuTree(List<ShoppingGoodsclass> list, JSONArray array, String fatherId) {

        for (ShoppingGoodsclass shoppingGoodsclass : list) {
            String father = null;
            if (null != shoppingGoodsclass.getParentId()) {
                father = shoppingGoodsclass.getParentId();
            }
//            if (father .equals("0") && fatherId .equals("0")) {
            if (father != null && father.equals(fatherId)) {
                JSONObject obj = new JSONObject();
                obj.put("id", shoppingGoodsclass.getId());
                obj.put("parentId", shoppingGoodsclass.getParentId());
                obj.put("className", shoppingGoodsclass.getClassName());


                JSONArray children = new JSONArray();
                menuTree(list, children, shoppingGoodsclass.getId());
                obj.put("children", children);
                array.add(obj);
            }
        }
    }

    /**
     * 查询文创商品列表
     */
    @Override
    public JSONObject queryCulGoodsList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            int pageNo = reqJson.get("pageNo") == null ? 1 : reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize") == null ? 10 : reqJson.getInteger("pageSize");
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("startRow", (pageNo - 1) * pageSize);
            reqMap.put("pageSize", pageSize);
            reqMap.put("deleteStatus", "0");
            reqMap.put("goodsStatus", "0");
            reqMap.put("recmmondType", 1);
            if (null == reqMap.get("gcId") || "".equals(reqMap.get("gcId"))) {
                reqMap.put("gcId", firstCulGoodsClassId);
            }

            bizDataJson.put("total", shoppingGoodsDao.queryClassGoodsTotalCount(reqMap));
            List<ShoppingGoods> goodsLiat = shoppingGoodsDao.queryClassGoodsList(reqMap);
            JSONArray resArray = new JSONArray();
            for (ShoppingGoods shoppingGoods : goodsLiat) {
                JSONObject resObj = new JSONObject();
                resObj.put("goodsId", shoppingGoods.getId());  //商品id
                resObj.put("goodsName", shoppingGoods.getGoodsName());  //商品名称
                resObj.put("photoId", shoppingGoods.getGoodsMainPhotoId());  //商品图id
                resObj.put("goodsPrice", shoppingGoods.getStorePrice());  //商品价格
                //固定积分需要显示现金价格+固定积分值
                if(shoppingGoods.getUseIntegralSet()==1){
                    int integralValue = shoppingGoods.getUseIntegralValue();
                    BigDecimal storePrice = shoppingGoods.getStorePrice();
                    BigDecimal integralAmount = (new BigDecimal(integralValue).divide(new BigDecimal(moneyToIntegralScale))).setScale(2, BigDecimal.ROUND_HALF_UP);
                    BigDecimal restPrice= storePrice.subtract(integralAmount);
                    resObj.put("restPrice", restPrice.compareTo(BigDecimal.ZERO)>=0?restPrice:BigDecimal.ZERO);   //扣除固定积分值的现金价格
                    resObj.put("integralValue", integralValue);  //固定积分值
                }
                //付款人数，目前简单计算为该商品的订单数
                reqMap.clear();
                reqMap.put("deleteStatus", "0");
                reqMap.put("goodsId", shoppingGoods.getId());
                resObj.put("orderCount", shoppingOrderformDao.queryTotalCount(reqMap));

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

    /**
     * 查询积分商品列表
     */
    @Override
    public JSONObject queryIntegralGoodsList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            int pageNo = reqJson.get("pageNo") == null ? 1 : reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize") == null ? 10 : reqJson.getInteger("pageSize");
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("startRow", (pageNo - 1) * pageSize);
            reqMap.put("pageSize", pageSize);
            reqMap.put("deleteStatus", "0");
            reqMap.put("goodsStatus", "0");
            reqMap.put("recmmondType", 2);
            if (null == reqMap.get("gcId") || "".equals(reqMap.get("gcId"))) {
                reqMap.put("gcId", firstIntegralGoodsClassId);
            }

            bizDataJson.put("total", shoppingGoodsDao.queryClassGoodsTotalCount(reqMap));
            List<ShoppingGoods> goodsLiat = shoppingGoodsDao.queryClassGoodsList(reqMap);
            JSONArray resArray = new JSONArray();
            for (ShoppingGoods shoppingGoods : goodsLiat) {
                JSONObject resObj = new JSONObject();
                resObj.put("goodsId", shoppingGoods.getId());  //商品id
                resObj.put("goodsName", shoppingGoods.getGoodsName());  //商品名称
                resObj.put("photoId", shoppingGoods.getGoodsMainPhotoId());  //商品图id
                resObj.put("goodsPrice", shoppingGoods.getStorePrice());  //商品价格
                //付款人数，目前简单计算为该商品的订单数
                reqMap.clear();
                reqMap.put("deleteStatus", "0");
                reqMap.put("goodsId", shoppingGoods.getId());
                resObj.put("orderCount", shoppingOrderformDao.queryTotalCount(reqMap));

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

    /**
     * 查询积分商品列表
     */
    @Override
    public JSONObject queryOtherGoodsList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            int pageNo = reqJson.get("pageNo") == null ? 1 : reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize") == null ? 10 : reqJson.getInteger("pageSize");
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("startRow", (pageNo - 1) * pageSize);
            reqMap.put("pageSize", pageSize);
            reqMap.put("deleteStatus", "0");
            reqMap.put("goodsStatus", "0");
            reqMap.put("recmmondType", 2);


            //其它分类
            reqMap.put("gcId",otherGoodsTypeId );

            bizDataJson.put("total", shoppingGoodsDao.queryClassGoodsTotalCount(reqMap));
            List<ShoppingGoods> goodsLiat = shoppingGoodsDao.queryClassGoodsList(reqMap);
            JSONArray resArray = new JSONArray();
            for (ShoppingGoods shoppingGoods : goodsLiat) {
                JSONObject resObj = new JSONObject();
                resObj.put("goodsId", shoppingGoods.getId());  //商品id
                resObj.put("goodsName", shoppingGoods.getGoodsName());  //商品名称
                resObj.put("photoId", shoppingGoods.getGoodsMainPhotoId());  //商品图id
                resObj.put("goodsPrice", shoppingGoods.getStorePrice());  //商品价格
                //付款人数，目前简单计算为该商品的订单数
                reqMap.clear();
                reqMap.put("deleteStatus", "0");
                reqMap.put("goodsId", shoppingGoods.getId());
                resObj.put("orderCount", shoppingOrderformDao.queryTotalCount(reqMap));

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

    /**
     * 获取商品详情
     */
    @Override
    public JSONObject goodsDetail(String goodsId, JSONObject reqJson) {
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
//            if(!redisStockService.checkGoods(key)){
//                redisStockService.initStock(key,shoppingGoods.getGoodsInventory());
//            }
            if (shoppingGoods.getDeleteStatus().equals("1") || shoppingGoods.getGoodsStatus().equals("1")) {
                bizDataJson.put("isOff", true);   //已下架
            } else {
                bizDataJson.put("isOff", false);   //未下架
            }

            JSONObject goodsDetail = new JSONObject();
            goodsDetail.put("goodsId", shoppingGoods.getId());
            goodsDetail.put("goodsName", shoppingGoods.getGoodsName());
            goodsDetail.put("photoId", shoppingGoods.getGoodsMainPhotoId());  //商品图id
            goodsDetail.put("storePrice", shoppingGoods.getStorePrice());  //商品现价
            goodsDetail.put("originPrice", shoppingGoods.getGoodsPrice());  //商品原价
            goodsDetail.put("goodsInventory", shoppingGoods.getGoodsInventory());  //商品库存
            goodsDetail.put("goodsNotice", shoppingGoods.getGoodsNotice());  //商品须知
            goodsDetail.put("returnExplain", shoppingGoods.getReturnExplain());  //退换说明
            goodsDetail.put("limitBuy", shoppingGoods.getLimitBuy());  //商品限购值，0表示不限购
            goodsDetail.put("inventoryType", shoppingGoods.getInventoryType());  //库存配置 all:全局配置；spec:规格配置
            goodsDetail.put("goodsCheckbox", shoppingGoods.getGoodsCheckbox());  //商品标识字段
            goodsDetail.put("details", shoppingGoods.getGoodsDetails());  //商品详情

//            //商品积分抵扣设置
//            goodsDetail.put("useIntegralSet", shoppingGoods.getUseIntegralSet());
//            goodsDetail.put("useIntegralValue", shoppingGoods.getUseIntegralValue());
//            //如果是限额积分抵扣，需要把商品价格切分为现金+积分
//            if(shoppingGoods.getUseIntegralSet()==1){
//                int useIntegralValue = shoppingGoods.getUseIntegralValue();
//            }


            //商品规格信息
            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("goodsId", goodsId);

            List<BigDecimal> priceList = new ArrayList<>();
            List<ShoppingGoodsInventory> inventoryList = shoppingGoodsInventoryDao.queryList(reqMap);
            HashSet<String> propertyIds = new HashSet<>();
            for (ShoppingGoodsInventory shoppingGoodsInventory : inventoryList) {
                key = REDIS_KEY_GOODS + goodsId+":"+shoppingGoodsInventory.getPropertys();
//                if(!redisStockService.checkGoods(key)){
//                    redisStockService.initStock(key,shoppingGoodsInventory.getCount());
//                }
                String[] sps = shoppingGoodsInventory.getPropertys().split("_");
                for (int m = 0; m < sps.length; m++) {
                    String propertyId = sps[m];
                    propertyIds.add(propertyId);
                }
                priceList.add(shoppingGoodsInventory.getPrice());
            }
            if (priceList.size() == 1) {
                goodsDetail.put("minPrice", shoppingGoods.getStorePrice());
                goodsDetail.put("maxPrice", shoppingGoods.getStorePrice());
            } else if (priceList.size() > 1) {
                Map<String, BigDecimal> resMap = StringUtil.getMaxMin(priceList);
                goodsDetail.put("minPrice", resMap.get("min"));
                goodsDetail.put("maxPrice", resMap.get("max"));
            }

            List<ShoppingGoodsspecification> specs = shoppingGoodsspecificationDao.queryGoodsSpecs(reqMap);
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

            reqMap.clear();
            reqMap.put("goodsId", goodsId);
            List<ShoppingGoodsPhoto> photos = shoppingGoodsPhotoDao.queryList(reqMap);


            bizDataJson.put("detail", goodsDetail);  //商品信息
            bizDataJson.put("specs", specArray);  //规格信息
            bizDataJson.put("inventoryDetails", inventoryList);  //规格库存
            bizDataJson.put("photos", photos);  //商品图片
            //商品评价
            reqMap.clear();
            reqMap.put("evaluateGoodsId", goodsId);
            reqMap.put("deleteStatus", 0);
            bizDataJson.put("evaluate", shoppingEvaluateDao.queryLastEvaluate(reqMap));  //商品评价

            int goodsType = reqJson.getInteger("goodsType");
            //商品可用优惠券
//            if(goodsType==1){  //只有文创商品可以用优惠券
//                reqMap.clear();
//                reqMap.put("goodsId", goodsId);
//                reqMap.put("classId", shoppingGoods.getGcId());
//                reqMap.put("goodsType", goodsType);
//                List<ShoppingCoupon> couponList = shoppingCouponDao.queryGoodsCouponList(reqMap);
//                bizDataJson.put("couponList", couponList);
//            }
            //暂时将商品详情页面的领取优惠券置为空
            bizDataJson.put("couponList", new ArrayList<>());


            //收藏标识
            if (null != reqJson.get("userId")) {
                String userId = reqJson.getString("userId");


                //记录浏览历史
                ShoppingHistory shoppingHistory = new ShoppingHistory();
                shoppingHistory.setUserId(userId);
                shoppingHistory.setGoodsId(goodsId);
                shoppingHistory.setType(goodsType);
                shoppingHistoryDao.insert(shoppingHistory);

                reqMap.clear();
                reqMap.put("type", goodsType);
                reqMap.put("goodsId", goodsId);
                reqMap.put("userId", userId);
                reqMap.put("deleteStatus", '0');
                if (!shoppingFavoriteDao.queryList(reqMap).isEmpty()) {
                    bizDataJson.put("isFav", true);   //已收藏
                } else {
                    bizDataJson.put("isFav", false);   //未收藏
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

    /**
     * 查询商品评价列表
     */
    @Override
    public JSONObject queryEvaluatePageList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            int pageNo = reqJson.get("pageNo") == null ? 1 : reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize") == null ? 10 : reqJson.getInteger("pageSize");
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("startRow", (pageNo - 1) * pageSize);
            reqMap.put("pageSize", pageSize);
            reqMap.put("deleteStatus", "0");

            bizDataJson.put("total", shoppingEvaluateDao.queryTotalCount(reqMap));
            bizDataJson.put("objList", shoppingEvaluateDao.queryList(reqMap));

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
     * 判断下单或者添加购物车商品数量是否超过限购值
     */
    @Override
    public JSONObject checkLimitBuy(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String goodsId = reqJson.getString("goodsId");  //商品id
            int count = reqJson.getInteger("count");  //商品数量
            String userId = reqJson.getString("userId");    //用户id
            String goodsStoreId = CommonUtil.getSystemStore().getId();  //默认商户id

            ShoppingGoods shoppingGoods = new ShoppingGoods();
            shoppingGoods.setId(goodsId);
            //查询商品主体信息
            shoppingGoods = shoppingGoodsDao.queryDetail(shoppingGoods);
            bizDataJson.put("limitBuy", shoppingGoods.getLimitBuy());
            if (shoppingGoods.getLimitBuy() == 0) {  //商品不限购
                bizDataJson.put("result", false);
            } else {
                int limitBuy = shoppingGoods.getLimitBuy();  //限购数量
                //已提交订单内的该商品数量
                int cartCount = 0;
                String scId = CommonUtil.getUserScId(userId);
                HashMap<String, Object> reqMap = new HashMap<>();
                reqMap.put("scId", scId);
                reqMap.put("goodsId", goodsId);
                reqMap.put("userId", userId);
                reqMap.put("deleteStatus", "0");
                List<ShoppingGoodscart> shoppingGoodscartList = shoppingGoodscartDao.queryBuyList(reqMap);

                for (ShoppingGoodscart shoppingGoodscart : shoppingGoodscartList) {
                    cartCount += shoppingGoodscart.getCount();
                }
                //已提交订单的商品数量
                bizDataJson.put("doneCount", cartCount);
                if (cartCount + count > limitBuy) {
                    bizDataJson.put("result", true);
                } else {
                    bizDataJson.put("result", false);
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

    /**
     * 订单初始化--获取用户可用的优惠、积分余额、收货地址等信息（订单商品数量发生变动时也需要进行初始化）
     */
    @Override
    public JSONObject getUserPromotion(JSONObject reqJson, HttpServletRequest request) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "服务器内部错误！";
        JSONObject bizDataJson = new JSONObject();
        try {
//            String data = request.getParameter("data");// 参数
//            JSONObject reqJson = JSONObject.parseObject(URLDecoder.decode(data, "utf-8"));

            String userId = reqJson.getString("userId");  //userId
            String goodsId = reqJson.getString("goodsId");  //商品id
//            BigDecimal currentPrice = reqJson.getBigDecimal("currentPrice");  //商品现价
            int goodsCount = reqJson.getInteger("goodsCount");  //购买的商品数量
            ShoppingGoods shoppingGoods = new ShoppingGoods();
            shoppingGoods.setId(goodsId);
            //查询商品主体信息
            shoppingGoods = shoppingGoodsDao.queryDetail(shoppingGoods);

            //实时查询商品现价
            BigDecimal currentPrice = BigDecimal.ZERO;
            if (null != reqJson.get("propertys")) {
                ShoppingGoodsInventory shoppingGoodsInventory = new ShoppingGoodsInventory();
                shoppingGoodsInventory.setGoodsId(shoppingGoods.getId());
                shoppingGoodsInventory.setPropertys(reqJson.getString("propertys"));
                shoppingGoodsInventory = shoppingGoodsInventoryDao.queryDetail(shoppingGoodsInventory);
                currentPrice = shoppingGoodsInventory.getPrice();
            } else {
                currentPrice = shoppingGoods.getStorePrice();
            }
            bizDataJson.put("currentPrice", currentPrice);  //返回实时价格

            //运费计算
            BigDecimal shipPrice = BigDecimal.ZERO;
            if (shoppingGoods.getGoodsTransfee() == 0) {   //买家承担运费
                //通过运费模板计算运费
                if (null != shoppingGoods.getTransportId() && (shoppingGoods.getGoodsTransfee() == null || shoppingGoods.getGoodsTransfee() == 0)) {
                    bizDataJson.put("shipPrice", buildShipPrice(shoppingGoods, goodsCount));
                } else {
                    bizDataJson.put("shipPrice", shoppingGoods.getGoodsTransfee());
                }
            } else {
                bizDataJson.put("shipPrice", BigDecimal.ZERO);
            }
            //商品价格为单价*数量
            bizDataJson.put("goodsPrice", currentPrice.multiply(new BigDecimal(goodsId)));

            int useIntegralSet = shoppingGoods.getUseIntegralSet();   //使用积分抵扣设置 0:不允许使用积分抵扣;1:可使用部分积分抵扣
            int useIntegralValue = shoppingGoods.getUseIntegralValue();    //单个商品积分抵扣值
            int useBalanceSet = shoppingGoods.getUseBalanceSet();   //使用余额支付设置 0:不允许使用;1:可使用
            int useCashSet = shoppingGoods.getUseCashSet();     //使用现金支付设置 0:不允许使用;1:可使用

            bizDataJson.put("useIntegralSet", useIntegralSet);
            bizDataJson.put("useBalanceSet", useBalanceSet);

            //可用优惠券
            HashMap<String, Object> couponMap = new HashMap<>();
            couponMap.put("couponId", 1);
            couponMap.put("couponName", "测试优惠券");
            couponMap.put("couponAmount", 5.00);
            couponMap.put("couponOrderAmount", 100.00);
            couponMap.put("couponInfo", "限商品现价满100元使用");
            couponMap.put("couponEndTime", "2021-12-31");
            List<Map<String, Object>> couponList = new ArrayList<>();
            couponList.add(couponMap);
            bizDataJson.put("couponList", couponList);

            //查询账户积分和余额信息
            if (useIntegralSet == 1 || useBalanceSet == 1) {
                //从卖座实时查询账户积分和余额
                int account_point = 15000;   //会员账户积分剩余点数，单位：点数；
                int account_money_fen = 10000;  //账户余额；单位：分
                int account_open_state = 1;    //会员账户开通状态；1=已开通，2=未开通；未开通不能进行使用余额下单
                int account_lock_state = 1;      //账户锁定状态；1=未锁定，2=锁定；锁定后，不能进行使用余额下单

                if (useIntegralSet == 1) {
                    bizDataJson.put("useIntegralValue", useIntegralValue * goodsCount);  //可使用的积分额为单个单个商品的积分额*商品数量
                    bizDataJson.put("accountPoint", useIntegralValue * goodsCount);
                }

                if (useBalanceSet == 1 && account_open_state == 1 && account_lock_state == 1) {
                    bizDataJson.put("accountMoney", account_money_fen);
                } else {
                    bizDataJson.put("useBalanceSet", 0);
                }
            }

            //查询用户可用的收货地址
            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("userId", userId);
            reqMap.put("deleteStatus", false);
            bizDataJson.put("addressList", shoppingAddressDao.queryList(reqMap));

        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 订单页面渲染（计算订单金额，返回可用优惠，考虑会员等级、优惠券、营销规则、积分）
     */
    @Override
    public JSONObject renderGoodsOrder(JSONObject reqJson, HttpServletRequest request) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "服务器内部错误！";
        JSONObject bizDataJson = new JSONObject();
        try {
//            String data = request.getParameter("data");// 参数
//            JSONObject reqJson = JSONObject.parseObject(URLDecoder.decode(data, "utf-8"));

            String userId = reqJson.getString("userId");   //userId
            String mzUserId = CommonUtil.getMzUserId(userId);   //麦座用户id
            String goodsId = reqJson.getString("goodsId");  //商品id
            int goodsCount = reqJson.getInteger("goodsCount");  //购买的商品数量

            ShoppingGoods shoppingGoods = new ShoppingGoods();
            shoppingGoods.setId(goodsId);
            //查询商品主体信息
            shoppingGoods = shoppingGoodsDao.queryDetail(shoppingGoods);
            //实时查询商品现价
            BigDecimal currentPrice = BigDecimal.ZERO;
            String specInfo = "";
            if (null != reqJson.get("propertys") && !"".equals(reqJson.get("propertys"))) {     //按指定规格属性查询
                ShoppingGoodsInventory shoppingGoodsInventory = new ShoppingGoodsInventory();
                shoppingGoodsInventory.setGoodsId(shoppingGoods.getId());
                shoppingGoodsInventory.setPropertys(reqJson.getString("propertys"));
                shoppingGoodsInventory = shoppingGoodsInventoryDao.queryDetail(shoppingGoodsInventory);
                currentPrice = shoppingGoodsInventory.getPrice();
                String[] strs = reqJson.getString("propertys").split("_");
                //拼接规格属性信息
                for (int i = 0; i < strs.length; i++) {
                    ShoppingGoodsspecproperty shoppingGoodsspecproperty = new ShoppingGoodsspecproperty();
                    shoppingGoodsspecproperty.setId(strs[i]);
                    shoppingGoodsspecproperty = shoppingGoodsspecpropertyDao.queryDetail(shoppingGoodsspecproperty);
                    specInfo += shoppingGoodsspecproperty.getValue() + ";";
                }
            } else {
                currentPrice = shoppingGoods.getStorePrice();
            }
            bizDataJson.put("currentPrice", currentPrice);  //返回实时价格

            JSONArray goodsArray = new JSONArray();
            JSONObject goodsObj = new JSONObject();
            goodsObj.put("goodsId", goodsId);
            goodsObj.put("goodsName", shoppingGoods.getGoodsName());
            goodsObj.put("currentPrice", currentPrice);
            goodsObj.put("goodsCount", goodsCount);
            goodsObj.put("photoId", shoppingGoods.getGoodsMainPhotoId());
            if (null != reqJson.get("propertys")) {
                goodsObj.put("propertys", reqJson.get("propertys"));
                goodsObj.put("specInfo", specInfo);
            }
            goodsArray.add(goodsObj);
            bizDataJson.put("goodsInfoList", goodsArray);   //返回商品信息（按数组形式，与合并支付渲染接口统一）

            //返回订单商品价格，为单价*数量
            BigDecimal goodsPrice = currentPrice.multiply(new BigDecimal(goodsCount)).setScale(2, BigDecimal.ROUND_HALF_UP);
            bizDataJson.put("goodsPrice", goodsPrice);

            //返回配送相关信息
            bizDataJson.put("goodsTransfee", shoppingGoods.getGoodsTransfee());  //返回运费承担类型0：买家承担；1：卖家承担；2：不支持快递
            bizDataJson.put("selfextractionSet", shoppingGoods.getSelfextractionSet());  //是否支持自提0:不支持;1:支持
            bizDataJson.put("selfextractionAddress", shoppingGoods.getSelfextractionAddress());  //自提地址

            //查询用户收货地址（从卖座实时获取）
            JSONObject addObj = null;
            if (null == reqJson.get("addressId") || "".equals(reqJson.get("addressId"))) {
                //没有选择地址时，取用户默认收货地址
                JSONObject addressList = MZService.getUserAddress(mzUserId, 100, 1);
                if (null != addressList) {
                    JSONObject data_list = addressList.getJSONObject("data_list");
                    if (null != data_list.get("user_address_detail_v_o")) {
                        JSONArray addressArray = data_list.getJSONArray("user_address_detail_v_o");
                        for (int i = 0; i < addressArray.size(); i++) {
                            JSONObject addressObj = addressArray.getJSONObject(i);
                            addObj = addressObj;
                            if (addressObj.getBoolean("default_address_boolean")) {
                                break;
                            }
                        }
                    }

                }
            } else {
                String address_id = reqJson.getString("addressId");
                JSONObject addressObj = MZService.getAddressDetail(mzUserId, address_id);
                addObj = addressObj;
            }
            //返回收货地址信息
            bizDataJson.put("address", addObj);

            //订单运费计算
            BigDecimal shipPrice = BigDecimal.ZERO;
            if(shoppingGoods.getGoodsTransfee() == 0&&addObj == null){
                retCode = "1";
                retMsg = "请先至”设置-收货地址“添加个人收货地址！";
            }else{
                if (shoppingGoods.getGoodsTransfee() == 0) {   //买家承担运费
                    //通过运费模板计算运费
                    if (null != shoppingGoods.getTransportId() && (shoppingGoods.getExpressTransFee() == null || shoppingGoods.getExpressTransFee().compareTo(BigDecimal.ZERO) == 0)) {
                        shipPrice = buildShipPrice(shoppingGoods, goodsCount,addObj);
                    } else {
                        shipPrice = shoppingGoods.getExpressTransFee() == null ? BigDecimal.ZERO : shoppingGoods.getExpressTransFee();
                    }
                }

                //查询并返回积分、余额等使用条件
                int useIntegralSet = shoppingGoods.getUseIntegralSet();   //使用积分抵扣设置 0:不允许使用积分抵扣;1:可使用部分积分抵扣
                int useIntegralValue = shoppingGoods.getUseIntegralValue();    //单个商品积分抵扣值
                int useBalanceSet = shoppingGoods.getUseBalanceSet();   //使用余额支付设置 0:不允许使用;1:可使用
                int useMembershipSet = shoppingGoods.getUseMembershipSet();   //是否支持会员权益 0:不支持;1:支持
                bizDataJson.put("useMembershipSet", useMembershipSet);
                bizDataJson.put("useIntegralSet", useIntegralSet);
                bizDataJson.put("useBalanceSet", useBalanceSet);
                //查询账户积分和余额信息
                boolean accountState = true;
                if (useIntegralSet != 0 || useBalanceSet != 0) {
                    //从卖座实时查询账户积分和余额
                    JSONObject accountObj = MZService.getAssetinfo(mzUserId);
                    if (null != accountObj) {
                        int account_point = accountObj.get("account_point") == null ? 0 : accountObj.getInteger("account_point");   //会员账户积分剩余点数，单位：点数；
                        int account_money_fen = accountObj.get("account_money_fen") == null ? 0 : accountObj.getInteger("account_money_fen");
                        //账户余额；单位：分
                        BigDecimal accountMoney = new BigDecimal(account_money_fen).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);  //分转换为元
                        bizDataJson.put("accountPoint", account_point);
                        bizDataJson.put("accountMoney", accountMoney);
                    } else {
                        accountState = false;
                        bizDataJson.put("accountPoint", 0);
                        bizDataJson.put("accountMoney", 0);
                    }

                    //查询积分和余额免密限额
                    ShoppingAssetRule shoppingAssetRule = CommonUtil.getAssetRule();
                    if (null != shoppingAssetRule) {
                        bizDataJson.put("accountPointLimit", shoppingAssetRule.getPointAvoidLimit());
                        bizDataJson.put("accountMoneyLimit", shoppingAssetRule.getAccountAvoidLimit());
                    }
                }
                if (!accountState) {
                    retCode = "-1";
                    retMsg = "无法获取您的账户积分和余额数据，请稍后再试！";
                } else {
                    //开始计算订单优惠和支付金额
                    //商品费用
                    BigDecimal totalPrice = currentPrice.multiply(new BigDecimal(goodsCount));
                    //订单总金额为商品费用+运费(用户自提时不需要运费)
                    if (null == reqJson.get("transport") || "".equals(reqJson.get("transport"))) {
                        if (shoppingGoods.getGoodsTransfee() == 0) {   //买家承担运费
                            totalPrice = currentPrice.multiply(new BigDecimal(goodsCount)).add(shipPrice);
                        }
                    } else if (reqJson.get("transport").equals("快递")) {
                        totalPrice = currentPrice.multiply(new BigDecimal(goodsCount)).add(shipPrice);
                    }else{
                        shipPrice = BigDecimal.ZERO;
                    }
                    //useIntegralSet == 1时为定额积分抵扣，需要特殊处理，先获取到定额积分和剩下的现金支付的数额
                    BigDecimal cashPrice = BigDecimal.ZERO;
                    int fixedIntegalValue = 0;
                    if (useIntegralSet == 1) {
                        if (currentPrice.multiply(new BigDecimal((100))).intValue() < useIntegralValue) {
                            useIntegralValue = currentPrice.multiply(new BigDecimal((100))).intValue();
                        }
                        fixedIntegalValue = useIntegralValue * goodsCount;
                        //现价减去定额积分抵扣的额度，即为剩下的需要支付的现金额度
                        cashPrice = currentPrice.multiply(new BigDecimal(goodsCount)).subtract(new BigDecimal(fixedIntegalValue).divide(new BigDecimal(moneyToIntegralScale)).setScale(2, BigDecimal.ROUND_HALF_UP));
                    }

                    //计算优惠时只在商品费用的基础上进行计算
                    BigDecimal payPrice = BigDecimal.ZERO;   //现金支付金额
                    if (useIntegralSet == 1) {
                        payPrice = cashPrice.multiply(new BigDecimal(goodsCount));
                    } else {
                        payPrice = currentPrice.multiply(new BigDecimal(goodsCount));
                    }

                    //查询商品可用的可用优惠券
                    List<String> couponIds = new ArrayList<>();
                    JSONArray couponArray = CommonUtil.getGoodsCouppon(goodsId, 1, userId, goodsPrice, payPrice);
                    for (int i = 0; i < couponArray.size(); i++) {
                        JSONObject obj = couponArray.getJSONObject(i);
                        couponIds.add(obj.getString("id"));
                    }
                    //返回可用优惠券列表
                    bizDataJson.put("couponList", couponArray);

                    BigDecimal originPrice = payPrice;
                    int originntegalValue = fixedIntegalValue;

                    BigDecimal couponCut = BigDecimal.ZERO;  //优惠券折扣
                    BigDecimal accountCut = BigDecimal.ZERO;  //会员体系折扣
                    BigDecimal integralCut = BigDecimal.ZERO;  //积分抵扣
                    BigDecimal balanceCut = BigDecimal.ZERO;  //余额抵扣

                    //用户选择使用优惠券
                    if (null != reqJson.get("couponId") && !"".equals(reqJson.get("couponId"))) {
                        String couponId = reqJson.getString("couponId");
                        //校验用户选择的优惠券是否在可用优惠券列表中
                        if (couponIds.contains(couponId)) {
                            //获取优惠券信息
                            //获取用户选择的优惠券详情
                            JSONObject couponDtl = CRMService.getUserCouponDtl(couponId);
                            if (null != couponDtl) {
                                String right_No = couponDtl.getString("right_No");
                                ShoppingCoupon shoppingCoupon = new ShoppingCoupon();
                                shoppingCoupon.setRight_No(right_No);
                                shoppingCoupon = shoppingCouponDao.queryDetail(shoppingCoupon);

                                //如果是代金券，直接抵扣相应金额
                                if (shoppingCoupon.getRight_Type().equals("coincp")) {
                                    int couponAmount = Integer.valueOf(shoppingCoupon.getRight_Content());
                                    if (payPrice.compareTo(new BigDecimal(couponAmount)) < 0) {
                                        payPrice = BigDecimal.ZERO;
                                    } else {
                                        if (new BigDecimal(couponAmount).compareTo(payPrice) == 1) {
                                            payPrice = BigDecimal.ZERO;
                                        } else {
                                            payPrice = payPrice.subtract(new BigDecimal(couponAmount));
                                        }

                                    }
                                } else {
                                    //如果是折扣优惠券，需要计算折扣
                                    BigDecimal discount = new BigDecimal(shoppingCoupon.getRight_Content());
                                    payPrice = payPrice.multiply(discount).setScale(2, BigDecimal.ROUND_HALF_UP);
                                    //如果是定额积分抵扣的情况，那么定额积分值也要参与折扣
                                    if (useIntegralSet == 1) {
                                        fixedIntegalValue = new BigDecimal(fixedIntegalValue).multiply(discount).intValue();
                                    }
                                }
                                //计算优惠券抵扣的金额
                                if (useIntegralSet == 1) {
                                    //现金和积分各自抵扣的部分相加
                                    couponCut = originPrice.subtract(payPrice).add(new BigDecimal(originntegalValue - fixedIntegalValue).divide(new BigDecimal(moneyToIntegralScale)).setScale(2, BigDecimal.ROUND_HALF_UP));
                                } else {
                                    couponCut = originPrice.subtract(payPrice);
                                }
                                originPrice = payPrice;
                                originntegalValue = fixedIntegalValue;
                            }
                        }
                    }

                    //会员等级折扣计算
                    if (useMembershipSet == 1) {
                        BigDecimal discount = CommonUtil.getUserMemberShip(userId);
                        payPrice = payPrice.multiply(discount).setScale(2, BigDecimal.ROUND_HALF_UP);
                        if (useIntegralSet == 1) {
                            fixedIntegalValue = new BigDecimal(fixedIntegalValue).multiply(discount).intValue();
                        }
                        //会员等级抵扣的金额
                        if (useIntegralSet == 1) {
                            accountCut = originPrice.subtract(payPrice).add(new BigDecimal(originntegalValue - fixedIntegalValue).divide(new BigDecimal(moneyToIntegralScale)).setScale(2, BigDecimal.ROUND_HALF_UP));
                        } else {
                            accountCut = originPrice.subtract(payPrice);
                        }
                        originPrice = payPrice;
                        originntegalValue = fixedIntegalValue;
                    }

                    //获取积分和余额单次支付的上限配置
                    ShoppingPayLimit payLimit = CommonUtil.getPayLimit();
                    bizDataJson.put("pointPayLimit", payLimit.getPointPay());    //积分单次支付上限
                    bizDataJson.put("balancePayLimit", payLimit.getBalancePay());      //余额单次支付上限
                    int maxIntegralValue = 0;   //需要支付的积分数额
                    int accountPoint = bizDataJson.get("accountPoint")==null?0:bizDataJson.getInteger("accountPoint");
                    int pointPay = payLimit.getPointPay();  //后台设置的积分单次支付限额
                    //用户选择使用积分支付,或者该商品为定额积分支付
                    if (useIntegralSet == 1 || (null != reqJson.get("useIntegral") && reqJson.getInteger("useIntegral") == 1)) {
                        if (useIntegralSet == 1) {  //限额抵扣时，用户需支付固定额度的积分
                            maxIntegralValue = fixedIntegalValue;
                        } else if (useIntegralSet == 2) {
                            //不限额积分抵扣，当前商品可抵扣的积分上限为单个商品积分上限useIntegralValue * goodsCount，与还需支付的payPrice比较，取两者的小值
                            int payPriceToInt =payPrice.multiply(new BigDecimal(moneyToIntegralScale)).intValue();
                            maxIntegralValue = useIntegralValue * goodsCount<payPriceToInt?useIntegralValue * goodsCount:payPriceToInt;
                            int pointLimit = accountPoint;   //账户积分
                            if(pointPay>0){
                                pointLimit = pointPay>pointLimit?pointLimit:pointPay;
                            }
                            if (maxIntegralValue > pointLimit) {
                                maxIntegralValue = pointLimit;
                            }
                        }
                        //返回用户需要支付的积分数额
                        bizDataJson.put("useIntegralValue", maxIntegralValue);
                        //将积分根据比例转换为相应的金额
                        BigDecimal integralAmount = (new BigDecimal(maxIntegralValue).divide(new BigDecimal(moneyToIntegralScale))).setScale(2, BigDecimal.ROUND_HALF_UP);
                        if (useIntegralSet == 1) {
                            integralCut = integralAmount;
                        } else {
                            payPrice = payPrice.subtract(integralAmount);
                            integralCut = originPrice.subtract(payPrice);
                        }
                    }

                    if (maxIntegralValue > accountPoint) {
                        retCode = "1";
                        retMsg = "当前账户积分不够，无法下单！";
                    }else if(maxIntegralValue >pointPay){
                        retCode = "1";
                        retMsg = "积分单次支付限额"+pointPay+",当前订单已超出该额度，无法下单！";
                    }else{
                        //运费可以用余额支付，因此在计算余额抵扣时，加上运费
                        payPrice = payPrice.add(shipPrice);
                        originPrice = payPrice;
                        //用户选择扣除的余额
                        if (null != reqJson.get("useBalance") && reqJson.getInteger("useBalance") == 1) {
                            BigDecimal deductionBalance = BigDecimal.ZERO;
                            BigDecimal accountLimit = bizDataJson.getBigDecimal("accountMoney");
                            BigDecimal balancePay = new BigDecimal(payLimit.getBalancePay());   //余额单次支付上限
                            if(balancePay.compareTo(BigDecimal.ZERO)>0&&balancePay.compareTo(accountLimit)<0){    //余额支付上限为账户余额和支付限额两者中的较小值
                                accountLimit = balancePay;
                            }
                            if (payPrice.compareTo(accountLimit) == 1) {
                                deductionBalance = accountLimit;
                            } else {
                                deductionBalance = payPrice;
                            }

                            payPrice = payPrice.subtract(deductionBalance);
                            balanceCut = originPrice.subtract(payPrice);
                        }

                        bizDataJson.put("shipPrice", shipPrice);
                        bizDataJson.put("totalPrice", totalPrice);       //订单金额（商品费用+运费）
                        bizDataJson.put("payPrice", payPrice);              //还需支付的现金金额
                        bizDataJson.put("deductionCouponPrice", couponCut);      //优惠券抵扣金额
                        bizDataJson.put("deductionMemberPrice", accountCut);     //会员权益抵扣金额
                        bizDataJson.put("deductionIntegralPrice", integralCut);  //积分抵扣金额
                        bizDataJson.put("deductionBalancePrice", balanceCut);    //账户余额抵扣金额

                        retCode = "0";
                        retMsg = "操作成功！";
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

    /**
     * 订单页面渲染（计算订单金额，返回可用优惠，考虑会员等级、优惠券、营销规则、积分）
     */
    @Override
    public JSONObject renderGoodsOrder_new(JSONObject reqJson, HttpServletRequest request) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "服务器内部错误！";
        JSONObject bizDataJson = new JSONObject();
        try {

            String userId = reqJson.getString("userId");   //userId
            String mzUserId = CommonUtil.getMzUserId(userId);   //麦座用户id
            String goodsId = reqJson.getString("goodsId");  //商品id
            int goodsCount = reqJson.getInteger("goodsCount");  //购买的商品数量
            //商品规格信息
            String propertys = reqJson.get("propertys")==null?null:reqJson.getString("propertys");
            //拼接规格属性信息
            String specInfo = propertys==null?"":shoppingOrderUtil.getGoodsSpecInfo(propertys);
            //收货地址
            String addressId = reqJson.get("addressId")==null?null:reqJson.getString("addressId");
            //收货方式
            String transport = reqJson.get("transport")==null?null:reqJson.getString("transport");
            //优惠券id
            String couponId = reqJson.get("couponId")==null?null:reqJson.getString("couponId");
            //用户是否打开积分抵扣开关
            boolean useIntegral = (null != reqJson.get("useIntegral") && reqJson.getInteger("useIntegral") == 1)?true:false;
            //用户是否打开余额抵扣开关
            boolean useBalance = (null != reqJson.get("useBalance") && reqJson.getInteger("useBalance") == 1)?true:false;

            //查询商品主体信息
            ShoppingGoods shoppingGoods = new ShoppingGoods();
            shoppingGoods.setId(goodsId);
            shoppingGoods = shoppingGoodsDao.queryDetail(shoppingGoods);

            //从卖座实时查询账户积分和余额，麦座账户信息为必须，否则无法计算相关订单金额
            UserAccountInfo userAccountInfo=accountInfoUtil.getUserAccountInfo(mzUserId);
            if(!userAccountInfo.getAccountEnable()){
                retMsg = "无法获取您的账户积分和余额数据，请稍后再试！";
            }else{
                    /*
                    开始处理订单渲染数据，计算订单各类金额
                    */
//                ShoppingOrderInfo shoppingOrderInfo = new ShoppingOrderInfo(userId,mzUserId,userAccountInfo,couponId,useIntegral,useBalance);
                SimpleOrdernfo shoppingOrderInfo = new SimpleOrdernfo(userId,mzUserId,goodsCount,propertys,useIntegral,useBalance);
                shoppingOrderInfo.setCouponId(couponId);
                shoppingOrderInfo.setTransport(transport);
                shoppingOrderInfo.setAddressId(addressId);
                shoppingOrderInfo =shoppingOrderUtil.countCulOrderAmount(shoppingGoods,shoppingOrderInfo,userAccountInfo);

                if(shoppingOrderInfo.getCountSuccess()){
                    /*
                处理需要返回给移动端的订单渲染数据
                */
                    //商品信息（按数组形式，与合并支付渲染接口统一）
                    JSONObject goodsObj = new JSONObject();
                    goodsObj.put("goodsId", goodsId);
                    goodsObj.put("goodsName", shoppingGoods.getGoodsName());
                    goodsObj.put("currentPrice", shoppingOrderInfo.getCurrentPrice());
                    goodsObj.put("goodsCount", goodsCount);
                    goodsObj.put("photoId", shoppingGoods.getGoodsMainPhotoId());
                    if (StringUtil.isNotNull(propertys)) {
                        goodsObj.put("propertys", propertys);
                        goodsObj.put("specInfo", specInfo);
                    }
                    JSONArray goodsArray = new JSONArray();
                    goodsArray.add(goodsObj);
                    bizDataJson.put("goodsInfoList", goodsArray);
                    //实时价格
                    bizDataJson.put("currentPrice", shoppingOrderInfo.getCurrentPrice());
                    //配送相关信息
                    bizDataJson.put("goodsTransfee", shoppingGoods.getGoodsTransfee());  //返回运费承担类型0：买家承担；1：卖家承担；2：不支持快递
                    bizDataJson.put("selfextractionSet", shoppingGoods.getSelfextractionSet());  //是否支持自提0:不支持;1:支持
                    bizDataJson.put("selfextractionAddress", shoppingGoods.getSelfextractionAddress());  //自提地址
                    // 收货地址信息
                    bizDataJson.put("address", shoppingOrderInfo.getAddObj());

                    //账户积分与余额
                    bizDataJson.put("accountPoint", userAccountInfo.getAccount_point());
                    bizDataJson.put("accountMoney", userAccountInfo.getAccountMoney());
                    //积分和余额免密限额
                    ShoppingAssetRule shoppingAssetRule = CommonUtil.getAssetRule();
                    bizDataJson.put("accountPointLimit", shoppingAssetRule.getPointAvoidLimit());
                    bizDataJson.put("accountMoneyLimit", shoppingAssetRule.getAccountAvoidLimit());
                    //积分和余额单次支付的上限配置
                    ShoppingPayLimit payLimit = CommonUtil.getPayLimit();
                    bizDataJson.put("pointPayLimit", payLimit.getPointPay());    //积分单次支付上限
                    bizDataJson.put("balancePayLimit", payLimit.getBalancePay());      //余额单次支付上限

                    //可用优惠券列表
                    bizDataJson.put("couponList", shoppingOrderInfo.getCouponArray());

                    //商品各优惠开关
                    bizDataJson.put("useMembershipSet", shoppingGoods.getUseMembershipSet());
                    bizDataJson.put("useIntegralSet", shoppingGoods.getUseIntegralSet());
                    bizDataJson.put("useBalanceSet", shoppingGoods.getUseBalanceSet());

                    bizDataJson.put("useIntegralValue", shoppingOrderInfo.getIntegralValue()); //用户需要支付的积分数额
                    bizDataJson.put("goodsPrice", shoppingOrderInfo.getGoodsAmount());//返回订单商品价格，为单价*数量
                    bizDataJson.put("shipPrice", shoppingOrderInfo.getShipAmount());//返回订单运费
                    bizDataJson.put("totalPrice", shoppingOrderInfo.getTotalAmount());       //订单金额（商品费用+运费）
                    bizDataJson.put("payPrice", shoppingOrderInfo.getPayAmount());              //还需支付的现金金额
                    bizDataJson.put("deductionCouponPrice", shoppingOrderInfo.getCouponCut());      //优惠券抵扣金额
                    bizDataJson.put("deductionMemberPrice", shoppingOrderInfo.getAccountCut());     //会员权益抵扣金额
                    bizDataJson.put("deductionIntegralPrice", shoppingOrderInfo.getIntegralCut());  //积分抵扣金额
                    bizDataJson.put("deductionBalancePrice", shoppingOrderInfo.getBalanceCut());    //账户余额抵扣金额

                    retCode = "0";
                    retMsg = "操作成功！";
                }else{
                    retMsg = shoppingOrderInfo.getErrorMsg();
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

    /**
     * 订单页面渲染（积分商城）
     */
    @Override
    public JSONObject renderInregraGoodsOrder(JSONObject reqJson, HttpServletRequest request) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "服务器内部错误！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String userId = reqJson.getString("userId");   //userId
            String mzUserId = CommonUtil.getMzUserId(userId);
            String goodsId = reqJson.getString("goodsId");  //商品id
            int goodsCount = reqJson.getInteger("goodsCount");  //购买的商品数量

            ShoppingGoods shoppingGoods = new ShoppingGoods();
            shoppingGoods.setId(goodsId);
            //查询商品主体信息
            shoppingGoods = shoppingGoodsDao.queryDetail(shoppingGoods);
            //实时查询商品现价
            BigDecimal currentPrice = BigDecimal.ZERO;
            String specInfo = "";
            if (null != reqJson.get("propertys") && !"".equals(reqJson.get("propertys"))) {
                ShoppingGoodsInventory shoppingGoodsInventory = new ShoppingGoodsInventory();
                shoppingGoodsInventory.setGoodsId(shoppingGoods.getId());
                shoppingGoodsInventory.setPropertys(reqJson.getString("propertys"));
                shoppingGoodsInventory = shoppingGoodsInventoryDao.queryDetail(shoppingGoodsInventory);
                currentPrice = shoppingGoodsInventory.getPrice();
                String[] strs = reqJson.getString("propertys").split("_");
                for (int i = 0; i < strs.length; i++) {
                    ShoppingGoodsspecproperty shoppingGoodsspecproperty = new ShoppingGoodsspecproperty();
                    shoppingGoodsspecproperty.setId(strs[i]);
                    shoppingGoodsspecproperty = shoppingGoodsspecpropertyDao.queryDetail(shoppingGoodsspecproperty);
                    specInfo += shoppingGoodsspecproperty.getValue() + ";";
                }
            } else {
                currentPrice = shoppingGoods.getStorePrice();
            }
            bizDataJson.put("currentPrice", currentPrice);  //返回实时价格

            JSONArray goodsArray = new JSONArray();
            JSONObject goodsObj = new JSONObject();
            goodsObj.put("goodsId", goodsId);
            goodsObj.put("goodsName", shoppingGoods.getGoodsName());
            goodsObj.put("currentPrice", currentPrice);
            goodsObj.put("goodsCount", goodsCount);
            goodsObj.put("photoId", shoppingGoods.getGoodsMainPhotoId());
            if (null != reqJson.get("propertys")) {
                goodsObj.put("propertys", reqJson.get("propertys"));
                goodsObj.put("specInfo", specInfo);
            }

            goodsArray.add(goodsObj);
            bizDataJson.put("goodsInfoList", goodsArray);

            //订单商品价格为单价*数量
            bizDataJson.put("goodsPrice", currentPrice.multiply(new BigDecimal(goodsCount)).setScale(2, BigDecimal.ROUND_HALF_UP));

            bizDataJson.put("goodsTransfee", shoppingGoods.getGoodsTransfee());  //运费承担类型0：买家承担；1：卖家承担；2：不支持快递
            bizDataJson.put("selfextractionSet", shoppingGoods.getSelfextractionSet());  //是否支持自提0:不支持;1:支持
            bizDataJson.put("selfextractionAddress", shoppingGoods.getSelfextractionAddress());  //自提地址

            //查询用户收货地址
            JSONObject addObj = null;
            if (null == reqJson.get("addressId") || "".equals(reqJson.get("addressId"))) {
                //获取用户默认收货地址
                JSONObject addressList = MZService.getUserAddress(mzUserId, 100, 1);
                if (null != addressList) {
                    JSONObject data_list = addressList.getJSONObject("data_list");
                    if (null != data_list.get("user_address_detail_v_o")) {
                        JSONArray addressArray = data_list.getJSONArray("user_address_detail_v_o");
                        for (int i = 0; i < addressArray.size(); i++) {
                            JSONObject addressObj = addressArray.getJSONObject(i);
                            addObj = addressObj;
                            if (addressObj.getBoolean("default_address_boolean")) {
                                break;
                            }
                        }
                    }

                }
            } else {
                String address_id = reqJson.getString("addressId");

                JSONObject addressObj = MZService.getAddressDetail(mzUserId, address_id);
                addObj = addressObj;
//                orderRenderParam.setAddressId(reqJson.getString("addressId"));
            }
            bizDataJson.put("address", addObj);

//            HashMap<String, Object> reqMap = new HashMap<>();
//            reqMap.put("userId", userId);
//            reqMap.put("deleteStatus", false);
//            bizDataJson.put("addressList", shoppingAddressDao.queryList(reqMap));

            //订单运费计算
            BigDecimal shipPrice = BigDecimal.ZERO;
            if(shoppingGoods.getGoodsTransfee() == 0&&addObj == null){
                retCode = "1";
                retMsg = "请先至”设置-收货地址“添加个人收货地址！";
            }else{
                if (shoppingGoods.getGoodsTransfee() == 0) {   //买家承担运费
                    //通过运费模板计算运费
                    if (null != shoppingGoods.getTransportId() && (shoppingGoods.getExpressTransFee() == null || shoppingGoods.getExpressTransFee().compareTo(BigDecimal.ZERO) == 0)) {
                        shipPrice = buildShipPrice(shoppingGoods, goodsCount,addObj);
                    } else {
                        shipPrice = shoppingGoods.getExpressTransFee() == null ? BigDecimal.ZERO : shoppingGoods.getExpressTransFee();
                    }
                }

                int useBalanceSet = shoppingGoods.getUseBalanceSet();   //使用余额支付设置 0:不允许使用;1:可使用
                int useMembershipSet = shoppingGoods.getUseMembershipSet();   //是否支持会员权益 0:不支持;1:支持
                bizDataJson.put("useMembershipSet", useMembershipSet);
                bizDataJson.put("useBalanceSet", useBalanceSet);
                //查询账户积分和余额信息
                boolean accountState = true;

                //从卖座实时查询账户积分和余额
                JSONObject accountObj = MZService.getAssetinfo(mzUserId);
                if (null != accountObj) {
                    int account_point = accountObj.get("account_point") == null ? 0 : accountObj.getInteger("account_point");   //会员账户积分剩余点数，单位：点数；
                    int account_money_fen = accountObj.get("account_money_fen") == null ? 0 : accountObj.getInteger("account_money_fen");
                    ;  //账户余额；单位：分
                    BigDecimal accountMoney = new BigDecimal(account_money_fen).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);  //分转换为元
                    bizDataJson.put("accountPoint", account_point);
                    bizDataJson.put("accountMoney", accountMoney);
                } else {
                    accountState = false;
                }

                //查询积分和余额免密限额
                ShoppingAssetRule shoppingAssetRule = CommonUtil.getAssetRule();
                if (null != shoppingAssetRule) {
                    bizDataJson.put("accountPointLimit", shoppingAssetRule.getPointAvoidLimit());
                    bizDataJson.put("accountMoneyLimit", shoppingAssetRule.getAccountAvoidLimit());
                }


                if (!accountState) {
                    retCode = "-1";
                    retMsg = "无法获取您的账户积分和余额数据，请稍后再试！";
                } else {
                    //开始计算订单优惠和支付金额
                    BigDecimal totalPrice = currentPrice.multiply(new BigDecimal(goodsCount));
                    //订单总金额为商品费用+运费(用户自提时不需要运费)
                    if (null == reqJson.get("transport") || "".equals(reqJson.get("transport"))) {
                        if (shoppingGoods.getGoodsTransfee() == 0) {   //买家承担运费
                            totalPrice = currentPrice.multiply(new BigDecimal(goodsCount)).add(shipPrice);
                        }
                    } else if (reqJson.get("transport").equals("快递")) {
                        totalPrice = currentPrice.multiply(new BigDecimal(goodsCount)).add(shipPrice);
                    }else{
                        shipPrice=BigDecimal.ZERO;
                    }
                    //计算优惠时只在商品费用的基础上进行计算
                    BigDecimal payPrice = currentPrice.multiply(new BigDecimal(goodsCount));
                    BigDecimal originPrice = payPrice;

                    BigDecimal accountCut = BigDecimal.ZERO;  //会员体系折扣
                    BigDecimal integralCut = BigDecimal.ZERO;  //积分抵扣
                    BigDecimal balanceCut = BigDecimal.ZERO;  //余额抵扣

                    //会员等级折扣
                    if (useMembershipSet == 1) {
                        BigDecimal discount = CommonUtil.getUserMemberShip(userId);
                        payPrice = payPrice.multiply(discount).setScale(2, BigDecimal.ROUND_HALF_UP);
                        //会员等级抵扣的金额
                        accountCut = originPrice.subtract(payPrice);
                    }

                    //获取积分和余额单次支付的上限配置
                    ShoppingPayLimit payLimit = CommonUtil.getPayLimit();
                    bizDataJson.put("pointPayLimit", payLimit.getPointPay());    //积分单次支付上限
                    bizDataJson.put("balancePayLimit", payLimit.getBalancePay());      //余额单次支付上限

                    //用户需要支付的积分额是固定的
                    int maxIntegralValue = payPrice.multiply(new BigDecimal(moneyToIntegralScale)).intValue();
                    int accountPoint = bizDataJson.get("accountPoint")==null?0:bizDataJson.getInteger("accountPoint");
                    int pointPay = payLimit.getPointPay();  //后台设置的积分单次支付限额
                    if (maxIntegralValue > accountPoint) {
                        retCode = "1";
                        retMsg = "当前账户积分不够，无法下单！";
                    }else if(maxIntegralValue >pointPay){
                        retCode = "1";
                        retMsg = "积分单次支付限额"+pointPay+",当前订单已超出该额度，无法下单！";
                    }else{
                        bizDataJson.put("useIntegralValue", maxIntegralValue);
                        integralCut = payPrice;

                        //运费可以用余额支付，因此如果存在运费，需要用余额或现金支付
                        payPrice = shipPrice;
                        originPrice = payPrice;
                        //用户选择扣除的余额
                        if (null != reqJson.get("useBalance") && reqJson.getInteger("useBalance") == 1) {
                            BigDecimal deductionBalance = BigDecimal.ZERO;
                            BigDecimal accountLimit = bizDataJson.getBigDecimal("accountMoney");
                            BigDecimal balancePay = new BigDecimal(payLimit.getBalancePay());   //余额单次支付上限
                            if(balancePay.compareTo(BigDecimal.ZERO)>0&&balancePay.compareTo(accountLimit)<0){    //余额支付上限为账户余额和支付限额两者中的较小值
                                accountLimit = balancePay;
                            }
                            if (payPrice.compareTo(accountLimit) == 1) {
                                deductionBalance = accountLimit;
                            } else {
                                deductionBalance = payPrice;
                            }

                            payPrice = payPrice.subtract(deductionBalance);
                            balanceCut = originPrice.subtract(payPrice);
                        }

                        bizDataJson.put("shipPrice", shipPrice);
                        bizDataJson.put("totalPrice", totalPrice);       //订单金额（商品费用+运费）
                        bizDataJson.put("payPrice", payPrice);              //还需支付的现金金额
                        bizDataJson.put("deductionMemberPrice", accountCut);     //会员权益抵扣金额
                        bizDataJson.put("deductionIntegralPrice", integralCut);  //积分抵扣金额
                        bizDataJson.put("deductionBalancePrice", balanceCut);    //账户余额抵扣金额

                        retCode = "0";
                        retMsg = "操作成功！";
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

    /**
     * 订单页面渲染（积分商城）
     */
//    @Override
//    public JSONObject renderInregraGoodsOrder_new(JSONObject reqJson, HttpServletRequest request) {
//        JSONObject retJson = new JSONObject();
//        String retCode = "1";
//        String retMsg = "服务器内部错误！";
//        JSONObject bizDataJson = new JSONObject();
//        try {
//
//            String userId = reqJson.getString("userId");   //userId
//            String mzUserId = CommonUtil.getMzUserId(userId);
//            String goodsId = reqJson.getString("goodsId");  //商品id
//            int goodsCount = reqJson.getInteger("goodsCount");  //购买的商品数量
//            //商品规格信息
//            String propertys = reqJson.get("propertys")==null?null:reqJson.getString("propertys");
//            //拼接规格属性信息
//            String specInfo = propertys==null?"":shoppingOrderUtil.getGoodsSpecInfo(propertys);
//            //收货地址
//            String addressId = reqJson.get("addressId")==null?null:reqJson.getString("addressId");
//            //收货方式
//            String transport = reqJson.get("transport")==null?null:reqJson.getString("transport");
//            //用户是否打开余额抵扣开关
//            boolean useBalance = (null != reqJson.get("useBalance") && reqJson.getInteger("useBalance") == 1)?true:false;
//
//            ShoppingGoods shoppingGoods = new ShoppingGoods();
//            shoppingGoods.setId(goodsId);
//            //查询商品主体信息
//            shoppingGoods = shoppingGoodsDao.queryDetail(shoppingGoods);
//
//            //从卖座实时查询账户积分和余额，麦座账户信息为必须，否则无法计算相关订单金额
//            UserAccountInfo userAccountInfo=accountInfoUtil.getUserAccountInfo(mzUserId);
//            if(!userAccountInfo.getAccountEnable()){
//                retMsg = "无法获取您的账户积分和余额数据，请稍后再试！";
//            }else{
//                    /*
//                    开始处理订单渲染数据，计算订单各类金额
//                    */
////                ShoppingOrderInfo shoppingOrderInfo = new ShoppingOrderInfo(userId,mzUserId,userAccountInfo,null,true,useBalance);
//                SimpleOrdernfo shoppingOrderInfo = new SimpleOrdernfo(userId,mzUserId,goodsCount,propertys,true,useBalance);
////                shoppingOrderInfo.setShoppingGoods(shoppingGoods);
////                shoppingOrderInfo.setGoodsCount(goodsCount);
////                shoppingOrderInfo.setPropertys(propertys);
//                shoppingOrderInfo.setTransport(transport);
//                shoppingOrderInfo.setAddressId(addressId);
//                shoppingOrderInfo =shoppingOrderUtil.countIntOrderAmount(shoppingGoods,shoppingOrderInfo,userAccountInfo);
//
//                if(shoppingOrderInfo.getCountSuccess()){
//                    /*
//                处理需要返回给移动端的订单渲染数据
//                */
//                    //商品信息（按数组形式，与合并支付渲染接口统一）
//                    JSONArray goodsArray = new JSONArray();
//                    JSONObject goodsObj = new JSONObject();
//                    goodsObj.put("goodsId", goodsId);
//                    goodsObj.put("goodsName", shoppingGoods.getGoodsName());
//                    goodsObj.put("currentPrice", shoppingOrderInfo.getCurrentPrice());
//                    goodsObj.put("goodsCount", goodsCount);
//                    goodsObj.put("photoId", shoppingGoods.getGoodsMainPhotoId());
//                    if (StringUtil.isNotNull(propertys)) {
//                        goodsObj.put("propertys", propertys);
//                        goodsObj.put("specInfo", specInfo);
//                    }
//                    goodsArray.add(goodsObj);
//                    bizDataJson.put("goodsInfoList", goodsArray);
//
//                    bizDataJson.put("currentPrice", shoppingOrderInfo.getCurrentPrice());  //返回实时价格
//                    //配送相关信息
//                    bizDataJson.put("goodsTransfee", shoppingGoods.getGoodsTransfee());  //运费承担类型0：买家承担；1：卖家承担；2：不支持快递
//                    bizDataJson.put("selfextractionSet", shoppingGoods.getSelfextractionSet());  //是否支持自提0:不支持;1:支持
//                    bizDataJson.put("selfextractionAddress", shoppingGoods.getSelfextractionAddress());  //自提地址
//                    // 收货地址信息
//                    bizDataJson.put("address", shoppingOrderInfo.getAddObj());
//
//                    //账户积分与余额
//                    bizDataJson.put("accountPoint", userAccountInfo.getAccount_point());
//                    bizDataJson.put("accountMoney", userAccountInfo.getAccountMoney());
//                    //积分和余额免密限额
//                    ShoppingAssetRule shoppingAssetRule = CommonUtil.getAssetRule();
//                    bizDataJson.put("accountPointLimit", shoppingAssetRule.getPointAvoidLimit());
//                    bizDataJson.put("accountMoneyLimit", shoppingAssetRule.getAccountAvoidLimit());
//                    //积分和余额单次支付的上限配置
//                    ShoppingPayLimit payLimit = CommonUtil.getPayLimit();
//                    bizDataJson.put("pointPayLimit", payLimit.getPointPay());    //积分单次支付上限
//                    bizDataJson.put("balancePayLimit", payLimit.getBalancePay());      //余额单次支付上限
//
//                    //商品各优惠开关
//                    bizDataJson.put("useMembershipSet", shoppingGoods.getUseMembershipSet());
//                    bizDataJson.put("useBalanceSet", shoppingGoods.getUseBalanceSet());
//
//                    //订单商品价格为单价*数量
//                    bizDataJson.put("goodsPrice", shoppingOrderInfo.getGoodsAmount());
//                    bizDataJson.put("shipPrice", shoppingOrderInfo.getShipAmount());
//                    bizDataJson.put("totalPrice", shoppingOrderInfo.getTotalAmount());       //订单金额（商品费用+运费）
//                    bizDataJson.put("payPrice", shoppingOrderInfo.getPayAmount());              //还需支付的现金金额
//                    bizDataJson.put("deductionMemberPrice", shoppingOrderInfo.getAccountCut());     //会员权益抵扣金额
//                    bizDataJson.put("deductionIntegralPrice", shoppingOrderInfo.getIntegralCut());  //积分抵扣金额
//                    bizDataJson.put("deductionBalancePrice", shoppingOrderInfo.getBalanceCut());    //账户余额抵扣金额
//
//                    retCode = "0";
//                    retMsg = "操作成功！";
//                }else{
//                    retMsg = shoppingOrderInfo.getErrorMsg();
//                }
//            }
//        } catch (Exception e) {
//            log.error(e);
//        }
//        retJson.put("retCode", retCode);
//        retJson.put("retMsg", retMsg);
//        retJson.put("bizData", bizDataJson);
//        return retJson;
//    }

    /**
     * 创建订单（直接下单）
     */
    @Override
    public JSONObject addOrder(JSONObject reqJson, HttpServletRequest request) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "服务器内部错误！";
        JSONObject bizDataJson = new JSONObject();
        try {
//            String data = request.getParameter("data");// 参数
//            JSONObject reqJson = JSONObject.parseObject(URLDecoder.decode(data, "utf-8"));

            String userId = reqJson.getString("userId");//userId
            String mzUserId = CommonUtil.getMzUserId(userId);
            BigDecimal orderTotalPrice = reqJson.getBigDecimal("orderTotalPrice");//订单总金额（商品费用+运费）
            BigDecimal orderShipPrice = reqJson.getBigDecimal("orderShipPrice");//运费金额
            BigDecimal orderPayPrice = reqJson.getBigDecimal("orderPayPrice");//待支付的现金金额
            int orderUseIntegralValue = reqJson.get("orderUseIntegralValue") != null ? reqJson.getInteger("orderUseIntegralValue") : 0;//积分抵扣值
            BigDecimal orderDeductionBalancePrice = reqJson.get("orderDeductionBalancePrice") != null ? reqJson.getBigDecimal("orderDeductionBalancePrice") : BigDecimal.ZERO;//账户余额抵扣金额
            BigDecimal unitPrice = reqJson.getBigDecimal("unitPrice");//移动端传递的商品单价
            String goodsId = reqJson.getString("goodsId");  //商品id
            int goodsCount = reqJson.getInteger("goodsCount");  //购买的商品数量

            ShoppingGoods shoppingGoods = new ShoppingGoods();
            shoppingGoods.setId(goodsId);
            //商品主体信息
            shoppingGoods = shoppingGoodsDao.queryDetail(shoppingGoods);

            //实时查询商品现价
            BigDecimal currentPrice = BigDecimal.ZERO;
            if (null != reqJson.get("propertys") && !"".equals(reqJson.get("propertys"))) {
                ShoppingGoodsInventory shoppingGoodsInventory = new ShoppingGoodsInventory();
                shoppingGoodsInventory.setGoodsId(shoppingGoods.getId());
                shoppingGoodsInventory.setPropertys(reqJson.getString("propertys"));
                shoppingGoodsInventory = shoppingGoodsInventoryDao.queryDetail(shoppingGoodsInventory);
                currentPrice = shoppingGoodsInventory.getPrice();
            } else {
                currentPrice = shoppingGoods.getStorePrice();
            }

            boolean flag = true;
            int inventoryCount = 0;
            //判断商品库存状态
            if (null != reqJson.get("propertys") && !"".equals(reqJson.get("propertys"))) {
                ShoppingGoodsInventory shoppingGoodsInventory = new ShoppingGoodsInventory();
                shoppingGoodsInventory.setGoodsId(goodsId);
                shoppingGoodsInventory.setPropertys(reqJson.getString("propertys"));
                shoppingGoodsInventory = shoppingGoodsInventoryDao.queryDetail(shoppingGoodsInventory);
                inventoryCount = shoppingGoodsInventory.getCount();
                if (null == shoppingGoodsInventory || shoppingGoodsInventory.getCount() < goodsCount) {
                    flag = false;
                }
            }
            if (!flag || shoppingGoods.getGoodsInventory() < goodsCount) {
                retCode = "-1";
                retMsg = "库存不足！";
            } else if (currentPrice.compareTo(unitPrice) != 0) {
                retCode = "-1";
                retMsg = "商品价格发生变化，请重新确认订单信息！";
            } else {
                //从购物车下单,需要判断该购物车信息是否已经在其他手设备被提交，避免造成同一购物车信息的多次提交
                boolean cartState = false;
                if (null != reqJson.get("cartId") && !"".equals(reqJson.get("cartId"))) {
                    ShoppingGoodscart goodscart = new ShoppingGoodscart();
                    goodscart.setId(reqJson.getString("cartId"));
                    goodscart = shoppingGoodscartDao.queryDetail(goodscart);
                    if(StringUtil.isNotNull(goodscart.getOfId())){   //有订单id则表示该购物车信息已经被提交
                        cartState = true;
                    }
                }
                if(cartState){
                    retCode = "-1";
                    retMsg = "该购物车记录不存在，请刷新购物车！";
                }else{
                    //查询账户积分和余额信息
                    //从卖座实时查询账户积分和余额
                    int account_point = 0;   //会员账户积分剩余点数，单位：点数；
                    BigDecimal accountMoney = BigDecimal.ZERO;
                    int point_avoid_limit = 0;   //积分支付免密限额；
                    int account_money_fen = 0;  //账户余额；单位：分
                    int account_avoid_limit = 0;

                    //查询账户积分和余额信息
                    boolean accountState = true;
                    if (orderUseIntegralValue > 0 || orderDeductionBalancePrice.compareTo(BigDecimal.ZERO) == 1||(null != reqJson.get("useIntegral") && reqJson.getInteger("useIntegral") == 1)||(null != reqJson.get("useBalance") && reqJson.getInteger("useBalance") == 1)) {
                        //从卖座实时查询账户积分和余额
                        JSONObject accountObj = MZService.getAssetinfo(mzUserId);
                        if (null != accountObj) {
                            account_point = accountObj.get("account_point") == null ? 0 : accountObj.getInteger("account_point");   //会员账户积分剩余点数，单位：点数；
                            account_money_fen = accountObj.get("account_money_fen") == null ? 0 : accountObj.getInteger("account_money_fen");
                            //账户余额；单位：分
                            accountMoney = new BigDecimal(account_money_fen).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);  //分转换为元
                            bizDataJson.put("accountPoint", account_point);
                            bizDataJson.put("accountMoney", accountMoney);
                        } else {
                            accountState = false;
                            bizDataJson.put("accountPoint", 0);
                            bizDataJson.put("accountMoney", 0);
                        }

                        //查询积分和余额免密限额
                        ShoppingAssetRule shoppingAssetRule = CommonUtil.getAssetRule();
                        if (null != shoppingAssetRule) {
                            bizDataJson.put("accountPointLimit", shoppingAssetRule.getPointAvoidLimit());
                            bizDataJson.put("accountMoneyLimit", shoppingAssetRule.getAccountAvoidLimit());
                        }
                    }


                    //开始计算订单优惠和支付金额
                    int useIntegralSet = shoppingGoods.getUseIntegralSet();   //使用积分抵扣设置 0:不允许使用积分抵扣;1:可使用部分积分抵扣
                    int useIntegralValue = shoppingGoods.getUseIntegralValue();    //单个商品积分抵扣值
                    int useBalanceSet = shoppingGoods.getUseBalanceSet();   //使用余额支付设置 0:不允许使用;1:可使用
                    int useMembershipSet = shoppingGoods.getUseMembershipSet();   //是否支持会员权益 0:不支持;1:支持
                    //订单运费计算
                    BigDecimal shipPrice = BigDecimal.ZERO;
                    if (shoppingGoods.getGoodsTransfee() == 0) {   //买家承担运费
                        JSONObject addObj=null;
                        if (null != reqJson.get("addressId")) {
                            addObj = MZService.getAddressDetail(mzUserId, reqJson.getString("addressId"));
                        }
                        //通过运费模板计算运费
                        if (null != shoppingGoods.getTransportId() && (shoppingGoods.getExpressTransFee() == null || shoppingGoods.getExpressTransFee().compareTo(BigDecimal.ZERO) == 0)) {
                            shipPrice = buildShipPrice(shoppingGoods, goodsCount,addObj);
                        } else {
                            shipPrice = shoppingGoods.getExpressTransFee() == null ? BigDecimal.ZERO : shoppingGoods.getExpressTransFee();
                        }
                    }
                    if (!accountState) {
                        retCode = "-1";
                        retMsg = "无法获取您的账户积分和余额数据，请稍后下单！";
                    }
                    else if (accountMoney.compareTo(orderDeductionBalancePrice) < 0 || account_point < orderUseIntegralValue) {
                        retCode = "-1";
                        retMsg = "账户积分或余额不足，请重新确认订单信息！";
                    } else {
                        //开始计算订单优惠和支付金额
                        BigDecimal totalPrice = currentPrice.multiply(new BigDecimal(goodsCount));
                        //订单总金额为商品费用+运费(用户自提时不需要运费)
                        if (null == reqJson.get("transport") || "".equals(reqJson.get("transport"))) {
                            if (shoppingGoods.getGoodsTransfee() == 0) {   //买家承担运费
                                totalPrice = currentPrice.multiply(new BigDecimal(goodsCount)).add(shipPrice);
                            }
                        } else if (reqJson.get("transport").equals("快递")) {
                            totalPrice = currentPrice.multiply(new BigDecimal(goodsCount)).add(shipPrice);
                        }else{
                            shipPrice = BigDecimal.ZERO;
                        }
                        BigDecimal goodsPrice = currentPrice.multiply(new BigDecimal(goodsCount)).setScale(2, BigDecimal.ROUND_HALF_UP);
                        //定额积分抵扣的情况需要特殊处理，先获取到定额积分和剩下的现金支付的数额
                        BigDecimal cashPrice = BigDecimal.ZERO;
                        int fixedIntegalValue = 0;
                        if (useIntegralSet == 1) {
                            if (currentPrice.multiply(new BigDecimal((100))).intValue() < useIntegralValue) {
                                useIntegralValue = currentPrice.multiply(new BigDecimal((100))).intValue();
                            }
                            fixedIntegalValue = useIntegralValue * goodsCount;
                            //现价减去定额积分抵扣的额度，即为剩下的需要支付的现金额度
                            cashPrice = currentPrice.multiply(new BigDecimal(goodsCount)).subtract(new BigDecimal(fixedIntegalValue).divide(new BigDecimal(moneyToIntegralScale)).setScale(2, BigDecimal.ROUND_HALF_UP));
                        }

                        //计算优惠时只在商品费用的基础上进行计算
                        BigDecimal payPrice = BigDecimal.ZERO;
                        if (useIntegralSet == 1) {
                            payPrice = cashPrice.multiply(new BigDecimal(goodsCount));
                        } else {
                            payPrice = currentPrice.multiply(new BigDecimal(goodsCount));
                        }
                        BigDecimal originPrice = payPrice;
                        int originntegalValue = fixedIntegalValue;

                        BigDecimal couponCut = BigDecimal.ZERO;  //优惠券折扣
                        BigDecimal accountCut = BigDecimal.ZERO; //会员体系折扣
                        int integralValue = 0;  //积分抵扣数量
                        BigDecimal integralCut = BigDecimal.ZERO;  //积分抵扣
                        BigDecimal balanceCut = BigDecimal.ZERO;  //余额抵扣

                        //查询商品可用的可用优惠券
                        List<String> couponIds = new ArrayList<>();
                        JSONArray couponArray = CommonUtil.getGoodsCouppon(goodsId, 1, userId, goodsPrice, payPrice);
                        for (int i = 0; i < couponArray.size(); i++) {
                            JSONObject obj = couponArray.getJSONObject(i);
                            couponIds.add(obj.getString("id"));
                        }
                        //用户选择的优惠券
                        if (null != reqJson.get("couponId") && !"".equals(reqJson.get("couponId")) && couponIds.contains(reqJson.get("couponId"))) {
                            String couponId = reqJson.getString("couponId");
                            //先获取用户选择的优惠券详情
                            JSONObject couponDtl = CRMService.getUserCouponDtl(couponId);
                            if (null != couponDtl) {
                                String right_No = couponDtl.getString("right_No");
                                ShoppingCoupon shoppingCoupon = new ShoppingCoupon();
                                shoppingCoupon.setRight_No(right_No);
                                shoppingCoupon = shoppingCouponDao.queryDetail(shoppingCoupon);
                                //如果是减优惠券或者代金券，直接抵扣相应金额
                                if (shoppingCoupon.getRight_Type().equals("coincp")) {
                                    int couponAmount = Integer.valueOf(shoppingCoupon.getRight_Content());
                                    if (new BigDecimal(couponAmount).compareTo(payPrice) == 1) {
                                        payPrice = BigDecimal.ZERO;
                                    } else {
                                        payPrice = payPrice.subtract(new BigDecimal(couponAmount));
                                    }
                                } else {
                                    //如果是折扣优惠券，需要计算折扣
                                    BigDecimal discount = new BigDecimal(shoppingCoupon.getRight_Content());
                                    payPrice = payPrice.multiply(discount).setScale(2, BigDecimal.ROUND_HALF_UP);
                                    //如果是定额积分抵扣，那么定额积分值也要参与折扣
                                    if (useIntegralSet == 1) {
                                        fixedIntegalValue = new BigDecimal(fixedIntegalValue).multiply(discount).intValue();
                                    }
                                }
                                //计算优惠券抵扣的金额
                                if (useIntegralSet == 1) {
                                    //现金和积分各自抵扣的部分相加
                                    couponCut = originPrice.subtract(payPrice).add(new BigDecimal(originntegalValue - fixedIntegalValue).divide(new BigDecimal(moneyToIntegralScale)).setScale(2, BigDecimal.ROUND_HALF_UP));
                                } else {
                                    couponCut = originPrice.subtract(payPrice);
                                }
                                originPrice = payPrice;
                                originntegalValue = fixedIntegalValue;
                            }
                        }

                        //会员等级折扣
                        if (useMembershipSet == 1) {
                            BigDecimal discount = CommonUtil.getUserMemberShip(userId);
                            payPrice = payPrice.multiply(discount).setScale(2, BigDecimal.ROUND_HALF_UP);
                            if (useIntegralSet == 1) {
                                fixedIntegalValue = new BigDecimal(fixedIntegalValue).multiply(discount).intValue();
                            }
                            //会员等级抵扣的金额
                            if (useIntegralSet == 1) {
                                accountCut = originPrice.subtract(payPrice).add(new BigDecimal(originntegalValue - fixedIntegalValue).divide(new BigDecimal(moneyToIntegralScale)).setScale(2, BigDecimal.ROUND_HALF_UP));
                            } else {
                                accountCut = originPrice.subtract(payPrice);
                            }
                            originPrice = payPrice;
                            originntegalValue = fixedIntegalValue;
                        }

                        //获取积分和余额单次支付的上限配置
                        ShoppingPayLimit payLimit = CommonUtil.getPayLimit();

                        int maxIntegralValue = 0;
                        int accountPoint = bizDataJson.get("accountPoint")==null?0:bizDataJson.getInteger("accountPoint");
                        int pointPay = payLimit.getPointPay();  //后台设置的积分单次支付限额
                        //用户选择使用积分抵扣，如果账户积分足够，则必须按商品抵扣积分最大值进行扣除，不支持移动端手动输入积分抵扣的数值
                        if (useIntegralSet == 1 || (null != reqJson.get("useIntegral") && reqJson.getInteger("useIntegral") == 1)) {

                            if (useIntegralSet == 1) {  //限额抵扣时，用户需支付固定额度的积分
                                maxIntegralValue = fixedIntegalValue;
                            } else {
                                int payPriceToInt =payPrice.multiply(new BigDecimal(moneyToIntegralScale)).intValue();
                                maxIntegralValue = useIntegralValue * goodsCount<payPriceToInt?useIntegralValue * goodsCount:payPriceToInt;
                                int pointLimit = accountPoint;   //账户积分
                                if(pointPay>0){
                                    pointLimit = pointPay>pointLimit?pointLimit:pointPay;
                                }
                                if (maxIntegralValue > pointLimit) {
                                    maxIntegralValue = pointLimit;
                                }
//
//                            if (maxIntegralValue > bizDataJson.getInteger("accountPoint")) {
//                                maxIntegralValue = bizDataJson.getInteger("accountPoint");
//                            }
                            }

                            integralValue = maxIntegralValue;
                            BigDecimal integralAmount = (new BigDecimal(maxIntegralValue).divide(new BigDecimal(moneyToIntegralScale))).setScale(2, BigDecimal.ROUND_HALF_UP);
                            if (useIntegralSet == 1) {
                                integralCut = integralAmount;
                            } else {
                                payPrice = payPrice.subtract(integralAmount);
                                integralCut = originPrice.subtract(payPrice);
                            }
                        }
                        if (maxIntegralValue > accountPoint) {
                            retCode = "1";
                            retMsg = "当前账户积分不够，无法下单！";
                        }else if(maxIntegralValue >pointPay){
                            retCode = "1";
                            retMsg = "积分单次支付限额"+pointPay+",当前订单已超出该额度，无法下单！";
                        }else{
                            //运费不参与优惠，不能用积分抵抵扣，只能用余额或现金支付
                            payPrice = payPrice.add(shipPrice);
                            originPrice = payPrice;
                            //用户选择使用余额支付，则表示所有剩下的待支付金额都使用余额支付，不支持移动端手动输入要支付的余额值
                            BigDecimal deductionBalance = BigDecimal.ZERO;
                            if (null != reqJson.get("useBalance") && reqJson.getInteger("useBalance") == 1) {
                                BigDecimal accountLimit = bizDataJson.getBigDecimal("accountMoney");
                                BigDecimal balancePay = new BigDecimal(payLimit.getBalancePay());   //余额单次支付上限
                                if(balancePay.compareTo(BigDecimal.ZERO)>0&&balancePay.compareTo(accountLimit)<0){    //余额支付上限为账户余额和支付限额两者中的较小值
                                    accountLimit = balancePay;
                                }
                                //如果账户余额不够支付所有的待支付金额，则默认扣除所有的账户余额
                                if (payPrice.compareTo(accountLimit) == 1) {
                                    deductionBalance = accountLimit;
                                } else {
                                    deductionBalance = payPrice;
                                }
                                payPrice = payPrice.subtract(deductionBalance);
                                balanceCut = deductionBalance;
                            }
                            //订单总金额/待支付金额/扣除积分/余额抵扣值与移动端传值不一致，需要重新确认订单
                            if (totalPrice.compareTo(orderTotalPrice) != 0 || payPrice.compareTo(orderPayPrice) != 0 || maxIntegralValue != orderUseIntegralValue || deductionBalance.compareTo(orderDeductionBalancePrice) != 0) {
                                retCode = "-1";
                                retMsg = "订单金额发生变化，请重新确认订单信息！";
                            } else if (maxIntegralValue > account_point) {
                                retCode = "-1";
                                retMsg = "当前账户积分不足，请重新确认订单信息！";
                            } else if(upLimitBuy(goodsId,goodsCount,userId)){
                                retCode = "-1";
                                retMsg = "已经超出当前商品购买上限！";
                            }else {
                                if(!dealGoodsStock(reqJson)){
                                    retCode = "-1";
                                    retMsg = "剩余商品数量不足！";
                                }else{
                                    //更新商品库存
                                    if (null != reqJson.get("propertys") && !"".equals(reqJson.get("propertys"))) {
//                                    ShoppingGoodsInventory shoppingGoodsInventory = new ShoppingGoodsInventory();
//                                    shoppingGoodsInventory.setGoodsId(goodsId);
//                                    shoppingGoodsInventory.setPropertys(reqJson.getString("propertys"));
//                                    shoppingGoodsInventory.setCount(inventoryCount - goodsCount);
//                                    shoppingGoodsInventoryDao.update(shoppingGoodsInventory);

                                        HashMap<String, Object> cutMap = new HashMap<>();
                                        cutMap.put("goodsId",goodsId);
                                        cutMap.put("propertys",reqJson.getString("propertys"));
                                        cutMap.put("cutCount",goodsCount);
                                        shoppingGoodsInventoryDao.cutInventory(cutMap);
                                    }
                                shoppingGoods.setGoodsInventory(shoppingGoods.getGoodsInventory() - goodsCount);
//                                shoppingGoodsDao.updateGoodsInventory(shoppingGoods);
                                    HashMap<String, Object> cutMap = new HashMap<>();
                                    cutMap.put("goodsId",goodsId);
                                    cutMap.put("cutInventory",goodsCount);
                                    shoppingGoodsDao.cutGoodsInventory(cutMap);

                                    //创建订单信息
                                    ShoppingOrderform orderform = new ShoppingOrderform();
                                    //订单id（系统订单全局唯一标识）
                                    String orderId = PayUtil.getOrderNo(Const.SHOPPING_CUL_ORDER);
                                    orderform.setOrderId(orderId);
                                    //订单类型：文创商品
                                    orderform.setOrderType(Const.SHOPPING_CUL_ORDER_TYPE);
                                    //订单状态：待支付
                                    orderform.setOrderStatus(10);
                                    //订单金额
                                    orderform.setTotalPrice(totalPrice);
                                    //运费金额
                                    orderform.setShipPrice(shipPrice);
                                    //需支付的现金金额
                                    orderform.setPayPrice(payPrice);
//                        if (payPrice.compareTo(BigDecimal.ZERO) == 1) {
//                            orderform.setPayPrice(new BigDecimal(0.01));
//                        }

                                    //收货地址
                                    if (null != reqJson.get("transport")) {
                                        String transport = reqJson.getString("transport");
                                        orderform.setTransport(transport);  //快递/自提
                                    }
                                    if (null != reqJson.get("addressId")) {
                                        orderform.setAddrId(reqJson.getString("addressId"));
                                    }
                                    //商店id
                                    orderform.setStoreId(Const.STORE_ID);
                                    //用户id
                                    orderform.setUserId(userId);

                                    //订单支付信息
                                    ShoppingOrderPay shoppingOrderPay = new ShoppingOrderPay();
                                    shoppingOrderPay.setUserId(userId);
                                    if (payPrice.compareTo(BigDecimal.ZERO) == 1) {
                                        shoppingOrderPay.setCashStatus(0);
                                    }
                                    //优惠券信息
                                    if (null != reqJson.get("couponId") && !"".equals(reqJson.get("couponId"))) {
                                        String couponId = reqJson.getString("couponId");//用户选择的优惠券id
                                        //订单使用的优惠券id
                                        orderform.setCiId(couponId);
                                        //优惠券抵扣金额
//                            BigDecimal deductionCouponPrice = reqJson.get("deductionCouponPrice")==null?BigDecimal.ZERO:reqJson.getBigDecimal("deductionCouponPrice");
//                            orderform.setDeductionCouponPrice(deductionCouponPrice);
                                        orderform.setDeductionCouponPrice(couponCut);
                                        shoppingOrderPay.setCouponStatus(0);

                                        //将用户选择的优惠券保存到优惠券临时锁定表中，该优惠券不可在地方再被使用
                                        ShoppingCouponUsertemp shoppingCouponUsertemp = new ShoppingCouponUsertemp();
                                        shoppingCouponUsertemp.setUserId(userId);
                                        shoppingCouponUsertemp.setCouponId(couponId);
                                        shoppingCouponUsertempDao.insert(shoppingCouponUsertemp);
                                    }

                                    ShoppingOrderPaykey shoppingOrderPaykey = new ShoppingOrderPaykey();
                                    //用户选择抵扣的积分数额
                                    if (useIntegralSet == 1 || (null != reqJson.get("useIntegral") && reqJson.getInteger("useIntegral") == 1)) {
//                            int deductionIntegral = reqJson.getInteger("deductionIntegral");
//                            BigDecimal deductionIntegralPrice = reqJson.get("deductionIntegralPrice")==null?BigDecimal.ZERO:reqJson.getBigDecimal("deductionIntegralPrice");
                                        orderform.setDeductionIntegralPrice(integralCut);
                                        orderform.setDeductionIntegral(integralValue);
                                        shoppingOrderPay.setIntegralStatus(0);

                                        //积分支付限额验证码
                                        if (null != reqJson.get("accountPointPayKey") && !"".equals(reqJson.get("accountPointPayKey"))) {
                                            shoppingOrderPaykey.setAccountPointPayKey(reqJson.getString("accountPointPayKey"));
                                        }
                                    }
                                    //会员权益抵扣金额
                                    orderform.setDeductionMemberPrice(accountCut);

                                    if (null != reqJson.get("useBalance") && reqJson.getInteger("useBalance") == 1) {
                                        //账户余额支付金额
                                        orderform.setDeductionBalancePrice(balanceCut);
                                        shoppingOrderPay.setBalanceStatus(0);

                                        //余额支付限额验证码
                                        if (null != reqJson.get("accountMoneyPayKey") && !"".equals(reqJson.get("accountMoneyPayKey"))) {
                                            shoppingOrderPaykey.setAccountMoneyPayKey(reqJson.getString("accountMoneyPayKey"));
                                        }
                                    }

                                    //保存订单信息
                                    shoppingOrderformDao.insert(orderform);


                                    //保存资产业务key
                                    if (null != shoppingOrderPaykey.getAccountPointPayKey() || null != shoppingOrderPaykey.getAccountMoneyPayKey()) {
                                        shoppingOrderPaykey.setOfId(orderform.getId());
                                        shoppingOrderPaykeyDao.insert(shoppingOrderPaykey);
                                    }

                                    //保存订单支付信息
                                    shoppingOrderPay.setOfId(orderform.getId());
                                    shoppingOrderPayDao.insert(shoppingOrderPay);


                                    // 添加订单日志
                                    ShoppingOrderLog shoppingOrderLog = new ShoppingOrderLog();
                                    shoppingOrderLog.setLogInfo("提交订单");
//                        shoppingOrderLog.setStateInfo(orderform.getOrderType());
                                    shoppingOrderLog.setLogUserId(userId);
                                    shoppingOrderLog.setOfId(orderform.getId());
                                    shoppingOrderLogDao.insert(shoppingOrderLog);


                                    //保存订单-商品关联信息
                                    ShoppingGoodscart goodscart = new ShoppingGoodscart();
                                    String transport = reqJson.get("transport")==null?"快递":reqJson.getString("transport");

                                    //从购物车下单
                                    if (null != reqJson.get("cartId") && !"".equals(reqJson.get("cartId"))) {
                                        goodscart.setId(reqJson.getString("cartId"));
                                        goodscart = shoppingGoodscartDao.queryDetail(goodscart);

                                        goodscart.setTransport(transport);  //快递/自提
                                        goodscart.setDeductionCouponPrice(orderform.getDeductionCouponPrice());
                                        goodscart.setDeductionMemberPrice(orderform.getDeductionMemberPrice());
                                        goodscart.setDeductionIntegral(orderform.getDeductionIntegral());
                                        goodscart.setDeductionIntegralPrice(orderform.getDeductionIntegralPrice());
                                        goodscart.setDeductionBalancePrice(orderform.getDeductionBalancePrice());
                                        goodscart.setPayPrice(orderform.getPayPrice());
                                        goodscart.setShipPrice(orderform.getShipPrice());
                                        //下单时的商品价格
                                        goodscart.setPrice(currentPrice);
                                        goodscart.setOfId(orderform.getId());
                                        goodscart.setCount(goodsCount);
                                        shoppingGoodscartDao.update(goodscart);
                                    } else {
                                        String scId = CommonUtil.getUserScId(userId);
                                        goodscart.setScId(scId);
                                        goodscart.setGoodsId(goodsId);
                                        goodscart.setCartType(Const.SHOPPING_CUL_CART_TYPE);
                                        goodscart.setCount(goodsCount);
                                        if (null != reqJson.get("propertys")) {
                                            goodscart.setSpecInfo(reqJson.getString("specInfo"));
                                            goodscart.setPropertys(reqJson.getString("propertys"));
                                        }
                                        goodscart.setTransport(transport);  //快递/自提
                                        goodscart.setDeductionCouponPrice(orderform.getDeductionCouponPrice());
                                        goodscart.setDeductionMemberPrice(orderform.getDeductionMemberPrice());
                                        goodscart.setDeductionIntegral(orderform.getDeductionIntegral());
                                        goodscart.setDeductionIntegralPrice(orderform.getDeductionIntegralPrice());
                                        goodscart.setDeductionBalancePrice(orderform.getDeductionBalancePrice());
                                        goodscart.setPayPrice(orderform.getPayPrice());
                                        goodscart.setShipPrice(orderform.getShipPrice());
                                        //下单时的商品价格
                                        goodscart.setPrice(currentPrice);
                                        goodscart.setOfId(orderform.getId());
                                        shoppingGoodscartDao.insert(goodscart);
                                    }

                                    if(transport.equals("自提")){  //自提默认需要核销
                                        //如果该商品需要核销，则需要保存核销信息表
                                        String gcId = goodscart.getId();
                                        ShoppingWriteoff shoppingWriteoff = new ShoppingWriteoff();
                                        shoppingWriteoff.setGcId(gcId);
                                        shoppingWriteoff.setGoodsCount(goodscart.getCount());
                                        shoppingWriteoff.setOffCode(StringUtil.randomOffCode(offcodeLength));
                                        shoppingWriteoffDao.insert(shoppingWriteoff);

                                    }



                                    //获取当前系统可用支付方式
                                    HashMap<String, Object> reqMap = new HashMap<>();
                                    reqMap.put("deleteStatus", 0);
                                    List<ShoppingPayment> payments = shoppingPaymentDao.queryList(reqMap);
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

    /**
     * 创建订单（直接下单）
     */
    @Override
    public JSONObject addOrder_new(JSONObject reqJson, HttpServletRequest request) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "服务器内部错误！";
        JSONObject bizDataJson = new JSONObject();
        try {

            String userId = reqJson.getString("userId");//userId
            String mzUserId = CommonUtil.getMzUserId(userId);
            BigDecimal orderTotalPrice = reqJson.getBigDecimal("orderTotalPrice");//订单总金额（商品费用+运费）
            BigDecimal orderShipPrice = reqJson.getBigDecimal("orderShipPrice");//运费金额
            BigDecimal orderPayPrice = reqJson.getBigDecimal("orderPayPrice");//待支付的现金金额
            int orderUseIntegralValue = reqJson.get("orderUseIntegralValue") != null ? reqJson.getInteger("orderUseIntegralValue") : 0;//积分抵扣值
            BigDecimal orderDeductionBalancePrice = reqJson.get("orderDeductionBalancePrice") != null ? reqJson.getBigDecimal("orderDeductionBalancePrice") : BigDecimal.ZERO;//账户余额抵扣金额
            BigDecimal unitPrice = reqJson.getBigDecimal("unitPrice");//移动端传递的商品单价
            String goodsId = reqJson.getString("goodsId");  //商品id
            int goodsCount = reqJson.getInteger("goodsCount");  //购买的商品数量
            //商品规格信息
            String propertys = reqJson.get("propertys")==null?null:reqJson.getString("propertys");
            //拼接规格属性信息
            String specInfo = reqJson.get("specInfo")==null?null:reqJson.getString("specInfo");
            //收货地址
            String addressId = reqJson.get("addressId")==null?null:reqJson.getString("addressId");
            //收货方式
            String transport = reqJson.get("transport")==null?null:reqJson.getString("transport");
            //优惠券id
            String couponId = reqJson.get("couponId")==null?null:reqJson.getString("couponId");
            //用户是否打开积分抵扣开关
            boolean useIntegral = (null != reqJson.get("useIntegral") && reqJson.getInteger("useIntegral") == 1)?true:false;
            //用户是否打开余额抵扣开关
            boolean useBalance = (null != reqJson.get("useBalance") && reqJson.getInteger("useBalance") == 1)?true:false;
            //资产验证key
            String accountPointPayKey= reqJson.get("accountPointPayKey")==null?null:reqJson.getString("accountPointPayKey");
            String accountMoneyPayKey= reqJson.get("accountMoneyPayKey")==null?null:reqJson.getString("accountMoneyPayKey");
            //购物车id，从购物车页面下单时会传递该值
            String cartId= reqJson.get("cartId")==null?null:reqJson.getString("cartId");

            ShoppingGoods shoppingGoods = new ShoppingGoods();
            shoppingGoods.setId(goodsId);
            //商品主体信息
            shoppingGoods = shoppingGoodsDao.queryDetail(shoppingGoods);

            //从购物车下单,需要判断该购物车信息是否已经在其他手设备被提交，避免造成同一购物车信息的多次提交
            if(!shoppingOrderUtil.isCartEnable(cartId)){
                retCode = "-1";
                retMsg = "该购物车记录不存在，请刷新购物车！";
            }else{
                //从卖座实时查询账户积分和余额，麦座账户信息为必须，否则无法计算相关订单金额
                UserAccountInfo userAccountInfo=accountInfoUtil.getUserAccountInfo(mzUserId);
                if(!userAccountInfo.getAccountEnable()){
                    retCode = "-1";
                    retMsg = "无法获取您的账户积分和余额数据，请稍后下单！";
                }else{
                    bizDataJson.put("accountPoint", userAccountInfo.getAccountEnable()?userAccountInfo.getAccount_point():0);
                    bizDataJson.put("accountMoney", userAccountInfo.getAccountEnable()?userAccountInfo.getAccountMoney():0);

                    //查询积分和余额免密限额
                    ShoppingAssetRule shoppingAssetRule = CommonUtil.getAssetRule();
                    if (null != shoppingAssetRule) {
                        bizDataJson.put("accountPointLimit", shoppingAssetRule.getPointAvoidLimit());
                        bizDataJson.put("accountMoneyLimit", shoppingAssetRule.getAccountAvoidLimit());
                    }
                    //重新计算订单优惠和支付金额
                    SimpleOrdernfo shoppingOrderInfo = new SimpleOrdernfo(userId,mzUserId,goodsCount,propertys,useIntegral,useBalance);
//                    ShoppingOrderInfo shoppingOrderInfo = new ShoppingOrderInfo(userId,mzUserId,userAccountInfo,couponId,useIntegral,useBalance);
//                    shoppingOrderInfo.setShoppingGoods(shoppingGoods);
//                    shoppingOrderInfo.setGoodsCount(goodsCount);
//                    shoppingOrderInfo.setPropertys(propertys);
                    shoppingOrderInfo.setSpecInfo(specInfo);
                    shoppingOrderInfo.setCouponId(couponId);
                    shoppingOrderInfo.setTransport(transport);
                    shoppingOrderInfo.setAddressId(addressId);
                    shoppingOrderInfo =shoppingOrderUtil.countCulOrderAmount(shoppingGoods,shoppingOrderInfo,userAccountInfo);

                    //订单金额计算失败
                    if(!shoppingOrderInfo.getCountSuccess()){
                        retCode = "-1";
                        retMsg =shoppingOrderInfo.getErrorMsg();
                    }else{
                        BigDecimal totalAmount = shoppingOrderInfo.getTotalAmount();
                        BigDecimal payAmount = shoppingOrderInfo.getPayAmount();
                        int integralValue = shoppingOrderInfo.getIntegralValue();
                        BigDecimal balanceCut =shoppingOrderInfo.getBalanceCut();
                        BigDecimal currentPrice =shoppingOrderInfo.getCurrentPrice();

                        //订单总金额/待支付金额/扣除积分/余额抵扣值与移动端传值不一致，需要重新确认订单
                        if (currentPrice.compareTo(unitPrice) != 0 ||totalAmount.compareTo(orderTotalPrice) != 0 || payAmount.compareTo(orderPayPrice) != 0 || integralValue != orderUseIntegralValue || balanceCut.compareTo(orderDeductionBalancePrice) != 0) {
                            retCode = "-1";
                            retMsg = "订单金额发生变化，请重新确认订单信息！";
                        } else if(shoppingOrderUtil.upLimitBuy(goodsId,Const.SHOPPING_CUL_CART_TYPE,shoppingGoods.getLimitBuy(),goodsCount,userId)){
                            retCode = "-1";
                            retMsg = "超出当前商品购买上限！";
                        }else if(!shoppingGoods.getGoodsStatus().equals("0")){
                            retCode = "-1";
                            retMsg = "当前商品已下架！";
                        }else {
                            //处理商品库存
                            Long stock = shoppingOrderUtil.cutGoodsStock(goodsId,propertys,goodsCount);
                            if(stock>=0){
                                //获取系统当前支付方式
                                JSONArray payArray= OrderCommonUtil.getPayments();
                                shoppingOrderInfo.setAccountPointPayKey(accountPointPayKey);
                                shoppingOrderInfo.setAccountMoneyPayKey(accountMoneyPayKey);
                                shoppingOrderInfo.setCartId(cartId);
                                try{
                                    //创建订单
                                    String orderId = PayUtil.getOrderNo(Const.SHOPPING_CUL_ORDER);  //生成文创订单id
                                    ShoppingOrderform orderform=shoppingOrderUtil.addOrder(orderId,Const.SHOPPING_CUL_ORDER_TYPE,Const.SHOPPING_CUL_CART_TYPE,goodsId,shoppingOrderInfo);

                                    bizDataJson.put("payments", payArray);
                                    bizDataJson.put("orderId", orderform.getOrderId());
                                    bizDataJson.put("price", orderform.getPayPrice());
                                    retCode = "0";
                                    retMsg = "操作成功！";
                                }catch (Exception e){
                                    e.printStackTrace();
                                    retCode = "-1";
                                    retMsg = "订单创建失败，请稍后再试！";
                                    //订单创建失败，返库存
                                    shoppingOrderUtil.addGoodsStock(goodsId,propertys,goodsCount);
                                }
                            }else if(stock==-2L){
                                retCode = "-1";
                                retMsg = "库存不足！";
                            }else{
                                retCode = "-1";
                                retMsg = "库存异常，请稍后再试！";
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


    /**
     * 创建订单（积分、直接下单）
     */
    @Override
    public JSONObject addIntegralOrder(JSONObject reqJson, HttpServletRequest request) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "服务器内部错误！";
        JSONObject bizDataJson = new JSONObject();
        try {
//            String data = request.getParameter("data");// 参数
//            JSONObject reqJson = JSONObject.parseObject(URLDecoder.decode(data, "utf-8"));

            String userId = reqJson.getString("userId");//userId
            String mzUserId = CommonUtil.getMzUserId(userId);
            BigDecimal orderTotalPrice = reqJson.getBigDecimal("orderTotalPrice");//订单总金额（商品费用+运费）
            BigDecimal orderShipPrice = reqJson.getBigDecimal("orderShipPrice");//运费金额
            BigDecimal orderPayPrice = reqJson.getBigDecimal("orderPayPrice");//待支付的现金金额
            int orderUseIntegralValue = reqJson.get("orderUseIntegralValue") != null ? reqJson.getInteger("orderUseIntegralValue") : 0;//积分抵扣值
            BigDecimal orderDeductionBalancePrice = reqJson.get("orderDeductionBalancePrice") != null ? reqJson.getBigDecimal("orderDeductionBalancePrice") : BigDecimal.ZERO;//账户余额抵扣金额
            BigDecimal unitPrice = reqJson.getBigDecimal("unitPrice");//移动端传递的商品单价
            String goodsId = reqJson.getString("goodsId");  //商品id
            int goodsCount = reqJson.getInteger("goodsCount");  //购买的商品数量

            ShoppingGoods shoppingGoods = new ShoppingGoods();
            shoppingGoods.setId(goodsId);
            //商品主体信息
            shoppingGoods = shoppingGoodsDao.queryDetail(shoppingGoods);

            //实时查询商品现价
            BigDecimal currentPrice = BigDecimal.ZERO;
            if (null != reqJson.get("propertys") && !"".equals(reqJson.get("propertys"))) {
                ShoppingGoodsInventory shoppingGoodsInventory = new ShoppingGoodsInventory();
                shoppingGoodsInventory.setGoodsId(shoppingGoods.getId());
                shoppingGoodsInventory.setPropertys(reqJson.getString("propertys"));
                shoppingGoodsInventory = shoppingGoodsInventoryDao.queryDetail(shoppingGoodsInventory);
                currentPrice = shoppingGoodsInventory.getPrice();
            } else {
                currentPrice = shoppingGoods.getStorePrice();
            }

            boolean flag = true;
            int inventoryCount = 0;
            //判断商品库存状态
            if (null != reqJson.get("propertys") && !"".equals(reqJson.get("propertys"))) {
                ShoppingGoodsInventory shoppingGoodsInventory = new ShoppingGoodsInventory();
                shoppingGoodsInventory.setGoodsId(goodsId);
                shoppingGoodsInventory.setPropertys(reqJson.getString("propertys"));
                shoppingGoodsInventory = shoppingGoodsInventoryDao.queryDetail(shoppingGoodsInventory);
                inventoryCount = shoppingGoodsInventory.getCount();
                if (null == shoppingGoodsInventory || shoppingGoodsInventory.getCount() < goodsCount) {
                    flag = false;
                }
            }
            if (!flag || shoppingGoods.getGoodsInventory() < goodsCount) {
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
                    goodscart = shoppingGoodscartDao.queryDetail(goodscart);
                    if(StringUtil.isNotNull(goodscart.getOfId())){   //有订单id则表示该购物车信息已经被提交
                        cartState = true;
                    }
                }
                if(cartState){
                    retCode = "-1";
                    retMsg = "该购物车记录不存在，请刷新购物车！";
                }else{
                    //查询账户积分和余额信息
                    //从卖座实时查询账户积分和余额
                    int account_point = 0;   //会员账户积分剩余点数，单位：点数；
                    BigDecimal accountMoney = BigDecimal.ZERO;
                    int point_avoid_limit = 0;   //积分支付免密限额；
                    int account_money_fen = 0;  //账户余额；单位：分
                    int account_avoid_limit = 0;

                    //查询账户积分和余额信息
                    boolean accountState = true;
                    if (orderUseIntegralValue > 0 || orderDeductionBalancePrice.compareTo(BigDecimal.ZERO) == 1) {
                        //从卖座实时查询账户积分和余额
                        JSONObject accountObj = MZService.getAssetinfo(mzUserId);
                        if (null != accountObj) {
                            account_point = accountObj.get("account_point") == null ? 0 : accountObj.getInteger("account_point");   //会员账户积分剩余点数，单位：点数；
                            account_money_fen = accountObj.get("account_money_fen") == null ? 0 : accountObj.getInteger("account_money_fen");
                            ;  //账户余额；单位：分
                            accountMoney = new BigDecimal(account_money_fen).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);  //分转换为元
                            bizDataJson.put("accountPoint", account_point);
                            bizDataJson.put("accountMoney", accountMoney);
                        } else {
                            accountState = false;
                        }

                        //查询积分和余额免密限额
                        ShoppingAssetRule shoppingAssetRule = CommonUtil.getAssetRule();
                        if (null != shoppingAssetRule) {
                            bizDataJson.put("accountPointLimit", shoppingAssetRule.getPointAvoidLimit());
                            bizDataJson.put("accountMoneyLimit", shoppingAssetRule.getAccountAvoidLimit());
                        }
                    }


                    //开始计算订单优惠和支付金额
                    int useMembershipSet = shoppingGoods.getUseMembershipSet();   //是否支持会员权益 0:不支持;1:支持
                    //订单运费计算
                    BigDecimal shipPrice = BigDecimal.ZERO;
                    if (shoppingGoods.getGoodsTransfee() == 0) {   //买家承担运费
                        JSONObject addObj=null;
                        if (null != reqJson.get("addressId")) {
                            addObj = MZService.getAddressDetail(mzUserId, reqJson.getString("addressId"));
                        }
                        //通过运费模板计算运费
                        if (null != shoppingGoods.getTransportId() && (shoppingGoods.getExpressTransFee() == null || shoppingGoods.getExpressTransFee().compareTo(BigDecimal.ZERO) == 0)) {
                            shipPrice = buildShipPrice(shoppingGoods, goodsCount,addObj);
                        } else {
                            shipPrice = shoppingGoods.getExpressTransFee() == null ? BigDecimal.ZERO : shoppingGoods.getExpressTransFee();
                        }
                    }
                    if (!accountState) {
                        retCode = "-1";
                        retMsg = "无法获取您的账户积分和余额数据，请稍后下单！";
                    } else if (accountMoney.compareTo(orderDeductionBalancePrice) < 0 || account_point < orderUseIntegralValue) {
                        retCode = "-1";
                        retMsg = "账户积分或余额不足，请重新确认订单信息！";
                    } else {
                        //订单总金额为商品费用+运费
                        BigDecimal totalPrice = currentPrice.multiply(new BigDecimal(goodsCount));
                        //订单总金额为商品费用+运费(用户自提时不需要运费)
                        if (null == reqJson.get("transport") || "".equals(reqJson.get("transport"))) {
                            if (shoppingGoods.getGoodsTransfee() == 0) {   //买家承担运费
                                totalPrice = currentPrice.multiply(new BigDecimal(goodsCount)).add(shipPrice);
                            }
                        } else if (reqJson.get("transport").equals("快递")) {
                            totalPrice = currentPrice.multiply(new BigDecimal(goodsCount)).add(shipPrice);
                        }else{
                            shipPrice = BigDecimal.ZERO;
                        }
                        //计算优惠时只在商品费用的基础上进行计算
                        BigDecimal payPrice = currentPrice.multiply(new BigDecimal(goodsCount));
                        BigDecimal originPrice = payPrice;

                        BigDecimal accountCut = BigDecimal.ZERO;  //会员体系折扣
                        int integralValue = 0;  //积分抵扣数量
                        BigDecimal integralCut = BigDecimal.ZERO;  //积分抵扣
                        BigDecimal balanceCut = BigDecimal.ZERO;  //余额抵扣

                        if (useMembershipSet == 1) {
                            //会员等级折扣
                            BigDecimal discount = CommonUtil.getUserMemberShip(userId);
                            payPrice = payPrice.multiply(discount).setScale(2, BigDecimal.ROUND_HALF_UP);
                            //会员等级抵扣的金额
                            accountCut = originPrice.subtract(payPrice);
                            originPrice = payPrice;
                        }

                        //获取积分和余额单次支付的上限配置
                        ShoppingPayLimit payLimit = CommonUtil.getPayLimit();

                        //积分商城只能用积分支付所有金额
                        int maxIntegralValue = 0;
                        maxIntegralValue = payPrice.multiply(new BigDecimal(moneyToIntegralScale)).intValue(); //将待支付金额换算成积分值
                        int accountPoint = bizDataJson.get("accountPoint")==null?0:bizDataJson.getInteger("accountPoint");
                        int pointPay = payLimit.getPointPay();  //后台设置的积分单次支付限额
                        if (maxIntegralValue > accountPoint) {
                            retCode = "1";
                            retMsg = "当前账户积分不够，无法下单！";
                        }else if(maxIntegralValue >pointPay){
                            retCode = "1";
                            retMsg = "积分单次支付限额"+pointPay+",当前订单已超出该额度，无法下单！";
                        }else{
                            integralValue = maxIntegralValue;
                            integralCut = payPrice;

                            //积分商城要支付的现金只有运费，运费不参与优惠，不能用积分抵抵扣，只能用余额或现金支付
                            payPrice = shipPrice;
                            originPrice = payPrice;
                            //用户选择使用余额支付，则表示所有剩下的待支付金额都使用余额支付，不支持移动端手动输入要支付的余额值
                            BigDecimal deductionBalance = BigDecimal.ZERO;
                            if (null != reqJson.get("useBalance") && reqJson.getInteger("useBalance") == 1) {
                                BigDecimal accountLimit = bizDataJson.getBigDecimal("accountMoney");
                                BigDecimal balancePay = new BigDecimal(payLimit.getBalancePay());   //余额单次支付上限
                                if(balancePay.compareTo(BigDecimal.ZERO)>0&&balancePay.compareTo(accountLimit)<0){    //余额支付上限为账户余额和支付限额两者中的较小值
                                    accountLimit = balancePay;
                                }
                                //如果账户余额不够支付所有的待支付金额，则默认扣除所有的账户余额
                                if (payPrice.compareTo(accountLimit) == 1) {
                                    deductionBalance = accountLimit;
                                } else {
                                    deductionBalance = payPrice;
                                }
                                payPrice = payPrice.subtract(deductionBalance);
                                balanceCut = deductionBalance;
                            }
                            //订单总金额/待支付金额/扣除积分/余额抵扣值与移动端传值不一致，需要重新确认订单
                            if (totalPrice.compareTo(orderTotalPrice) != 0 || payPrice.compareTo(orderPayPrice) != 0 || maxIntegralValue != orderUseIntegralValue || deductionBalance.compareTo(orderDeductionBalancePrice) != 0) {
                                retCode = "-1";
                                retMsg = "订单金额发生变化，请重新确认订单信息！";
                            } else if (maxIntegralValue > account_point) {
                                retCode = "-1";
                                retMsg = "当前账户积分不足，请重新确认订单信息！";
                            } else {
                                if(!dealGoodsStock(reqJson)){
                                    retCode = "-1";
                                    retMsg = "剩余商品数量不足！";
                                }else{
                                    //更新商品库存
                                    if (null != reqJson.get("propertys") && !"".equals(reqJson.get("propertys"))) {
//                                    ShoppingGoodsInventory shoppingGoodsInventory = new ShoppingGoodsInventory();
//                                    shoppingGoodsInventory.setGoodsId(goodsId);
//                                    shoppingGoodsInventory.setPropertys(reqJson.getString("propertys"));
//                                    shoppingGoodsInventory.setCount(inventoryCount - goodsCount);
//                                    shoppingGoodsInventoryDao.update(shoppingGoodsInventory);

                                        HashMap<String, Object> cutMap = new HashMap<>();
                                        cutMap.put("goodsId",goodsId);
                                        cutMap.put("propertys",reqJson.getString("propertys"));
                                        cutMap.put("cutCount",goodsCount);
                                        shoppingGoodsInventoryDao.cutInventory(cutMap);
                                    }
                                    shoppingGoods.setGoodsInventory(shoppingGoods.getGoodsInventory() - goodsCount);
//                                shoppingGoodsDao.updateGoodsInventory(shoppingGoods);
                                    HashMap<String, Object> cutMap = new HashMap<>();
                                    cutMap.put("goodsId",goodsId);
                                    cutMap.put("cutInventory",goodsCount);
                                    shoppingGoodsDao.cutGoodsInventory(cutMap);
                                    //创建订单信息
                                    ShoppingOrderform orderform = new ShoppingOrderform();
                                    //订单id（系统订单全局唯一标识）
                                    String orderId = PayUtil.getOrderNo(Const.SHOPPING_INT_ORDER);
                                    orderform.setOrderId(orderId);
                                    //订单类型：文创商品
                                    orderform.setOrderType(Const.SHOPPING_INT_ORDER_TYPE);
                                    //订单状态：待支付
                                    orderform.setOrderStatus(10);
                                    //订单金额
                                    orderform.setTotalPrice(totalPrice);
                                    //运费金额
                                    orderform.setShipPrice(shipPrice);
                                    //需支付的现金金额
                                    orderform.setPayPrice(payPrice);
//                        if (payPrice.compareTo(BigDecimal.ZERO) == 1) {
//                            orderform.setPayPrice(new BigDecimal(0.01));
//                        }

                                    //收货地址
                                    if (null != reqJson.get("transport")) {
                                        String transport = reqJson.getString("transport");
                                        orderform.setTransport(transport);  //快递/自提
                                    }
                                    if (null != reqJson.get("addressId")) {
                                        orderform.setAddrId(reqJson.getString("addressId"));
                                    }
                                    //商店id
                                    orderform.setStoreId(Const.STORE_ID);
                                    //用户id
                                    orderform.setUserId(userId);

                                    //订单支付信息
                                    ShoppingOrderPay shoppingOrderPay = new ShoppingOrderPay();
                                    shoppingOrderPay.setUserId(userId);
                                    if (payPrice.compareTo(BigDecimal.ZERO) == 1) {
                                        shoppingOrderPay.setCashStatus(0);
                                    }

                                    ShoppingOrderPaykey shoppingOrderPaykey = new ShoppingOrderPaykey();
                                    //用户选择抵扣的积分数额
                                    if (null != reqJson.get("useIntegral") && reqJson.getInteger("useIntegral") == 1) {
//                            int deductionIntegral = reqJson.getInteger("deductionIntegral");
//                            BigDecimal deductionIntegralPrice = reqJson.get("deductionIntegralPrice")==null?BigDecimal.ZERO:reqJson.getBigDecimal("deductionIntegralPrice");
                                        orderform.setDeductionIntegralPrice(integralCut);
                                        orderform.setDeductionIntegral(integralValue);
                                        shoppingOrderPay.setIntegralStatus(0);

                                        //积分支付限额验证码
                                        if (null != reqJson.get("accountPointPayKey") && !"".equals(reqJson.get("accountPointPayKey"))) {
                                            shoppingOrderPaykey.setAccountPointPayKey(reqJson.getString("accountPointPayKey"));
                                        }
                                    }
                                    //会员权益抵扣金额
//                        BigDecimal deductionMemberPrice = reqJson.get("deductionMemberPrice")==null?BigDecimal.ZERO:reqJson.getBigDecimal("deductionMemberPrice");
//                        orderform.setDeductionMemberPrice(deductionMemberPrice);
                                    orderform.setDeductionMemberPrice(accountCut);

                                    if (null != reqJson.get("useBalance") && reqJson.getInteger("useBalance") == 1) {
                                        //账户余额支付金额
//                            BigDecimal deductionBalancePrice = reqJson.get("deductionBalancePrice")==null?BigDecimal.ZERO:reqJson.getBigDecimal("deductionBalancePrice");
//                            orderform.setDeductionBalancePrice(deductionBalancePrice);
                                        orderform.setDeductionBalancePrice(balanceCut);
                                        shoppingOrderPay.setBalanceStatus(0);

                                        //余额支付限额验证码
                                        if (null != reqJson.get("accountMoneyPayKey") && !"".equals(reqJson.get("accountMoneyPayKey"))) {
                                            if (null != reqJson.get("accountMoneyPayKey")) {
                                                shoppingOrderPaykey.setAccountMoneyPayKey(reqJson.getString("accountMoneyPayKey"));
                                            }
                                        }
                                    }

                                    //保存订单信息
                                    shoppingOrderformDao.insert(orderform);

                                    //保存资产业务key
                                    if (null != shoppingOrderPaykey.getAccountPointPayKey() || null != shoppingOrderPaykey.getAccountMoneyPayKey()) {
                                        shoppingOrderPaykey.setOfId(orderform.getId());
                                        shoppingOrderPaykeyDao.insert(shoppingOrderPaykey);
                                    }

                                    //保存订单支付信息
                                    shoppingOrderPay.setOfId(orderform.getId());
                                    shoppingOrderPayDao.insert(shoppingOrderPay);


                                    // 添加订单日志
                                    ShoppingOrderLog shoppingOrderLog = new ShoppingOrderLog();
                                    shoppingOrderLog.setLogInfo("提交订单");
//                        shoppingOrderLog.setStateInfo(orderform.getOrderType());
                                    shoppingOrderLog.setLogUserId(userId);
                                    shoppingOrderLog.setOfId(orderform.getId());
                                    shoppingOrderLogDao.insert(shoppingOrderLog);


                                    //保存订单-商品关联信息
                                    ShoppingGoodscart goodscart = new ShoppingGoodscart();
                                    String transport = reqJson.get("transport")==null?"快递":reqJson.getString("transport");

                                    //从购物车下单
                                    if (null != reqJson.get("cartId") && !"".equals(reqJson.get("cartId"))) {
                                        goodscart.setId(reqJson.getString("cartId"));
                                        goodscart = shoppingGoodscartDao.queryDetail(goodscart);
                                        goodscart.setTransport(transport);  //快递/自提
                                        goodscart.setDeductionCouponPrice(orderform.getDeductionCouponPrice());
                                        goodscart.setDeductionMemberPrice(orderform.getDeductionMemberPrice());
                                        goodscart.setDeductionIntegral(orderform.getDeductionIntegral());
                                        goodscart.setDeductionIntegralPrice(orderform.getDeductionIntegralPrice());
                                        goodscart.setDeductionBalancePrice(orderform.getDeductionBalancePrice());
                                        goodscart.setPayPrice(orderform.getPayPrice());
                                        goodscart.setShipPrice(orderform.getShipPrice());
                                        //下单时的商品价格
                                        goodscart.setPrice(currentPrice);
                                        goodscart.setOfId(orderform.getId());
                                        shoppingGoodscartDao.update(goodscart);
                                    } else {
                                        String scId = CommonUtil.getUserScId(userId);
                                        goodscart.setScId(scId);
                                        goodscart.setGoodsId(goodsId);
                                        goodscart.setCount(goodsCount);
                                        goodscart.setCartType(Const.SHOPPING_INT_CART_TYPE);
                                        if (null != reqJson.get("propertys")) {
                                            goodscart.setSpecInfo(reqJson.getString("specInfo"));
                                            goodscart.setPropertys(reqJson.getString("propertys"));
                                        }
                                        goodscart.setTransport(transport);  //快递/自提
                                        goodscart.setDeductionCouponPrice(orderform.getDeductionCouponPrice());
                                        goodscart.setDeductionMemberPrice(orderform.getDeductionMemberPrice());
                                        goodscart.setDeductionIntegral(orderform.getDeductionIntegral());
                                        goodscart.setDeductionIntegralPrice(orderform.getDeductionIntegralPrice());
                                        goodscart.setDeductionBalancePrice(orderform.getDeductionBalancePrice());
                                        goodscart.setPayPrice(orderform.getPayPrice());
                                        goodscart.setShipPrice(orderform.getShipPrice());
                                        //下单时的商品价格
                                        goodscart.setPrice(currentPrice);
                                        goodscart.setOfId(orderform.getId());
                                        shoppingGoodscartDao.insert(goodscart);
                                    }

                                    //如果该商品需要核销，则需要保存核销信息表
                                    if (transport.equals("自提")) {
                                        String gcId = goodscart.getId();
                                        ShoppingWriteoff shoppingWriteoff = new ShoppingWriteoff();
                                        shoppingWriteoff.setGcId(gcId);
                                        shoppingWriteoff.setGoodsCount(goodscart.getCount());
                                        shoppingWriteoff.setOffCode(StringUtil.randomOffCode(offcodeLength));
                                        shoppingWriteoffDao.insert(shoppingWriteoff);
                                    }

//                                //更新商品库存
//                                if (null != reqJson.get("propertys") && !"".equals(reqJson.get("propertys"))) {
//                                    ShoppingGoodsInventory shoppingGoodsInventory = new ShoppingGoodsInventory();
//                                    shoppingGoodsInventory.setGoodsId(goodsId);
//                                    shoppingGoodsInventory.setPropertys(reqJson.getString("propertys"));
//                                    shoppingGoodsInventory.setCount(inventoryCount - goodsCount);
//                                    shoppingGoodsInventoryDao.update(shoppingGoodsInventory);
//                                }
//                                shoppingGoods.setGoodsInventory(shoppingGoods.getGoodsInventory() - goodsCount);
//                                shoppingGoodsDao.updateGoodsInventory(shoppingGoods);

                                    //获取当前系统可用支付方式
                                    HashMap<String, Object> reqMap = new HashMap<>();
                                    reqMap.put("deleteStatus", 0);
                                    List<ShoppingPayment> payments = shoppingPaymentDao.queryList(reqMap);
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

//    /**
//     * 创建订单（积分、直接下单）
//     */
//    @Override
//    public JSONObject addIntegralOrder_new(JSONObject reqJson, HttpServletRequest request) {
//        JSONObject retJson = new JSONObject();
//        String retCode = "1";
//        String retMsg = "服务器内部错误！";
//        JSONObject bizDataJson = new JSONObject();
//        try {
//            String userId = reqJson.getString("userId");//userId
//            String mzUserId = CommonUtil.getMzUserId(userId);
//            BigDecimal orderTotalPrice = reqJson.getBigDecimal("orderTotalPrice");//订单总金额（商品费用+运费）
//            BigDecimal orderShipPrice = reqJson.getBigDecimal("orderShipPrice");//运费金额
//            BigDecimal orderPayPrice = reqJson.getBigDecimal("orderPayPrice");//待支付的现金金额
//            int orderUseIntegralValue = reqJson.get("orderUseIntegralValue") != null ? reqJson.getInteger("orderUseIntegralValue") : 0;//积分抵扣值
//            BigDecimal orderDeductionBalancePrice = reqJson.get("orderDeductionBalancePrice") != null ? reqJson.getBigDecimal("orderDeductionBalancePrice") : BigDecimal.ZERO;//账户余额抵扣金额
//            BigDecimal unitPrice = reqJson.getBigDecimal("unitPrice");//移动端传递的商品单价
//            String goodsId = reqJson.getString("goodsId");  //商品id
//            int goodsCount = reqJson.getInteger("goodsCount");  //购买的商品数量
//            //商品规格信息
//            String propertys = reqJson.get("propertys")==null?null:reqJson.getString("propertys");
//            //拼接规格属性信息
//            String specInfo = reqJson.get("specInfo")==null?null:reqJson.getString("specInfo");
//            //收货地址
//            String addressId = reqJson.get("addressId")==null?null:reqJson.getString("addressId");
//            //收货方式
//            String transport = reqJson.get("transport")==null?null:reqJson.getString("transport");
//            //用户是否打开余额抵扣开关
//            boolean useBalance = (null != reqJson.get("useBalance") && reqJson.getInteger("useBalance") == 1)?true:false;
//            //资产验证key
//            String accountPointPayKey= reqJson.get("accountPointPayKey")==null?null:reqJson.getString("accountPointPayKey");
//            String accountMoneyPayKey= reqJson.get("accountMoneyPayKey")==null?null:reqJson.getString("accountMoneyPayKey");
//            //购物车id，从购物车页面下单时会传递该值
//            String cartId= reqJson.get("cartId")==null?null:reqJson.getString("cartId");
//
//            ShoppingGoods shoppingGoods = new ShoppingGoods();
//            shoppingGoods.setId(goodsId);
//            //商品主体信息
//            shoppingGoods = shoppingGoodsDao.queryDetail(shoppingGoods);
//
//            //从购物车下单,需要判断该购物车信息是否已经在其他手设备被提交，避免造成同一购物车信息的多次提交
//            boolean cartState = false;
//            if (StringUtil.isNotNull(cartId)) {
//                ShoppingGoodscart goodscart = new ShoppingGoodscart();
//                goodscart.setId(reqJson.getString("cartId"));
//                goodscart = shoppingGoodscartDao.queryDetail(goodscart);
//                if(StringUtil.isNotNull(goodscart.getOfId())){   //有订单id则表示该购物车信息已经被提交
//                    cartState = true;
//                }
//            }
//            if(cartState){
//                retCode = "-1";
//                retMsg = "该购物车记录不存在，请刷新购物车！";
//            }else{
//                //从卖座实时查询账户积分和余额，麦座账户信息为必须，否则无法计算相关订单金额
//                UserAccountInfo userAccountInfo=accountInfoUtil.getUserAccountInfo(mzUserId);
//                if(!userAccountInfo.getAccountEnable()){
//                    retCode = "-1";
//                    retMsg = "无法获取您的账户积分和余额数据，请稍后下单！";
//                }else{
//                    bizDataJson.put("accountPoint", userAccountInfo.getAccountEnable()?userAccountInfo.getAccount_point():0);
//                    bizDataJson.put("accountMoney", userAccountInfo.getAccountEnable()?userAccountInfo.getAccountMoney():0);
//                    //查询积分和余额免密限额
//                    ShoppingAssetRule shoppingAssetRule = CommonUtil.getAssetRule();
//                    if (null != shoppingAssetRule) {
//                        bizDataJson.put("accountPointLimit", shoppingAssetRule.getPointAvoidLimit());
//                        bizDataJson.put("accountMoneyLimit", shoppingAssetRule.getAccountAvoidLimit());
//                    }
//                    //重新计算订单优惠和支付金额
////                    ShoppingOrderInfo shoppingOrderInfo = new ShoppingOrderInfo(userId,mzUserId,userAccountInfo,null,true,useBalance);
//                    SimpleOrdernfo shoppingOrderInfo = new SimpleOrdernfo(userId,mzUserId,goodsCount,propertys,true,useBalance);
////                    shoppingOrderInfo.setShoppingGoods(shoppingGoods);
////                    shoppingOrderInfo.setGoodsCount(goodsCount);
////                    shoppingOrderInfo.setPropertys(propertys);
//                    shoppingOrderInfo.setSpecInfo(specInfo);
//                    shoppingOrderInfo.setTransport(transport);
//                    shoppingOrderInfo.setAddressId(addressId);
//                    shoppingOrderInfo =shoppingOrderUtil.countIntOrderAmount(shoppingGoods,shoppingOrderInfo,userAccountInfo);
//
//                    //订单金额计算失败
//                    if(!shoppingOrderInfo.getCountSuccess()){
//                        retCode = "-1";
//                        retMsg =shoppingOrderInfo.getErrorMsg();
//                    }else{
//                        BigDecimal totalAmount = shoppingOrderInfo.getTotalAmount();
//                        BigDecimal payAmount = shoppingOrderInfo.getPayAmount();
//                        int integralValue = shoppingOrderInfo.getIntegralValue();
//                        BigDecimal balanceCut =shoppingOrderInfo.getBalanceCut();
//                        BigDecimal currentPrice =shoppingOrderInfo.getCurrentPrice();
//
//                        //订单总金额/待支付金额/扣除积分/余额抵扣值与移动端传值不一致，需要重新确认订单
//                        if (currentPrice.compareTo(unitPrice) != 0 ||totalAmount.compareTo(orderTotalPrice) != 0 || payAmount.compareTo(orderPayPrice) != 0 || integralValue != orderUseIntegralValue || balanceCut.compareTo(orderDeductionBalancePrice) != 0) {
//                            retCode = "-1";
//                            retMsg = "订单金额发生变化，请重新确认订单信息！";
//                        } else if(shoppingOrderUtil.upLimitBuy(goodsId,Const.SHOPPING_INT_CART_TYPE,shoppingGoods.getLimitBuy(),goodsCount,userId)){
//                            retCode = "-1";
//                            retMsg = "超出当前商品购买上限！";
//                        }else if(!shoppingGoods.getGoodsStatus().equals("0")){
//                            retCode = "-1";
//                            retMsg = "当前商品已下架！";
//                        }else {
//                            //处理商品库存
//                            Long stock = shoppingOrderUtil.cutGoodsStock(goodsId,propertys,goodsCount);
//                            if(stock>=0){
//                                //获取系统当前支付方式
//                                JSONArray payArray= OrderCommonUtil.getPayments();
//                                shoppingOrderInfo.setAccountPointPayKey(accountPointPayKey);
//                                shoppingOrderInfo.setAccountMoneyPayKey(accountMoneyPayKey);
//                                shoppingOrderInfo.setCartId(cartId);
//                                try{
//                                    //创建订单
//                                    String orderId = PayUtil.getOrderNo(Const.SHOPPING_INT_ORDER);
//                                    ShoppingOrderform orderform=shoppingOrderUtil.addOrder(orderId,Const.SHOPPING_INT_ORDER_TYPE,Const.SHOPPING_INT_CART_TYPE,goodsId,shoppingOrderInfo);
//
//                                    bizDataJson.put("payments", payArray);
//                                    bizDataJson.put("orderId", orderform.getOrderId());
//                                    bizDataJson.put("price", orderform.getPayPrice());
//                                    retCode = "0";
//                                    retMsg = "操作成功！";
//                                }catch (Exception e){
//                                    e.printStackTrace();
//                                    retCode = "-1";
//                                    retMsg = "订单创建失败，请稍后再试！";
//                                    //订单创建失败，返库存
//                                    shoppingOrderUtil.addGoodsStock(goodsId,propertys,goodsCount);
//                                }
//                            }else if(stock==-2L){
//                                retCode = "-1";
//                                retMsg = "库存不足！";
//                            }else{
//                                retCode = "-1";
//                                retMsg = "库存异常，请稍后再试！";
//                            }
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//            log.error(e);
//        }
//        retJson.put("retCode", retCode);
//        retJson.put("retMsg", retMsg);
//        retJson.put("bizData", bizDataJson);
//        return retJson;
//    }

    public BigDecimal buildShipPrice(ShoppingGoods shoppingGoods, int goodsCount,JSONObject addObj) {
        ShoppingTransport shoppingTransport = new ShoppingTransport();
//        //系统默认运费模板
//        shoppingTransport.setId(Const.TRANSPORT_ID);
        //根据商品配置的模板id获取运费模板详情
        shoppingTransport.setId(shoppingGoods.getTransportId());
        shoppingTransport = shoppingTransportDao.queryDetail(shoppingTransport);
        String transInfo = shoppingTransport.getTransEmsInfo() == null ? (shoppingTransport.getTransExpressInfo()==null?shoppingTransport.getTransMailInfo():shoppingTransport.getTransExpressInfo()) : shoppingTransport.getTransEmsInfo();

        BigDecimal shipPrice = BigDecimal.ZERO;

        if (shoppingTransport.getTransType() == 3){  //按地区收费
            if(addObj==null){
                shipPrice=null;
            }else{
                String cityCode = addObj.getString("city_code");
                String provinceCode = addObj.getString("province_code");
                BigDecimal areaPrice =shoppingTransportDao.queryAreaPrice(cityCode);
                if(areaPrice==null){
                    areaPrice =shoppingTransportDao.queryAreaPrice(provinceCode);
                }
                shipPrice = areaPrice==null?new BigDecimal(100):areaPrice;
            }
        }
        else if (shoppingTransport.getTransType() == 1){//按重量计费
            BigDecimal totalWeight = shoppingGoods.getGoodsWeight().multiply(new BigDecimal(goodsCount)).setScale(2, BigDecimal.ROUND_HALF_UP);
            if (totalWeight.compareTo(BigDecimal.ZERO) == 0)
                return BigDecimal.ZERO;
            if (transInfo != null) {
                JSONArray array = JSONArray.parseArray(transInfo);
                for (int i = 0; i < array.size(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    if (obj.get("city_id").equals("-1")) {
                        int transWeight = obj.getInteger("trans_weight");  //首重
                        BigDecimal transFee = obj.getBigDecimal("trans_fee");  //默认运费
                        int transAddWeight = obj.getInteger("trans_add_weight");
                        BigDecimal transAddFee = obj.getBigDecimal("trans_add_fee");

                        if (totalWeight.compareTo(new BigDecimal(transWeight)) != 1) {  //未超过首重/首体积
                            shipPrice = transFee;
                        } else {   //超过首重/首体积
                            BigDecimal diffWeight = totalWeight.subtract(new BigDecimal(transWeight));
                            BigDecimal amount = diffWeight.divide(new BigDecimal(transAddWeight), 2, RoundingMode.HALF_UP); // 向上取整
                            shipPrice = transFee.add(transAddFee.multiply(amount).setScale(2, BigDecimal.ROUND_HALF_UP));
                        }
                    }
                }
            }
        }
        else if (shoppingTransport.getTransType() == 2){//按体积计费
            BigDecimal totalVolume = shoppingGoods.getGoodsVolume().multiply(new BigDecimal(goodsCount)).setScale(2, BigDecimal.ROUND_HALF_UP);
            if (totalVolume.compareTo(BigDecimal.ZERO) == 0)
                return BigDecimal.ZERO;
            if (transInfo != null) {
                JSONArray array = JSONArray.parseArray(transInfo);
                for (int i = 0; i < array.size(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    if (obj.get("city_id").equals("-1")) {
                        int transWeight = obj.getInteger("trans_weight");  //首重
                        BigDecimal transFee = obj.getBigDecimal("trans_fee");  //默认运费
                        int transAddWeight = obj.getInteger("trans_add_weight");
                        BigDecimal transAddFee = obj.getBigDecimal("trans_add_fee");

                        if (totalVolume.compareTo(new BigDecimal(transWeight)) != 1) {  //未超过首体积
                            shipPrice = transFee;
                        } else {   //超过首体积
                            BigDecimal diffWeight = totalVolume.subtract(new BigDecimal(transWeight));
                            BigDecimal amount = diffWeight.divide(new BigDecimal(transAddWeight), 2, RoundingMode.HALF_UP); // 向上取整
                            shipPrice = transFee.add(transAddFee.multiply(amount).setScale(2, BigDecimal.ROUND_HALF_UP));
                        }
                    }
                }
            }
        }
        else if (shoppingTransport.getTransType() == 0) {//按件计费
            if (transInfo != null) {
                JSONArray array = JSONArray.parseArray(transInfo);
                for (int i = 0; i < array.size(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    if (obj.get("city_id").equals("-1")) {
                        int transWeight = obj.getInteger("trans_weight");  //首件
                        BigDecimal transFee = obj.getBigDecimal("trans_fee");  //默认运费
                        int transAddWeight = obj.getInteger("trans_add_weight");
                        BigDecimal transAddFee = obj.getBigDecimal("trans_add_fee");

                        if (goodsCount<=transWeight) {  //未超过首件
                            shipPrice = transFee;
                        } else {   //超过首件
                            BigDecimal diffWeight = new BigDecimal(goodsCount-transWeight);
                            BigDecimal amount = diffWeight.divide(new BigDecimal(transAddWeight), 2, RoundingMode.HALF_UP); // 向上取整
                            shipPrice = transFee.add(transAddFee.multiply(amount).setScale(2, BigDecimal.ROUND_HALF_UP));
                        }
                    }
                }
            }
        }
        return shipPrice;
    }

    public BigDecimal buildShipPrice(ShoppingGoods shoppingGoods, int goodsCount) {
        ShoppingTransport shoppingTransport = new ShoppingTransport();
//        //系统默认运费模板
//        shoppingTransport.setId(Const.TRANSPORT_ID);
        //根据商品配置的模板id获取运费模板详情
        shoppingTransport.setId(shoppingGoods.getTransportId());
        shoppingTransport = shoppingTransportDao.queryDetail(shoppingTransport);
        String transInfo = shoppingTransport.getTransEmsInfo() == null ? (shoppingTransport.getTransExpressInfo()==null?shoppingTransport.getTransMailInfo():shoppingTransport.getTransExpressInfo()) : shoppingTransport.getTransEmsInfo();

        BigDecimal shipPrice = BigDecimal.ZERO;

        if (shoppingTransport.getTransType() == 1){//按重量计费
            BigDecimal totalWeight = shoppingGoods.getGoodsWeight().multiply(new BigDecimal(goodsCount)).setScale(2, BigDecimal.ROUND_HALF_UP);
            if (totalWeight.compareTo(BigDecimal.ZERO) == 0)
                return BigDecimal.ZERO;
            if (transInfo != null) {
                JSONArray array = JSONArray.parseArray(transInfo);
                for (int i = 0; i < array.size(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    if (obj.get("city_id").equals("-1")) {
                        int transWeight = obj.getInteger("trans_weight");  //首重
                        BigDecimal transFee = obj.getBigDecimal("trans_fee");  //默认运费
                        int transAddWeight = obj.getInteger("trans_add_weight");
                        BigDecimal transAddFee = obj.getBigDecimal("trans_add_fee");

                        if (totalWeight.compareTo(new BigDecimal(transWeight)) != 1) {  //未超过首重/首体积
                            shipPrice = transFee;
                        } else {   //超过首重/首体积
                            BigDecimal diffWeight = totalWeight.subtract(new BigDecimal(transWeight));
                            BigDecimal amount = diffWeight.divide(new BigDecimal(transAddWeight), 2, RoundingMode.HALF_UP); // 向上取整
                            shipPrice = transFee.add(transAddFee.multiply(amount).setScale(2, BigDecimal.ROUND_HALF_UP));
                        }
                    }
                }
            }
        }
        else if (shoppingTransport.getTransType() == 2){//按体积计费
            BigDecimal totalVolume = shoppingGoods.getGoodsVolume().multiply(new BigDecimal(goodsCount)).setScale(2, BigDecimal.ROUND_HALF_UP);
            if (totalVolume.compareTo(BigDecimal.ZERO) == 0)
                return BigDecimal.ZERO;
            if (transInfo != null) {
                JSONArray array = JSONArray.parseArray(transInfo);
                for (int i = 0; i < array.size(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    if (obj.get("city_id").equals("-1")) {
                        int transWeight = obj.getInteger("trans_weight");  //首重
                        BigDecimal transFee = obj.getBigDecimal("trans_fee");  //默认运费
                        int transAddWeight = obj.getInteger("trans_add_weight");
                        BigDecimal transAddFee = obj.getBigDecimal("trans_add_fee");

                        if (totalVolume.compareTo(new BigDecimal(transWeight)) != 1) {  //未超过首体积
                            shipPrice = transFee;
                        } else {   //超过首体积
                            BigDecimal diffWeight = totalVolume.subtract(new BigDecimal(transWeight));
                            BigDecimal amount = diffWeight.divide(new BigDecimal(transAddWeight), 2, RoundingMode.HALF_UP); // 向上取整
                            shipPrice = transFee.add(transAddFee.multiply(amount).setScale(2, BigDecimal.ROUND_HALF_UP));
                        }
                    }
                }
            }
        }
        else if (shoppingTransport.getTransType() == 0) {//按件计费
            if (transInfo != null) {
                JSONArray array = JSONArray.parseArray(transInfo);
                for (int i = 0; i < array.size(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    if (obj.get("city_id").equals("-1")) {
                        int transWeight = obj.getInteger("trans_weight");  //首件
                        BigDecimal transFee = obj.getBigDecimal("trans_fee");  //默认运费
                        int transAddWeight = obj.getInteger("trans_add_weight");
                        BigDecimal transAddFee = obj.getBigDecimal("trans_add_fee");

                        if (goodsCount<=transWeight) {  //未超过首件
                            shipPrice = transFee;
                        } else {   //超过首件
                            BigDecimal diffWeight = new BigDecimal(goodsCount-transWeight);
                            BigDecimal amount = diffWeight.divide(new BigDecimal(transAddWeight), 2, RoundingMode.HALF_UP); // 向上取整
                            shipPrice = transFee.add(transAddFee.multiply(amount).setScale(2, BigDecimal.ROUND_HALF_UP));
                        }
                    }
                }
            }
        }
        return shipPrice;
    }

    public boolean dealGoodsStock(JSONObject reqJson){
        String goodsId = reqJson.getString("goodsId");  //商品id
        int goodsCount = reqJson.getInteger("goodsCount");  //购买的商品数量
//        boolean flag = true;
//        int inventoryCount = 0;
        //判断商品库存状态
        if (null != reqJson.get("propertys") && !"".equals(reqJson.get("propertys"))) {
            String inventory_key = REDIS_KEY_GOODS + goodsId+reqJson.get("propertys");
            String key = REDIS_KEY_GOODS + goodsId;
            if(redisStockService.updateStock(key,goodsCount)>=0&&redisStockService.updateStock(inventory_key,goodsCount)>=0){
                return true;
            }
        }else{
            String key = REDIS_KEY_GOODS + goodsId;
            if(redisStockService.updateStock(key,goodsCount)>=0){
                return true;
            }
        }
        return false;
    }

    //是否超出限购
    public Boolean upLimitBuy(String goodsId,int count,String userId) {

        ShoppingGoods shoppingGoods = new ShoppingGoods();
        shoppingGoods.setId(goodsId);
        //查询商品主体信息
        shoppingGoods = shoppingGoodsDao.queryDetail(shoppingGoods);
        if (shoppingGoods.getLimitBuy() == 0) {  //商品不限购
            return false;
        } else {
            int limitBuy = shoppingGoods.getLimitBuy();  //限购数量
            //已提交订单内的该商品数量
            int cartCount = 0;
            String scId = CommonUtil.getUserScId(userId);
            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("scId", scId);
            reqMap.put("goodsId", goodsId);
            reqMap.put("userId", userId);
            reqMap.put("deleteStatus", "0");
            List<ShoppingGoodscart> shoppingGoodscartList = shoppingGoodscartDao.queryBuyList(reqMap);

            for (ShoppingGoodscart shoppingGoodscart : shoppingGoodscartList) {
                cartCount += shoppingGoodscart.getCount();
            }
            if (cartCount + count > limitBuy) {
                return true;
            } else {
                return false;
            }

        }
    }
}
