package com.centit.shopping.webmgr.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.dao.*;
import com.centit.shopping.po.ShoppingRecommend;
import com.centit.shopping.webmgr.service.StoreRecommendService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * Author: 苏依林
 * Create Data: 2021/4/26
 */
@Service
public class StoreRecommendServiceImpl implements StoreRecommendService {
    public static final Log log = LogFactory.getLog(StoreRecommendService.class);

    @Resource
    private ShoppingCarouselDao carouselDao;
    @Resource
    private ShoppingArtactivityDao artactivityDao;
    @Resource
    private ShoppingArtplanDao artplanDao;
    @Resource
    private ShoppingArtclassDao artclassDao;

    @Resource
    private ShoppingRecommendDao recommendDao;

    @Value("${goods.culGoods.firstClassId}")
    private String firstCulGoodsClassId;

    @Value("${goods.integralGoods.firstClassId}")
    private String firstIntegralGoodsClassId;


    @Override
    public JSONObject getList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizData = new JSONObject();
        try {
            int pageNo = reqJson.get("pageNo") == null ? 1 : reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize") == null ? 10 : reqJson.getInteger("pageSize");
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("startRow", (pageNo - 1) * pageSize);
            reqMap.put("pageSize", pageSize);
            List<ShoppingRecommend> shoppingRecommends = recommendDao.queryListDetail(reqMap);
            bizData.put("objList", shoppingRecommends);
            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            e.printStackTrace();
        }
        retJson.put("bizData", bizData);
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        return retJson;
    }

    @Override
    public JSONObject getDetail(String id) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizData = new JSONObject();

        try {
            ShoppingRecommend entity = new ShoppingRecommend();
            entity.setId(id);
            entity = recommendDao.queryDetail(entity);
            bizData.put("data", entity);
            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            e.printStackTrace();
        }
        retJson.put("bizData", bizData);
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        return retJson;
    }

    @Override
    public JSONObject add(JSONObject param) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizData = new JSONObject();

        try {
            ShoppingRecommend entity = JSON.parseObject(param.toJSONString(), ShoppingRecommend.class);
            recommendDao.insert(entity);
            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            e.printStackTrace();
        }
        retJson.put("bizData", bizData);
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        return retJson;
    }

    @Override
    public JSONObject modify(JSONObject param) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizData = new JSONObject();

        try {
            ShoppingRecommend entity = JSON.parseObject(param.toJSONString(), ShoppingRecommend.class);
            recommendDao.update(entity);
            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            e.printStackTrace();
        }
        retJson.put("bizData", bizData);
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        return retJson;
    }

    @Override
    public JSONObject remove(String id) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizData = new JSONObject();

        try {
            ShoppingRecommend entity = new ShoppingRecommend();
            entity.setId(id);
            recommendDao.delete(entity);
            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            e.printStackTrace();
        }
        retJson.put("bizData", bizData);
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        return retJson;
    }

    @Override
    public JSONObject queryAllInfo(JSONObject requestParametersRetJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();

        try {
            HashMap<String, Object> reqMap = JSON.parseObject(requestParametersRetJson.toJSONString(), HashMap.class);
            reqMap.put("goodsName", requestParametersRetJson.getString("search"));
            int pageNo = requestParametersRetJson.get("pageNo") == null ? 1 : requestParametersRetJson.getInteger("pageNo");
            int pageSize = requestParametersRetJson.get("pageSize") == null ? 10 : requestParametersRetJson.getInteger("pageSize");
            Page<Object> page = PageHelper.startPage(pageNo, pageSize);
            switch (requestParametersRetJson.getString("category")) {
                case "1":
                    reqMap.put("deleteStatus", "0");
                    reqMap.put("gcId", firstCulGoodsClassId);
                    // 全部文创商品
                    List<Map> culGoods = carouselDao.queryClassGoodsList(reqMap);
                    culGoods.forEach(map -> map.put("type", 1));
                    bizDataJson.put("data", culGoods);
                    break;
                case "2":
                    // 全部积分兑换商品
                    reqMap.put("deleteStatus", "0");
                    reqMap.put("gcId", firstIntegralGoodsClassId);
                    List<Map> integralGoods = carouselDao.queryClassGoodsList(reqMap);
                    integralGoods.forEach(map -> map.put("type", 2));
                    bizDataJson.put("data", integralGoods);
                    break;
                case "3":
                    // 全部文艺活动
                    reqMap.put("activityName", requestParametersRetJson.getString("search"));
                    List<Map> artactivities = artactivityDao.queryAllArtactivity(reqMap);
                    artactivities.forEach(map -> map.put("type", 3));
                    bizDataJson.put("data", artactivities);
                    break;
                case "4":
                    // 全部艺术课程
                    reqMap.put("className", requestParametersRetJson.getString("search"));
                    List<Map> artclasses = artclassDao.queryAllArtclass(reqMap);
                    artclasses.forEach(map -> map.put("type", 4));
                    bizDataJson.put("data", artclasses);
                    break;
                case "6":
                    // 全部爱艺计划
                    reqMap.put("activityName", requestParametersRetJson.getString("search"));
                    List<Map> artplans = artplanDao.queryAllArtplan(reqMap);
                    artplans.forEach(map -> map.put("type", 6));
                    bizDataJson.put("data", artplans);
                    break;
            }
            bizDataJson.put("total", page.getTotal());
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
}
