package com.centit.shopping.webmgr.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.dao.ShoppingArtactivityDao;
import com.centit.shopping.dao.ShoppingArtclassDao;
import com.centit.shopping.dao.ShoppingCarouselDao;
import com.centit.shopping.dao.ShoppingMovieInfoDao;
import com.centit.shopping.po.ShoppingCarousel;
import com.centit.shopping.webmgr.service.ShoppingCarouselService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p><p>
 *
 * @version : 1.0
 * @Author : su_yl
 * @Description : 服务实现类
 * @Date : 2021-04-19
 **/
@Transactional
@Service
public class ShoppingCarouselServiceImpl implements ShoppingCarouselService {
    public static final Log log = LogFactory.getLog(ShoppingCarouselService.class);

    @Resource
    private ShoppingCarouselDao carouselDao;
    @Resource
    private ShoppingArtactivityDao artactivityDao;
    @Resource
    private ShoppingArtclassDao artclassDao;
    @Resource
    private ShoppingMovieInfoDao movieInfoDao;

    @Value("${goods.culGoods.firstClassId}")
    private String firstCulGoodsClassId;

    @Value("${goods.integralGoods.firstClassId}")
    private String firstIntegralGoodsClassId;

    /**
     * 查询列表
     */
    @Override
    public JSONObject queryList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            int pageNo = reqJson.get("pageNo") == null ? 1 : reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize") == null ? 10 : reqJson.getInteger("pageSize");
            HashMap reqMap = JSONObject.parseObject(reqJson.toJSONString(), HashMap.class);
            Page<Object> page = PageHelper.startPage(pageNo, pageSize);
            List list = carouselDao.queryList(reqMap);
            bizDataJson.put("objList", list);
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


    @Override
    public JSONObject add(JSONObject reqParam) {
        String retCode = "1";
        String retMsg = "操作失败！";
        ShoppingCarousel carousel = JSONObject.parseObject(reqParam.toJSONString(), ShoppingCarousel.class);

        JSONObject retJson = new JSONObject();
        JSONObject bizDataJson = new JSONObject();
        try {

            if (inspect(retCode, carousel, retJson)) return retJson;
            carouselDao.insert(carousel);
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

    @Override
    public JSONObject modify(JSONObject reqParam) {
        String retCode = "1";
        String retMsg = "操作失败！";
        ShoppingCarousel carousel = JSONObject.parseObject(reqParam.toJSONString(), ShoppingCarousel.class);
        JSONObject retJson = new JSONObject();
        JSONObject bizDataJson = new JSONObject();
        try {
            if (inspect(retCode, carousel, retJson)) return retJson;
            carouselDao.update(carousel);
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

    private boolean inspect(String retCode, ShoppingCarousel carousel, JSONObject retJson) {
        if ("1".equals(carousel.getType()) && (
                carousel.getCategory().equals("3") ||
                        carousel.getCategory().equals("4") ||
                        carousel.getCategory().equals("5") ||
                        carousel.getCategory().equals("6") ||
                        carousel.getCategory().equals("7")
        )) {
            retJson.put("retCode", retCode);
            retJson.put("retMsg", "添加类型有误");
            return true;
        }
        if ("2".equals(carousel.getType()) && (carousel.getCategory().equals("2")
        )) {
            retJson.put("retCode", retCode);
            retJson.put("retMsg", "添加类型有误");
            return true;
        }
        return false;
    }

    @Override
    public JSONObject remove(String id) {
        String retCode = "1";
        String retMsg = "操作失败！";
        ShoppingCarousel carousel = new ShoppingCarousel();
        carousel.setId(Integer.parseInt(id));
        JSONObject retJson = new JSONObject();
        JSONObject bizDataJson = new JSONObject();
        try {
            carouselDao.delete(carousel);
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

    @Override
    public JSONObject detail(String id) {
        String retCode = "1";
        String retMsg = "操作失败！";
        ShoppingCarousel carousel = new ShoppingCarousel();
        carousel.setId(Integer.parseInt(id));
        JSONObject retJson = new JSONObject();
        JSONObject bizDataJson = new JSONObject();
        try {
            carousel = carouselDao.queryDetail(carousel);
            bizDataJson.put("data", carousel);
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
                case "2":
                    // 全部演出
                    List<Map> tickets = carouselDao.queryAllTicket(reqMap);
                    tickets.forEach(map -> map.put("type", 2));
                    bizDataJson.put("data", tickets);
                    break;
                case "3":
                    reqMap.put("deleteStatus", "0");
                    reqMap.put("gcId", firstCulGoodsClassId);
                    // 全部文创商品
                    List<Map> culGoods = carouselDao.queryClassGoodsList(reqMap);
                    culGoods.forEach(map -> map.put("type", 3));
                    bizDataJson.put("data", culGoods);
                    break;
                case "4":
                    // 全部积分兑换商品
                    reqMap.put("deleteStatus", "0");
                    reqMap.put("gcId", firstIntegralGoodsClassId);
                    List<Map> integralGoods = carouselDao.queryClassGoodsList(reqMap);
                    integralGoods.forEach(map -> map.put("type", 4));
                    bizDataJson.put("data", integralGoods);
                    break;
                case "5":
                    // 全部文艺活动
                    reqMap.put("activityName", requestParametersRetJson.getString("search"));
                    List<Map> artactivities = artactivityDao.queryAllArtactivity(reqMap);
                    artactivities.forEach(map -> map.put("type", 5));
                    bizDataJson.put("data", artactivities);
                    break;
                case "6":
                    // 全部艺术课程
                    reqMap.put("className", requestParametersRetJson.getString("search"));
                    List<Map> artclasses = artclassDao.queryAllArtclass(reqMap);
                    artclasses.forEach(map -> map.put("type", 6));
                    bizDataJson.put("data", artclasses);
                    break;
                case "7":
                    // 全部电影
                    reqMap.put("movieName", requestParametersRetJson.getString("search"));
                    List<Map> movieInfos = movieInfoDao.queryAllMovie(reqMap);
                    movieInfos.forEach(map -> map.put("type", 7));
                    bizDataJson.put("data", movieInfos);
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
