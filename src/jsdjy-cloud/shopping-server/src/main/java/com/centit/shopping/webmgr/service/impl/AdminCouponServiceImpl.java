package com.centit.shopping.webmgr.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.dao.*;
import com.centit.shopping.po.*;
import com.centit.shopping.utils.CRMService;
import com.centit.shopping.utils.CommonInit;
import com.centit.shopping.utils.CommonUtil;
import com.centit.shopping.utils.StringUtil;
import com.centit.shopping.webmgr.service.AdminCouponService;
import com.centit.shopping.webmgr.service.AdminGoodsManageService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>优惠券管理<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务实现类
 * @Date : 2021-02-21
 **/
@Transactional
@Service
public class AdminCouponServiceImpl implements AdminCouponService {
    public static final Log log = LogFactory.getLog(AdminCouponService.class);

    @Resource
    private ShoppingCouponDao shoppingCouponDao;
    @Resource
    private ShoppingCouponRelateDao shoppingCouponRelateDao;
    @Resource
    private ShoppingGoodsclassDao shoppingGoodsclassDao;
    @Resource
    private ShoppingGoodsDao shoppingGoodsDao;
    @Resource
    private ShoppingArtactivityDao shoppingArtactivityDao;
    @Resource
    private ShoppingArtclassDao shoppingArtclassDao;
    @Resource
    private ShoppingArtplanDao shoppingArtplanDao;
    @Resource
    private ShoppingCouponGrantDao shoppingCouponGrantDao;
    @Resource
    private ShoppingCouponRecordDao shoppingCouponRecordDao;

    @Resource
    private ShoppingCouponDirectgrantRecordDao shoppingCouponDirectgrantRecordDao;
    @Resource
    private ShoppingCouponDirectgrantDao shoppingCouponDirectgrantDao;
    @Resource
    private ShoppingWriteoffCouponDao shoppingWriteoffCouponDao;

    @Value("${writeOffStr}")
    private String writeOffStr;

    /**
     * 同步优惠券
     */
    @Override
    public JSONObject syncCoupons(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            if(syncCoupon()){
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
     * 创建优惠券
     */
    @Override
    public JSONObject createCoupon(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            JSONObject resObj =CRMService.createCoupon(reqJson);
            if(null !=resObj){
                if(resObj.get("result").equals("ok")&&syncCoupon()){  //创建优惠券之后要重新同步优惠券
                    retCode = "0";
                    retMsg = "操作成功！";
                }else{
                    retMsg=resObj.getString("msg");
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
     * 创建兑换券
     */
    @Override
    public JSONObject createWriteOffCoupon(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            reqJson.put("memo",reqJson.get("memo")+writeOffStr);
            JSONObject resObj =CRMService.createCoupon(reqJson);
            if(null !=resObj){
                if(resObj.get("result").equals("ok")&&syncCoupon()){  //创建优惠券之后要重新同步优惠券
                    retCode = "0";
                    retMsg = "操作成功！";
                }else{
                    retMsg=resObj.getString("msg");
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
     * 查询优惠券列表（全部）
     */
    @Override
    public JSONObject queryAllCouponPageList(JSONObject reqJson) {
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

            reqMap.put("isdelete","0");
//            reqMap.put("writeOff",0);
//            reqMap.put("writeOffStr",writeOffStr);

            bizDataJson.put("total",shoppingCouponDao.queryTotalCount(reqMap));
            List<ShoppingCoupon> objList = shoppingCouponDao.queryList(reqMap);
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
     * 查询线上可发放的优惠券列表
     */
    @Override
    public JSONObject queryOnlineCouponPageList(JSONObject reqJson) {
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

            reqMap.put("isdelete","0");
            reqMap.put("offline",0);

            bizDataJson.put("total",shoppingCouponDao.queryTotalCount(reqMap));
            List<ShoppingCoupon> objList = shoppingCouponDao.queryList(reqMap);
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
     * 查询优惠券列表（过滤掉兑换券）
     */
    @Override
    public JSONObject queryCouponPageList(JSONObject reqJson) {
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

            reqMap.put("isdelete","0");
            reqMap.put("writeOff",0);
            reqMap.put("writeOffStr",writeOffStr);

            bizDataJson.put("total",shoppingCouponDao.queryTotalCount(reqMap));
            List<ShoppingCoupon> objList = shoppingCouponDao.queryList(reqMap);
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
     * 查询兑换券列表
     */
    @Override
    public JSONObject queryWriteOffCouponPageList(JSONObject reqJson) {
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

            reqMap.put("isdelete","0");
            reqMap.put("writeOff",1);
            reqMap.put("writeOffStr",writeOffStr);

            bizDataJson.put("total",shoppingCouponDao.queryTotalCount(reqMap));
            List<ShoppingCoupon> objList = shoppingCouponDao.queryList(reqMap);
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
     * 删除优惠券
     */
    @Override
    public JSONObject delCoupon(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String right_No = reqJson.getString("right_No");
            JSONObject resObj =CRMService.delCoupon(right_No);
            if(null !=resObj){
                if(resObj.get("result").equals("ok")&&syncCoupon()){  //删除优惠券之后要重新同步优惠券
                    retCode = "0";
                    retMsg = "操作成功！";
                }else{
                    retMsg=resObj.getString("msg");
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
     * 获取优惠券详情
     */
    @Override
    public JSONObject queryCouponDetail(String right_No) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingCoupon shoppingCoupon = new ShoppingCoupon();
            shoppingCoupon.setRight_No(right_No);

            bizDataJson.put("data",shoppingCouponDao.queryDetail(shoppingCoupon));
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
     * 上/下架优惠券
     */
    @Override
    public JSONObject pubCoupon(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
//            String right_No = reqJson.getString("right_No");
//            String ispub = reqJson.getString("ispub");
//            ShoppingCoupon shoppingCoupon = new ShoppingCoupon();
//            shoppingCoupon.setRight_No(right_No);
//            shoppingCoupon.setIspub(ispub);
                    ShoppingCoupon shoppingCoupon = JSON.parseObject(reqJson.toJSONString(), ShoppingCoupon.class);
            shoppingCouponDao.update(shoppingCoupon);
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
     * 设置优惠券单人限领数量
     */
    @Override
    public JSONObject setCouponLimit(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {

            ShoppingCoupon shoppingCoupon = JSON.parseObject(reqJson.toJSONString(), ShoppingCoupon.class);
            shoppingCouponDao.update(shoppingCoupon);
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
     * 编辑优惠券（目前仅支持编辑该券是否为手动发放）
     */
    @Override
    public JSONObject editCoupon(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {

            ShoppingCoupon shoppingCoupon = JSON.parseObject(reqJson.toJSONString(), ShoppingCoupon.class);
            shoppingCouponDao.update(shoppingCoupon);
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
     * 设置核销账户(仅支持兑换券)
     */
    @Override
    public JSONObject editWriteOffCount(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {

            ShoppingCoupon shoppingCoupon = JSON.parseObject(reqJson.toJSONString(), ShoppingCoupon.class);
            if(StringUtil.isNotNull(shoppingCoupon.getWriteOffCount())){
                shoppingCoupon.setOffline(1);
            }else{
                shoppingCoupon.setOffline(0);
            }
            shoppingCouponDao.updateWriteOffCount(shoppingCoupon);
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
     * 获取优惠券已关联的商品/分类
     */
    @Override
    public JSONObject queryCouponRelation(String right_No) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("rightNo",right_No);
            List<ShoppingCouponRelate> ojList = shoppingCouponRelateDao.queryList(reqMap);
            for(ShoppingCouponRelate shoppingCouponRelate:ojList){
                if(shoppingCouponRelate.getLinkType()==1){  //关联到分类
                    if(shoppingCouponRelate.getGoodsType()==1||shoppingCouponRelate.getGoodsType()==2){
                        ShoppingGoodsclass shoppingGoodsclass = new ShoppingGoodsclass();
                        shoppingGoodsclass.setId(shoppingCouponRelate.getGoodsId());
                        shoppingGoodsclass = shoppingGoodsclassDao.queryDetail(shoppingGoodsclass);
                        shoppingCouponRelate.setGoodsName(shoppingGoodsclass.getClassName());
                    }else if(shoppingCouponRelate.getGoodsType()==3){
                        shoppingCouponRelate.setGoodsName("会员活动");
                    }else if(shoppingCouponRelate.getGoodsType()==4){
                        shoppingCouponRelate.setGoodsName("艺术培训");
                    }else if(shoppingCouponRelate.getGoodsType()==5){
                        shoppingCouponRelate.setGoodsName("视频点播");
                    }else if(shoppingCouponRelate.getGoodsType()==6){
                        shoppingCouponRelate.setGoodsName("爱艺计划");
                    }
                }else if(shoppingCouponRelate.getLinkType()==2){//关联到具体商品
                    if(shoppingCouponRelate.getGoodsType()==1||shoppingCouponRelate.getGoodsType()==2){
                        ShoppingGoods shoppingGoods = new ShoppingGoods();
                        shoppingGoods.setId(shoppingCouponRelate.getGoodsId());
                        //查询商品主体信息
                        shoppingGoods = shoppingGoodsDao.queryDetail(shoppingGoods);
                        shoppingCouponRelate.setGoodsName(shoppingGoods.getGoodsName());
                    }else if(shoppingCouponRelate.getGoodsType()==3){
                        ShoppingArtactivity shoppingArtactivity = new ShoppingArtactivity();
                        shoppingArtactivity.setId(shoppingCouponRelate.getGoodsId());
                        shoppingArtactivity = shoppingArtactivityDao.queryDetail(shoppingArtactivity);
                        shoppingCouponRelate.setGoodsName(shoppingArtactivity.getActivityName());
                    }else if(shoppingCouponRelate.getGoodsType()==4){
                        ShoppingArtclass shoppingArtclass = new ShoppingArtclass();
                        shoppingArtclass.setId(shoppingCouponRelate.getGoodsId());
                        shoppingArtclass = shoppingArtclassDao.queryDetail(shoppingArtclass);
                        shoppingCouponRelate.setGoodsName(shoppingArtclass.getClassName());
                    }else if(shoppingCouponRelate.getGoodsType()==6){
                        ShoppingArtplan shoppingArtplan = new ShoppingArtplan();
                        shoppingArtplan.setId(shoppingCouponRelate.getGoodsId());
                        shoppingArtplan = shoppingArtplanDao.queryDetail(shoppingArtplan);
                        shoppingCouponRelate.setGoodsName(shoppingArtplan.getActivityName());
                    }
                }
            }

            bizDataJson.put("ojList",ojList);
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
     * 保存优惠券关联商品/分类
     */
    @Override
    public JSONObject saveCouponRelation(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingCouponRelate shoppingCouponRelate = JSON.parseObject(reqJson.toJSONString(), ShoppingCouponRelate.class);
            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("rightNo",shoppingCouponRelate.getRightNo());
            reqMap.put("linkType",shoppingCouponRelate.getLinkType());
            reqMap.put("goodsType",shoppingCouponRelate.getGoodsType());
            reqMap.put("goodsId",shoppingCouponRelate.getGoodsId());
            if(!shoppingCouponRelateDao.queryList(reqMap).isEmpty()){
                retMsg = "已关联相同商品/分类，请勿重复添加！";
            }else{
                shoppingCouponRelateDao.insert(shoppingCouponRelate);
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
     * 删除优惠券关联商品/分类
     */
    @Override
    public JSONObject delCouponRelation(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingCouponRelate shoppingCouponRelate = JSON.parseObject(reqJson.toJSONString(), ShoppingCouponRelate.class);
            shoppingCouponRelateDao.delete(shoppingCouponRelate);
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
     * 查询优惠券指定优惠券发放记录
     */
    @Override
    public JSONObject queryCouponGrantPageList(JSONObject reqJson) {
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
            List<ShoppingCouponGrant> objList = new ArrayList<>();
            if(StringUtil.isNotNull(reqJson.get("rightNo"))){
                bizDataJson.put("total",shoppingCouponGrantDao.queryListCount(reqMap));
                objList = shoppingCouponGrantDao.queryList(reqMap);
                bizDataJson.put("objList",objList);
            }else{
                bizDataJson.put("total",0);
                bizDataJson.put("objList",objList);
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
     * 查询优惠券指定优惠券消费记录
     */
    @Override
    public JSONObject queryCouponRecordPageList(JSONObject reqJson) {
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
            List<ShoppingCouponRecord> objList = new ArrayList<>();
            if(StringUtil.isNotNull(reqJson.get("rightNo"))){
                bizDataJson.put("total",shoppingCouponRecordDao.queryListCount(reqMap));
                objList = shoppingCouponRecordDao.queryList(reqMap);
                bizDataJson.put("objList",objList);
            }else{
                bizDataJson.put("total",0);
                bizDataJson.put("objList",objList);
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


    public boolean syncCoupon(){
        try{
            JSONObject resObj =CRMService.getCouponList();
            if(null !=resObj){
                JSONArray dataArray = resObj.getJSONArray("dataList");
                List<String> ids = new ArrayList<>();
                for(int i=0;i<dataArray.size();i++){
                    ShoppingCoupon shoppingCoupon = JSON.parseObject(dataArray.getJSONObject(i).toJSONString(), ShoppingCoupon.class);
//                    shoppingCouponDao.delete(shoppingCoupon);
//                    if(shoppingCoupon.getTime_Type().equals("limit")&&(null==shoppingCoupon.getEnd_Date()||"".equals(shoppingCoupon.getEnd_Date()))){
//                        String createDate = shoppingCoupon.getCreatedate();
//                        int fixMonth = shoppingCoupon.getFix_Month();
//                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//                        if(shoppingCoupon.getTime_Unit()==0){ //月
//                            String endTime = StringUtil.addDay(StringUtil.addMonth(createDate,fixMonth),1);
//                            shoppingCoupon.setEnd_Date(sdf.format(sdf.parse(endTime)));
//                        }else{
//                            String endTime = StringUtil.addDay(createDate,fixMonth+1);
//                            shoppingCoupon.setEnd_Date(sdf.format(sdf.parse(endTime)));
//                        }
//                    }
                    if(null !=shoppingCouponDao.queryDetail(shoppingCoupon)){
                        shoppingCouponDao.update(shoppingCoupon);
                    }else{

                        shoppingCouponDao.insert(shoppingCoupon);
                    }

                    ids.add(shoppingCoupon.getId());
                }
                if(ids.size()>0){
                    shoppingCouponDao.updateCouponState(ids);
                }else{
                    shoppingCouponDao.deleteAllCoupon(new HashMap<>());
                }

            }
            return true;
        }catch (Exception e){
            return false;
        }

    }

    /**
     * 查询已配置的可直接发放的优惠券列表
     */
    @Override
    public JSONObject queryDirectgrantCoupons(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {

//            int pageNo =reqJson.get("pageNo")==null?1:reqJson.getInteger("pageNo");
//            int pageSize = reqJson.get("pageSize")==null?10:reqJson.getInteger("pageSize");
//            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
//            reqMap.put("startRow", (pageNo-1)*pageSize);
//            reqMap.put("pageSize", pageSize);
            HashMap<String, Object> reqMap =new HashMap<>();

            List<ShoppingCoupon> coupons =  shoppingCouponDao.queryDirectgrantCoupon(reqMap);
//            bizDataJson.put("total",shoppingCouponDao.queryDirectgrantCouponTotalCount(reqMap));
            bizDataJson.put("objList",coupons);

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
     * 添加直接发放的优惠券
     */
    @Override
    public JSONObject addDirectgrantCoupon(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String rightNo =reqJson.getString("rightNo");
            ShoppingCouponDirectgrant shoppingCouponDirectgrant=new ShoppingCouponDirectgrant();
            shoppingCouponDirectgrant.setRightNo(rightNo);
            shoppingCouponDirectgrant.setIsDelete("0");
            shoppingCouponDirectgrantDao.insert(shoppingCouponDirectgrant);

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
     * 删除直接发放的优惠券
     */
    @Override
    public JSONObject delDirectgrantCoupon(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String rightNo =reqJson.getString("rightNo");
            ShoppingCouponDirectgrant shoppingCouponDirectgrant=new ShoppingCouponDirectgrant();
            shoppingCouponDirectgrant.setRightNo(rightNo);
            shoppingCouponDirectgrant.setIsDelete("1");
            shoppingCouponDirectgrantDao.update(shoppingCouponDirectgrant);

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
     * 直接发放优惠码
     */
    @Override
    public JSONObject directGrantCoupon(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String phoneNumbers = reqJson.getString("phoneNumbers");
            String rightNo= reqJson.getString("rightNo");
            ShoppingCoupon shoppingCoupon = new ShoppingCoupon();
            shoppingCoupon.setRight_No(rightNo);
            shoppingCoupon = shoppingCouponDao.queryDetail(shoppingCoupon);

            phoneNumbers=phoneNumbers.replaceAll("；",";");
            String[] phones = phoneNumbers.split(";");
            List<String> errorPhones = new ArrayList<>();
            for(int i=0;i<phones.length;i++){
                String phone = phones[i];
                ShoppingCouponDirectgrantRecord record = new ShoppingCouponDirectgrantRecord();
                record.setPhone(phone);
                record.setRightNo(rightNo);
                ShoppingUser user = CommonUtil.getShoppingUserByMobile(phone);
                if(null!=user){
                    if (shoppingCoupon.getPerLimit() > 0) {
                        int num = 0;
                        JSONArray resArray = CRMService.getUserCouponList(user.getId(),phone,null);
                        for (int j = 0; j < resArray.size(); j++) {
                            JSONObject obj = resArray.getJSONObject(j);
                            if (obj.get("right_No").equals(rightNo)) {
                                num++;
                            }
                        }
                        if (num >= shoppingCoupon.getPerLimit()) {
                            errorPhones.add(phone);
                            record.setGrantStatus("1");
                            record.setMsg("该优惠券每人至多领取" + shoppingCoupon.getPerLimit() + "张，该手机号领取数量已达上限");
                        } else {
                            JSONObject resObj=CRMService.directGrantCoupon(phone, rightNo);
                            if (resObj!=null&&resObj.get("result").equals("ok")) {
                                record.setGrantStatus("0");
                                record.setMsg("success");

                                //是否需要生成核销二维码
                                if(StringUtil.isNotNull(shoppingCoupon.getWriteOffCount())){
                                    //获取该该用户针对该优惠券已经生成的核销记录
                                    HashMap<String, Object> reqMap = new HashMap<>();
                                    reqMap.put("rightNo",rightNo);
                                    reqMap.put("userId",user.getId());
                                    List<ShoppingWriteoffCoupon> couponList = shoppingWriteoffCouponDao.queryList(reqMap);
                                    List<String> cIds = new ArrayList<>();
                                    for(ShoppingWriteoffCoupon shoppingWriteoffCoupon:couponList){
                                        cIds.add(shoppingWriteoffCoupon.getRightId());
                                    }
                                    //从CRM获取会员优惠券
                                    JSONArray couponArray = CRMService.getUserCouponList(user.getId(),phone,"0");
                                    if(null !=couponArray){
                                        for (int j = 0; j < couponArray.size(); j++) {
                                            JSONObject obj = (JSONObject) couponArray.get(j);
                                            String c_right_No =  obj.get("right_No").toString();
                                            if(c_right_No.equals(rightNo)&&!cIds.contains(obj.get("id").toString())){
                                                String rightId = obj.get("id").toString();
                                                ShoppingWriteoffCoupon shoppingWriteoffCoupon = new ShoppingWriteoffCoupon();
                                                shoppingWriteoffCoupon.setRightNo(rightNo);
                                                shoppingWriteoffCoupon.setRightId(rightId);
                                                shoppingWriteoffCoupon.setUserId(user.getId());
                                                shoppingWriteoffCoupon.setOffCode("CP_"+StringUtil.randomOffCode(6));
                                                shoppingWriteoffCoupon.setOffCount(1);
                                                shoppingWriteoffCoupon.setOffAccount(shoppingCoupon.getWriteOffCount());
                                                shoppingWriteoffCouponDao.insert(shoppingWriteoffCoupon);
                                            }
                                        }
                                    }
                                }
                            } else if(resObj!=null){
                                errorPhones.add(phone);
                                record.setGrantStatus("1");
                                record.setMsg(resObj.getString("msg"));
                            }else{
                                errorPhones.add(phone);
                                record.setGrantStatus("1");
                                record.setMsg("调用CRM接口失败，请联系开发人员");
                            }

                        }
                    }else{
                        JSONObject resObj=CRMService.directGrantCoupon(phone, rightNo);
                        if (resObj!=null&&resObj.get("result").equals("ok")) {
                            record.setGrantStatus("0");
                            record.setMsg("success");

                            //是否需要生成核销二维码
                            if(StringUtil.isNotNull(shoppingCoupon.getWriteOffCount())){
                                //获取该该用户针对该优惠券已经生成的核销记录
                                HashMap<String, Object> reqMap = new HashMap<>();
                                reqMap.put("rightNo",rightNo);
                                reqMap.put("userId",user.getId());
                                List<ShoppingWriteoffCoupon> couponList = shoppingWriteoffCouponDao.queryList(reqMap);
                                List<String> cIds = new ArrayList<>();
                                for(ShoppingWriteoffCoupon shoppingWriteoffCoupon:couponList){
                                    cIds.add(shoppingWriteoffCoupon.getRightId());
                                }
                                //从CRM获取会员优惠券
                                JSONArray couponArray = CRMService.getUserCouponList(user.getId(),phone,"0");
                                if(null !=couponArray){
                                    for (int j = 0; j < couponArray.size(); j++) {
                                        JSONObject obj = (JSONObject) couponArray.get(j);
                                        String c_right_No =  obj.get("right_No").toString();
                                        if(c_right_No.equals(rightNo)&&!cIds.contains(obj.get("id").toString())){
                                            String rightId = obj.get("id").toString();
                                            ShoppingWriteoffCoupon shoppingWriteoffCoupon = new ShoppingWriteoffCoupon();
                                            shoppingWriteoffCoupon.setRightNo(rightNo);
                                            shoppingWriteoffCoupon.setRightId(rightId);
                                            shoppingWriteoffCoupon.setUserId(user.getId());
                                            shoppingWriteoffCoupon.setOffCode("CP_"+StringUtil.randomOffCode(6));
                                            shoppingWriteoffCoupon.setOffCount(1);
                                            shoppingWriteoffCoupon.setOffAccount(shoppingCoupon.getWriteOffCount());
                                            shoppingWriteoffCouponDao.insert(shoppingWriteoffCoupon);
                                        }
                                    }
                                }
                            }
                        } else if(resObj!=null){
                            errorPhones.add(phone);
                            record.setGrantStatus("1");
                            record.setMsg(resObj.getString("msg"));
                        }else{
                            errorPhones.add(phone);
                            record.setGrantStatus("1");
                            record.setMsg("调用CRM接口失败，请联系开发人员");
                        }
                    }
                }else{
                    errorPhones.add(phone);
                    record.setGrantStatus("1");
                    record.setMsg("根据手机号获取用户信息失败");
                }

                shoppingCouponDirectgrantRecordDao.insert(record);
            }
            retCode = "0";
            if(errorPhones.isEmpty()){
                retMsg = "操作成功！";
            }else{
                retMsg = "以下手机号发放优惠码失败："+errorPhones.toString();
            }

        } catch (Exception e) {
            log.error(e);
        }
        retJson.put("retCode", retCode);
        retJson.put("retMsg", retMsg);
        retJson.put("bizData", bizDataJson);
        return retJson;
    }

}
