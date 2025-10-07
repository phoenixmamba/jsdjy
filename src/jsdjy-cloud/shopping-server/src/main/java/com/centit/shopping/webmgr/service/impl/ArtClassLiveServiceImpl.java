package com.centit.shopping.webmgr.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
import java.util.Map;

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
public class ArtClassLiveServiceImpl implements ArtClassLiveService {
    public static final Log log = LogFactory.getLog(ArtClassLiveService.class);


    @Resource
    private ShoppingArtclassLiveroomDao shoppingArtclassLiveroomDao;
    @Resource
    private ShoppingArtclassDao shoppingArtclassDao;
    @Resource
    private ShoppingArtclassLivesetDao shoppingArtclassLivesetDao;
    @Resource
    private ShoppingArtclassExtraDao shoppingArtclassExtraDao;
    @Resource
    private ShoppingUserDao shoppingUserDao;


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
            String teacherId = reqJson.getString("teacherId");
            int source = reqJson.getInteger("source");
            TLSSigAPIv2 tlsSigAPIv2 = new TLSSigAPIv2(Long.valueOf(sDKAppID), SecretKey);

            bizDataJson.put("sdkAppId", sDKAppID);
            bizDataJson.put("userSig", tlsSigAPIv2.genUserSig(teacherId, Long.valueOf(expireTime)));


//            if (source == 0) { //移动端
//                bizDataJson.put("data", tlsSigAPIv2.genUserSig(userId, Long.valueOf(expireTime)));
//            } else {  //PC端
//                ShoppingArtclassLiveroom shoppingArtclassLiveroom = new ShoppingArtclassLiveroom();
//                shoppingArtclassLiveroom.setUseridPc(userId);
//                shoppingArtclassLiveroom = shoppingArtclassLiveroomDao.queryDetail(shoppingArtclassLiveroom);
//
//                bizDataJson.put("sdkAppId", sDKAppID);
//                bizDataJson.put("userSig", tlsSigAPIv2.genUserSig(shoppingArtclassLiveroom.getUseridLive(), Long.valueOf(expireTime)));
//            }

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
            int pageNo = reqJson.get("pageNo") == null ? 1 : reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize") == null ? 10 : reqJson.getInteger("pageSize");
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("startRow", (pageNo - 1) * pageSize);
            reqMap.put("pageSize", pageSize);

            reqMap.put("deleteStatus", "0");
            reqMap.put("classStatus", 1);
            reqMap.put("isOnline", "1");
            bizDataJson.put("total", shoppingArtclassDao.queryTotalCount(reqMap));
            List<ShoppingArtclass> classList = shoppingArtclassDao.queryLiveAbleClassList(reqMap);
            JSONArray objList = new JSONArray();
            for (ShoppingArtclass shoppingArtclass : classList) {
                JSONObject obj = new JSONObject();
                obj.put("classId", shoppingArtclass.getId());
                obj.put("photoId", shoppingArtclass.getMainPhotoId());
                obj.put("className", shoppingArtclass.getClassName());
                ShoppingArtclassLiveset shoppingArtclassLiveset = new ShoppingArtclassLiveset();
                shoppingArtclassLiveset.setClassId(shoppingArtclass.getId());
                shoppingArtclassLiveset.setDeleteStatus("0");
                shoppingArtclassLiveset = shoppingArtclassLivesetDao.queryDetail(shoppingArtclassLiveset);
                if (null == shoppingArtclassLiveset) {   //未开课
                    obj.put("classState", 0);
                } else {
                    obj.put("classState", 1);
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

                }
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
     * 获取老师信息
     */
    @Override
    public JSONObject getTeacherInfo(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String classId = reqJson.getString("classId");
            String userId = reqJson.getString("userId");
            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("userId", userId);
            reqMap.put("deleteStatus", "0");
            if (!shoppingArtclassLivesetDao.queryList(reqMap).isEmpty()) {
                retMsg = "您当前已在其它课程开课！";
            } else {
                ShoppingArtclassLiveset shoppingArtclassLiveset = new ShoppingArtclassLiveset();
                shoppingArtclassLiveset.setClassId(classId);
                shoppingArtclassLiveset.setDeleteStatus("0");
                shoppingArtclassLiveset = shoppingArtclassLivesetDao.queryDetail(shoppingArtclassLiveset);
                if(null !=shoppingArtclassLiveset){
                    retMsg = "当前课程已经开课，请勿重复开课！";
                }else{
                    ShoppingArtclassLiveroom shoppingArtclassLiveroom = new ShoppingArtclassLiveroom();
                    shoppingArtclassLiveroom.setUseridPc(userId);
                    shoppingArtclassLiveroom = shoppingArtclassLiveroomDao.queryDetail(shoppingArtclassLiveroom);
                    if (null == shoppingArtclassLiveroom) {  //老师未开过课
                        shoppingArtclassLiveroom = new ShoppingArtclassLiveroom();
                        shoppingArtclassLiveroom.setUseridPc(userId);
                        shoppingArtclassLiveroom.setUseridLive(StringUtil.randomNumber(9));
                        shoppingArtclassLiveroom.setRoomId(StringUtil.randomNumber(9));
                        shoppingArtclassLiveroomDao.insert(shoppingArtclassLiveroom);
                    }

//                ShoppingArtclassLiveset shoppingArtclassLiveset = new ShoppingArtclassLiveset();
//                shoppingArtclassLiveset.setClassId(classId);
//                shoppingArtclassLiveset.setUserId(userId);
//                shoppingArtclassLivesetDao.insert(shoppingArtclassLiveset);
                    FUserinfo user = CommonUtil.getFUserInfo(userId);
                    if (null != user) {
                        bizDataJson.put("teacherName", user.getUserName());
                    }
                    bizDataJson.put("teacherId", shoppingArtclassLiveroom.getUseridLive());
                    bizDataJson.put("roomId", shoppingArtclassLiveroom.getRoomId());
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

    /**
     * 开课
     */
    @Override
    public JSONObject openClass(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String classId = reqJson.getString("classId");
            String teacherId = reqJson.getString("teacherId");

            ShoppingArtclassLiveroom shoppingArtclassLiveroom = new ShoppingArtclassLiveroom();
            shoppingArtclassLiveroom.setUseridLive(teacherId);
            shoppingArtclassLiveroom = shoppingArtclassLiveroomDao.queryDetail(shoppingArtclassLiveroom);

            String userId = shoppingArtclassLiveroom.getUseridPc();

            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("userId", userId);
            reqMap.put("deleteStatus", "0");
            if (!shoppingArtclassLivesetDao.queryList(reqMap).isEmpty()) {
                retMsg = "您当前已在其它课程开课！";
            } else {

                ShoppingArtclassLiveset shoppingArtclassLiveset = new ShoppingArtclassLiveset();
                shoppingArtclassLiveset.setClassId(classId);
                shoppingArtclassLiveset.setDeleteStatus("0");
                shoppingArtclassLiveset = shoppingArtclassLivesetDao.queryDetail(shoppingArtclassLiveset);
                if(null !=shoppingArtclassLiveset){
                    retMsg = "当前课程已经开课，请勿重复开课！";
                }else{
                    shoppingArtclassLiveset = new ShoppingArtclassLiveset();
                    shoppingArtclassLiveset.setClassId(classId);
                    shoppingArtclassLiveset.setUserId(userId);
                    shoppingArtclassLivesetDao.insert(shoppingArtclassLiveset);

//                    bizDataJson.put("teacherId", shoppingArtclassLiveroom.getUseridLive());
//                    bizDataJson.put("roomId", shoppingArtclassLiveroom.getRoomId());
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

    /**
     * 下课
     */
    @Override
    public JSONObject closeClass(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String classId = reqJson.getString("classId");
            String teacherId = reqJson.getString("teacherId");

            ShoppingArtclassLiveset shoppingArtclassLiveset = new ShoppingArtclassLiveset();
            shoppingArtclassLiveset.setClassId(classId);

            ShoppingArtclassLiveroom shoppingArtclassLiveroom = new ShoppingArtclassLiveroom();
            shoppingArtclassLiveroom.setUseridLive(teacherId);
            shoppingArtclassLiveroom = shoppingArtclassLiveroomDao.queryDetail(shoppingArtclassLiveroom);

            shoppingArtclassLiveset.setUserId(shoppingArtclassLiveroom.getUseridPc());
            shoppingArtclassLivesetDao.closeClass(shoppingArtclassLiveset);
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

//    /**
//     * 获取课程额外可听课的用户手机号
//     */
//    @Override
//    public JSONObject getClassExtraMobiles(JSONObject reqJson) {
//        JSONObject retJson = new JSONObject();
//        String retCode = "1";
//        String retMsg = "操作失败！";
//        JSONObject bizDataJson = new JSONObject();
//        try {
////            String classId = reqJson.getString("classId");
////            ShoppingArtclassExtra shoppingArtclassExtra = new ShoppingArtclassExtra();
////            shoppingArtclassExtra.setClassId(classId);
////            shoppingArtclassExtra = shoppingArtclassExtraDao.queryDetail(shoppingArtclassExtra);
////
////            bizDataJson.put("mobiles", shoppingArtclassExtra == null ? "" : shoppingArtclassExtra.getMobiles());
//            retCode = "0";
//            retMsg = "操作成功！";
//
//        } catch (Exception e) {
//            log.error(e);
//        }
//        retJson.put("retCode", retCode);
//        retJson.put("retMsg", retMsg);
//        retJson.put("bizData", bizDataJson);
//        return retJson;
//    }
//
//    /**
//     * 编辑课程额外可听课的用户手机号
//     */
//    @Override
//    public JSONObject saveClassExtraMobiles(JSONObject reqJson) {
//        JSONObject retJson = new JSONObject();
//        String retCode = "1";
//        String retMsg = "操作失败！";
//        JSONObject bizDataJson = new JSONObject();
//        try {
//            String classId = reqJson.getString("classId");
//            String mobiles = reqJson.getString("mobiles");
//            ShoppingArtclassExtra shoppingArtclassExtra = new ShoppingArtclassExtra();
//            shoppingArtclassExtra.setClassId(classId);
////            if (null == shoppingArtclassExtraDao.queryDetail(shoppingArtclassExtra)) {
////                shoppingArtclassExtra = new ShoppingArtclassExtra();
////                shoppingArtclassExtra.setClassId(classId);
////                shoppingArtclassExtra.setMobiles(mobiles);
////                shoppingArtclassExtraDao.insert(shoppingArtclassExtra);
////            } else {
////                shoppingArtclassExtra.setMobiles(mobiles);
////                shoppingArtclassExtraDao.update(shoppingArtclassExtra);
////            }
//            retCode = "0";
//            retMsg = "操作成功！";
//
//        } catch (Exception e) {
//            log.error(e);
//        }
//        retJson.put("retCode", retCode);
//        retJson.put("retMsg", retMsg);
//        retJson.put("bizData", bizDataJson);
//        return retJson;
//    }

    /**
     * 获取课程有听课权限的用户
     */
    @Override
    public JSONObject getClassUsers(JSONObject reqJson) {
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

            int type =reqJson.get("type")==null?1:reqJson.getInteger("type");
            List<ShoppingUser> users;
            if(type ==1){   //已购买课程的用户
                 users = shoppingUserDao.queryClassUserList(reqMap);
                bizDataJson.put("total", shoppingUserDao.queryClassUserCount(reqMap));
            }else{
                users = shoppingUserDao.queryClassExtraUserList(reqMap);
                bizDataJson.put("total", shoppingUserDao.queryClassExtraUserCount(reqMap));
            }
            List<HashMap<String,String>> userList= new ArrayList<>();
            for(ShoppingUser shoppingUser:users){
                HashMap<String,String> userMap = new HashMap<>();
                userMap.put("name",shoppingUser.getNickName());
                userMap.put("mobile",shoppingUser.getMobile());
                userList.add(userMap);
            }
            bizDataJson.put("users", userList);
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
     * 获取课程可选用户列表
     */
    @Override
    public JSONObject getClassToSelectUsers(JSONObject reqJson) {
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

            bizDataJson.put("total", shoppingUserDao.queryClassToSelectUserCount(reqMap));
            List<ShoppingUser> users = shoppingUserDao.queryClassToSelectUserList(reqMap);

            List<HashMap<String,String>> userList= new ArrayList<>();
            for(ShoppingUser shoppingUser:users){
                HashMap<String,String> userMap = new HashMap<>();
                userMap.put("name",shoppingUser.getNickName());
                userMap.put("mobile",shoppingUser.getMobile());
                userList.add(userMap);
            }
            bizDataJson.put("users", userList);
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
     * 添加额外听课用户
     */
    @Override
    public JSONObject addClassExtraUser(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingArtclassExtra shoppingArtclassExtra = JSON.parseObject(reqJson.toJSONString(), ShoppingArtclassExtra.class);
            shoppingArtclassExtraDao.insert(shoppingArtclassExtra);

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
     * 删除额外听课用户
     */
    @Override
    public JSONObject delClassExtraUser(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingArtclassExtra shoppingArtclassExtra = JSON.parseObject(reqJson.toJSONString(), ShoppingArtclassExtra.class);
            shoppingArtclassExtraDao.delete(shoppingArtclassExtra);

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
