package com.centit.shopping.biz.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.biz.service.HomeService;
import com.centit.shopping.common.enums.Const;
import com.centit.shopping.dao.*;
import com.centit.shopping.po.*;
import com.centit.shopping.utils.CRMService;
import com.centit.shopping.utils.CommonUtil;
import com.centit.shopping.utils.MZService;
import com.centit.shopping.utils.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>首页数据<p>
 *
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务实现类
 * @Date : 2021-02-24
 **/
@Transactional
@Service
public class HomeServiceImpl implements HomeService {
    public static final Log log = LogFactory.getLog(HomeService.class);


    @Resource
    private ShoppingRecommendDao shoppingRecommendDao;
    @Resource
    private ShoppingStorecartDao shoppingStorecartDao;
    @Resource
    private ShoppingHomeGoodsDao shoppingHomeGoodsDao;
    @Resource
    private ShoppingOrderformDao shoppingOrderformDao;
    @Resource
    private ShoppingFavoriteDao shoppingFavoriteDao;
    @Resource
    private ShoppingGoodsDao shoppingGoodsDao;
    @Resource
    private ShoppingArtactivityDao shoppingArtactivityDao;
    @Resource
    private ShoppingArtplanDao shoppingArtplanDao;
    @Resource
    private ShoppingArtclassDao shoppingArtclassDao;
    @Resource
    private ShoppingHistoryDao shoppingHistoryDao;
    @Resource
    private VSearchDao vSearchDao;
    @Resource
    private ShoppingSearchHistoryDao shoppingSearchHistoryDao;
    @Resource
    private ShoppingUserDao shoppingUserDao;
    @Resource
    private ShoppingCouponDao shoppingCouponDao;
    @Resource
    private ShoppingNewUserDao shoppingNewUserDao;
    @Resource
    private ShoppingBirthUserDao shoppingBirthUserDao;
    @Resource
    private ShoppingActivityDao shoppingActivityDao;
    @Resource
    private ShoppingActivityUsertimeDao shoppingActivityUsertimeDao;
    @Resource
    private ShoppingActivityUserDao shoppingActivityUserDao;
    @Resource
    private ShoppingSignDao shoppingSignDao;

    @Resource
    private ShoppingAssetDao shoppingAssetDao;

    @Resource
    private ShoppingIntegralRecordDao shoppingIntegralRecordDao;

    @Resource
    private TDjAppointmentDao tDjAppointmentDao;

    @Resource
    private ShoppingImgtextDao shoppingImgtextDao;
    @Resource
    private ShoppingIntegralTotalDao shoppingIntegralTotalDao;
    @Resource
    private ShoppingWriteoffCouponDao shoppingWriteoffCouponDao;

    @Resource
    private TicketCouponExchangeDao ticketCouponExchangeDao;

    @Resource
    private TicketCouponExchangeRecordDao ticketCouponExchangeRecordDao;

    @Resource
    private TConcurrencySwitchDao tConcurrencySwitchDao;

    @Value("${moneyToIntegralScale}")
    private int moneyToIntegralScale;

//    @Value("${signIntegral}")
//    private int signIntegral;

    /**
     * 获取商城推荐数据
     */
    @Override
    public JSONObject homeGoodsList(JSONObject reqJson) {
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
            if(!openConcurrencySwitch()){
                String userId = reqJson.get("userId") == null ? "default" : reqJson.getString("userId");
                if (pageNo == 1) {
                    buildData(userId);
                }
                reqMap.put("userId", userId);
            }else{
                reqMap.put("userId", "default");
            }
            List<HomeGoods> objList = shoppingHomeGoodsDao.queryList(reqMap);
            for(HomeGoods homeGoods:objList){
                if(homeGoods.getType()==1){
                    ShoppingGoods goods = new ShoppingGoods();
                    goods.setId(homeGoods.getGoodsId());
                    goods = shoppingGoodsDao.queryDetail(goods);
                    if(goods.getUseIntegralSet()==1){
                        int integralValue = goods.getUseIntegralValue();
                        BigDecimal storePrice = goods.getStorePrice();
                        BigDecimal integralAmount = (new BigDecimal(integralValue).divide(new BigDecimal(moneyToIntegralScale))).setScale(2, BigDecimal.ROUND_HALF_UP);
                        BigDecimal restPrice= storePrice.subtract(integralAmount);
                        homeGoods.setRestPrice(restPrice.compareTo(BigDecimal.ZERO)>=0?restPrice:BigDecimal.ZERO);
                        homeGoods.setIntegralValue(integralValue);
                    }
                }
                else if(homeGoods.getType()==3){
                    ShoppingArtactivity artactivity = new ShoppingArtactivity();
                    artactivity.setId(homeGoods.getGoodsId());
                    artactivity = shoppingArtactivityDao.queryDetail(artactivity);
                    if(artactivity.getUseIntegralSet()==1){
                        int integralValue = artactivity.getUseIntegralValue();
                        BigDecimal storePrice = artactivity.getCurrentPrice();
                        BigDecimal integralAmount = (new BigDecimal(integralValue).divide(new BigDecimal(moneyToIntegralScale))).setScale(2, BigDecimal.ROUND_HALF_UP);
                        BigDecimal restPrice= storePrice.subtract(integralAmount);
                        homeGoods.setRestPrice(restPrice.compareTo(BigDecimal.ZERO)>=0?restPrice:BigDecimal.ZERO);
                        homeGoods.setIntegralValue(integralValue);
                    }
                }
            }
            bizDataJson.put("total", shoppingHomeGoodsDao.queryTotalCount(reqMap));
            bizDataJson.put("objList", objList);

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


    public void buildData(String userId) {
        HashMap<String, Object> reqMap = new HashMap<>();
        //查询所有商城商品
        reqMap.put("deleteStatus", "0");
        reqMap.put("goodsStatus", "0");
        List<HomeGoods> allGoods = shoppingRecommendDao.queryAllList(reqMap);
        //查询后台推荐商品
        List<ShoppingRecommend> recommends = shoppingRecommendDao.queryList(new HashMap<>());
        //查询用户已购买商品的同分类商品
        List<HomeGoods> buyGoodsList = new ArrayList<>();
        if (!userId.equals("default")) {
            //商户购物车信息
            reqMap.clear();
            reqMap.put("userId", userId);
            reqMap.put("storeId", Const.STORE_ID);
            reqMap.put("deleteStatus", '0');
            List<ShoppingStorecart> shoppingStorecartList = shoppingStorecartDao.queryList(reqMap);
            ShoppingStorecart shoppingStorecart = new ShoppingStorecart();
            if (!shoppingStorecartList.isEmpty()) {
                shoppingStorecart = shoppingStorecartList.get(0);
                String scId = shoppingStorecart.getId();
                reqMap.clear();
                reqMap.put("scId", scId);
                buyGoodsList = shoppingRecommendDao.queryBuyList(reqMap);
            }
        }

        //查询当前热卖商品
        reqMap.clear();
        reqMap.put("limit", 10);   //每种取下单数量前10的商品
        List<HomeGoods> hotGoodsList = shoppingRecommendDao.queryHotList(reqMap);

        //计算权重
        for (HomeGoods goods : allGoods) {
            goods.setUserId(userId);
            String goodsId = goods.getGoodsId();
            int type = goods.getType();
            //付款人数，目前简单计算为该商品的订单数
            reqMap.clear();
            reqMap.put("deleteStatus", "0");
            reqMap.put("goodsId", goodsId);
            goods.setOrderCount(shoppingOrderformDao.queryGoodsOrderTotalCount(reqMap));
            for (ShoppingRecommend recommend : recommends) {
                if (goodsId.equals(recommend.getGoodsId()) && type == recommend.getGoodsType()) {
                    goods.addWeight(50);  //后台推荐商品权重
                    break;
                }
            }
            for (HomeGoods buyGoods : buyGoodsList) {
                if (goodsId.equals(buyGoods.getGoodsId()) && type == buyGoods.getType()) {
                    goods.addWeight(30);  //购买过的同类商品权重
                    break;
                }
            }
            for (HomeGoods hotGoods : hotGoodsList) {
                if (goodsId.equals(hotGoods.getGoodsId()) && type == hotGoods.getType()) {
                    goods.addWeight(20);  //热销商品权重
                    break;
                }
            }
        }
        shoppingHomeGoodsDao.delete(userId);
        if(allGoods.size()>0){
            shoppingHomeGoodsDao.insertHomeGoods(allGoods);
        }

    }

    /**
     * 收藏
     */
    @Override
    public JSONObject addFavorite(JSONObject reqJson, HttpServletRequest request) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "服务器内部错误！";
        JSONObject bizDataJson = new JSONObject();
        try {

            ShoppingFavorite shoppingFavorite = JSON.parseObject(reqJson.toJSONString(), ShoppingFavorite.class);
            shoppingFavoriteDao.insert(shoppingFavorite);

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
     * 取消收藏
     */
    @Override
    public JSONObject cancelFavorite(JSONObject reqJson, HttpServletRequest request) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "服务器内部错误！";
        JSONObject bizDataJson = new JSONObject();
        try {

            ShoppingFavorite shoppingFavorite = JSON.parseObject(reqJson.toJSONString(), ShoppingFavorite.class);
            shoppingFavoriteDao.cancelFav(shoppingFavorite);

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
     * 我的收藏列表
     */
    @Override
    public JSONObject myFavList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            int pageNo = reqJson.get("pageNo") == null ? 1 : reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize") == null ? 10 : reqJson.getInteger("pageSize");
            String userId = reqJson.getString("userId");

            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("startRow", (pageNo - 1) * pageSize);
            reqMap.put("pageSize", pageSize);
            reqMap.put("userId", userId);
            reqMap.put("deleteStatus", "0");
            List<ShoppingFavorite> objList = shoppingFavoriteDao.queryList(reqMap);
            bizDataJson.put("total", shoppingFavoriteDao.queryTotalCount(reqMap));

            JSONArray resArray = new JSONArray();
            for (ShoppingFavorite shoppingFavorite : objList) {
                int goodsType = shoppingFavorite.getType();
                String goodsId = shoppingFavorite.getGoodsId();
                JSONObject resObj = new JSONObject();
                resObj.put("goodsId", goodsId);
                resObj.put("goodsType", goodsType);
                if (goodsType == 1 || goodsType == 2) {   //文创或积分商品
                    ShoppingGoods shoppingGoods = new ShoppingGoods();
                    shoppingGoods.setId(goodsId);
                    shoppingGoods = shoppingGoodsDao.queryDetail(shoppingGoods);
                    //商品不存在或已下架
                    if (shoppingGoods != null) {
                        resObj.put("goodsName", shoppingGoods.getGoodsName());  //商品名称
                        resObj.put("photoId", shoppingGoods.getGoodsMainPhotoId());  //商品图id
                        resObj.put("goodsPrice", shoppingGoods.getStorePrice());  //商品价格
                        if (shoppingGoods.getDeleteStatus().equals("1") || shoppingGoods.getGoodsStatus().equals("1")) {
                            resObj.put("invalid", true);  //失效
                        } else {
                            resObj.put("invalid", false);
                        }
                    }

                } else if (goodsType == 3) {//艺术活动
                    ShoppingArtactivity shoppingArtactivity = new ShoppingArtactivity();
                    shoppingArtactivity.setId(goodsId);
                    shoppingArtactivity = shoppingArtactivityDao.queryDetail(shoppingArtactivity);
                    if (null != shoppingArtactivity) {
                        resObj.put("goodsName", shoppingArtactivity.getActivityName());  //活动名称
                        resObj.put("photoId", shoppingArtactivity.getMainPhotoId());  //主图id
                        resObj.put("goodsPrice", shoppingArtactivity.getCurrentPrice());  //价格
                        if (shoppingArtactivity.getDeleteStatus().equals("1") || shoppingArtactivity.getActivityStatus() == 0) {
                            resObj.put("invalid", true);  //失效
                        } else {
                            resObj.put("invalid", false);
                        }
                    }

                } else if (goodsType == 5) {//爱艺计划
                    ShoppingArtplan shoppingArtplan = new ShoppingArtplan();
                    shoppingArtplan.setId(goodsId);
                    shoppingArtplan = shoppingArtplanDao.queryDetail(shoppingArtplan);
                    if (null != shoppingArtplan) {
                        resObj.put("goodsName", shoppingArtplan.getActivityName());  //活动名称
                        resObj.put("photoId", shoppingArtplan.getMainPhotoId());  //主图id
                        resObj.put("goodsPrice", shoppingArtplan.getCurrentPrice());  //价格
                        if (shoppingArtplan.getDeleteStatus().equals("1") || shoppingArtplan.getActivityStatus() == 0) {
                            resObj.put("invalid", true);  //失效
                        } else {
                            resObj.put("invalid", false);
                        }
                    }

                }else if (goodsType == 4) {//艺术培训
                    ShoppingArtclass shoppingArtclass = new ShoppingArtclass();
                    shoppingArtclass.setId(goodsId);
                    shoppingArtclass = shoppingArtclassDao.queryDetail(shoppingArtclass);
                    if (null != shoppingArtclass) {
                        resObj.put("goodsName", shoppingArtclass.getClassName());  //培训名称
                        resObj.put("photoId", shoppingArtclass.getMainPhotoId());  //主图id
                        resObj.put("goodsPrice", shoppingArtclass.getCurrentPrice());  //价格
                        if (shoppingArtclass.getDeleteStatus().equals("1") || shoppingArtclass.getClassStatus() == 0) {
                            resObj.put("invalid", true);  //失效
                        } else {
                            resObj.put("invalid", false);
                        }
                    }

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

    /**
     * 我的历史足迹列表
     */
    @Override
    public JSONObject myHistoryList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            int pageNo = reqJson.get("pageNo") == null ? 1 : reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize") == null ? 10 : reqJson.getInteger("pageSize");
            String userId = reqJson.getString("userId");

            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("startRow", (pageNo - 1) * pageSize);
            reqMap.put("pageSize", pageSize);
            reqMap.put("userId", userId);
            List<HashMap<String, Object>> objList = shoppingHistoryDao.queryMyHistoryList(reqMap);
            bizDataJson.put("total", shoppingHistoryDao.queryMyHistoryCount(reqMap));

            JSONArray resArray = new JSONArray();
            for (HashMap<String, Object> objMap : objList) {
                int goodsType = Integer.valueOf(objMap.get("type").toString());
                String goodsId = objMap.get("goods_id").toString();
                String dateStr = objMap.get("dateStr").toString();
                JSONObject resObj = new JSONObject();
                resObj.put("goodsId", goodsId);
                resObj.put("goodsType", goodsType);
                resObj.put("dateStr", dateStr);
                if (goodsType == 1 || goodsType == 2) {   //文创或积分商品
                    ShoppingGoods shoppingGoods = new ShoppingGoods();
                    shoppingGoods.setId(goodsId);
                    shoppingGoods = shoppingGoodsDao.queryDetail(shoppingGoods);
                    //商品不存在或已下架
                    if (shoppingGoods != null) {
                        resObj.put("goodsName", shoppingGoods.getGoodsName());  //商品名称
                        resObj.put("photoId", shoppingGoods.getGoodsMainPhotoId());  //商品图id
                        resObj.put("goodsPrice", shoppingGoods.getStorePrice());  //商品价格
                        if (shoppingGoods.getDeleteStatus().equals("1") || shoppingGoods.getGoodsStatus().equals("1")) {
                            resObj.put("invalid", true);  //失效
                        } else {
                            resObj.put("invalid", false);
                        }
                    }

                } else if (goodsType == 3) {//艺术活动
                    ShoppingArtactivity shoppingArtactivity = new ShoppingArtactivity();
                    shoppingArtactivity.setId(goodsId);
                    shoppingArtactivity = shoppingArtactivityDao.queryDetail(shoppingArtactivity);
                    if (null != shoppingArtactivity) {
                        resObj.put("goodsName", shoppingArtactivity.getActivityName());  //活动名称
                        resObj.put("photoId", shoppingArtactivity.getMainPhotoId());  //主图id
                        resObj.put("goodsPrice", shoppingArtactivity.getCurrentPrice());  //价格
                        if (shoppingArtactivity.getDeleteStatus().equals("1") || shoppingArtactivity.getActivityStatus() == 0) {
                            resObj.put("invalid", true);  //失效
                        } else {
                            resObj.put("invalid", false);
                        }
                    }

                } else if (goodsType == 5) {//爱艺计划
                    ShoppingArtplan shoppingArtplan = new ShoppingArtplan();
                    shoppingArtplan.setId(goodsId);
                    shoppingArtplan = shoppingArtplanDao.queryDetail(shoppingArtplan);
                    if (null != shoppingArtplan) {
                        resObj.put("goodsName", shoppingArtplan.getActivityName());  //活动名称
                        resObj.put("photoId", shoppingArtplan.getMainPhotoId());  //主图id
                        resObj.put("goodsPrice", shoppingArtplan.getCurrentPrice());  //价格
                        if (shoppingArtplan.getDeleteStatus().equals("1") || shoppingArtplan.getActivityStatus() == 0) {
                            resObj.put("invalid", true);  //失效
                        } else {
                            resObj.put("invalid", false);
                        }
                    }

                } else if (goodsType == 4) {//艺术培训
                    ShoppingArtclass shoppingArtclass = new ShoppingArtclass();
                    shoppingArtclass.setId(goodsId);
                    shoppingArtclass = shoppingArtclassDao.queryDetail(shoppingArtclass);
                    if (null != shoppingArtclass) {
                        resObj.put("goodsName", shoppingArtclass.getClassName());  //培训名称
                        resObj.put("photoId", shoppingArtclass.getMainPhotoId());  //主图id
                        resObj.put("goodsPrice", shoppingArtclass.getCurrentPrice());  //价格
                        if (shoppingArtclass.getDeleteStatus().equals("1") || shoppingArtclass.getClassStatus() == 0) {
                            resObj.put("invalid", true);  //失效
                        } else {
                            resObj.put("invalid", false);
                        }
                    }

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

    /**
     * 清除历史轨迹
     */
    @Override
    public JSONObject clearHistory(JSONObject reqJson, HttpServletRequest request) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "服务器内部错误！";
        JSONObject bizDataJson = new JSONObject();
        try {

            ShoppingHistory shoppingHistory = JSON.parseObject(reqJson.toJSONString(), ShoppingHistory.class);
            shoppingHistoryDao.delete(shoppingHistory);

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
     * 获取搜索热词
     */
    @Override
    public JSONObject hotSearchWords(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            List<String> words = new ArrayList<>();
            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("limitNum", 10);
            List<HashMap<String, Object>> objList = shoppingSearchHistoryDao.queryHotWords(reqMap);
            for (HashMap<String, Object> map : objList) {
                words.add(map.get("str").toString());
            }
            bizDataJson.put("objList", words);
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
     * 全局搜素
     */
    @Override
    public JSONObject allSearch(JSONObject reqJson) {
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

            List<VSearch> objList = vSearchDao.queryList(reqMap);
            bizDataJson.put("total", vSearchDao.queryTotalCount(reqMap));
            bizDataJson.put("objList", objList);

            //记录搜索词
            if (null != reqMap.get("str") && !"".equals(reqMap.get("str"))) {
                ShoppingSearchHistory shoppingSearchHistory = new ShoppingSearchHistory();
                shoppingSearchHistory.setStr(reqMap.get("str").toString());
                shoppingSearchHistory.setUserId(reqMap.get("userId") == null ? null : reqMap.get("userId").toString());
                shoppingSearchHistoryDao.insert(shoppingSearchHistory);
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
     * 领取优惠券
     */
    @Override
    public JSONObject grantCoupon(JSONObject reqJson, HttpServletRequest request) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "服务器内部错误！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String userId = reqJson.getString("userId");
            String right_No = reqJson.getString("right_No");
            ShoppingCoupon shoppingCoupon = new ShoppingCoupon();
            shoppingCoupon.setRight_No(right_No);
            shoppingCoupon = shoppingCouponDao.queryDetail(shoppingCoupon);

            ShoppingUser shoppingUser = new ShoppingUser();
            shoppingUser.setId(userId);
            shoppingUser = shoppingUserDao.queryDetail(shoppingUser);

            if (shoppingCoupon.getPerLimit() > 0) {
                int num = 0;
                if (null != shoppingUser && null != shoppingUser.getMobile()) {
                    JSONArray resArray = CRMService.getUserCouponList(userId,shoppingUser.getMobile(),null);
                    for (int i = 0; i < resArray.size(); i++) {
                        JSONObject obj = resArray.getJSONObject(i);
                        if (obj.get("right_No").equals(right_No)) {
                            num++;
                        }
                    }
                }
                if (num >= shoppingCoupon.getPerLimit()) {
                    retCode = "-1";
                    retMsg = "该优惠券每人至多领取" + shoppingCoupon.getPerLimit() + "张，您的领取数量已达上限！";
                } else {
                    JSONObject resObj = CRMService.grantCoupon(shoppingUser.getMobile(), right_No);
                    if (resObj!=null&&resObj.get("result").equals("ok")) {
                        retCode = "0";
                        retMsg = "操作成功！";
                    } else if(resObj!=null){
                        retMsg = resObj.getString("msg");
                    }

                }
            } else {
                JSONObject resObj = CRMService.grantCoupon(shoppingUser.getMobile(), right_No);
                if (resObj!=null&&resObj.get("result").equals("ok")) {
                    retCode = "0";
                    retMsg = "操作成功！";
                } else if(resObj!=null){
                    retMsg = resObj.getString("msg");
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
     * 首页活动弹框
     */
    @Override
    public JSONObject homeActivity(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String userId = reqJson.getString("userId");
            ShoppingUser shoppingUser = new ShoppingUser();
            shoppingUser.setId(userId);
            shoppingUser = shoppingUserDao.queryDetail(shoppingUser);

            ShoppingNewUser shoppingNewUser = new ShoppingNewUser();
            shoppingNewUser.setUserId(userId);
            shoppingNewUser = shoppingNewUserDao.queryDetail(shoppingNewUser);

            List<ShoppingCoupon> ncoupons = shoppingCouponDao.queryNewCoupon(new HashMap<>());
            if (shoppingNewUser == null&&!ncoupons.isEmpty()) {   //未发放过新用户券

                for (ShoppingCoupon shoppingCoupon : ncoupons) {
                    for (int i = 0; i < shoppingCoupon.getAcPerLimit(); i++) {
                        CRMService.grantCoupon(shoppingUser.getMobile(), shoppingCoupon.getRight_No());
                    }
                }
                shoppingNewUser = new ShoppingNewUser();
                shoppingNewUser.setUserId(userId);
                shoppingNewUserDao.insert(shoppingNewUser);

                bizDataJson.put("activityType", "new");
            } else {
                if(StringUtil.isNotNull(shoppingUser.getBirthday())){
                    //判断当前日期是否在用户的生日前后十天
                    String birttDay = shoppingUser.getBirthday();
                    int dayNum = StringUtil.differentDaysByMillisecond(birttDay);

                    ShoppingBirthUser shoppingBirthUser = new ShoppingBirthUser();
                    shoppingBirthUser.setUserId(userId);
                    shoppingBirthUser.setYearStr(StringUtil.nowYearString());
                    shoppingBirthUser = shoppingBirthUserDao.queryDetail(shoppingBirthUser);
                    List<ShoppingCoupon> bcoupons = shoppingCouponDao.queryBirthCoupon(new HashMap<>());
                    if (-10 <= dayNum && dayNum <= 10 && shoppingBirthUser == null&&!bcoupons.isEmpty()) {

                        for (ShoppingCoupon shoppingCoupon : bcoupons) {
                            for (int i = 0; i < shoppingCoupon.getAcPerLimit(); i++) {
                                CRMService.grantCoupon(shoppingUser.getMobile(), shoppingCoupon.getRight_No());
                            }
                        }
                        shoppingBirthUser = new ShoppingBirthUser();
                        shoppingBirthUser.setUserId(userId);
                        shoppingBirthUser.setYearStr(StringUtil.nowYearString());
                        shoppingBirthUserDao.insert(shoppingBirthUser);
                        bizDataJson.put("activityType", "birth");
                    } else {
                        bizDataJson.put("activityType", "normal");
                        HashMap<String, Object> reqMap = new HashMap<>();
                        reqMap.put("deleteStatus","0");
                        reqMap.put("acStatus", 1);
                        reqMap.put("acHome", 1);
                        reqMap.put("timeStr", StringUtil.nowTimeString());
                        reqMap.put("userId", userId);

                        List<ShoppingActivity> activitys = shoppingActivityDao.queryList(reqMap);
                        for (ShoppingActivity activity : activitys) {
                            ShoppingActivityUsertime shoppingActivityUsertime = new ShoppingActivityUsertime();
                            shoppingActivityUsertime.setAcId(activity.getId());
                            shoppingActivityUsertime.setUserId(userId);
                            shoppingActivityUsertime = shoppingActivityUsertimeDao.queryDetail(shoppingActivityUsertime);
                            if (null == shoppingActivityUsertime || StringUtil.differentHoursByMillisecond(shoppingActivityUsertime.getLastTime()) > 4) {
                                reqMap.clear();
                                reqMap.put("acId",activity.getId());
                                List<ShoppingCoupon> coupons =  shoppingCouponDao.queryActivityCoupon(reqMap);
                                activity.setCoupons(coupons);

                                bizDataJson.put("activity", activity);

//                            reqMap.clear();
//                            reqMap.put("acId",activity.getId());
//                            List<ShoppingCoupon> coupons = shoppingCouponDao.queryActivityCoupon(new HashMap<>());
//                            for(ShoppingCoupon shoppingCoupon:coupons){
//                                for(int i=0;i<shoppingCoupon.getAcPerLimit();i++){
//                                    CRMService.grantCoupon(shoppingUser.getMobile(),shoppingCoupon.getRight_No());
//                                }
//                            }
                                //记录客户端展示该次活动的时间
                                if (null == shoppingActivityUsertime) {
                                    shoppingActivityUsertime = new ShoppingActivityUsertime();
                                    shoppingActivityUsertime.setAcId(activity.getId());
                                    shoppingActivityUsertime.setUserId(userId);
                                    shoppingActivityUsertime.setLastTime(StringUtil.nowTimeString());
                                    shoppingActivityUsertimeDao.insert(shoppingActivityUsertime);
                                } else {
                                    shoppingActivityUsertime.setLastTime(StringUtil.nowTimeString());
                                    shoppingActivityUsertimeDao.update(shoppingActivityUsertime);
                                }

                                break;
                            }
                        }

                    }
                }else {
                    bizDataJson.put("activityType", "normal");
                    HashMap<String, Object> reqMap = new HashMap<>();
                    reqMap.put("deleteStatus","0");
                    reqMap.put("acStatus", 1);
                    reqMap.put("acHome", 1);
                    reqMap.put("timeStr", StringUtil.nowTimeString());
                    reqMap.put("userId", userId);

                    List<ShoppingActivity> activitys = shoppingActivityDao.queryList(reqMap);
                    for (ShoppingActivity activity : activitys) {
                        ShoppingActivityUsertime shoppingActivityUsertime = new ShoppingActivityUsertime();
                        shoppingActivityUsertime.setAcId(activity.getId());
                        shoppingActivityUsertime.setUserId(userId);
                        shoppingActivityUsertime = shoppingActivityUsertimeDao.queryDetail(shoppingActivityUsertime);
                        if (null == shoppingActivityUsertime || StringUtil.differentHoursByMillisecond(shoppingActivityUsertime.getLastTime()) > 4) {
                            reqMap.clear();
                            reqMap.put("acId",activity.getId());
                            List<ShoppingCoupon> coupons =  shoppingCouponDao.queryActivityCoupon(reqMap);
                            activity.setCoupons(coupons);

                            bizDataJson.put("activity", activity);

//                            reqMap.clear();
//                            reqMap.put("acId",activity.getId());
//                            List<ShoppingCoupon> coupons = shoppingCouponDao.queryActivityCoupon(new HashMap<>());
//                            for(ShoppingCoupon shoppingCoupon:coupons){
//                                for(int i=0;i<shoppingCoupon.getAcPerLimit();i++){
//                                    CRMService.grantCoupon(shoppingUser.getMobile(),shoppingCoupon.getRight_No());
//                                }
//                            }
                            //记录客户端展示该次活动的时间
                            if (null == shoppingActivityUsertime) {
                                shoppingActivityUsertime = new ShoppingActivityUsertime();
                                shoppingActivityUsertime.setAcId(activity.getId());
                                shoppingActivityUsertime.setUserId(userId);
                                shoppingActivityUsertime.setLastTime(StringUtil.nowTimeString());
                                shoppingActivityUsertimeDao.insert(shoppingActivityUsertime);
                            } else {
                                shoppingActivityUsertime.setLastTime(StringUtil.nowTimeString());
                                shoppingActivityUsertimeDao.update(shoppingActivityUsertime);
                            }

                            break;
                        }
                    }

                }
            }

            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 一键领取活动优惠券
     */
    @Override
    public JSONObject grantActivityCoupon(JSONObject reqJson, HttpServletRequest request) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "服务器内部错误！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String userId = reqJson.getString("userId");
            ShoppingUser shoppingUser = new ShoppingUser();
            shoppingUser.setId(userId);
            shoppingUser = shoppingUserDao.queryDetail(shoppingUser);

            String activityId = reqJson.getString("activityId");
            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("acId", activityId);
            List<ShoppingCoupon> coupons = shoppingCouponDao.queryActivityCoupon(reqMap);
            for (ShoppingCoupon shoppingCoupon : coupons) {
                for (int i = 0; i < shoppingCoupon.getAcPerLimit(); i++) {
                    CRMService.grantCoupon(shoppingUser.getMobile(), shoppingCoupon.getRight_No());
                }
            }
            ShoppingActivityUser shoppingActivityUser = new ShoppingActivityUser();
            shoppingActivityUser.setAcId(activityId);
            shoppingActivityUser.setUserId(userId);
            shoppingActivityUserDao.insert(shoppingActivityUser);

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
     * 活动列表
     */
    @Override
    public JSONObject activityList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String userId = reqJson.getString("userId");

            int pageNo = reqJson.get("pageNo") == null ? 1 : reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize") == null ? 10 : reqJson.getInteger("pageSize");
            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("startRow", (pageNo - 1) * pageSize);
            reqMap.put("pageSize", pageSize);
            reqMap.put("acStatus", 1);
            reqMap.put("deleteStatus", "0");
            reqMap.put("timeStr", StringUtil.nowTimeString());

            bizDataJson.put("total", shoppingActivityDao.queryTotalCount(reqMap));
            List<ShoppingActivity> activitys = shoppingActivityDao.queryList(reqMap);
            reqMap.clear();
            reqMap.put("userId",userId);
            List<ShoppingActivityUser> shoppingActivityUsers = shoppingActivityUserDao.queryList(reqMap);
            List<String> activityIds = new ArrayList<>();
            for(ShoppingActivityUser shoppingActivityUser:shoppingActivityUsers){
                activityIds.add(shoppingActivityUser.getAcId());
            }
            for(ShoppingActivity shoppingActivity:activitys){
                reqMap.clear();
                reqMap.put("acId",shoppingActivity.getId());
                List<ShoppingCoupon> coupons =  shoppingCouponDao.queryActivityCoupon(reqMap);
                shoppingActivity.setCoupons(coupons);
                if(activityIds.contains(shoppingActivity.getId())){
                    shoppingActivity.setUserStatus(true);    //标识用户已参加过该活动
                }
            }
            bizDataJson.put("objList", activitys);
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
     * 我的优惠券
     */
    @Override
    public JSONObject userCouponList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            int pageNo = reqJson.get("pageNo") == null ? 1 : reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize") == null ? 10 : reqJson.getInteger("pageSize");

            String userId = reqJson.getString("userId");
            String flag = reqJson.getString("flag");    //0:未过期|1:过期|2:已使用
            ShoppingUser shoppingUser = new ShoppingUser();
            shoppingUser.setId(userId);
            shoppingUser = shoppingUserDao.queryDetail(shoppingUser);
            if(null !=shoppingUser.getMobile()){
                JSONArray resArray = CRMService.getUserCouponList(userId,shoppingUser.getMobile(),flag);
                if(resArray!=null){
                    JSONArray array = new JSONArray();
                    for (int i = 0; i < resArray.size(); i++) {
                        JSONObject obj = resArray.getJSONObject(i);
                        String right_No =  obj.get("right_No").toString();
                        ShoppingCoupon shoppingCoupon = new ShoppingCoupon();
                        shoppingCoupon.setRight_No(right_No);
                        ShoppingCoupon entity =shoppingCouponDao.queryDetail(shoppingCoupon);
                        if(null !=entity){
                            obj.put("detail",shoppingCouponDao.queryDetail(shoppingCoupon));
                            array.add(obj);
                        }
                    }
                    bizDataJson.put("total",array.size());
                    List<Object> res =  array.subList((pageNo - 1) * pageSize,pageNo  * pageSize>array.size()?array.size():pageNo  * pageSize);
//                    for (int i = 0; i < res.size(); i++) {
//                        JSONObject obj = (JSONObject) res.get(i);
//                        String right_No =  obj.get("right_No").toString();
//                        ShoppingCoupon shoppingCoupon = new ShoppingCoupon();
//                        shoppingCoupon.setRight_No(right_No);
//                        ShoppingCoupon entity =shoppingCouponDao.queryDetail(shoppingCoupon)
//                        if(null !=entity){
//                            obj.put("detail",shoppingCouponDao.queryDetail(shoppingCoupon));
//                        }
//                    }
                    bizDataJson.put("objList",res);
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
     * 我的商城优惠券
     */
    @Override
    public JSONObject myShoppingCouponList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            int pageNo = reqJson.get("pageNo") == null ? 1 : reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize") == null ? 10 : reqJson.getInteger("pageSize");

            String userId = reqJson.getString("userId");
            String flag = reqJson.getString("flag");    //0:未过期|1:过期|2:已使用
            ShoppingUser shoppingUser = new ShoppingUser();
            shoppingUser.setId(userId);
            shoppingUser = shoppingUserDao.queryDetail(shoppingUser);
            if(null !=shoppingUser.getMobile()){
                JSONArray resArray = CRMService.getUserCouponList(userId,shoppingUser.getMobile(),flag);
                if(resArray!=null){
                    JSONArray array = new JSONArray();
                    for (int i = 0; i < resArray.size(); i++) {
                        JSONObject obj = resArray.getJSONObject(i);
                        String right_No =  obj.get("right_No").toString();
                        ShoppingCoupon shoppingCoupon = new ShoppingCoupon();
                        shoppingCoupon.setRight_No(right_No);
                        ShoppingCoupon entity =shoppingCouponDao.queryDetail(shoppingCoupon);

                        if(null !=entity&&!entity.getRight_Type().equals("park")&&!StringUtil.isNotNull(entity.getWriteOffCount())){  //过滤掉停车券和到店消费券
                            obj.put("detail",entity);
                            array.add(obj);
                        }
                    }
                    bizDataJson.put("total",array.size());
                    List<Object> res =  array.subList((pageNo - 1) * pageSize,pageNo  * pageSize>array.size()?array.size():pageNo  * pageSize);
                    bizDataJson.put("objList",res);
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
     * 我的停车券
     */
    @Override
    public JSONObject myParkCouponList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            int pageNo = reqJson.get("pageNo") == null ? 1 : reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize") == null ? 10 : reqJson.getInteger("pageSize");

            String userId = reqJson.getString("userId");
            String flag = reqJson.getString("flag");    //0:未过期|1:过期|2:已使用
            ShoppingUser shoppingUser = new ShoppingUser();
            shoppingUser.setId(userId);
            shoppingUser = shoppingUserDao.queryDetail(shoppingUser);
            if(null !=shoppingUser.getMobile()){
                JSONArray resArray = CRMService.getUserCouponList(userId,shoppingUser.getMobile(),flag);
                if(resArray!=null){
                    JSONArray array = new JSONArray();
                    for (int i = 0; i < resArray.size(); i++) {
                        JSONObject obj = resArray.getJSONObject(i);
                        String right_No =  obj.get("right_No").toString();
                        ShoppingCoupon shoppingCoupon = new ShoppingCoupon();
                        shoppingCoupon.setRight_No(right_No);
                        ShoppingCoupon entity =shoppingCouponDao.queryDetail(shoppingCoupon);

                        if(null !=entity&&entity.getRight_Type().equals("park")){  //只展示停车券
                            obj.put("detail",entity);
                            array.add(obj);
                        }
                    }
                    bizDataJson.put("total",array.size());
                    List<Object> res =  array.subList((pageNo - 1) * pageSize,pageNo  * pageSize>array.size()?array.size():pageNo  * pageSize);
                    bizDataJson.put("objList",res);
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
     * 我的演出券
     */
    @Override
    public JSONObject myTicketCouponList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            int pageNo = reqJson.get("pageNo") == null ? 1 : reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize") == null ? 10 : reqJson.getInteger("pageSize");

            String userId = reqJson.getString("userId");
//            String flag = reqJson.getString("flag");    //0:未过期|1:过期|2:已使用
            String mzUserId= CommonUtil.getMzUserId(userId);
            JSONObject resultObj = MZService.getUserCouponList(mzUserId,pageNo,pageSize);
            if(null !=resultObj){
                bizDataJson.put("total",resultObj.get("total"));

                if(null !=resultObj.get("objList")){
                    try{
                        JSONArray array = resultObj.getJSONArray("objList");
                        for(int i=0;i<array.size();i++){
                            JSONObject obj = array.getJSONObject(i);
                            String coupon_code_start_time = obj.getString("coupon_code_start_time");
                            String coupon_code_expire_time = obj.getString("coupon_code_expire_time");
                            SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            obj.put("coupon_code_start_time",sf.format(sf.parse(coupon_code_start_time)));
                            obj.put("coupon_code_expire_time",sf.format(sf.parse(coupon_code_expire_time)));
                            bizDataJson.put("objList",array);
                        }
                    }catch (Exception e){
                        bizDataJson.put("objList",resultObj.get("objList"));
                    }

                }else{
                    bizDataJson.put("objList",resultObj.get("objList"));
                }





                retCode = "0";
                retMsg = "操作成功！";
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
     * 我的消费券
     */
    @Override
    public JSONObject myConsumeCouponList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            int pageNo = reqJson.get("pageNo") == null ? 1 : reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize") == null ? 10 : reqJson.getInteger("pageSize");

            String userId = reqJson.getString("userId");
            String flag = reqJson.getString("flag");    //0:未过期|1:过期|2:已使用
            ShoppingUser shoppingUser = new ShoppingUser();
            shoppingUser.setId(userId);
            shoppingUser = shoppingUserDao.queryDetail(shoppingUser);
            if(null !=shoppingUser.getMobile()){
                //查询系统中的消费券（配置了核销账号，即表明该优惠券为消费券）
                List<ShoppingCoupon> writeOffCoupons = shoppingCouponDao.queryWriteOffCoupon(new HashMap<>());
                List<String> ids = new ArrayList<>();
                for(ShoppingCoupon coupon:writeOffCoupons){
                    ids.add(coupon.getRight_No());
                }

                JSONArray resArray = CRMService.getUserCouponList(userId,shoppingUser.getMobile(),flag);
                if(resArray!=null){
                    JSONArray array = new JSONArray();
                    for (int i = 0; i < resArray.size(); i++) {
                        JSONObject obj = resArray.getJSONObject(i);
                        String right_No =  obj.get("right_No").toString();
                        ShoppingCoupon shoppingCoupon = new ShoppingCoupon();
                        shoppingCoupon.setRight_No(right_No);
                        ShoppingCoupon entity =shoppingCouponDao.queryDetail(shoppingCoupon);

                        if(ids.contains(right_No)){  //该券为消费券
                            obj.put("detail",entity);
                            ShoppingWriteoffCoupon shoppingWriteoffCoupon = new ShoppingWriteoffCoupon();
                            shoppingWriteoffCoupon.setRightId(obj.getString("id"));
                            shoppingWriteoffCoupon.setRightNo(right_No);
                            shoppingWriteoffCoupon = shoppingWriteoffCouponDao.queryDetail(shoppingWriteoffCoupon);
                            obj.put("offCode",shoppingWriteoffCoupon.getOffCode());
                            array.add(obj);
                        }
                    }
                    bizDataJson.put("total",array.size());
                    List<Object> res =  array.subList((pageNo - 1) * pageSize,pageNo  * pageSize>array.size()?array.size():pageNo  * pageSize);
                    bizDataJson.put("objList",res);
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
     * 领券中心
     */
    @Override
    public JSONObject couponCenter(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {

            String userId = reqJson.getString("userId");

            ShoppingUser shoppingUser = new ShoppingUser();
            shoppingUser.setId(userId);
            shoppingUser = shoppingUserDao.queryDetail(shoppingUser);
            Map<String,Integer> resMap = new HashMap<>();
            if(null !=shoppingUser.getMobile()){
                JSONArray resArray = CRMService.getUserCouponList(userId,shoppingUser.getMobile(),null);
                if(resArray!=null){
                    for (int i = 0; i < resArray.size(); i++) {
                        JSONObject obj = (JSONObject) resArray.get(i);
                        String right_No =  obj.get("right_No").toString();
                        if(null ==resMap.get(right_No)){
                            resMap.put(right_No,1);
                        }else{
                            int num=resMap.get(right_No);
                            resMap.put(right_No,num+1);
                        }
                    }
                }
            }
            //获取新人活动、生日活动以及优惠券活动的券
            List<String> ids = new ArrayList<>();
            List<ShoppingCoupon> ncoupons = shoppingCouponDao.queryNewCoupon(new HashMap<>());
            List<ShoppingCoupon> bcoupons = shoppingCouponDao.queryBirthCoupon(new HashMap<>());
            for(ShoppingCoupon shoppingCoupon:ncoupons){
                ids.add(shoppingCoupon.getRight_No());
            }
            for(ShoppingCoupon shoppingCoupon:bcoupons){
                ids.add(shoppingCoupon.getRight_No());
            }
            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("acStatus", 1);
            reqMap.put("deleteStatus", "0");
            List<ShoppingActivity> activitys = shoppingActivityDao.queryList(reqMap);
            for(ShoppingActivity shoppingActivity:activitys){
                reqMap.clear();
                reqMap.put("acId",shoppingActivity.getId());
                List<ShoppingCoupon> coupons =  shoppingCouponDao.queryActivityCoupon(reqMap);
                for(ShoppingCoupon shoppingCoupon:coupons){
                    ids.add(shoppingCoupon.getRight_No());
                }
            }
            //获取仅支持手动发放的券
            reqMap.clear();
            reqMap.put("offline","1");
            List<ShoppingCoupon> offCouponList = shoppingCouponDao.queryList(reqMap);
            for(ShoppingCoupon shoppingCoupon:offCouponList){
                ids.add(shoppingCoupon.getRight_No());
            }

            reqMap.clear();
            reqMap.put("isdelete","0");
            reqMap.put("ispub","1");
            List<ShoppingCoupon> couponList = shoppingCouponDao.queryList(reqMap);
            List<ShoppingCoupon> objList = new ArrayList<>();
            for(ShoppingCoupon shoppingCoupon:couponList){
                //已经过期的优惠券不能进入领券中心
                if(shoppingCoupon.getTime_Type().equals("normal")){
                    if(null !=shoppingCoupon.getEnd_Date()){
                        String endDate = shoppingCoupon.getEnd_Date();
                        //比较截止日期与当前日期
                        Date date1 = new Date();
//                        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
//                        Date date2 = null;
                        try {
//                            date2 = sf.parse(endDate);
                            Calendar cd = Calendar.getInstance();
                            cd.setTime(sf.parse(endDate));
                            cd.add(Calendar.DATE, 1);//增加n天
                            if(date1.getTime()>sf.parse(sf.format(cd.getTime())).getTime()){
                                continue;
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
                String right_No = shoppingCoupon.getRight_No();
                if(!ids.contains(right_No)&&(!resMap.containsKey(right_No)||resMap.get(right_No)<shoppingCoupon.getPerLimit())){
                    objList.add(shoppingCoupon);
                }
            }

            bizDataJson.put("objList",objList);
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
     * 每日签到
     */
    @Override
    public JSONObject dailySign(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String userId = reqJson.getString("userId");
            ShoppingSign shoppingSign = new ShoppingSign();
            shoppingSign.setUserId(userId);
            shoppingSign.setDateStr(StringUtil.nowDateString());
            shoppingSign = shoppingSignDao.queryDetail(shoppingSign);
            if(null !=shoppingSign){
                retMsg = "您今天已经签到过，请勿重复签到！";
            }else{
                //赠送积分
                ShoppingAsset shoppingAsset=new ShoppingAsset();
                shoppingAsset.setUserId(userId);
                shoppingAsset = shoppingAssetDao.queryDetail(shoppingAsset);
                int integral = CommonUtil.getIntegralSet("签到").getIntegral();
                if(shoppingAsset==null){
                    shoppingAsset=new ShoppingAsset();
                    shoppingAsset.setUserId(userId);
                    shoppingAsset.setIntegralValue(integral);
                    shoppingAssetDao.insert(shoppingAsset);
                }else{
                    shoppingAsset.setIntegralValue(shoppingAsset.getIntegralValue()+integral);
                    shoppingAssetDao.update(shoppingAsset);
                }

                shoppingSign = new ShoppingSign();
                shoppingSign.setUserId(userId);
                shoppingSign.setDateStr(StringUtil.nowDateString());
                shoppingSignDao.insert(shoppingSign);

                //用户积分新增记录
                ShoppingIntegralRecord shoppingIntegralRecord = new ShoppingIntegralRecord();
                shoppingIntegralRecord.setUserId(userId);
                shoppingIntegralRecord.setIntegralCount(integral);
                shoppingIntegralRecord.setRemark("签到");
                shoppingIntegralRecordDao.insert(shoppingIntegralRecord);

                retCode = "0";
                retMsg = "操作成功！";
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
     * 赠送积分接口
     */
    @Override
    public JSONObject addIntegral(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String userId = reqJson.getString("userId");
            String name = reqJson.getString("name");
//            int type = reqJson.getInteger("type");  //1：发帖

            //积分赠送设置
            ShoppingIntegralSet shoppingIntegralSet = CommonUtil.getIntegralSet(name);

            if(shoppingIntegralSet==null){
                retMsg = "未查询到积分赠送配置";
            }else{
                List<ShoppingIntegralTotal> totalList =  shoppingIntegralTotalDao.queryList(new HashMap<>());
                int total = totalList.isEmpty()?0:totalList.get(0).getDailyTotalIntegral();
                //获取当天已赠送的积分总数
                String dayStr = StringUtil.nowDateString();
                HashMap<String, Object> reqMap = new HashMap<>();
                reqMap.put("dayStr",dayStr);
                reqMap.put("userId",userId);
                int dailySum = shoppingIntegralRecordDao.queryDailySum(reqMap);
                if(total>0&&dailySum >=total){
                    retCode = "0";
                    retMsg = "本日获赠积分数额已达上限";
                }else{
                    int integral = shoppingIntegralSet.getIntegral();  //单次赠送积分数额
                    int dailymax= shoppingIntegralSet.getDailyMax();   //每日最多赠送积分数额
                    reqMap.put("remark",name);
                    int dailyCount = shoppingIntegralRecordDao.queryDailySum(reqMap);
                    if(dailymax>0&&dailyCount >=dailymax){
                        retCode = "0";
                        retMsg = "本日获赠积分数额已达上限";
                    }else{
                        if(total>0&&dailySum+integral>total){
                            integral = total-dailySum;
                        }
                        if(dailymax>0&&dailyCount+integral>dailymax){
                            integral=dailymax-dailyCount;
                        }
                        ShoppingAsset shoppingAsset=new ShoppingAsset();
                        shoppingAsset.setUserId(userId);
                        shoppingAsset = shoppingAssetDao.queryDetail(shoppingAsset);
                        if(shoppingAsset==null){
                            shoppingAsset=new ShoppingAsset();
                            shoppingAsset.setUserId(userId);
                            shoppingAsset.setIntegralValue(integral);
                            shoppingAssetDao.insert(shoppingAsset);
                        }else{
                            shoppingAsset.setIntegralValue(shoppingAsset.getIntegralValue()+integral);
                            shoppingAssetDao.update(shoppingAsset);
                        }

                        //用户积分新增记录
                        ShoppingIntegralRecord shoppingIntegralRecord = new ShoppingIntegralRecord();
                        shoppingIntegralRecord.setUserId(userId);
                        shoppingIntegralRecord.setIntegralCount(integral);
                        shoppingIntegralRecord.setRemark(name);
                        shoppingIntegralRecordDao.insert(shoppingIntegralRecord);

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
     * 提交党建预约
     */
    @Override
    public JSONObject addAppointment(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            TDjAppointment tDjAppointment = JSON.parseObject(reqJson.toJSONString(), TDjAppointment.class);
            tDjAppointmentDao.insert(tDjAppointment);
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
     * 我的党建预约列表
     */
    @Override
    public JSONObject myAppointmentList(JSONObject reqJson) {
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

            bizDataJson.put("total", tDjAppointmentDao.queryTotalCount(reqMap));
            bizDataJson.put("objList", tDjAppointmentDao.queryList(reqMap));

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
     * 获取图文内容
     */
    @Override
    public JSONObject getImgtext(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("type",reqJson.getString("type"));
            bizDataJson.put("objList", shoppingImgtextDao.queryList(reqMap));

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
     * 兑换优惠码
     */
    @Override
    public JSONObject exchangeCouponCode(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String code = reqJson.getString("code");
            String userId = reqJson.getString("userId");

            TicketCouponExchangeRecord ticketCouponExchangeRecord = new TicketCouponExchangeRecord();
            ticketCouponExchangeRecord.setCouponCode(code);
            ticketCouponExchangeRecord = ticketCouponExchangeRecordDao.queryDetail(ticketCouponExchangeRecord);
            if(null==ticketCouponExchangeRecord){
                retMsg = "您输入的兑换码无效！";
            }else{
                if(ticketCouponExchangeRecord.getExchangeMobile()!=null){
                    retMsg = "该兑换码已被使用，请勿重复兑换！";
                }else{
                    String actId = ticketCouponExchangeRecord.getActId();
                    TicketCouponExchange ticketCouponExchange = new TicketCouponExchange();
                    ticketCouponExchange.setId(actId);
                    ticketCouponExchange = ticketCouponExchangeDao.queryDetail(ticketCouponExchange);
                    if(ticketCouponExchange.getPubStatus().equals("0")||ticketCouponExchange.getIsDelete().equals("1")){
                        retMsg = "该兑换码活动尚未上架！";
                    }else{
                        boolean isTime = true;
                        if(null !=ticketCouponExchange.getStartTime()){
                            String startTime =ticketCouponExchange.getStartTime();
                            SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date startDate = sf.parse(startTime);
                            Date nowDate = new Date();
                            if(startDate.getTime()>nowDate.getTime()){
                                retMsg = "活动时间尚未开始！";
                                isTime = false;
                            }
                        }
                        if(null !=ticketCouponExchange.getEndTime()){
                            String getEndTime =ticketCouponExchange.getEndTime();
                            SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date endDate = sf.parse(getEndTime);
                            Date nowDate = new Date();
                            if(endDate.getTime()<nowDate.getTime()){
                                retMsg = "活动时间已经结束！";
                                isTime = false;
                            }
                        }
                        if(isTime){
                            if(ticketCouponExchangeRecord.getCouponPwd()==null||(StringUtil.isNotNull(reqJson.get("pwd"))&&(reqJson.getString("pwd").equals(ticketCouponExchangeRecord.getCouponPwd())))){
                                if(ticketCouponExchange.getSingleLimit()>0){  //每个用户有兑换数量限制
                                    HashMap<String, Object> reqMap = new HashMap<>();
                                    reqMap.put("actId",actId);
                                    reqMap.put("exchangeUser",userId);
                                    List<TicketCouponExchangeRecord> objList=ticketCouponExchangeRecordDao.queryList(reqMap);
                                    if(objList.size()>=ticketCouponExchange.getSingleLimit()){
                                        retMsg = "本次活动您的兑换数量已经达到上限！";
                                    }else{
                                        ShoppingUser user = CommonUtil.getShoppingUserByUserId(userId);
                                        if(MZService.bindCoupon(user.getMobile(),user.getMzuserid(),ticketCouponExchangeRecord.getPromotionId())){
                                            ticketCouponExchangeRecord.setExchangeUser(userId);
                                            ticketCouponExchangeRecord.setExchangeMobile(user.getMobile());
                                            ticketCouponExchangeRecord.setExchangeTime(StringUtil.nowTimeMilesString());
                                            ticketCouponExchangeRecordDao.update(ticketCouponExchangeRecord);
                                            retCode = "0";
                                            retMsg = "操作成功！";
                                        }else{
                                            retMsg = "兑换失败，请稍后再试！";
                                        }
                                    }
                                }else{
                                    ShoppingUser user = CommonUtil.getShoppingUserByUserId(userId);
                                    if(MZService.bindCoupon(user.getMobile(),user.getMzuserid(),ticketCouponExchangeRecord.getPromotionId())){
                                        ticketCouponExchangeRecord.setExchangeUser(userId);
                                        ticketCouponExchangeRecord.setExchangeMobile(user.getMobile());
                                        ticketCouponExchangeRecord.setExchangeTime(StringUtil.nowTimeMilesString());
                                        ticketCouponExchangeRecordDao.update(ticketCouponExchangeRecord);
                                        retCode = "0";
                                        retMsg = "操作成功！";
                                    }else{
                                        retMsg = "兑换失败，请稍后再试！";
                                    }
                                }
                            }else{
                                retMsg = "兑换密码错误！";
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
     * 获取我的兑换码记录
     */
    @Override
    public JSONObject getMyExchangeCodeRecords(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            int pageNo =reqJson.get("pageNo")==null?1:reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize")==null?10:reqJson.getInteger("pageSize");
            String userId = reqJson.getString("userId");
            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("exchangeUser",userId);
            reqMap.put("startRow", (pageNo-1)*pageSize);
            reqMap.put("pageSize", pageSize);
            bizDataJson.put("total",ticketCouponExchangeRecordDao.queryTotalCount(reqMap));
            List<TicketCouponExchangeRecord> objList=ticketCouponExchangeRecordDao.queryList(reqMap);
            bizDataJson.put("objList",objList);

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
     * 注销用户
     */
    @Override
    public JSONObject userCancellation(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String userId = reqJson.getString("userId");
            ShoppingUser user = new ShoppingUser();
            user.setId(userId);
            user.setIsValid("F");
            shoppingUserDao.update(user);

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

    public boolean openConcurrencySwitch(){
        try{
            TConcurrencySwitch tConcurrencySwitch= tConcurrencySwitchDao.queryDetail(null);
            if(tConcurrencySwitch !=null &&tConcurrencySwitch.getHomepageApiSwitch().equals("on")){
                return true;
            }
        }catch (Exception e){
            return false;
        }
        return false;
    }
}
