package com.centit.shopping.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.biz.service.ParkService;
import com.centit.shopping.biz.service.WalletService;
import com.centit.shopping.common.enums.Const;
import com.centit.shopping.dao.*;
import com.centit.shopping.po.*;
import com.centit.shopping.utils.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>电子钱包<p>
 *
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 服务实现类
 * @Date : 2021-02-24
 **/
@Transactional
@Service
public class WalletServiceImpl implements WalletService {
    public static final Log log = LogFactory.getLog(WalletService.class);

    @Resource
    private ShoppingOrderformDao shoppingOrderformDao;
    @Resource
    private ShoppingOrderLogDao shoppingOrderLogDao;
    @Resource
    private ShoppingOrderPayDao shoppingOrderPayDao;
    @Resource
    private ShoppingGoodscartDao shoppingGoodscartDao;
    @Resource
    private ShoppingPaymentDao shoppingPaymentDao;
    @Resource
    private ShoppingRechargeDao shoppingRechargeDao;
    @Resource
    private ShoppingAssetDao shoppingAssetDao;
    @Resource
    private ShoppingAssetRecordDao shoppingAssetRecordDao;
    @Resource
    private ShoppingOrderPaykeyDao shoppingOrderPaykeyDao;
    @Resource
    private ShoppingRechargeActivityDao shoppingRechargeActivityDao;
    @Resource
    private ShoppingIntegralRecordDao shoppingIntegralRecordDao;
    @Resource
    private ShoppingBalanceRecordDao shoppingBalanceRecordDao;

    /**
     * 获取用户当前账户积分与余额
     */
    @Override
    public JSONObject queryAccountInfo(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String userId = reqJson.getString("userId");
            String mzUserId= CommonUtil.getMzUserId(userId);
            //从卖座实时查询账户积分和余额
            JSONObject accountObj = MZService.getAssetinfo(mzUserId);
            if (null != accountObj) {
                int accountOpenState =accountObj.getInteger("account_open_state");
                int accountLockState =accountObj.getInteger("account_lock_state");
                int account_point = accountObj.get("account_point") == null ? 0 : accountObj.getInteger("account_point");   //会员账户积分剩余点数，单位：点数；
                int account_money_fen = accountObj.get("account_money_fen") == null ? 0 : accountObj.getInteger("account_money_fen");
                BigDecimal accountMoney = new BigDecimal(account_money_fen).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);  //分转换为元
                bizDataJson.put("accountPoint", account_point);
                bizDataJson.put("accountMoney", accountMoney);
                bizDataJson.put("accountOpenState", accountOpenState);
                bizDataJson.put("accountLockState", accountLockState);
            }

            //查询积分和余额免密限额
            ShoppingAssetRule shoppingAssetRule = CommonUtil.getAssetRule();
            if (null != shoppingAssetRule) {
                bizDataJson.put("accountPointLimit", shoppingAssetRule.getPointAvoidLimit());
                bizDataJson.put("accountMoneyLimit", shoppingAssetRule.getAccountAvoidLimit());
            }
            //获取系统设置的余额充值优惠折扣
            bizDataJson.put("discountNum", CommonUtil.getRechargeDiscountNum());

            //获取用户当前可领取的积分和余额
            int toAccountPoint=0;
            BigDecimal toAccountMoney =BigDecimal.ZERO;
            ShoppingAsset shoppingAsset=new ShoppingAsset();
            shoppingAsset.setUserId(userId);
            shoppingAsset = shoppingAssetDao.queryDetail(shoppingAsset);
            if(null !=shoppingAsset){
                toAccountPoint=shoppingAsset.getIntegralValue();
                int balanceValue = shoppingAsset.getBalanceValue();
                toAccountMoney = new BigDecimal(balanceValue).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);  //分转换为元

            }
            bizDataJson.put("toAccountPoint", toAccountPoint);
            bizDataJson.put("toAccountMoney", toAccountMoney);

            //获取当前余额充值活动信息
            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("isPub", "1");
            reqMap.put("isDelete", "0");
            List<ShoppingRechargeActivity> activities=shoppingRechargeActivityDao.queryList(reqMap);
            if(activities.size()==1){
                ShoppingRechargeActivity activity = activities.get(0);
                String startTime = activity.getStartTime();
                String endTime = activity.getEndTime();
                //判断当前时间是否在充值活动时间内
                SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date sDate = sf.parse(startTime);
                Date eDate = sf.parse(endTime);
                Date nowDate = new Date();
                if(nowDate.getTime()>=sDate.getTime()&&nowDate.getTime()<=eDate.getTime()){
                    bizDataJson.put("rechargeActivity", activity);
                }
            }

            //获取余额单次充值上限
            ShoppingPayLimit payLimit = CommonUtil.getPayLimit();
            bizDataJson.put("rechargeLimit", payLimit==null?0:payLimit.getBalanceRecharge());

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
     * 获取用户会员资产变更记录
     */
    @Override
    public JSONObject queryAssetRecord(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String userId = reqJson.getString("userId");
            String mzUserId= CommonUtil.getMzUserId(userId);
            int asset_type = reqJson.getInteger("assetType");
            String start_time = reqJson.getString("startTime");
            String end_time = reqJson.getString("endTime");
            int page = reqJson.getInteger("pageNo");
            int page_size = reqJson.getInteger("pageSize");
            //从卖座查询资产变更记录
            JSONObject recordObj = MZService.getAssetRecordList(mzUserId,asset_type,start_time,end_time,page,page_size);
            if (null != recordObj) {
                if(null!=recordObj.getJSONObject("data_list")){
                    int total = recordObj.getInteger("total_row");
                    JSONArray objList = new JSONArray();
                    JSONObject dataObject = recordObj.getJSONObject("data_list");
                    if(null !=dataObject.get("data_list")){
                        JSONArray dataArray = dataObject.getJSONArray("data_list");
                        for(int i=0;i<dataArray.size();i++){
                            JSONObject resObj = dataArray.getJSONObject(i);
                            JSONObject obj = new JSONObject();
                            obj.put("record_id",resObj.getInteger("record_id"));
                            obj.put("asset_type",resObj.getInteger("asset_type"));
                            obj.put("change_type",resObj.getInteger("change_type"));
                            obj.put("change_value",resObj.getInteger("change_value"));
                            obj.put("change_time",resObj.getString("change_time"));
                            obj.put("change_reason",resObj.getString("change_reason"));
                            objList.add(obj);
                        }
                    }
                    bizDataJson.put("total", total);
                    bizDataJson.put("objList", objList);
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
     * 领取账户积分/余额
     */
    @Override
    public JSONObject addAsset(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "操作失败！";
        JSONObject bizDataJson = new JSONObject();
        try {
            String userId = reqJson.getString("userId");
            String mzUserId= CommonUtil.getMzUserId(userId);
            int assetType = reqJson.getInteger("assetType"); //1=积分 2=余额
            String asset_biz_key= reqJson.getString("asset_biz_key"); //资产key
            ShoppingAsset shoppingAsset=new ShoppingAsset();
            shoppingAsset.setUserId(userId);
            shoppingAsset = shoppingAssetDao.queryDetail(shoppingAsset);
            if(null !=shoppingAsset){
                int toAccountPoint=shoppingAsset.getIntegralValue();
                int toBalanceValue = shoppingAsset.getBalanceValue();
                String bussinessId = StringUtil.UUID();
                if(assetType==1){
                    if(toAccountPoint>0){
                        //查询赠送用户积分表内的未领取的积分数量，与待领取积分做对比，
                        HashMap<String, Object> reqMap = new HashMap<>();
                        reqMap.put("status", "0");
                        List<ShoppingIntegralRecord> records = shoppingIntegralRecordDao.queryList(reqMap);
                        int totalValue =0;
                        for(ShoppingIntegralRecord shoppingIntegralRecord:records){
                            totalValue+=shoppingIntegralRecord.getIntegralCount();
                        }
                        //只有待领取积分<=用户未领取的积分总数，才可以领取积分
                        if(toAccountPoint<=totalValue){
                            if(MZService.addPoint(mzUserId,toAccountPoint,asset_biz_key,bussinessId)){
                                shoppingAsset.setIntegralValue(0);
                                shoppingAssetDao.update(shoppingAsset);

                                ShoppingAssetRecord shoppingAssetRecord=new ShoppingAssetRecord();
                                shoppingAssetRecord.setUserId(userId);
                                shoppingAssetRecord.setAssetType(assetType);
                                shoppingAssetRecord.setChangeType(1);  //1 增加，2 减少
                                shoppingAssetRecord.setChangeReason("领取积分");
                                shoppingAssetRecord.setChangeValue(toAccountPoint);
                                shoppingAssetRecord.setBusinessId(bussinessId);
                                shoppingAssetRecordDao.insert(shoppingAssetRecord);

                                //将用户积分记录表中未领取的积分状态置为已领取，并关联上此次积分领取的记录id
                                String assetId = shoppingAssetRecord.getId();
                                ShoppingIntegralRecord record = new ShoppingIntegralRecord();
                                record.setUserId(userId);
                                record.setStatus("1");
                                record.setAssetId(assetId);
                                shoppingIntegralRecordDao.updateIntegralStatus(record);

                                retCode = "0";
                                retMsg = "操作成功！";
                            }else{
                                retCode = "1";
                                retMsg = "积分领取失败！";
                            }

                        }else{
                            retCode = "1";
                            retMsg = "您待领取的积分数量有误，请联系工作人员！";
                        }

                    }else{
                        retCode = "1";
                        retMsg = "您当前没有待领取的积分！";
                    }
                }else{
                    if(toBalanceValue>0){
                        //查询用户余额表内的未领取的余额数量，与待领取余额做对比，
                        HashMap<String, Object> reqMap = new HashMap<>();
                        reqMap.put("status", "0");
                        List<ShoppingBalanceRecord> records = shoppingBalanceRecordDao.queryList(reqMap);
                        int totalValue =0;
                        for(ShoppingBalanceRecord shoppingBalanceRecord:records){
                            totalValue+=shoppingBalanceRecord.getBalanceCount();
                        }
                        //只有待领取积分<=用户未领取的积分总数，才可以领取积分
                        if(toBalanceValue<=totalValue){
                            if(MZService.addMoney(mzUserId,toBalanceValue,asset_biz_key,bussinessId)){
                                shoppingAsset.setBalanceValue(0);
                                shoppingAssetDao.update(shoppingAsset);

                                ShoppingAssetRecord shoppingAssetRecord=new ShoppingAssetRecord();
                                shoppingAssetRecord.setUserId(userId);
                                shoppingAssetRecord.setAssetType(assetType);
                                shoppingAssetRecord.setChangeType(1);  //1 增加，2 减少
                                shoppingAssetRecord.setChangeReason("领取余额");
                                shoppingAssetRecord.setChangeValue(toBalanceValue);
                                shoppingAssetRecord.setBusinessId(bussinessId);
                                shoppingAssetRecordDao.insert(shoppingAssetRecord);

                                //将用户积分记录表中未领取的积分状态置为已领取，并关联上此次积分领取的记录id
                                String assetId = shoppingAssetRecord.getId();
                                ShoppingBalanceRecord record = new ShoppingBalanceRecord();
                                record.setStatus("1");
                                record.setUserId(userId);
                                record.setAssetId(assetId);
                                shoppingBalanceRecordDao.updateBalanceStatus(record);
                                retCode = "0";
                                retMsg = "操作成功！";
                            }else{
                                retCode = "1";
                                retMsg = "调用麦座接口失败！";
                            }
                        }else{
                            retCode = "1";
                            retMsg = "您待领取的积分数量有误，请联系工作人员！";
                        }
                    }else{
                        retCode = "1";
                        retMsg = "您当前没有待领取的积分！";
                    }

                }

            }else{
                retCode = "1";
                retMsg = "该用户没有可领取的资产！";
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
     * 充值待支付金额计算
     */
    @Override
    public JSONObject renderRechargeOrder(JSONObject reqJson, HttpServletRequest request) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "服务器内部错误！";
        JSONObject bizDataJson = new JSONObject();
        try {
//            String data = request.getParameter("data");// 参数
//            JSONObject reqJson = JSONObject.parseObject(URLDecoder.decode(data, "utf-8"));

            String userId = reqJson.getString("userId");   //userId
            BigDecimal moneyAmount= reqJson.getBigDecimal("moneyAmount");
//            BigDecimal discountNum  = CommonUtil.getRechargeDiscountNum();  //折扣
//            BigDecimal payPrice = moneyAmount.multiply(discountNum).setScale(2, BigDecimal.ROUND_HALF_UP);

            bizDataJson.put("payPrice", moneyAmount);       //需要支付的金额
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
     * 创建充值订单
     */
    @Override
    public JSONObject addRechargeOrder(JSONObject reqJson, HttpServletRequest request) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "服务器内部错误！";
        JSONObject bizDataJson = new JSONObject();
        try {
//            String data = request.getParameter("data");// 参数
//            JSONObject reqJson = JSONObject.parseObject(URLDecoder.decode(data, "utf-8"));

            String userId = reqJson.getString("userId");//userId
            BigDecimal orderTotalPrice = reqJson.getBigDecimal("orderTotalPrice");//订单金额，即充值金额
            BigDecimal orderPayPrice = reqJson.getBigDecimal("orderPayPrice");//待支付的现金金额

            //计算实时金额
//            BigDecimal discountNum  = CommonUtil.getRechargeDiscountNum();  //折扣
//            BigDecimal payPrice = orderTotalPrice.multiply(discountNum).setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal payPrice = orderTotalPrice;
            if (payPrice.compareTo(orderPayPrice)!=0) {
                retCode = "-1";
                retMsg = "待支付金额发生变化，请重新确认订单信息！";
            } else {
                //创建订单信息
                ShoppingOrderform orderform = new ShoppingOrderform();
                //订单id（系统订单全局唯一标识）
                String orderId = PayUtil.getOrderNo(Const.RECHARGE_ORDER);
                orderform.setOrderId(orderId);
                //订单类型：充值
                orderform.setOrderType(Const.RECHARGE_ORDER_TYPE);
                //订单状态：待支付
                orderform.setOrderStatus(10);
                //订单金额
                orderform.setTotalPrice(orderTotalPrice);
                //需支付的现金金额
                orderform.setPayPrice(payPrice);
                //商店id
                orderform.setStoreId(Const.STORE_ID);
                //用户id
                orderform.setUserId(userId);

                //订单支付信息
                ShoppingOrderPay shoppingOrderPay = new ShoppingOrderPay();
                shoppingOrderPay.setUserId(userId);
                if (payPrice.compareTo(BigDecimal.ZERO) == 1) {
                    shoppingOrderPay.setCashStatus(0);
                }

                //保存订单信息
                shoppingOrderformDao.insert(orderform);

                //保存订单支付信息
                shoppingOrderPay.setOfId(orderform.getId());
                shoppingOrderPayDao.insert(shoppingOrderPay);

                //保存资产验证码
                ShoppingOrderPaykey shoppingOrderPaykey = new ShoppingOrderPaykey();
                shoppingOrderPaykey.setAccountMoneyPayKey(reqJson.getString("accountMoneyPayKey"));
                shoppingOrderPaykey.setOfId(orderform.getId());
                shoppingOrderPaykeyDao.insert(shoppingOrderPaykey);

                // 添加订单日志
                ShoppingOrderLog shoppingOrderLog = new ShoppingOrderLog();
                shoppingOrderLog.setLogInfo("提交订单");
//                        shoppingOrderLog.setStateInfo(orderform.getOrderType());
                shoppingOrderLog.setLogUserId(userId);
                shoppingOrderLog.setOfId(orderform.getId());
                shoppingOrderLogDao.insert(shoppingOrderLog);

                //添加充值记录
                ShoppingRecharge shoppingRecharge = new ShoppingRecharge();
                shoppingRecharge.setUserId(userId);
                shoppingRecharge.setMoneyAmount(orderTotalPrice);
                shoppingRecharge.setPayAmount(payPrice);
                shoppingRechargeDao.insert(shoppingRecharge);

                //保存订单-商品关联信息
                ShoppingGoodscart goodscart = new ShoppingGoodscart();
                String scId = CommonUtil.getUserScId(userId);
                goodscart.setScId(scId);
                goodscart.setGoodsId(shoppingRecharge.getId());
                goodscart.setCount(1);
                goodscart.setCartType(Const.RECHARGE_CART_TYPE);

//                goodscart.setDeductionCouponPrice(orderform.getDeductionCouponPrice());
//                goodscart.setDeductionMemberPrice(orderform.getDeductionMemberPrice());
//                goodscart.setDeductionIntegral(orderform.getDeductionIntegral());
//                goodscart.setDeductionIntegralPrice(orderform.getDeductionIntegralPrice());
//                goodscart.setDeductionBalancePrice(orderform.getDeductionBalancePrice());
                goodscart.setPayPrice(orderform.getPayPrice());

                //下单时的商品价格
                goodscart.setPrice(payPrice);
                goodscart.setOfId(orderform.getId());
                shoppingGoodscartDao.insert(goodscart);

                //获取当前系统可用支付方式
                HashMap<String, Object> reqMap = new HashMap<>();
                reqMap.put("deleteStatus", 0);
                List<ShoppingPayment> payments = shoppingPaymentDao.queryList(reqMap);
                bizDataJson.put("payments", payments);

                bizDataJson.put("orderId", orderId);
                bizDataJson.put("price", payPrice);
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
     * 查看我的充值记录
     */
    @Override
    public JSONObject myRechargeList(JSONObject reqJson) {
        JSONObject retJson = new JSONObject();
        String retCode = "1";
        String retMsg = "服务器内部错误！";
        JSONObject bizDataJson = new JSONObject();
        try {
//            String data = request.getParameter("data");// 参数
//            JSONObject reqJson = JSONObject.parseObject(URLDecoder.decode(data, "utf-8"));

            String userId = reqJson.getString("userId");   //userId
            int pageNo =reqJson.get("pageNo")==null?1:reqJson.getInteger("pageNo");
            int pageSize = reqJson.get("pageSize")==null?10:reqJson.getInteger("pageSize");
            HashMap<String, Object> reqMap = JSON.parseObject(reqJson.toJSONString(), HashMap.class);
            reqMap.put("startRow", (pageNo-1)*pageSize);
            reqMap.put("pageSize", pageSize);
            reqMap.put("deleteStatus","0");
            reqMap.put("userId",userId);
            bizDataJson.put("total",shoppingRechargeDao.queryTotalCount(reqMap));
            List<ShoppingRecharge> objList = shoppingRechargeDao.queryList(reqMap);
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

    //
}
