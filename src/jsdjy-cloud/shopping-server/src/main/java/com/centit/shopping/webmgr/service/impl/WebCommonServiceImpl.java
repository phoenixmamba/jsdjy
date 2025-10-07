package com.centit.shopping.webmgr.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.dao.*;
import com.centit.shopping.feigin.JPushFeignClient;
import com.centit.shopping.po.*;
import com.centit.shopping.utils.*;
import com.centit.shopping.webmgr.service.WebCommonService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>系统通用接口<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务实现类
 * @Date : 2021-02-21
 **/
@Transactional
@Service
public class WebCommonServiceImpl implements WebCommonService {
    public static final Log log = LogFactory.getLog(WebCommonService.class);


    @Resource
    private ShoppingMembershipDao shoppingMembershipDao;
    @Resource
    private ShoppingGoodsDao shoppingGoodsDao;
    @Resource
    private ShoppingArtactivityDao shoppingArtactivityDao;
    @Resource
    private ShoppingArtclassDao shoppingArtclassDao;
    @Resource
    private ShoppingUserDao shoppingUserDao;
    @Resource
    private FUserinfoDao fUserinfoDao;
    @Resource
    private ShoppingIntegralSetDao shoppingIntegralSetDao;

    @Resource
    private TDjAppointmentDao tDjAppointmentDao;

    @Resource
    private JPushFeignClient jPushFeignClient;

    @Resource
    private FDatadictionaryDao fDatadictionaryDao;

    @Resource
    private ShoppingImgtextDao shoppingImgtextDao;

    @Resource
    private ShoppingIntegralTotalDao shoppingIntegralTotalDao;

    @Resource
    private TInTestDao tInTestDao;

    @Resource
    private ShoppingRechargeActivityDao shoppingRechargeActivityDao;

    @Resource
    private ShoppingRechargeActivityRecordDao shoppingRechargeActivityRecordDao;

    @Resource
    private ShoppingPayLimitDao shoppingPayLimitDao;

    @Resource
    private ShoppingAssetRuleDao shoppingAssetRuleDao;

    @Resource
    private TicketCouponBindDao ticketCouponBindDao;

    @Resource
    private TExportFileDao tExportFileDao;


    @Value("${writeOffRoleCode}")
    private String writeOffRoleCode;

    /**
     * 查询会员权益设置
     */
    @Override
    public JSONObject queryMemberShipDetail(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingMembership membership = new ShoppingMembership();
            membership= shoppingMembershipDao.queryDetail(membership);

            bizDataJson.put("data",membership);

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
     * 保存会员权益设置
     */
    @Override
    public JSONObject saveMemnerShip(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingMembership membership = JSONObject.parseObject(reqJson.toJSONString(), ShoppingMembership.class);
            shoppingMembershipDao.update(membership);

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
     * 商品推送
     */
    @Override
    public JSONObject pushGoodsMsg(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "服务器内部错误！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String goodsId = reqJson.getString("goodsId");
            int goodsType = reqJson.getInteger("goodsType");
            int pushType = reqJson.getInteger("pushType");  //1:全体；2：感兴趣的人；3：指定手机号的用户
            String message = reqJson.getString("message");
            String title = reqJson.getString("title");

//            String goodsName ="";
//            if(goodsType==(Const.SHOPPING_ACT_CART_TYPE)){
//                ShoppingArtactivity shoppingArtactivity = new ShoppingArtactivity();
//                shoppingArtactivity.setId(goodsId);
//                //活动主体信息
//                shoppingArtactivity = shoppingArtactivityDao.queryDetail(shoppingArtactivity);
//                goodsName = shoppingArtactivity.getActivityName();
//            }else if(goodsType==(Const.SHOPPING_CLASS_CART_TYPE)){
//                ShoppingArtclass shoppingArtclass = new ShoppingArtclass();
//                shoppingArtclass.setId(goodsId);
//                //活动主体信息
//                shoppingArtclass = shoppingArtclassDao.queryDetail(shoppingArtclass);
//                goodsName = shoppingArtclass.getClassName();
//
//            }else{
//                ShoppingGoods shoppingGoods=new ShoppingGoods();
//                shoppingGoods.setId(goodsId);
//                shoppingGoods = shoppingGoodsDao.queryDetail(shoppingGoods);
//                goodsName = shoppingGoods.getGoodsName();
//            }

            if(pushType==1){
                reqJson.put("title",title);
//                String message = push_Message.replaceAll("goodsName",goodsName);
                reqJson.put("message", message);
                reqJson.put("notification", message);
                JSONObject dataJson =new JSONObject();
                dataJson.put("code","shopping");
                dataJson.put("type",goodsType);
                dataJson.put("title",title);
                dataJson.put("id",goodsId);
                reqJson.put("data",dataJson);
                jPushFeignClient.pushMsgAll(reqJson);
            }else if(pushType==2){
                HashMap<String, Object> reqMap = new HashMap<>();
                reqMap.put("goodsId",goodsId);
                reqMap.put("cartType",goodsType);
                List<ShoppingUser> users = shoppingUserDao.queryGoodsUserList(reqMap);

                List<String> mobiles = new ArrayList<>();
//                for(ShoppingUser user:users){
//                    mobiles.add(user.getMobile());
//                }
                //暂时先写死几个手机号以便测试
                mobiles.add("13776407246");
                mobiles.add("13655174215");
                mobiles.add("13815897883");
                mobiles.add("18326162160");
                mobiles.add("13218479927");
                mobiles.add("17812301412");
                mobiles.add("13915940779");


                reqJson.put("mobiles",mobiles);
                reqJson.put("title",title);
//                String message = push_Message.replaceAll("goodsName",goodsName);
                reqJson.put("message", message);
                reqJson.put("notification", message);
                JSONObject dataJson =new JSONObject();
                dataJson.put("code","shopping");
                dataJson.put("type",goodsType);
                dataJson.put("title",title);
                dataJson.put("id",goodsId);
                reqJson.put("data",dataJson);
                jPushFeignClient.pushMsg(reqJson);
            }else if(pushType==3){   //指定手机号的用户
                HashMap<String, Object> reqMap = new HashMap<>();
                reqMap.put("goodsId",goodsId);
                reqMap.put("cartType",goodsType);

                String mobileStrs = reqJson.get("mobiles")==null?"":reqJson.getString("mobiles");
                String[] strs = mobileStrs.split(";");

                List<String> mobiles = new ArrayList<>();
                for(int i=0;i<strs.length;i++){
                    mobiles.add(strs[i]);
                }

                reqJson.put("mobiles",mobiles);
                reqJson.put("title",title);
//                String message = push_Message.replaceAll("goodsName",goodsName);
                reqJson.put("message", message);
                reqJson.put("notification", message);
                JSONObject dataJson =new JSONObject();
                dataJson.put("code","shopping");
                dataJson.put("type",goodsType);
                dataJson.put("title",title);
                dataJson.put("id",goodsId);
                reqJson.put("data",dataJson);
                jPushFeignClient.pushMsg(reqJson);
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
     * 全员推送
     */
    @Override
    public JSONObject pushAllMsg(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "服务器内部错误！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String message = reqJson.getString("message");
            String title = reqJson.getString("title");

            reqJson.put("title",title);
            reqJson.put("message", message);
            reqJson.put("notification", message);
//            JSONObject dataJson =new JSONObject();
//            dataJson.put("code","shopping");
//            dataJson.put("type",goodsType);
//            dataJson.put("title",title);
//            dataJson.put("id",goodsId);
//            reqJson.put("data",dataJson);
            jPushFeignClient.pushMsgAll(reqJson);

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
     * 获取具有核销权限的账户列表
     */
    @Override
    public JSONObject allWriteOffCounts(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "服务器内部错误！";
        JSONObject bizDataJson = new JSONObject();
        try {
            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("isValid","T");
            reqMap.put("roleCode",writeOffRoleCode);
            List<FUserinfo> users = fUserinfoDao.queryWriteOffCountList(reqMap);
            JSONArray objArray = new JSONArray();
            for(FUserinfo fUserinfo:users){
                JSONObject obj = new JSONObject();
                obj.put("userCode",fUserinfo.getUserCode());
                obj.put("userName",fUserinfo.getUserName());
                objArray.add(obj);
            }
            bizDataJson.put("objList",objArray);

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
     * 查询积分赠送设置设置
     */
    @Override
    public JSONObject queryIntegralSetDetail(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            //每天获取积分上限
            List<ShoppingIntegralTotal> totalList =  shoppingIntegralTotalDao.queryList(new HashMap<>());
            int total = totalList.isEmpty()?0:totalList.get(0).getDailyTotalIntegral();

            //各项获取积分设置
            List<ShoppingIntegralSet> setList = shoppingIntegralSetDao.queryList(new HashMap<>());
            bizDataJson.put("dailyTotal",total);
            bizDataJson.put("setList",setList);

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
     * 保存积分赠送每日总上限
     */
    @Override
    public JSONObject saveIntegralDailyTotal(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            int dailyTotal = reqJson.get("dailyTotal")==null?0:reqJson.getInteger("dailyTotal");
            ShoppingIntegralTotal shoppingIntegralTotal = new ShoppingIntegralTotal();
            shoppingIntegralTotal.setDailyTotalIntegral(dailyTotal);
            shoppingIntegralTotalDao.update(shoppingIntegralTotal);
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
     * 判断积分赠送项名称是否可用
     */
    @Override
    public JSONObject checkIntegralSetName(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            List<ShoppingIntegralSet> setList = shoppingIntegralSetDao.checkName(reqMap);
            if(setList.isEmpty())
                bizDataJson.put("result",true);
            else
                bizDataJson.put("result",false);
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
     * 保存积分赠送配置项
     */
    @Override
    public JSONObject addIntegralSet(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingIntegralSet shoppingIntegralSet = JSONObject.parseObject(reqJson.toJSONString(), ShoppingIntegralSet.class);
            shoppingIntegralSetDao.insert(shoppingIntegralSet);

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
     * 修改积分赠送配置项
     */
    @Override
    public JSONObject editIntegralSet(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingIntegralSet shoppingIntegralSet = JSONObject.parseObject(reqJson.toJSONString(), ShoppingIntegralSet.class);
            shoppingIntegralSetDao.update(shoppingIntegralSet);

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
     * 删除积分赠送配置项
     */
    @Override
    public JSONObject delIntegralSet(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingIntegralSet shoppingIntegralSet = JSONObject.parseObject(reqJson.toJSONString(), ShoppingIntegralSet.class);
            shoppingIntegralSetDao.delete(shoppingIntegralSet);

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
     * 党建预约列表
     */
    @Override
    public JSONObject appointmentList(JSONObject reqJson) {
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
     * 党建预约列表
     */
    @Override
    public JSONObject testInt(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            for(int i=0;i<500;i++){
                String  resStr = MZService.getProjectEvent("4603001014181");
                JSONObject resObj =CRMService.getCouponList();
                TInTest tInTest = new TInTest();
                tInTest.setMzstr(resStr);
                tInTest.setCrmstr(resObj.toString());
                tInTestDao.insert(tInTest);
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
     * 获取图文内容可选类型
     */
    @Override
    public JSONObject imgtextTypes(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {

            HashMap<String, Object> reqMap =new HashMap<>();
            reqMap.put("catalogCode","IMGTEXT_TYPE");
            List<FDatadictionary> datadictionaries= fDatadictionaryDao.queryList(reqMap);
            List<HashMap<String,String>> res=new ArrayList<>();
            for(FDatadictionary fDatadictionary:datadictionaries){
                HashMap<String,String> resMap= new HashMap<>();
                resMap.put("typeCode",fDatadictionary.getDataCode());
                resMap.put("typeName",fDatadictionary.getDataValue());
                res.add(resMap);
            }

            bizDataJson.put("objList", res);

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
     * 图文内容列表
     */
    @Override
    public JSONObject imgtextList(JSONObject reqJson) {
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

            bizDataJson.put("total", shoppingImgtextDao.queryTotalCount(reqMap));
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

//    /**
//     * 判断编码是否可用
//     */
//    @Override
//    public JSONObject checkImgtextCode(JSONObject reqJson) {
//        JSONObject retJson = new JSONObject();
//        String retCode = "1";
//        String retMsg = "操作失败！";
//        JSONObject bizDataJson = new JSONObject();
//        try {
//
//            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
//            List<ShoppingImgtext> codes = shoppingImgtextDao.queryExistCode(reqMap);
//            if(codes.isEmpty()){
//                bizDataJson.put("result", true);
//            }else{
//                bizDataJson.put("result", false);
//            }
//
//            retCode = "0";
//            retMsg = "操作成功！";
//        } catch (Exception e) {
//            log.error(e);
//        }
//        retJson.put("retCode", retCode);
//        retJson.put("retMsg", retMsg);
//        retJson.put("bizData", bizDataJson);
//        return retJson;
//    }

    /**
     * 新增图文内容
     */
    @Override
    public JSONObject saveImgtext(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingImgtext shoppingImgtext = JSONObject.parseObject(reqJson.toJSONString(), ShoppingImgtext.class);
            shoppingImgtextDao.insert(shoppingImgtext);

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
     * 编辑图文内容
     */
    @Override
    public JSONObject updateImgtext(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingImgtext shoppingImgtext = JSONObject.parseObject(reqJson.toJSONString(), ShoppingImgtext.class);
            shoppingImgtextDao.update(shoppingImgtext);

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
     * 删除图文内容
     */
    @Override
    public JSONObject delImgtext(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingImgtext shoppingImgtext = JSONObject.parseObject(reqJson.toJSONString(), ShoppingImgtext.class);
            shoppingImgtextDao.delete(shoppingImgtext);

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
     * 充值活动列表
     */
    @Override
    public JSONObject rechargeActivityList(JSONObject reqJson) {
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
            reqMap.put("isDelete", "0");

            bizDataJson.put("total", shoppingRechargeActivityDao.queryTotalCount(reqMap));
            bizDataJson.put("objList", shoppingRechargeActivityDao.queryList(reqMap));

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
     * 新增充值活动
     */
    @Override
    public JSONObject addRechargeActivity(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingRechargeActivity shoppingRechargeActivity = JSONObject.parseObject(reqJson.toJSONString(), ShoppingRechargeActivity.class);
            shoppingRechargeActivityDao.insert(shoppingRechargeActivity);

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
     * 编辑充值活动
     */
    @Override
    public JSONObject editRechargeActivity(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingRechargeActivity shoppingRechargeActivity = JSONObject.parseObject(reqJson.toJSONString(), ShoppingRechargeActivity.class);
            shoppingRechargeActivityDao.update(shoppingRechargeActivity);

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
     * 上/下架活动
     */
    @Override
    public JSONObject pubRechargeActivity(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingRechargeActivity shoppingRechargeActivity = JSONObject.parseObject(reqJson.toJSONString(), ShoppingRechargeActivity.class);
            if(shoppingRechargeActivity.getIsPub().equals("1")){  //上架活动会下架其它已上架的活动
                shoppingRechargeActivityDao.unpubActicitys(new ShoppingRechargeActivity());
                shoppingRechargeActivityDao.update(shoppingRechargeActivity);
            }else{
                shoppingRechargeActivityDao.update(shoppingRechargeActivity);
                retCode = "0";
                retMsg = "操作成功！";
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
     * 删除充值活动
     */
    @Override
    public JSONObject delRechargeActivity(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingRechargeActivity shoppingRechargeActivity = JSONObject.parseObject(reqJson.toJSONString(), ShoppingRechargeActivity.class);
            shoppingRechargeActivity.setIsPub("0");
            shoppingRechargeActivity.setIsDelete("1");
            shoppingRechargeActivityDao.update(shoppingRechargeActivity);
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
     * 充值活动已参加用户列表
     */
    @Override
    public JSONObject rechargeActivityUserList(JSONObject reqJson) {
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

            bizDataJson.put("total", shoppingRechargeActivityRecordDao.queryTotalCount(reqMap));
            bizDataJson.put("objList", shoppingRechargeActivityRecordDao.queryList(reqMap));

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
     * 获取支付限额配置
     */
    @Override
    public JSONObject getPayLimit(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            HashMap<String, Object> reqMap = new HashMap<>();
            List<ShoppingPayLimit> limits= shoppingPayLimitDao.queryList(reqMap);
            if(limits.size()==1){
                bizDataJson.put("data", limits.get(0));

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
     * 编辑支付限额
     */
    @Override
    public JSONObject editPayLimit(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingPayLimit shoppingPayLimit = JSONObject.parseObject(reqJson.toJSONString(), ShoppingPayLimit.class);
            shoppingPayLimitDao.update(shoppingPayLimit);

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
     * 获取会员资产限额规则
     */
    @Override
    public JSONObject getAssetRule(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            HashMap<String, Object> reqMap = new HashMap<>();
            List<ShoppingAssetRule> limits= shoppingAssetRuleDao.queryList(reqMap);
            if(limits.size()==1){
                bizDataJson.put("data", limits.get(0));

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
     * 同步会员资产限额规则
     */
    @Override
    public JSONObject syncAssetRule(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            //从麦座获取免密限额配置
            JSONObject limitObj = MZService.getAssetRule();
            if (null != limitObj) {
                ShoppingAssetRule shoppingAssetRule = new ShoppingAssetRule();
                if(null !=limitObj.get("point_risk_rule")){
                    JSONObject point_risk_rule= limitObj.getJSONObject("point_risk_rule");
                    if(point_risk_rule.getBoolean("point_avoid_pay")){
                        shoppingAssetRule.setPointAvoidPay("1");
                    }else{
                        shoppingAssetRule.setPointAvoidPay("0");
                    }
                    int point_avoid_limit = Integer.valueOf(point_risk_rule.getString("point_avoid_limit"));   //积分支付免密限额；
                    shoppingAssetRule.setPointAvoidLimit(point_avoid_limit);
                }
                if(null !=limitObj.get("account_risk_rule")){
                    JSONObject account_risk_rule= limitObj.getJSONObject("account_risk_rule");
                    if(account_risk_rule.getBoolean("account_avoid_pay")){
                        shoppingAssetRule.setAccountAvoidPay("1");
                    }else{
                        shoppingAssetRule.setAccountAvoidPay("0");
                    }
                    BigDecimal account_avoid_limit = new BigDecimal(account_risk_rule.getString("account_avoid_limit"));
                    shoppingAssetRule.setAccountAvoidLimit(account_avoid_limit);
                }
                shoppingAssetRuleDao.update(shoppingAssetRule);

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
     * 绑定麦座优惠码
     */
    @Override
    public JSONObject bindMZCoupon(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String phoneNumbers = reqJson.getString("phoneNumbers");
            String codePromotionId= reqJson.getString("codePromotionId").trim();
            String[] phones = phoneNumbers.split(";");
            List<String> errorPhones = new ArrayList<>();
            new Thread(()->{
                for(int i=0;i<phones.length;i++){
                    String phone = phones[i].trim();
                    ShoppingUser user = CommonUtil.getShoppingUserByMobile(phone);
                    String mzUserId = "";
                    if(user==null){
                        //通过手机号向麦座查询用户信息
                        JSONObject dataObj = MZService.getUserBaseInfoByPhone(phone);
                        if(dataObj !=null&&StringUtil.isNotNull(dataObj.get("mz_user_id")))
                            mzUserId = dataObj.getString("mz_user_id");

                        phone = phone+"(非APP用户)";
                    }else{
                        mzUserId = user.getMzuserid();
                    }

                    if(StringUtil.isNotNull(mzUserId))
                        MZService.bindCoupon(phone,mzUserId,codePromotionId);
                    else{
                        TicketCouponBind ticketCouponBind = new TicketCouponBind();
                        ticketCouponBind.setPhone(phone);
                        ticketCouponBind.setMsg("未查询到麦座用户信息");
                        ticketCouponBind.setCodePromotionId(codePromotionId);
                        CommonInit.staticTicketCouponBindDao.insert(ticketCouponBind);
                    }
//                    MZService.bindCoupon(phone,mzUserId,codePromotionId);
                }
            }).start();

            retCode = "0";
            if(errorPhones.isEmpty()){
                retMsg = "操作成功！";
            }
//            else{
//                retMsg = "以下手机号绑定优惠码失败："+errorPhones.toString();
//            }

        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 优惠码绑定记录
     */
    @Override
    public JSONObject couponBindList(JSONObject reqJson) {
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
            List<Map<String,Object>> objList = new ArrayList<>();
            if(StringUtil.isNotNull(reqJson.get("codePromotionId"))){
                bizDataJson.put("total", ticketCouponBindDao.queryTotalCount(reqMap));
                List<TicketCouponBind> bindList= ticketCouponBindDao.queryList(reqMap);

                for(TicketCouponBind ticketCouponBind:bindList){
                    Map<String,Object> objMap = new HashMap<>();
                    objMap.put("id",ticketCouponBind.getId());
                    objMap.put("addTime",ticketCouponBind.getAddTime());
                    objMap.put("phone",ticketCouponBind.getPhone());
                    objMap.put("codePromotionId",ticketCouponBind.getCodePromotionId());
                    if(ticketCouponBind.getMsg()!=null&&ticketCouponBind.getMsg().equals("success")){
                        objMap.put("status",1);
                    }else{
                        objMap.put("status",-1);
                        objMap.put("errorMsg",ticketCouponBind.getMsg());
                    }
                    objList.add(objMap);
                }
            }else{
                bizDataJson.put("total", 0);
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
     * 查询未绑定指定优惠码的用户列表
     */
    @Override
    public JSONObject couponUnBindUserList(JSONObject reqJson) {
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

            bizDataJson.put("total", ticketCouponBindDao.queryUnBindUserCount(reqMap));
            bizDataJson.put("objList", ticketCouponBindDao.queryUnBindUserList(reqMap));

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
     * 绑定麦座优惠码（按注册时间）
     */
    @Override
    public JSONObject bindMZCouponByAddTime(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String codePromotionId= reqJson.getString("codePromotionId").trim();
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            List<HashMap<String, Object>> userMapList =  ticketCouponBindDao.queryUnBindUserList(reqMap);
            new Thread(()->{
                for(HashMap<String, Object> map:userMapList){
                    String phone ="";
                    String mzUserId = "";
                    try {
                        phone = map.get("mobile").toString();
                        ShoppingUser user = CommonUtil.getShoppingUserByMobile(phone);
                        mzUserId = user.getMzuserid();
                        if(!StringUtil.isNotNull(mzUserId)){
                            //通过手机号向麦座查询用户信息
                            JSONObject dataObj = MZService.getUserBaseInfoByPhone(phone);
                            if(dataObj !=null&&StringUtil.isNotNull(dataObj.get("mz_user_id"))){
                                mzUserId = dataObj.getString("mz_user_id");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if(StringUtil.isNotNull(mzUserId))
                        MZService.bindCoupon(phone,mzUserId,codePromotionId);
                    else{
                        TicketCouponBind ticketCouponBind = new TicketCouponBind();
                        ticketCouponBind.setPhone(phone);
                        ticketCouponBind.setMsg("未查询到麦座用户信息");
                        ticketCouponBind.setCodePromotionId(codePromotionId);
                        CommonInit.staticTicketCouponBindDao.insert(ticketCouponBind);
                    }
                }
            }).start();

            retCode = "0";
            retMsg = "操作成功";

        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

    /**
     * 导出注册用户
     */
    @Override
    public JSONObject exportRegUserList(JSONObject reqJson, HttpServletResponse response) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            TExportFile tExportFile = new TExportFile();
            String fileId = String.valueOf(System.currentTimeMillis());
            tExportFile.setId(fileId);
            tExportFile.setDataType("注册用户");
            tExportFileDao.insert(tExportFile);

            ExecutorService fixPool = Executors.newFixedThreadPool(1);
            fixPool.execute(new Runnable() {
                @Override
                public void run() {
                    HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
                    reqMap.put("startTime",reqMap.get("startTime")+" 00:00:00");
                    reqMap.put("endTime",reqMap.get("endTime")+" 23:59:59");
                    List<ShoppingUser> users = shoppingUserDao.queryRegUsers(reqMap);

                    String sumStr ="注册用户";
                    // 导出表的标题
                    String title =sumStr;
                    // 导出表的列名
                    String[] rowsName =new String[]{"用户id","注册时间","手机号","用户名"};
                    List<Object[]> dataList = new ArrayList<Object[]>();
                    for(ShoppingUser user:users){
                        Object[] obj = new Object[4];
                        obj[0] = user.getId();
                        obj[1] = user.getAddTime();
                        obj[2] = user.getMobile();
                        obj[3] = user.getNickName();
                        dataList.add(obj);
                    }

                    String fileName = fileId + ".xls";
                    ShoppingSysconfig config = CommonUtil.getSysConfig();
                    String uploadFilePath = config.getUploadFilePath();
                    File file = new File(uploadFilePath + File.separator + "exportFile" +
                            File.separator + fileName);
                    try {
                        OutputStream out = new FileOutputStream(file);
                        ExportExcel ex = new ExportExcel(title, rowsName, dataList);
                        ex.export(out);

                        tExportFile.setFileName(fileName);
                        tExportFile.setFinishTime(StringUtil.nowTimeString());
                        tExportFile.setTaskStatus(1);  //已完成

                        tExportFileDao.update(tExportFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                        tExportFile.setTaskStatus(-1);
                        tExportFileDao.update(tExportFile);
                    }
                }
            });
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
