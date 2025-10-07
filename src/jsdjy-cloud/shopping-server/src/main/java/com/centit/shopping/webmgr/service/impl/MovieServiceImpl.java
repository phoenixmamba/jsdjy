package com.centit.shopping.webmgr.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.dao.ShoppingMovieActorInfoDao;
import com.centit.shopping.dao.ShoppingMovieInfoDao;
import com.centit.shopping.dao.ShoppingMoviePhotoDao;
import com.centit.shopping.po.ShoppingMovieActorInfo;
import com.centit.shopping.po.ShoppingMovieInfo;
import com.centit.shopping.po.ShoppingMoviePhoto;
import com.centit.shopping.webmgr.service.MovieService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * Description:
 * Author: 苏依林
 * Create Data: 2021/4/13
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MovieServiceImpl implements MovieService {
    public static final Log log = LogFactory.getLog(MovieService.class);

    @Resource
    private ShoppingMovieInfoDao movieInfoDao;
    @Resource
    private ShoppingMovieActorInfoDao movieActorInfoDao;
    @Resource
    private ShoppingMoviePhotoDao moviePhotoDao;


    @Override
    public JSONObject getMovieList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizData = new JSONObject();
        try {
            HashMap reqParam = JSONObject.parseObject(reqJson.toJSONString(), HashMap.class);
            // 展示上映中的电影
//            reqParam.put("status", 1);
            int pageNo = reqParam.get("pageNo") == null ? 1 : reqJson.getInteger("pageNo");
            int pageSize = reqParam.get("pageSize") == null ? 10 : reqJson.getInteger("pageSize");
            Page<Object> page = PageHelper.startPage(pageNo, pageSize);
            List<ShoppingMovieInfo> shoppingMovieInfos = movieInfoDao.queryList(reqParam);
            bizData.put("objList", shoppingMovieInfos);
            bizData.put("total", page.getTotal());
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

    @Override
    public JSONObject add(JSONObject param) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizData = new JSONObject();
        try {
            ShoppingMovieInfo shoppingMovieInfo = JSON.parseObject(param.toJSONString(), ShoppingMovieInfo.class);
            movieInfoDao.insert(shoppingMovieInfo);
            if (CollectionUtils.isNotEmpty(shoppingMovieInfo.getActorInfos())) {
                // 演职人员处理
                for (ShoppingMovieActorInfo actorInfo : shoppingMovieInfo.getActorInfos()) {
                    actorInfo.setMovieId(shoppingMovieInfo.getId());
                    movieActorInfoDao.insert(actorInfo);
                }
            }
            if (CollectionUtils.isNotEmpty(shoppingMovieInfo.getPhotos())) {
                // 剧照处理
                for (ShoppingMoviePhoto moviePhoto : shoppingMovieInfo.getPhotos()) {
                    moviePhoto.setMovieId(shoppingMovieInfo.getId());
                    moviePhotoDao.insert(moviePhoto);
                }
            }
            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e);
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
            ShoppingMovieInfo shoppingMovieInfo = JSON.parseObject(param.toJSONString(), ShoppingMovieInfo.class);
            movieInfoDao.update(shoppingMovieInfo);
            if (CollectionUtils.isNotEmpty(shoppingMovieInfo.getActorInfos())) {
                // 演职人员处理
                movieActorInfoDao.deleteByMovieId(shoppingMovieInfo.getId());
                for (ShoppingMovieActorInfo actorInfo : shoppingMovieInfo.getActorInfos()) {
                    actorInfo.setMovieId(shoppingMovieInfo.getId());
                    movieActorInfoDao.insert(actorInfo);
                }
            }
            if (CollectionUtils.isNotEmpty(shoppingMovieInfo.getPhotos())) {
                // 剧照处理
                moviePhotoDao.deleteByMovieId(shoppingMovieInfo.getId());
                for (ShoppingMoviePhoto moviePhoto : shoppingMovieInfo.getPhotos()) {
                    moviePhoto.setMovieId(shoppingMovieInfo.getId());
                    moviePhotoDao.insert(moviePhoto);
                }
            }
            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e);
        }
        retJson.put("bizData", bizData);
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        return retJson;
    }

    @Override
    public JSONObject remove(JSONObject param) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizData = new JSONObject();
        try {
            ShoppingMovieInfo shoppingMovieInfo = JSON.parseObject(param.toJSONString(), ShoppingMovieInfo.class);
            movieInfoDao.delete(shoppingMovieInfo);
            movieActorInfoDao.deleteByMovieId(shoppingMovieInfo.getId());
            moviePhotoDao.deleteByMovieId(shoppingMovieInfo.getId());
            retCode = "0";
            retMsg = "操作成功！";
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e);
        }
        retJson.put("bizData", bizData);
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        return retJson;
    }

    @Override
    public JSONObject detail(String id) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizData = new JSONObject();
        try {
            // 展示上映中的电影
            ShoppingMovieInfo shoppingMovieInfo = new ShoppingMovieInfo();
            shoppingMovieInfo.setId(id);
            shoppingMovieInfo = movieInfoDao.queryDetail(shoppingMovieInfo);
            bizData.put("objList", shoppingMovieInfo);
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
