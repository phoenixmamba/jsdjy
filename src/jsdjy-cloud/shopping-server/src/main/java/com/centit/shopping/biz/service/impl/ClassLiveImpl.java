package com.centit.shopping.biz.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.biz.service.ClassLiveService;
import com.centit.shopping.common.enums.Const;
import com.centit.shopping.dao.*;
import com.centit.shopping.po.*;
import com.centit.shopping.utils.CommonUtil;
import com.centit.shopping.utils.StringUtil;
import com.centit.shopping.utils.TLSSigAPIv2;
import com.centit.shopping.webmgr.service.ArtClassLiveService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
public class ClassLiveImpl implements ClassLiveService {
    public static final Log log = LogFactory.getLog(ClassLiveService.class);


    @Resource
    private ShoppingArtclassLiveroomDao shoppingArtclassLiveroomDao;
    @Resource
    private ShoppingOrderformDao shoppingOrderformDao;
    @Resource
    private ShoppingArtclassDao shoppingArtclassDao;
    @Resource
    private ShoppingArtclassLivesetDao shoppingArtclassLivesetDao;
    @Resource
    private ShoppingArtclassExtraDao shoppingArtclassExtraDao;


    @Value("${artClass_SDKAppID}")
    private String sDKAppID;

    @Value("${artClass_SecretKey}")
    private String SecretKey;

    @Value("${artClass_expireTime}")
    private String expireTime;

    /**
     * 获取UserSig
     */
    @Override
    public JSONObject getUserSig(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String mobile = reqJson.getString("mobile");
            TLSSigAPIv2 tlsSigAPIv2 = new TLSSigAPIv2(Long.valueOf(sDKAppID), SecretKey);

            bizDataJson.put("sdkAppId", sDKAppID);
            bizDataJson.put("data", tlsSigAPIv2.genUserSig(mobile, Long.valueOf(expireTime)));


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
     * 获取艺教课程列表
     */
    @Override
    public JSONObject getClassList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("deleteStatus", "0");
            List<ShoppingArtclassLiveset> livesetList = shoppingArtclassLivesetDao.queryList(reqMap);
            JSONArray objList = new JSONArray();
            for(ShoppingArtclassLiveset shoppingArtclassLiveset:livesetList){
                String classId = shoppingArtclassLiveset.getClassId();
                ShoppingArtclass shoppingArtclass = new ShoppingArtclass();
                shoppingArtclass.setId(classId);
                shoppingArtclass = shoppingArtclassDao.queryDetail(shoppingArtclass);

                JSONObject obj = new JSONObject();
                obj.put("classId", shoppingArtclass.getId());
                obj.put("photoId", shoppingArtclass.getMainPhotoId());
                obj.put("className", shoppingArtclass.getClassName());

                obj.put("roomId", shoppingArtclass.getClassName());
                obj.put("classStartTIme", shoppingArtclassLiveset.getAddTime());
                String userId = shoppingArtclassLiveset.getUserId();

                ShoppingArtclassLiveroom shoppingArtclassLiveroom = new ShoppingArtclassLiveroom();
                shoppingArtclassLiveroom.setUseridPc(userId);
                shoppingArtclassLiveroom = shoppingArtclassLiveroomDao.queryDetail(shoppingArtclassLiveroom);

                FUserinfo user = CommonUtil.getFUserInfo(userId);
                if (null != user) {
                    obj.put("teacherId", shoppingArtclassLiveroom.getUseridLive());
                    obj.put("teacherName", user.getUserName());
                }

                obj.put("roomId", shoppingArtclassLiveroom.getRoomId());
                objList.add(obj);
            }
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

    /**
     * 校验用户是否可进入课程
     */
    @Override
    public JSONObject checkUser(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String classId = reqJson.getString("classId");
            String userId = reqJson.getString("userId");
            boolean res =false;

            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("classId", classId);
            reqMap.put("userId", userId);
            List<ShoppingOrderform> orders = shoppingOrderformDao.queryArtClassList(reqMap);
            for(ShoppingOrderform shoppingOrderform:orders){
                if(shoppingOrderform.getOrderId().startsWith(Const.SHOPPING_CLASS_ORDER)){
                    if(shoppingOrderform.getOrderStatus()==50){
                        res = true;
                        break;
                    }
                }else if(shoppingOrderform.getOrderId().startsWith(Const.SHOPPING_MERGE_ORDER)){
                    if(shoppingOrderform.getOrderStatus()==20||shoppingOrderform.getOrderStatus()==30||shoppingOrderform.getOrderStatus()==50
                            ||shoppingOrderform.getOrderStatus()==60||shoppingOrderform.getOrderStatus()==70){
                        res = true;
                        break;
                    }
                }
            }
            if(!res){
                ShoppingUser user = CommonUtil.getShoppingUserByUserId(userId);
                if(null !=user){
                    String mobile = user.getMobile();
                    ShoppingArtclassExtra shoppingArtclassExtra = new ShoppingArtclassExtra();
                    shoppingArtclassExtra.setClassId(classId);
                    shoppingArtclassExtra.setMobile(mobile);
                    shoppingArtclassExtra = shoppingArtclassExtraDao.queryDetail(shoppingArtclassExtra);
                    if(shoppingArtclassExtra!=null){
                        res = true;

                    }
                }
            }
            bizDataJson.put("data", res);

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
