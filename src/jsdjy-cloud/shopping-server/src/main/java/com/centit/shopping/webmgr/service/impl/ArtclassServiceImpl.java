package com.centit.shopping.webmgr.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.dao.*;
import com.centit.shopping.po.*;
import com.centit.shopping.webmgr.service.AdminGoodsManageService;
import com.centit.shopping.webmgr.service.ArtclassService;
import com.centit.shopping.webmgr.service.ArtclassService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * company: www.abc.com
 * Author: 苏依林
 * Create Data: 2021/4/8
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ArtclassServiceImpl implements ArtclassService {
    public static final Log LOGGER = LogFactory.getLog(AdminGoodsManageService.class);

    @Resource
    private ShoppingArtclassDao artclassDao;

    @Resource
    private ShoppingArtclassPhotoDao artclassPhotoDao;

    @Resource
    private ShoppingArtclassInfoDao ArtclassInfoDao;

    @Resource
    private ShoppingArtinfosDao artinfosDao;

    @Resource
    private ShoppingArtclassSignupinfoDao shoppingArtclassSignupinfoDao;

    @Resource
    private ShoppingArtinfosDao shoppingArtinfosDao;

    @Override
    public JSONObject getArtclassList(JSONObject reqJson) {
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
            List list = artclassDao.queryList(reqMap);
            int total = artclassDao.queryTotalCount(reqMap);
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
    public JSONObject addArtclass(JSONObject param) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingArtclass shoppingArtclass = JSONObject.parseObject(param.toJSONString(), ShoppingArtclass.class);
            //插入活动信息
            artclassDao.insert(shoppingArtclass);
            JSONArray photos = param.getJSONArray("photos");
            if (CollectionUtils.isNotEmpty(photos)) {
                List<ShoppingArtclassPhoto> shoppingArtclassPhotos = JSONObject.parseArray(photos.toJSONString(), ShoppingArtclassPhoto.class);
                //图片插入
                for (ShoppingArtclassPhoto shoppingArtclassPhoto : shoppingArtclassPhotos) {
                    shoppingArtclassPhoto.setClassId(shoppingArtclass.getId());
                    artclassPhotoDao.insert(shoppingArtclassPhoto);
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
    public JSONObject modifyArtclass(JSONObject param) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingArtclass shoppingArtclass = JSONObject.parseObject(param.toJSONString(), ShoppingArtclass.class);
            //插入活动信息
            artclassDao.update(shoppingArtclass);
            JSONArray photos = param.getJSONArray("photos");
            if (CollectionUtils.isNotEmpty(photos)) {
                ShoppingArtclassPhoto entity = new ShoppingArtclassPhoto();
                entity.setClassId(shoppingArtclass.getId());
                artclassPhotoDao.delete(entity);
                List<ShoppingArtclassPhoto> shoppingArtclassPhotos = JSONObject.parseArray(photos.toJSONString(), ShoppingArtclassPhoto.class);
                //图片插入
                for (ShoppingArtclassPhoto shoppingArtclassPhoto : shoppingArtclassPhotos) {
                    shoppingArtclassPhoto.setClassId(shoppingArtclass.getId());
                    artclassPhotoDao.insert(shoppingArtclassPhoto);
                }
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
    public JSONObject delArtclass(JSONObject param) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String id = param.getString("id");
            ShoppingArtclass shoppingArtclass = new ShoppingArtclass();
            shoppingArtclass.setId(id);
            shoppingArtclass.setDeleteStatus("1");
            artclassDao.update(shoppingArtclass);

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
    public JSONObject removeArtclass(String id) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingArtclass shoppingArtclass = new ShoppingArtclass();
            shoppingArtclass.setId(id);
            artclassDao.delete(shoppingArtclass);
            ShoppingArtclassPhoto shoppingArtclassPhoto = new ShoppingArtclassPhoto();
            shoppingArtclassPhoto.setClassId(id);
            artclassPhotoDao.delete(shoppingArtclassPhoto);
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
            ShoppingArtclassInfo shoppingArtclassInfo = JSONObject.parseObject(param.toJSONString(), ShoppingArtclassInfo.class);
            ArtclassInfoDao.deleteByArtclassId(shoppingArtclassInfo);
            List<ShoppingArtclassInfo> ArtclassInfos = JSONObject.parseArray(param.getJSONArray("artclassInfos").toJSONString(), ShoppingArtclassInfo.class);

            for (ShoppingArtclassInfo artclassInfo : ArtclassInfos) {
                artclassInfo.setClassId(shoppingArtclassInfo.getClassId());
                ArtclassInfoDao.insert(artclassInfo);
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
            reqMap.put("classId", id);
            List<ShoppingArtclassInfo> artclassInfos = ArtclassInfoDao.queryList(reqMap);
            bizDataJson.put("objList", artclassInfos);
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
    public JSONObject queryArtclassById(String id) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingArtclass shoppingArtclass = new ShoppingArtclass();
            shoppingArtclass.setId(id);
            shoppingArtclass = artclassDao.queryDetail(shoppingArtclass);
            HashMap<String, Object> reqMap = new HashMap();
            reqMap.put("classId", id);
            List<ShoppingArtclassPhoto> shoppingArtclassPhotos = artclassPhotoDao.queryList(reqMap);
            shoppingArtclass.setPhotos(shoppingArtclassPhotos);
            bizDataJson.put("objList", shoppingArtclass);
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
    public JSONObject queryArtclassSignupinfo(JSONObject reqJson) {
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
            List<ShoppingArtclassSignupinfo> signupinfos=shoppingArtclassSignupinfoDao.querySignupInfos(reqMap);
            JSONArray objList = new JSONArray();
            for(ShoppingArtclassSignupinfo signupinfo:signupinfos){
                String infoStr=signupinfo.getSignupInfo();
                JSONArray infoArray = JSONArray.parseArray(infoStr);
                JSONObject obj =new JSONObject();
                for(int i=0;i<infoArray.size();i++){
                    JSONObject infoObj = infoArray.getJSONObject(i);
                    obj.put(infoObj.getString("inforName"),infoObj.get("inforValue"));
                }
                objList.add(obj);
            }
            bizDataJson.put("objList", objList);
            bizDataJson.put("total", shoppingArtclassSignupinfoDao.querySignupInfoCount(reqMap));

            //报名需要填写的信息项
            reqMap.clear();
            reqMap.put("classId", reqJson.getString("classId"));
            List<ShoppingArtinfos> infos = shoppingArtinfosDao.queryClassInfoList(reqMap);
            List<String> titles = new ArrayList<>();
            for(ShoppingArtinfos shoppingArtinfos:infos){
                titles.add(shoppingArtinfos.getInforName());
            }
            bizDataJson.put("titles", titles);

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
    public JSONObject putArtclass(JSONObject param) {
        JSONObject retJson = new JSONObject();
        String retCode = "0";
        String retMsg = "操作成功！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String[] ids = param.getString("ids").split(",");
            for (int i = 0; i < ids.length; i++) {
                ShoppingArtclass shoppingArtclass = new ShoppingArtclass();
                shoppingArtclass.setId(ids[i]);
                shoppingArtclass.setClassStatus(param.getInteger("classStatus"));
                artclassDao.update(shoppingArtclass);
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

}
