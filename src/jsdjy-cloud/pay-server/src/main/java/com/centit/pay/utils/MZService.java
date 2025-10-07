package com.centit.pay.utils;


import com.alibaba.fastjson.JSONObject;
import com.centit.pay.biz.po.ShoppingAssetRecord;
import com.centit.pay.biz.po.ShoppingOrderLog;
import com.centit.pay.biz.po.ShoppingOrderPaylog;
import com.centit.pay.biz.po.ShoppingOrderform;
import com.centit.pay.common.enums.Const;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.*;
import com.taobao.api.response.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;


public class MZService {


    private static final Logger log = LoggerFactory.getLogger(MZService.class);


    public static TaobaoClient getClient() {
        return new DefaultTaobaoClient(Const.MZ_CLIENT_URL,Const.MZ_CLIENT_APPKEY,Const.MZ_CLIENT_SECRET);
    }

    /**
     * @描述: 获取积分和余额免密限额信息
     * @参数: [userId 用户id]
     */
    public static JSONObject getAssetRule() {

            TaobaoClient client = getClient();
            AlibabaDamaiMzUserAssetRuleGetRequest req = new AlibabaDamaiMzUserAssetRuleGetRequest();
            String reqtime = StringUtil.nowTimeString();
        try {
            AlibabaDamaiMzUserAssetRuleGetResponse rsp = client.execute(req);
            String rettime = StringUtil.nowTimeString();

            log.info(rsp.getBody());
            try{
                JSONObject resObj = JSONObject.parseObject(rsp.getBody()).getJSONObject("alibaba_damai_mz_user_asset_rule_get_response").getJSONObject("result");
                CommonUtil.addThirdLog(0,Const.THIRDLOG_TYPE_MZ,"获取积分和余额免密限额信息","POST","AlibabaDamaiMzUserAssetRuleGetRequest",
                        reqtime,req.getTextParams().toString(),rettime,rsp.getBody());
                if(resObj.get("code").equals("200")&&resObj.getBoolean("success")){
                    return resObj.getJSONObject("data");
                }else{
                    return null;
                }
            }catch (Exception e) {
                CommonUtil.addThirdLog(1,Const.THIRDLOG_TYPE_MZ,"获取积分和余额免密限额信息","POST","AlibabaDamaiMzUserAssetRuleGetRequest",
                        reqtime,req.getTextParams().toString(),rettime,rsp.getBody());
                e.printStackTrace();
                return null;
            }

        } catch (Exception e) {
            String rettime = StringUtil.nowTimeString();
            CommonUtil.addThirdLog(1,Const.THIRDLOG_TYPE_MZ,"获取积分和余额免密限额信息","POST","AlibabaDamaiMzUserAssetRuleGetRequest",
                    reqtime,req.getTextParams().toString(),rettime,e.toString());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @描述: 获取账户资产信息
     * @参数: [userId 用户id]
     */
    public static JSONObject getAssetinfo(String mzUserId,ShoppingOrderform orderform) {

            TaobaoClient client = getClient();
            AlibabaDamaiMzUserAssetinfoGetRequest req = new AlibabaDamaiMzUserAssetinfoGetRequest();
            req.setMzUserId(mzUserId);
            String reqtime = StringUtil.nowTimeString();
        try {
            AlibabaDamaiMzUserAssetinfoGetResponse rsp = client.execute(req);
            String rettime = StringUtil.nowTimeString();

            //记录日志
            ShoppingOrderPaylog shoppingOrderPaylog = new ShoppingOrderPaylog();
            shoppingOrderPaylog.setTradeType("获取账户资产信息");
            shoppingOrderPaylog.setLogInfo("获取账户资产信息");
            shoppingOrderPaylog.setLogContent(rsp.getBody());
            shoppingOrderPaylog.setOfId(orderform.getId());
            CommonInit.staticShoppingOrderPaylogDao.insert(shoppingOrderPaylog);
            log.info(rsp.getBody());
            try{
                JSONObject resObj = JSONObject.parseObject(rsp.getBody()).getJSONObject("alibaba_damai_mz_user_assetinfo_get_response").getJSONObject("result");
                CommonUtil.addThirdLog(0,Const.THIRDLOG_TYPE_MZ,"获取账户资产信息","POST","AlibabaDamaiMzUserAssetinfoGetRequest",
                        reqtime,req.getTextParams().toString(),rettime,rsp.getBody());
                if(resObj.get("code").equals("200")&&resObj.getBoolean("success")){
                    return resObj.getJSONObject("data");
                }else{
                    return null;
                }
            }catch (Exception e) {
                CommonUtil.addThirdLog(1,Const.THIRDLOG_TYPE_MZ,"获取账户资产信息","POST","AlibabaDamaiMzUserAssetinfoGetRequest",
                        reqtime,req.getTextParams().toString(),rettime,rsp.getBody());
                e.printStackTrace();
                return null;
            }

        } catch (Exception e) {
            String rettime = StringUtil.nowTimeString();
            CommonUtil.addThirdLog(1,Const.THIRDLOG_TYPE_MZ,"获取账户资产信息","POST","AlibabaDamaiMzUserAssetinfoGetRequest",
                    reqtime,req.getTextParams().toString(),rettime,e.toString());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @描述: 发送资产验证码
     * @参数: [userId 用户id]
     */
    public static boolean sendVerifyCode(String mzUserId,String mobilePhone) {

            TaobaoClient client = getClient();
            AlibabaDamaiMzAssetVerifycodeSendRequest req = new AlibabaDamaiMzAssetVerifycodeSendRequest();
            req.setMzUserId(mzUserId);
            req.setMobilePhone(mobilePhone);
            String reqtime = StringUtil.nowTimeString();
        try {
            AlibabaDamaiMzAssetVerifycodeSendResponse rsp = client.execute(req);
            String rettime = StringUtil.nowTimeString();

            log.info(rsp.getBody());
            try{
                JSONObject resObj = JSONObject.parseObject(rsp.getBody()).getJSONObject("alibaba_damai_mz_asset_verifycode_send_response").getJSONObject("result");
                CommonUtil.addThirdLog(0,Const.THIRDLOG_TYPE_MZ,"发送资产验证码","POST","AlibabaDamaiMzAssetVerifycodeSendRequest",
                        reqtime,req.getTextParams().toString(),rettime,rsp.getBody());
                if(resObj.get("code").equals("200")&&resObj.getBoolean("success")){
                    return true;
                }else{
                    return false;
                }
            }catch (Exception e) {
                CommonUtil.addThirdLog(1,Const.THIRDLOG_TYPE_MZ,"发送资产验证码","POST","AlibabaDamaiMzAssetVerifycodeSendRequest",
                        reqtime,req.getTextParams().toString(),rettime,rsp.getBody());
                e.printStackTrace();
                return false;
            }

        } catch (Exception e) {
            String rettime = StringUtil.nowTimeString();
            CommonUtil.addThirdLog(1,Const.THIRDLOG_TYPE_MZ,"发送资产验证码","POST","AlibabaDamaiMzAssetVerifycodeSendRequest",
                    reqtime,req.getTextParams().toString(),rettime,e.toString());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @描述: 获取资产业务key
     * @参数: [userId 用户id]
     */
    public static JSONObject checkVerifyCode(String mzUserId,String verifyCode) {

            TaobaoClient client = getClient();
            AlibabaDamaiMzAssetVerifycodeCheckRequest req = new AlibabaDamaiMzAssetVerifycodeCheckRequest();
            req.setMzUserId(mzUserId);
            req.setVerifyCode(verifyCode);
            String reqtime = StringUtil.nowTimeString();
        try {
            AlibabaDamaiMzAssetVerifycodeCheckResponse rsp = client.execute(req);
            String rettime = StringUtil.nowTimeString();

            log.info(rsp.getBody());
            try{
                JSONObject resObj = JSONObject.parseObject(rsp.getBody()).getJSONObject("alibaba_damai_mz_asset_verifycode_check_response").getJSONObject("result");
                CommonUtil.addThirdLog(0,Const.THIRDLOG_TYPE_MZ,"获取资产业务key","POST","AlibabaDamaiMzAssetVerifycodeCheckRequest",
                        reqtime,req.getTextParams().toString(),rettime,rsp.getBody());
                if(resObj.get("code").equals("200")&&resObj.getBoolean("success")){
                    return resObj.getJSONObject("data");
                }else{
                    return null;
                }
            }catch (Exception e) {
                CommonUtil.addThirdLog(1,Const.THIRDLOG_TYPE_MZ,"获取资产业务key","POST","AlibabaDamaiMzAssetVerifycodeCheckRequest",
                        reqtime,req.getTextParams().toString(),rettime,rsp.getBody());
                e.printStackTrace();
                return null;
            }

        } catch (Exception e) {
            String rettime = StringUtil.nowTimeString();
            CommonUtil.addThirdLog(1,Const.THIRDLOG_TYPE_MZ,"获取资产业务key","POST","AlibabaDamaiMzAssetVerifycodeCheckRequest",
                    reqtime,req.getTextParams().toString(),rettime,e.toString());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @描述: 会员积分抵扣
     * @参数: [userId 用户id]
     */
    public static boolean cutPoint(String mzUserId,int integralValue,ShoppingOrderform orderform, String assetBizKey) {

            TaobaoClient client = getClient();
            AlibabaDamaiMzUserAssetModifyRequest req = new AlibabaDamaiMzUserAssetModifyRequest();
            req.setMzUserId(mzUserId);
            req.setAssetType(1L);  //资产类型： 1=积分 2=余额
            req.setChangeType(2L);  //变更类型 1 增加，2 减少
            req.setChangeValue(Long.valueOf(integralValue));
            req.setBusinessId("POINT_"+orderform.getOrderId());
            req.setChangeReason("江苏大剧院APP消费抵扣，订单号"+orderform.getOrderId());
            if(null !=assetBizKey){
                req.setAssetBizKey(assetBizKey);
            }
            String reqtime = StringUtil.nowTimeString();
        try {
            AlibabaDamaiMzUserAssetModifyResponse rsp = client.execute(req);
            String rettime = StringUtil.nowTimeString();

            //记录日志
            ShoppingOrderPaylog shoppingOrderPaylog = new ShoppingOrderPaylog();
            shoppingOrderPaylog.setTradeType("积分支付");
            shoppingOrderPaylog.setLogInfo("积分扣除");
            shoppingOrderPaylog.setLogContent(rsp.getBody());
            shoppingOrderPaylog.setOfId(orderform.getId());
            shoppingOrderPaylog.setOutTradeNo("POINT_"+orderform.getOrderId());
            CommonInit.staticShoppingOrderPaylogDao.insert(shoppingOrderPaylog);
            log.info(rsp.getBody());
            try{
                JSONObject resObj = JSONObject.parseObject(rsp.getBody()).getJSONObject("alibaba_damai_mz_user_asset_modify_response").getJSONObject("result");
                CommonUtil.addThirdLog(0,Const.THIRDLOG_TYPE_MZ,"会员积分抵扣","POST","AlibabaDamaiMzUserAssetModifyRequest",
                        reqtime,req.getTextParams().toString(),rettime,rsp.getBody());
                if(resObj.get("code").equals("200")&&resObj.getBoolean("success")){
                    shoppingOrderPaylog.setStateInfo("积分扣除成功");

                    ShoppingAssetRecord shoppingAssetRecord = new ShoppingAssetRecord();
                    shoppingAssetRecord.setUserId(orderform.getUserId());
                    int assetType = 1; //1=积分 2=余额
                    shoppingAssetRecord.setAssetType(assetType);
                    shoppingAssetRecord.setChangeType(2);  //1 增加，2 减少
                    shoppingAssetRecord.setChangeReason("江苏大剧院APP消费抵扣");
                    shoppingAssetRecord.setChangeValue(Long.valueOf(integralValue));
                    shoppingAssetRecord.setBusinessId("POINT_"+orderform.getOrderId());
                    CommonInit.staticShoppingAssetRecordDao.insert(shoppingAssetRecord);
                    return true;
                }else{
                    //失败时需要记录失败日志
                    ShoppingOrderLog shoppingOrderLog = new ShoppingOrderLog();
                    shoppingOrderLog.setLogInfo("订单积分扣除失败："+rsp.getBody());
                    shoppingOrderLog.setLogUserId(orderform.getUserId());
                    shoppingOrderLog.setOfId(orderform.getId());
                    CommonInit.staticShoppingOrderLogDao.insert(shoppingOrderLog);

                    shoppingOrderPaylog.setStateInfo("积分扣除失败");
                    return false;
                }
            }catch (Exception e) {
                CommonUtil.addThirdLog(1,Const.THIRDLOG_TYPE_MZ,"会员积分抵扣","POST","AlibabaDamaiMzUserAssetModifyRequest",
                        reqtime,req.getTextParams().toString(),rettime,rsp.getBody());
                e.printStackTrace();
                return false;
            }

        } catch (Exception e) {
            String rettime = StringUtil.nowTimeString();
            CommonUtil.addThirdLog(1,Const.THIRDLOG_TYPE_MZ,"会员积分抵扣","POST","AlibabaDamaiMzUserAssetModifyRequest",
                    reqtime,req.getTextParams().toString(),rettime,e.toString());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @描述: 会员余额抵扣
     * @参数: [userId 用户id]
     */
    public static boolean cutMoney(String mzUserId, int changeValue, ShoppingOrderform orderform, String assetBizKey) {

            TaobaoClient client = getClient();
            AlibabaDamaiMzUserAssetModifyRequest req = new AlibabaDamaiMzUserAssetModifyRequest();
            req.setMzUserId(mzUserId);
            req.setAssetType(2L);  //资产类型： 1=积分 2=余额
            req.setChangeType(2L);  //变更类型 1 增加，2 减少
            req.setChangeValue(Long.valueOf(changeValue));
            req.setBusinessId("MONEY_"+orderform.getOrderId());
            req.setChangeReason("江苏大剧院APP消费抵扣，订单号"+orderform.getOrderId());
            if(null !=assetBizKey){
                req.setAssetBizKey(assetBizKey);
            }
            String reqtime = StringUtil.nowTimeString();
        try {
            AlibabaDamaiMzUserAssetModifyResponse rsp = client.execute(req);
            String rettime = StringUtil.nowTimeString();

            //记录日志
            ShoppingOrderPaylog shoppingOrderPaylog = new ShoppingOrderPaylog();
            shoppingOrderPaylog.setTradeType("余额支付");
            shoppingOrderPaylog.setLogInfo("余额扣除");
            shoppingOrderPaylog.setLogContent(rsp.getBody());
            shoppingOrderPaylog.setOfId(orderform.getId());
            shoppingOrderPaylog.setOutTradeNo("MONEY"+orderform.getOrderId());
            CommonInit.staticShoppingOrderPaylogDao.insert(shoppingOrderPaylog);
            log.info(rsp.getBody());
            try{
                JSONObject resObj = JSONObject.parseObject(rsp.getBody()).getJSONObject("alibaba_damai_mz_user_asset_modify_response").getJSONObject("result");
                CommonUtil.addThirdLog(0,Const.THIRDLOG_TYPE_MZ,"会员余额抵扣","POST","AlibabaDamaiMzUserAssetModifyRequest",
                        reqtime,req.getTextParams().toString(),rettime,rsp.getBody());
                if(resObj.get("code").equals("200")&&resObj.getBoolean("success")){
                    shoppingOrderPaylog.setStateInfo("余额扣除成功");

                    ShoppingAssetRecord shoppingAssetRecord = new ShoppingAssetRecord();
                    shoppingAssetRecord.setUserId(orderform.getUserId());
                    int assetType = 2; //1=积分 2=余额
                    shoppingAssetRecord.setAssetType(assetType);
                    shoppingAssetRecord.setChangeType(2);  //1 增加，2 减少
                    shoppingAssetRecord.setChangeReason("江苏大剧院APP消费抵扣");
                    shoppingAssetRecord.setChangeValue(Long.valueOf(changeValue));
                    shoppingAssetRecord.setBusinessId("MONEY_"+orderform.getOrderId());
                    CommonInit.staticShoppingAssetRecordDao.insert(shoppingAssetRecord);
                    return true;
                }else{
                    //失败时需要记录失败日志
                    ShoppingOrderLog shoppingOrderLog = new ShoppingOrderLog();
                    shoppingOrderLog.setLogInfo("订单余额扣除失败："+rsp.getBody());
                    shoppingOrderLog.setLogUserId(orderform.getUserId());
                    shoppingOrderLog.setOfId(orderform.getId());
                    CommonInit.staticShoppingOrderLogDao.insert(shoppingOrderLog);

                    shoppingOrderPaylog.setStateInfo("余额扣除失败");
                    return false;
                }
            }catch (Exception e) {
                CommonUtil.addThirdLog(1,Const.THIRDLOG_TYPE_MZ,"会员余额抵扣","POST","AlibabaDamaiMzUserAssetModifyRequest",
                        reqtime,req.getTextParams().toString(),rettime,rsp.getBody());
                e.printStackTrace();
                return false;
            }

        } catch (Exception e) {
            String rettime = StringUtil.nowTimeString();
            CommonUtil.addThirdLog(1,Const.THIRDLOG_TYPE_MZ,"会员余额抵扣","POST","AlibabaDamaiMzUserAssetModifyRequest",
                    reqtime,req.getTextParams().toString(),rettime,e.toString());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @描述: 确认订单
     * @参数: [userId 用户id]
     */
    public static boolean confirmOrder(ShoppingOrderform orderform, String thirdPayTypeId,String out_order_id,String mzUserId) {
        String reStr = confirmOrderToMZ(orderform, thirdPayTypeId,out_order_id,mzUserId);
        if(reStr.equals("success")){
            return true;
        }else{
            int i=0;
            while(i<2&&(reStr.equals("15010020")||reStr.contains("连接超时")||reStr.contains("timed out")||reStr.contains("java.io.IOException"))){
                reStr = confirmOrderToMZ(orderform, thirdPayTypeId,out_order_id,mzUserId);
                i++;
            }
            if(reStr.equals("success")){
                return true;
            }else if(reStr.equals("15010020")){
                return true;
            }else{
                return false;
            }
        }

    }

    /**
     * @描述: 向麦座确认订单
     * @参数: [userId 用户id]
     */
    public static String confirmOrderToMZ(ShoppingOrderform orderform, String thirdPayTypeId,String out_order_id,String mzUserId) {

        TaobaoClient client = getClient();
        AlibabaDamaiMzOrderConfirmRequest req = new AlibabaDamaiMzOrderConfirmRequest();
//        //计算麦座订单应收金额，单位：分（麦座订单应收金额=商品应收金额+运费金额-优惠金额）
//        BigDecimal orderTolPrice = orderform.getOrderTolPrice();
//        //订单总价减去优惠金额，并转换为分
//        long orderReceiveMoneyFen = orderTolPrice.multiply(new BigDecimal(100)).longValue();

        //计算麦座订单应收金额，单位：分
        //4.25更新，orderReceiveMoneyFen改为传实际的现金支付的金额
        BigDecimal payPrice = orderform.getPayPrice();
        long orderReceiveMoneyFen = payPrice.multiply(new BigDecimal(100)).longValue();

        req.setOrderReceiveMoneyFen(orderReceiveMoneyFen);
        req.setThirdPayTypeId(thirdPayTypeId);
        req.setMzUserId(mzUserId);
        req.setMzOrderId(orderform.getOutOrderId());
        //第三方支付流水号
        req.setThirdPayNo(out_order_id);
        req.setSendMsgMark(true);
        String reqtime = StringUtil.nowTimeString();
        try {
            AlibabaDamaiMzOrderConfirmResponse rsp = client.execute(req);
            String rettime = StringUtil.nowTimeString();

            //记录日志
            ShoppingOrderPaylog shoppingOrderPaylog = new ShoppingOrderPaylog();
            shoppingOrderPaylog.setTradeType("确认麦座订单");
            shoppingOrderPaylog.setLogInfo("确认麦座订单");
            shoppingOrderPaylog.setLogContent(rsp.getBody());
            shoppingOrderPaylog.setOfId(orderform.getId());

            log.info(rsp.getBody());
            try{
                JSONObject resObj = JSONObject.parseObject(rsp.getBody()).getJSONObject("alibaba_damai_mz_order_confirm_response").getJSONObject("result");
                CommonUtil.addThirdLog(0,Const.THIRDLOG_TYPE_MZ,"确认订单","POST","AlibabaDamaiMzOrderConfirmRequest",
                        reqtime,req.getTextParams().toString(),rettime,rsp.getBody());
                if(resObj.get("code").equals("200")&&resObj.getBoolean("success")){
                    shoppingOrderPaylog.setStateInfo("确认麦座订单成功");
                    String mz_order_id = resObj.getJSONObject("data").getString("mz_order_id");
                    shoppingOrderPaylog.setOutTradeNo(mz_order_id);
                    CommonInit.staticShoppingOrderPaylogDao.insert(shoppingOrderPaylog);
                    return "success";
                }else if(resObj.get("code").equals("15040003")&&resObj.get("msg").equals("支付金额不匹配")){
                    //临时调整，由于orderReceiveMoneyFen目前存在新老项目的冲突,如果确认订单出现支付金额不匹配的情况，换另一个值重新尝试一次

                    //计算麦座订单应收金额，单位：分（麦座订单应收金额=商品应收金额+运费金额-优惠金额）
                    BigDecimal orderTolPrice = orderform.getOrderTolPrice();
                    //订单总价减去优惠金额，并转换为
                    orderReceiveMoneyFen = orderTolPrice.multiply(new BigDecimal(100)).longValue();
                    req.setOrderReceiveMoneyFen(orderReceiveMoneyFen);
                    reqtime = StringUtil.nowTimeString();

                    rsp = client.execute(req);
                    rettime = StringUtil.nowTimeString();

                    shoppingOrderPaylog = new ShoppingOrderPaylog();
                    shoppingOrderPaylog.setTradeType("确认麦座订单");
                    shoppingOrderPaylog.setLogInfo("确认麦座订单");
                    shoppingOrderPaylog.setLogContent(rsp.getBody());
                    shoppingOrderPaylog.setOfId(orderform.getId());
                    log.info(rsp.getBody());
                    resObj = JSONObject.parseObject(rsp.getBody()).getJSONObject("alibaba_damai_mz_order_confirm_response").getJSONObject("result");
                    CommonUtil.addThirdLog(0,Const.THIRDLOG_TYPE_MZ,"确认订单","POST","AlibabaDamaiMzOrderConfirmRequest",
                            reqtime,req.getTextParams().toString(),rettime,rsp.getBody());
                    if(resObj.get("code").equals("200")&&resObj.getBoolean("success")){
                        shoppingOrderPaylog.setStateInfo("确认麦座订单成功");
                        String mz_order_id = resObj.getJSONObject("data").getString("mz_order_id");
                        shoppingOrderPaylog.setOutTradeNo(mz_order_id);
                        CommonInit.staticShoppingOrderPaylogDao.insert(shoppingOrderPaylog);
                        return "success";
                    }else{
                        return resObj.get("code").toString();
                    }
                } else{
                    return resObj.get("code").toString();
                }
            }catch (Exception e) {
                CommonUtil.addThirdLog(1,Const.THIRDLOG_TYPE_MZ,"确认订单","POST","AlibabaDamaiMzOrderConfirmRequest",
                        reqtime,req.getTextParams().toString(),rettime,rsp.getBody());
                e.printStackTrace();
                return e.toString();
            }

        } catch (Exception e) {
            String rettime = StringUtil.nowTimeString();
            CommonUtil.addThirdLog(1,Const.THIRDLOG_TYPE_MZ,"确认订单","POST","AlibabaDamaiMzOrderConfirmRequest",
                    reqtime,req.getTextParams().toString(),rettime,e.toString());
            e.printStackTrace();
            return e.toString();
        }
    }


//    /**
//     * @描述: 确认订单
//     * @参数: [userId 用户id]
//     */
//    public static boolean confirmOrder(ShoppingOrderform orderform, String thirdPayTypeId,String mzUserId) {
//
//            TaobaoClient client = getClient();
//            AlibabaDamaiMzOrderConfirmRequest req = new AlibabaDamaiMzOrderConfirmRequest();
//            //计算麦座订单应收金额，单位：分（麦座订单应收金额=商品应收金额+运费金额-优惠金额）
//            BigDecimal orderTolPrice = orderform.getOrderTolPrice();
////            BigDecimal deductionCouponPrice = orderform.getDeductionCouponPrice();
//            //订单总价减去优惠金额，并转换为分
//            long orderReceiveMoneyFen = orderTolPrice.multiply(new BigDecimal(100)).longValue();
//            req.setOrderReceiveMoneyFen(orderReceiveMoneyFen);
//            req.setThirdPayTypeId(thirdPayTypeId);
//            req.setMzUserId(mzUserId);
//            req.setMzOrderId(orderform.getOutOrderId());
//            req.setSendMsgMark(true);
//            String reqtime = StringUtil.nowTimeString();
//        try {
//            AlibabaDamaiMzOrderConfirmResponse rsp = client.execute(req);
//            String rettime = StringUtil.nowTimeString();
//            CommonUtil.addThirdLog(0,Const.THIRDLOG_TYPE_MZ,"确认订单","POST","AlibabaDamaiMzOrderConfirmRequest",
//                    reqtime,req.getTextParams().toString(),rettime,rsp.getBody());
//            //记录日志
//            ShoppingOrderPaylog shoppingOrderPaylog = new ShoppingOrderPaylog();
//            shoppingOrderPaylog.setTradeType("确认麦座订单");
//            shoppingOrderPaylog.setLogInfo("确认麦座订单");
//            shoppingOrderPaylog.setLogContent(rsp.getBody());
//            shoppingOrderPaylog.setOfId(orderform.getId());
//
//            log.info(rsp.getBody());
//            JSONObject resObj = JSONObject.parseObject(rsp.getBody()).getJSONObject("alibaba_damai_mz_order_confirm_response").getJSONObject("result");
//            if(resObj.get("code").equals("200")&&resObj.getBoolean("success")){
//                shoppingOrderPaylog.setStateInfo("确认麦座订单成功");
//                String mz_order_id = resObj.getJSONObject("data").getString("mz_order_id");
//                shoppingOrderPaylog.setOutTradeNo(mz_order_id);
//                CommonInit.staticShoppingOrderPaylogDao.insert(shoppingOrderPaylog);
//                return true;
//            }else{
//                if(resObj.get("code").equals("15010020")){   //15010020的返回码表示麦座订单正在处理中，需要等待1s后再次请求
//                    shoppingOrderPaylog.setStateInfo("确认麦座订单失败，接口返回操作中");
////                    String mz_order_id = resObj.getJSONObject("data").getString("mz_order_id");
////                    shoppingOrderPaylog.setOutTradeNo(mz_order_id);
//                    CommonInit.staticShoppingOrderPaylogDao.insert(shoppingOrderPaylog);
//                    try {
//                        rsp = client.execute(req);
//                        rettime = StringUtil.nowTimeString();
//                        CommonUtil.addThirdLog(0,Const.THIRDLOG_TYPE_MZ,"再次确认订单","POST","AlibabaDamaiMzOrderConfirmRequest",
//                                reqtime,req.getTextParams().toString(),rettime,rsp.getBody());
//                        //记录日志
//                        shoppingOrderPaylog = new ShoppingOrderPaylog();
//                        shoppingOrderPaylog.setTradeType("再次确认麦座订单");
//                        shoppingOrderPaylog.setLogInfo("再次确认麦座订单");
//                        shoppingOrderPaylog.setLogContent(rsp.getBody());
//                        shoppingOrderPaylog.setOfId(orderform.getId());
//
//                        log.info(rsp.getBody());
//                        resObj = JSONObject.parseObject(rsp.getBody()).getJSONObject("alibaba_damai_mz_order_confirm_response").getJSONObject("result");
//                        if(resObj.get("code").equals("200")&&resObj.getBoolean("success")){
//                            shoppingOrderPaylog.setStateInfo("再次确认麦座订单成功");
//                            String mz_order_id = resObj.getJSONObject("data").getString("mz_order_id");
//                            shoppingOrderPaylog.setOutTradeNo(mz_order_id);
//                            CommonInit.staticShoppingOrderPaylogDao.insert(shoppingOrderPaylog);
//                            return true;
//                        }else{
//                            if(resObj.get("code").equals("15010020")){   //如果再次请求时还是操作中，则默认为订单确认成功
//                                shoppingOrderPaylog.setStateInfo("再次确认麦座订单失败，接口返回操作中");
//                                CommonInit.staticShoppingOrderPaylogDao.insert(shoppingOrderPaylog);
//                                return true;
//                            }else{
//                                shoppingOrderPaylog.setStateInfo("再次确认麦座订单失败");
//                                CommonInit.staticShoppingOrderPaylogDao.insert(shoppingOrderPaylog);
//                                return false;
//                            }
//                        }
//                    } catch (Exception e) {
//                        rettime = StringUtil.nowTimeString();
//                        CommonUtil.addThirdLog(1,Const.THIRDLOG_TYPE_MZ,"再次确认订单","POST","AlibabaDamaiMzOrderConfirmRequest",
//                                reqtime,req.getTextParams().toString(),rettime,e.toString());
//                        e.printStackTrace();
//                        return false;
//                    }
//
//                }else{
//                    shoppingOrderPaylog.setStateInfo("确认麦座订单失败");
//                    CommonInit.staticShoppingOrderPaylogDao.insert(shoppingOrderPaylog);
//                    return false;
//                }
//            }
//        } catch (Exception e) {
//            String rettime = StringUtil.nowTimeString();
//            CommonUtil.addThirdLog(1,Const.THIRDLOG_TYPE_MZ,"确认订单","POST","AlibabaDamaiMzOrderConfirmRequest",
//                    reqtime,req.getTextParams().toString(),rettime,e.toString());
//            e.printStackTrace();
//            return false;
//        }
//    }


    /**
     * @描述: 添加会员余额
     * @参数: [userId 用户id]
     */
    public static boolean addMoney(String mzUserId, int changeValue, String assetBizKey,String businessId,String changeReason) {

            TaobaoClient client = getClient();
            AlibabaDamaiMzUserAssetModifyRequest req = new AlibabaDamaiMzUserAssetModifyRequest();
            req.setMzUserId(mzUserId);
            req.setAssetType(2L);  //资产类型： 1=积分 2=余额
            req.setChangeType(1L);  //变更类型 1 增加，2 减少
            req.setChangeValue(Long.valueOf(changeValue));
            req.setBusinessId(businessId);
            req.setChangeReason(changeReason);
            req.setAssetBizKey(assetBizKey);
            String reqtime = StringUtil.nowTimeString();
        try {
            AlibabaDamaiMzUserAssetModifyResponse rsp = client.execute(req);
            String rettime = StringUtil.nowTimeString();

            log.info(rsp.getBody());
            try{
                JSONObject resObj = JSONObject.parseObject(rsp.getBody()).getJSONObject("alibaba_damai_mz_user_asset_modify_response").getJSONObject("result");
                CommonUtil.addThirdLog(0,Const.THIRDLOG_TYPE_MZ,"添加会员余额","POST","AlibabaDamaiMzUserAssetModifyRequest",
                        reqtime,req.getTextParams().toString(),rettime,rsp.getBody());
                if(resObj.get("code").equals("200")&&resObj.getBoolean("success")){
                    return true;
                }else{
                    return false;
                }
            }catch (Exception e) {
                CommonUtil.addThirdLog(1,Const.THIRDLOG_TYPE_MZ,"添加会员余额","POST","AlibabaDamaiMzUserAssetModifyRequest",
                        reqtime,req.getTextParams().toString(),rettime,rsp.getBody());
                e.printStackTrace();
                return false;
            }

        } catch (Exception e) {
            String rettime = StringUtil.nowTimeString();
            CommonUtil.addThirdLog(1,Const.THIRDLOG_TYPE_MZ,"添加会员余额","POST","AlibabaDamaiMzUserAssetModifyRequest",
                    reqtime,req.getTextParams().toString(),rettime,e.toString());
            e.printStackTrace();
            return false;
        }
    }
}
