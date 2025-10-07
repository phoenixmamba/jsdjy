package com.centit.shopping.webmgr.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.common.enums.Const;
import com.centit.shopping.dao.*;
import com.centit.shopping.po.*;
import com.centit.shopping.utils.CommonUtil;
import com.centit.shopping.webmgr.service.SellerOrderService;
import com.centit.shopping.webmgr.service.SellerRefundService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>退货管理<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务实现类
 * @Date : 2021-02-25
 **/
@Transactional
@Service
public class SellerRefundServiceImpl implements SellerRefundService {
    public static final Log log = LogFactory.getLog(SellerRefundService.class);

    @Resource
    private ShoppingOrderformDao shoppingOrderformDao;

    @Resource
    private ShoppingRefundDao shoppingRefundDao;

    @Resource
    private ShoppingGoodscartDao shoppingGoodscartDao;

    @Resource
    private ShoppingArtactivityDao shoppingArtactivityDao;

    @Resource
    private ShoppingArtplanDao shoppingArtplanDao;

    @Resource
    private ShoppingArtclassDao shoppingArtclassDao;

    @Resource
    private ShoppingRefundPhotoDao shoppingRefundPhotoDao;

    @Resource
    private ShoppingAssetDao shoppingAssetDao;

    @Resource
    private ShoppingGoodsDao shoppingGoodsDao;

    @Resource
    private ShoppingIntegralRecordDao shoppingIntegralRecordDao;

    @Resource
    private ShoppingBalanceRecordDao shoppingBalanceRecordDao;


    @Value("${order.orderState.hasRefund}")
    private int orderStateRefund;

    @Value("${order.orderState.hasDone}")
    private int orderStateDone;

    /**
     * 查询退货分页列表
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
            bizDataJson.put("total",shoppingRefundDao.queryTotalCount(reqMap));
            List<ShoppingRefund> shoppingRefundList= shoppingRefundDao.queryList(reqMap);

            JSONArray resArray = new JSONArray();
            for(ShoppingRefund shoppingRefund:shoppingRefundList){
                JSONObject obj = new JSONObject();
                String gcId = shoppingRefund.getGcId();
                ShoppingGoodscart shoppingGoodscart = new ShoppingGoodscart();
                shoppingGoodscart.setId(gcId);
                shoppingGoodscart = shoppingGoodscartDao.queryDetail(shoppingGoodscart);
                //订单信息
                ShoppingOrderform shoppingOrderform = new ShoppingOrderform();
                shoppingOrderform.setId(shoppingGoodscart.getOfId());
                shoppingOrderform = shoppingOrderformDao.queryDetail(shoppingOrderform);
                obj.put("refundId",shoppingRefund.getId());
                obj.put("ofId",shoppingOrderform.getId());
                obj.put("orderId",shoppingOrderform.getOrderId());    //订单编号
                obj.put("orderTime",shoppingOrderform.getAddTime());  //下单时间
                obj.put("totalPrice",shoppingOrderform.getTotalPrice());  //订单总价
                obj.put("userInfo",shoppingOrderform.getUserInfo());  //用户信息

                obj.put("cartType",shoppingGoodscart.getCartType());   //商品类型
                if(shoppingGoodscart.getCartType()== Const.SHOPPING_ACT_CART_TYPE){   //艺教活动
                    ShoppingArtactivity shoppingArtactivity = new ShoppingArtactivity();
                    shoppingArtactivity.setId(shoppingGoodscart.getGoodsId());
                    shoppingArtactivity = shoppingArtactivityDao.queryDetail(shoppingArtactivity);

                    obj.put("goodsName",shoppingArtactivity.getActivityName());
                    obj.put("photo",shoppingArtactivity.getMainPhotoId());
                    obj.put("goodsCount",shoppingGoodscart.getCount());
                }else if(shoppingGoodscart.getCartType()== Const.SHOPPING_PLAN_CART_TYPE){   //爱艺计划
                    ShoppingArtplan shoppingArtplan = new ShoppingArtplan();
                    shoppingArtplan.setId(shoppingGoodscart.getGoodsId());
                    shoppingArtplan = shoppingArtplanDao.queryDetail(shoppingArtplan);

                    obj.put("goodsName",shoppingArtplan.getActivityName());
                    obj.put("photo",shoppingArtplan.getMainPhotoId());
                    obj.put("goodsCount",shoppingGoodscart.getCount());
                }else if((shoppingGoodscart.getCartType()== Const.SHOPPING_CLASS_CART_TYPE)){   //艺教培训
                    ShoppingArtclass shoppingArtclas = new ShoppingArtclass();
                    shoppingArtclas.setId(shoppingGoodscart.getGoodsId());
                    shoppingArtclas = shoppingArtclassDao.queryDetail(shoppingArtclas);

                    obj.put("goodsName",shoppingArtclas.getClassName());
                    obj.put("photo",shoppingArtclas.getMainPhotoId());
                    obj.put("goodsCount",shoppingGoodscart.getCount());
                }else{
                    ShoppingGoods shoppingGoods=new ShoppingGoods();
                    shoppingGoods.setId(shoppingGoodscart.getGoodsId());
                    shoppingGoods = shoppingGoodsDao.queryDetail(shoppingGoods);

                    obj.put("goodsName",shoppingGoods.getGoodsName());
                    obj.put("photo",shoppingGoods.getGoodsMainPhotoId());
                    obj.put("goodsCount",shoppingGoodscart.getCount());
                }
                obj.put("refundTIme",shoppingRefund.getAddTime()); //申请退款时间
                obj.put("refundStatus",shoppingRefund.getRefundStatus()); //退款状态
                obj.put("updateTime",shoppingRefund.getUpdateTime()); //最近操作时间

                resArray.add(obj);
            }

            bizDataJson.put("objList",resArray);
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
     * 查看退货详情
     */
    @Override
    public JSONObject refundDetail(String refundId) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingRefund shoppingRefund = new ShoppingRefund();
            shoppingRefund.setId(refundId);
            shoppingRefund = shoppingRefundDao.queryDetail(shoppingRefund);

            String gcId = shoppingRefund.getGcId();
            ShoppingGoodscart shoppingGoodscart = new ShoppingGoodscart();
            shoppingGoodscart.setId(gcId);
            shoppingGoodscart = shoppingGoodscartDao.queryDetail(shoppingGoodscart);

            JSONObject orderInfo = new JSONObject();
            ShoppingOrderform shoppingOrderform = new ShoppingOrderform();
            shoppingOrderform.setId(shoppingGoodscart.getOfId());
            shoppingOrderform = shoppingOrderformDao.queryDetail(shoppingOrderform);
            orderInfo.put("ofId",shoppingOrderform.getId());
            orderInfo.put("orderId",shoppingOrderform.getOrderId());    //订单编号
            orderInfo.put("addTime",shoppingOrderform.getAddTime());  //下单时间
            orderInfo.put("payTime",shoppingOrderform.getPayTime());  //付款时间
            orderInfo.put("finishTime",shoppingOrderform.getFinishTime());  //完成时间
            orderInfo.put("shipPrice",shoppingOrderform.getShipPrice());   //订单运费
            orderInfo.put("totalPrice",shoppingOrderform.getTotalPrice());  //订单总价
            orderInfo.put("deductionMemberPrice",shoppingOrderform.getDeductionMemberPrice()); //会员权益抵扣
            orderInfo.put("deductionCouponPrice",shoppingOrderform.getDeductionCouponPrice()); //优惠券抵扣
            orderInfo.put("deductionIntegral",shoppingOrderform.getDeductionIntegral()); //积分抵扣值
            orderInfo.put("deductionIntegralPrice",shoppingOrderform.getDeductionIntegralPrice()); //积分抵扣金额
            orderInfo.put("deductionBalancePrice",shoppingOrderform.getDeductionBalancePrice()); //余额抵扣
            orderInfo.put("payPrice",shoppingOrderform.getPayPrice()); //订单商品
            orderInfo.put("orderGoods",CommonUtil.getOrderGoods(shoppingOrderform.getId())); //现金支付
            //用户信息
            bizDataJson.put("userInfo",shoppingOrderform.getUserInfo());
            //订单信息
            bizDataJson.put("orderInfo",orderInfo);
            //退款信息
            JSONObject refundInfo = new JSONObject();
            //退款商品
            JSONObject refundGoods = new JSONObject();
            refundGoods.put("cartType",shoppingGoodscart.getCartType());   //商品类型
            if(shoppingGoodscart.getCartType()== Const.SHOPPING_ACT_CART_TYPE){   //艺教活动
                ShoppingArtactivity shoppingArtactivity = new ShoppingArtactivity();
                shoppingArtactivity.setId(shoppingGoodscart.getGoodsId());
                shoppingArtactivity = shoppingArtactivityDao.queryDetail(shoppingArtactivity);

                refundGoods.put("goodsName",shoppingArtactivity.getActivityName());
                refundGoods.put("photo",shoppingArtactivity.getMainPhotoId());
                refundGoods.put("goodsCount",shoppingGoodscart.getCount());
                refundGoods.put("goodsPrice",shoppingGoodscart.getPrice());
            }else if(shoppingGoodscart.getCartType()== Const.SHOPPING_PLAN_CART_TYPE){   //艺教活动
                ShoppingArtplan shoppingArtplan = new ShoppingArtplan();
                shoppingArtplan.setId(shoppingGoodscart.getGoodsId());
                shoppingArtplan = shoppingArtplanDao.queryDetail(shoppingArtplan);

                refundGoods.put("goodsName",shoppingArtplan.getActivityName());
                refundGoods.put("photo",shoppingArtplan.getMainPhotoId());
                refundGoods.put("goodsCount",shoppingGoodscart.getCount());
                refundGoods.put("goodsPrice",shoppingGoodscart.getPrice());
            }else if((shoppingGoodscart.getCartType()== Const.SHOPPING_CLASS_CART_TYPE)){   //艺教培训
                ShoppingArtclass shoppingArtclas = new ShoppingArtclass();
                shoppingArtclas.setId(shoppingGoodscart.getGoodsId());
                shoppingArtclas = shoppingArtclassDao.queryDetail(shoppingArtclas);

                refundGoods.put("goodsName",shoppingArtclas.getClassName());
                refundGoods.put("photo",shoppingArtclas.getMainPhotoId());
                refundGoods.put("goodsCount",shoppingGoodscart.getCount());
                refundGoods.put("goodsPrice",shoppingGoodscart.getPrice());
            }else{
                ShoppingGoods shoppingGoods=new ShoppingGoods();
                shoppingGoods.setId(shoppingGoodscart.getGoodsId());
                shoppingGoods = shoppingGoodsDao.queryDetail(shoppingGoods);

                refundGoods.put("goodsName",shoppingGoods.getGoodsName());
                refundGoods.put("photo",shoppingGoods.getGoodsMainPhotoId());
                refundGoods.put("goodsCount",shoppingGoodscart.getCount());
                refundGoods.put("goodsPrice",shoppingGoodscart.getPrice());
            }
            refundGoods.put("proposalMemberPrice",shoppingGoodscart.getDeductionMemberPrice()); //会员权益抵扣
            refundGoods.put("proposalCouponPrice",shoppingGoodscart.getDeductionCouponPrice()); //优惠券抵扣
            refundGoods.put("proposalIntegral",shoppingGoodscart.getDeductionIntegral()); //积分抵扣值
            refundGoods.put("proposalIntegralPrice",shoppingGoodscart.getDeductionIntegralPrice()); //积分抵扣金额
            refundGoods.put("proposalBalancePrice",shoppingGoodscart.getDeductionBalancePrice()); //余额抵扣
            refundGoods.put("proposalPayPrice",shoppingGoodscart.getPayPrice()); //现金支付
            refundInfo.put("refundGoods",refundGoods);

            refundInfo.put("refundTIme",shoppingRefund.getAddTime()); //申请退款时间
            refundInfo.put("refundReason",shoppingRefund.getReason()); //申请退款理由
            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("refundId",refundId);
            refundInfo.put("refundPhotos",shoppingRefundPhotoDao.queryList(reqMap)); //照片
            refundInfo.put("refundStatus",shoppingRefund.getRefundStatus()); //退款状态
            refundInfo.put("adminUser",shoppingRefund.getAdminUser()); //操作人id
            refundInfo.put("adminName",shoppingRefund.getAdminName()); //操作人名称
            refundInfo.put("adminLog",shoppingRefund.getAdminLog()); //操作备注
            refundInfo.put("updateTime",shoppingRefund.getUpdateTime()); //最近操作时间
            refundInfo.put("refundIntegral",shoppingRefund.getRefundIntegral()); //退还积分数额
            refundInfo.put("refundBalance",shoppingRefund.getRefundBalance()); //退还余额数额
            refundInfo.put("refundCash",shoppingRefund.getRefundCash()); //退还现金数额

            bizDataJson.put("refundInfo",refundInfo);
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
     * 通过/不通过退货
     */
    @Override
    public JSONObject adminRefund(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            ShoppingRefund shoppingRefund = JSON.parseObject(reqJson.toJSONString(), ShoppingRefund.class);
            shoppingRefundDao.update(shoppingRefund);
            shoppingRefund = shoppingRefundDao.queryDetail(shoppingRefund);

            ShoppingGoodscart shoppingGoodscart = new ShoppingGoodscart();
            shoppingGoodscart.setId(shoppingRefund.getGcId());
            shoppingGoodscart = shoppingGoodscartDao.queryDetail(shoppingGoodscart);
            String ofId = shoppingGoodscart.getOfId();
            ShoppingOrderform shoppingOrderform = new ShoppingOrderform();
            shoppingOrderform.setId(ofId);
            shoppingOrderform = shoppingOrderformDao.queryDetail(shoppingOrderform);


            if(shoppingRefund.getRefundStatus()==1){  //退款通过
                //Todo:将退还的积分与余额数值存入用户待领取的数据表
                ShoppingAsset shoppingAsset=new ShoppingAsset();
                shoppingAsset.setUserId(shoppingOrderform.getUserId());
                shoppingAsset = shoppingAssetDao.queryDetail(shoppingAsset);
                if(shoppingAsset==null){
                    shoppingAsset=new ShoppingAsset();
                    shoppingAsset.setUserId(shoppingOrderform.getUserId());
                    shoppingAsset.setIntegralValue(shoppingRefund.getRefundIntegral());
                    int balanceValue = shoppingRefund.getRefundBalance().multiply(new BigDecimal(100)).intValue();
                    shoppingAsset.setBalanceValue(balanceValue);
                    shoppingAssetDao.insert(shoppingAsset);
                }else{
                    shoppingAsset.setIntegralValue(shoppingAsset.getIntegralValue()+shoppingRefund.getRefundIntegral());
                    int balanceValue = shoppingRefund.getRefundBalance().multiply(new BigDecimal(100)).intValue();
                    shoppingAsset.setBalanceValue(shoppingAsset.getBalanceValue()+balanceValue);
                    shoppingAssetDao.update(shoppingAsset);
                }
                if(shoppingRefund.getRefundIntegral()>0){
                    //用户积分新增记录
                    ShoppingIntegralRecord shoppingIntegralRecord = new ShoppingIntegralRecord();
                    shoppingIntegralRecord.setUserId(shoppingOrderform.getUserId());
                    shoppingIntegralRecord.setIntegralCount(shoppingRefund.getRefundIntegral());
                    shoppingIntegralRecord.setRemark("退还积分，订单号"+shoppingOrderform.getOrderId());
                    shoppingIntegralRecordDao.insert(shoppingIntegralRecord);
                }
                if(shoppingRefund.getRefundBalance().compareTo(BigDecimal.ZERO)>0){
                    //用户余额新增记录
                    ShoppingBalanceRecord shoppingBalanceRecord = new ShoppingBalanceRecord();
                    shoppingBalanceRecord.setUserId(shoppingOrderform.getUserId());
                    shoppingBalanceRecord.setBalanceCount(shoppingRefund.getRefundBalance().multiply(new BigDecimal(100)).intValue());
                    shoppingBalanceRecord.setRemark("退还余额，订单号"+shoppingOrderform.getOrderId());
                    shoppingBalanceRecordDao.insert(shoppingBalanceRecord);
                }

                if(shoppingOrderform.getOrderId().startsWith(Const.SHOPPING_MERGE_ORDER)){  //合并支付订单
                    shoppingOrderform.setOrderStatus(orderStateRefund);
                }else{
                    shoppingOrderform.setOrderStatus(orderStateRefund);
                }

            }else{
                shoppingRefund =shoppingRefundDao.queryDetail(shoppingRefund);
                shoppingOrderform.setOrderStatus(shoppingRefund.getPreOrderStatus());
            }
            shoppingOrderformDao.update(shoppingOrderform);
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
