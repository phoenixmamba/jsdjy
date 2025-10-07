package com.centit.shopping.webmgr.service.impl;


import java.util.HashMap;
import java.util.List;
import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.centit.shopping.dao.*;
import com.centit.shopping.po.*;
import com.centit.shopping.webmgr.service.ShoppingActivityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.alibaba.fastjson.JSONObject;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务实现类
 * @Date : 2021-05-27
 **/
@Transactional
@Service
public class ShoppingActivityServiceImpl implements ShoppingActivityService {
    public static final Log log = LogFactory.getLog(ShoppingActivityService.class);

    @Resource
    private ShoppingActivityDao shoppingActivityDao;
    @Resource
    private ShoppingActivityCouponDao shoppingActivityCouponDao;
    @Resource
    private ShoppingCouponDao shoppingCouponDao;
    @Resource
    private ShoppingBirthCouponDao shoppingBirthCouponDao;
    @Resource
    private ShoppingNewCouponDao shoppingNewCouponDao;

    /**
     * 查询活动列表
     */
    @Override
    public JSONObject queryPageList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            int pageNo =reqJson.get("pageNo")==null?1:reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize")==null?10:reqJson.getInteger("pageSize");
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("startRow", (pageNo-1)*pageSize);
            reqMap.put("pageSize", pageSize);
            reqMap.put("deleteStatus","0");

            bizDataJson.put("total",shoppingActivityDao.queryTotalCount(reqMap));
            List<ShoppingActivity> activityList= shoppingActivityDao.queryList(reqMap);
            for(ShoppingActivity shoppingActivity:activityList){
                reqMap.clear();
                reqMap.put("acId",shoppingActivity.getId());
                List<ShoppingCoupon> coupons =  shoppingCouponDao.queryActivityCoupon(reqMap);
                shoppingActivity.setCoupons(coupons);
            }
            bizDataJson.put("objList",activityList);

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
     * 查询活动详情
     */
    @Override
    public JSONObject queryActicityDetail(String acId) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingActivity shoppingActivity = new ShoppingActivity();
            shoppingActivity.setId(acId);
            shoppingActivity= shoppingActivityDao.queryDetail(shoppingActivity);

            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("acId",shoppingActivity.getId());
            List<ShoppingCoupon> coupons =  shoppingCouponDao.queryActivityCoupon(reqMap);
            shoppingActivity.setCoupons(coupons);

            bizDataJson.put("data",shoppingActivity);

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
     * 创建活动
     */
    @Override
    public JSONObject addActivity(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingActivity shoppingActivity = JSONObject.parseObject(reqJson.toJSONString(), ShoppingActivity.class);
            shoppingActivityDao.insert(shoppingActivity);
            JSONArray couponArray =  reqJson.getJSONArray("coupons");
            for(int i=0;i<couponArray.size();i++){
                JSONObject couponObj= couponArray.getJSONObject(i);
                ShoppingActivityCoupon shoppingActivityCoupon = new ShoppingActivityCoupon();
                shoppingActivityCoupon.setAcId(shoppingActivity.getId());
                shoppingActivityCoupon.setRightNo(couponObj.getString("right_No"));
                shoppingActivityCoupon.setAcPerLimit(couponObj.getInteger("acPerLimit"));
                shoppingActivityCouponDao.insert(shoppingActivityCoupon);
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
     * 编辑活动
     */
    @Override
    public JSONObject editActivity(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingActivity shoppingActivity = JSONObject.parseObject(reqJson.toJSONString(), ShoppingActivity.class);
            shoppingActivityDao.update(shoppingActivity);
            JSONArray couponArray =  reqJson.getJSONArray("coupons");
            ShoppingActivityCoupon activityCoupon = new ShoppingActivityCoupon();
            activityCoupon.setAcId(shoppingActivity.getId());
            shoppingActivityCouponDao.delete(activityCoupon);
            for(int i=0;i<couponArray.size();i++){
                JSONObject couponObj= couponArray.getJSONObject(i);
                ShoppingActivityCoupon shoppingActivityCoupon = new ShoppingActivityCoupon();
                shoppingActivityCoupon.setAcId(shoppingActivity.getId());
                shoppingActivityCoupon.setRightNo(couponObj.getString("right_No"));
                shoppingActivityCoupon.setAcPerLimit(couponObj.getInteger("acPerLimit"));
                shoppingActivityCouponDao.insert(shoppingActivityCoupon);
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
     * 上/下架活动
     */
    @Override
    public JSONObject pubActivity(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingActivity shoppingActivity = JSONObject.parseObject(reqJson.toJSONString(), ShoppingActivity.class);
            shoppingActivityDao.update(shoppingActivity);
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
     * 删除活动
     */
    @Override
    public JSONObject delActivity(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingActivity shoppingActivity = JSONObject.parseObject(reqJson.toJSONString(), ShoppingActivity.class);
            shoppingActivity.setDeleteStatus("1");
            shoppingActivityDao.update(shoppingActivity);
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
     * 查询生日活动已关联优惠券
     */
    @Override
    public JSONObject queryBirthCoupons(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {


            HashMap<String, Object> reqMap = new HashMap<>();
            List<ShoppingCoupon> coupons =  shoppingCouponDao.queryBirthCoupon(reqMap);

            bizDataJson.put("coupons",coupons);

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
     * 编辑生日活动关联优惠券
     */
    @Override
    public JSONObject editBirthCoupons(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            JSONArray couponArray =  reqJson.getJSONArray("coupons");
            ShoppingBirthCoupon shoppingBirthCoupon = new ShoppingBirthCoupon();
            shoppingBirthCouponDao.delete(shoppingBirthCoupon);
            for(int i=0;i<couponArray.size();i++){
                JSONObject couponObj= couponArray.getJSONObject(i);
                ShoppingBirthCoupon coupon = new ShoppingBirthCoupon();
                coupon.setRightNo(couponObj.getString("right_No"));
                coupon.setAcPerLimit(couponObj.getInteger("acPerLimit"));
                shoppingBirthCouponDao.insert(coupon);
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
     * 查询新人活动已关联优惠券
     */
    @Override
    public JSONObject queryNewCoupons(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {


            HashMap<String, Object> reqMap = new HashMap<>();
            List<ShoppingCoupon> coupons =  shoppingCouponDao.queryNewCoupon(reqMap);

            bizDataJson.put("coupons",coupons);

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
     * 编辑新人活动关联优惠券
     */
    @Override
    public JSONObject editNewCoupons(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            JSONArray couponArray =  reqJson.getJSONArray("coupons");
            ShoppingNewCoupon shoppingNewCoupon = new ShoppingNewCoupon();
            shoppingNewCouponDao.delete(shoppingNewCoupon);
            for(int i=0;i<couponArray.size();i++){
                JSONObject couponObj= couponArray.getJSONObject(i);
                ShoppingNewCoupon coupon = new ShoppingNewCoupon();
                coupon.setRightNo(couponObj.getString("right_No"));
                coupon.setAcPerLimit(couponObj.getInteger("acPerLimit"));
                shoppingNewCouponDao.insert(coupon);
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
}
