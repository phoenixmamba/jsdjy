package com.centit.shopping.biz.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.biz.service.ShoppingMovieService;
import com.centit.shopping.dao.ShoppingMovieInfoDao;
import com.centit.shopping.po.ShoppingMovieInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * Author: 苏依林
 * Create Data: 2021/4/13
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ShoppingMovieServiceImpl implements ShoppingMovieService {
    public static final Log log = LogFactory.getLog(ShoppingMovieService.class);

    @Resource
    private ShoppingMovieInfoDao movieInfoDao;


    @Override
    public JSONObject getMovieList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizData = new JSONObject();
        try {
            HashMap reqParam = JSONObject.parseObject(reqJson.toJSONString(), HashMap.class);
            // 展示上映中的电影
            reqParam.put("status", 1);
            List<ShoppingMovieInfo> shoppingMovieInfos = movieInfoDao.queryList(reqParam);
            int total = movieInfoDao.queryCount(reqParam);
            List<Map<Object, Object>> list = new ArrayList<>();
            shoppingMovieInfos.forEach(shoppingMovieInfo -> {
                Map<Object, Object> map = new HashMap<>();
                map.put("id", shoppingMovieInfo.getId());
                map.put("movieName", shoppingMovieInfo.getMovieName());
                map.put("movieImage", shoppingMovieInfo.getMovieImage());
                list.add(map);
            });
            bizData.put("movieList", list);
            bizData.put("objList", shoppingMovieInfos);
            bizData.put("total", total);
            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("bizData", bizData);
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        return retJson;
    }
}
