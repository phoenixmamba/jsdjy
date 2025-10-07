package com.centit.thirdserver.biz.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.centit.core.exp.ThirdApiException;
import com.centit.core.model.OrderPayInfo;
import com.centit.core.result.Result;
import com.centit.core.result.ResultCodeEnum;
import com.centit.thirdserver.biz.enums.MzApiEnum;
import com.centit.thirdserver.biz.enums.ThirdApiLogTypeEnum;
import com.centit.thirdserver.biz.service.MzService;
import com.centit.thirdserver.biz.service.ThirdLogService;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoResponse;
import com.taobao.api.request.*;
import com.taobao.api.response.AlibabaDamaiMzOrderConfirmResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2025/1/3 16:43
 **/
@Slf4j
@Service
public class MzServiceImpl implements MzService {
    private final static String REQUEST_METHOD_POST ="POST";
    private final static String RESPONSE_PARAM_CODE="code";
    private final static String RESPONSE_PARAM_CODE_SUCCESS ="200";
    private final static String RESPONSE_PARAM_STATUS ="success";
    private final static String RESPONSE_PARAM_RESULT="result";
    private final static String RESPONSE_PARAM_DATA="data";
    private final static String RESPONSE_ERROR="error_response";

    @Resource
    private ThirdLogService thirdLogService;
    @Resource
    private DefaultTaobaoClient defaultTaobaoClient;
    @Override
    public Result<JSONObject> getAssetRule() {
        AlibabaDamaiMzUserAssetRuleGetRequest req = new AlibabaDamaiMzUserAssetRuleGetRequest();
        JSONObject obj = sendMzRequestForJson(req,MzApiEnum.ASSET_RULE);
        return Result.jsonObjectResult(obj);
    }

    @Override
    public Result<JSONObject> getUserAccountInfo(String mzUserId) {
        AlibabaDamaiMzUserAssetinfoGetRequest req = new AlibabaDamaiMzUserAssetinfoGetRequest();
        req.setMzUserId(mzUserId);
        return Result.jsonObjectResult(sendMzRequestForJson(req,MzApiEnum.ASSET_INFO));
    }

    @Override
    public Result<JSONObject> getUserAddressDetail(String mzUserId, String addressId) {
        AlibabaDamaiMzUserAddressDetailRequest req = new AlibabaDamaiMzUserAddressDetailRequest();
        req.setMzUserId(mzUserId);
        req.setAddressId(addressId);
        return Result.jsonObjectResult(sendMzRequestForJson(req,MzApiEnum.ADDRESS_DETAIL));
    }

    @Override
    public Result<JSONObject> getUserAddressList(String mzUserId, int pageSize, int page) {
        AlibabaDamaiMzUserAddressListRequest req = new AlibabaDamaiMzUserAddressListRequest();
        req.setMzUserId(mzUserId);
        req.setPageSize((long) pageSize);
        req.setPage((long) page);
        return Result.jsonObjectResult(sendMzRequestForJson(req,MzApiEnum.ADDRESS_LIST));
    }

    @Override
    public Result<String> confirmOrder(OrderPayInfo orderPayInfo) {
        try{
            JSONObject mzObj = confirmOrderToMZ(orderPayInfo);
//            if(mzObj.get("code").equals("200")&&mzObj.getBoolean("success")){
//                return Result.jsonObjectResult(mzObj);
//            }else if(mzObj.get("code").equals("15040003")&&mzObj.get("msg").equals("支付金额不匹配")){
//                orderPayInfo.setPayPrice(orderPayInfo.getOrderTolPrice());
//                mzObj = confirmOrderToMZ(orderPayInfo);
//                if(mzObj.get("code").equals("200")&&mzObj.getBoolean("success")){
//                    return Result.jsonObjectResult(mzObj);
//                }
//            }
            return Result.defaultSuccess(mzObj.getString("mz_order_id"));
        }catch (Exception e){
            return Result.error(ResultCodeEnum.MZ_REQUEST_ERROR,"向麦座确认订单失败");
        }
    }

    @Override
    public Result cutMoney(OrderPayInfo orderPayInfo) {
        AlibabaDamaiMzUserAssetModifyRequest req = new AlibabaDamaiMzUserAssetModifyRequest();
        req.setMzUserId(orderPayInfo.getMzUserId());
        req.setAssetType(2L);  //资产类型： 1=积分 2=余额
        req.setChangeType(2L);  //变更类型 1 增加，2 减少
        //将余额抵扣值转换为分
        int deductionBalanceValue = orderPayInfo.getDeductionBalancePrice().multiply(new BigDecimal(100)).intValue();
        req.setChangeValue((long) deductionBalanceValue);
        req.setBusinessId("MONEY_"+orderPayInfo.getOrderId());
        req.setChangeReason("江苏大剧院APP消费抵扣，订单号"+orderPayInfo.getOrderId());
        if(StringUtils.isNotBlank(orderPayInfo.getAssetBizKey())){
            req.setAssetBizKey(orderPayInfo.getAssetBizKey());
        }
        sendMzRequestForSuccess(req,MzApiEnum.CUT_BALANCE);
        return Result.defaultSuccess();
    }

    @Override
    public Result<JSONObject> cutPoint(OrderPayInfo orderPayInfo) {
        AlibabaDamaiMzUserAssetModifyRequest req = new AlibabaDamaiMzUserAssetModifyRequest();
        req.setMzUserId(orderPayInfo.getMzUserId());
        req.setAssetType(1L);  //资产类型： 1=积分 2=余额
        req.setChangeType(2L);  //变更类型 1 增加，2 减少
        req.setChangeValue(Long.valueOf(orderPayInfo.getDeductionIntegral()));
        req.setBusinessId("POINT_"+orderPayInfo.getOrderId());
        req.setChangeReason("江苏大剧院APP消费抵扣，订单号"+orderPayInfo.getOrderId());
        if(StringUtils.isNotBlank(orderPayInfo.getAssetBizKey())){
            req.setAssetBizKey(orderPayInfo.getAssetBizKey());
        }
        sendMzRequestForSuccess(req,MzApiEnum.CUT_INTEGRAL);
        return Result.defaultSuccess();
    }

    @Override
    public Result<JSONObject> checkVerifyCode(String mzUserId, String verifyCode) {
        AlibabaDamaiMzAssetVerifycodeCheckRequest req = new AlibabaDamaiMzAssetVerifycodeCheckRequest();
        req.setMzUserId(mzUserId);
        req.setVerifyCode(verifyCode);
        sendMzRequestForSuccess(req,MzApiEnum.ASSET_VERIFY_KEY);
        return Result.jsonObjectResult(sendMzRequestForJson(req,MzApiEnum.ASSET_VERIFY_KEY));
    }

    public JSONObject confirmOrderToMZ(OrderPayInfo orderPayInfo) {
        int maxRetries = 2;
        int retryCount = 0;

        while (retryCount <= maxRetries) {
            try {
                AlibabaDamaiMzOrderConfirmRequest req = new AlibabaDamaiMzOrderConfirmRequest();
                //计算麦座订单应收金额，单位：分
                //4.25更新，orderReceiveMoneyFen改为传实际的现金支付的金额
                BigDecimal payPrice = orderPayInfo.getPayPrice();
                long orderReceiveMoneyFen = payPrice.multiply(new BigDecimal(100)).longValue();

                req.setOrderReceiveMoneyFen(orderReceiveMoneyFen);
                req.setThirdPayTypeId(orderPayInfo.getMzPaymentId());
                req.setMzUserId(orderPayInfo.getMzUserId());
                req.setMzOrderId(orderPayInfo.getOutTradeNo());
                //第三方支付流水号
                req.setThirdPayNo(orderPayInfo.getOutTradeNo());
                req.setSendMsgMark(true);
                AlibabaDamaiMzOrderConfirmResponse rsp= defaultTaobaoClient.execute(req);
                JSONObject result =JSONObject.parseObject(rsp.getBody()).getJSONObject(MzApiEnum.CONFIRM_ORDER.getResponseName()).getJSONObject(RESPONSE_PARAM_RESULT);

                // 检查是否需要重试
                if (shouldRetry(result) && retryCount < maxRetries) {
                    retryCount++;
                    log.warn("麦座确认订单接口返回需要重试的结果，第{}次重试", retryCount);
                    Thread.sleep(500L * retryCount);
                    continue;
                }
                return result;
            } catch (Exception e) {
                if (retryCount < maxRetries) {
                    retryCount++;
                    log.warn("麦座确认订单接口调用异常，第{}次重试", retryCount, e);
                    try {
                        Thread.sleep(500L * retryCount);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new ThirdApiException(ResultCodeEnum.MZ_REQUEST_ERROR, "请求麦座确认订单接口被中断");
                    }
                } else {
                    throw new ThirdApiException(ResultCodeEnum.MZ_REQUEST_ERROR, "请求麦座确认订单接口达到最大重试次数");
                }
            }
        }
        throw new ThirdApiException(ResultCodeEnum.MZ_REQUEST_ERROR, "请求麦座确认订单接口达到最大重试次数");
    }

    /**
     * 判断是否需要重试
     * @param result 接口返回结果
     * @return 是否需要重试
     */
    private boolean shouldRetry(JSONObject result) {
        if (result == null) {
            return true;
        }
        try {
            // 根据返回码判断是否需要重试
            Object codeObj = result.get("code");
            if (codeObj != null) {
                String code = codeObj.toString();
                if ("15010020".equals(code)) {
                    return true;
                }
            }
            // 根据消息内容判断是否需要重试
            Object msgObj = result.get("msg");
            if (msgObj != null) {
                String msg = msgObj.toString();
                if (msg.contains("超时") || msg.contains("timeout") || msg.contains("网络") || msg.contains("network")) {
                    return true;
                }
            }
        } catch (Exception e) {
            log.warn("检查重试条件时发生异常，默认需要重试", e);
            return true;
        }

        return false;
    }

    /**
     * 调用卖座接口
     * @return JSONObject
     */
    public <T extends BaseTaobaoRequest> JSONObject sendMzRequestForJson(T req, MzApiEnum mzApiEnum) {
        String apiName =mzApiEnum.getRequestInfo();
        String reqTime = DateUtil.format(DateUtil.date(), DatePattern.NORM_DATETIME_MS_PATTERN);
        log.info("麦座接口-{}请求报文：{}",apiName,req.getTextParams().toString());
        TaobaoResponse rsp;
        try {
            rsp = defaultTaobaoClient.execute(req);
        } catch (Exception e) {
            log.error("麦座接口-{}调用失败:",apiName,e);
            throw new ThirdApiException(ResultCodeEnum.MZ_REQUEST_ERROR,"请求麦座"+apiName+"接口异常");
        }
        log.info("麦座接口-{}返回报文：{}",apiName,rsp.getBody());
        //校验麦座返回报文是否能正常解析
        checkMzResponse(req, mzApiEnum,reqTime,rsp);
        return JSONObject.parseObject(rsp.getBody()).getJSONObject(mzApiEnum.getResponseName()).getJSONObject(RESPONSE_PARAM_RESULT).getJSONObject(RESPONSE_PARAM_DATA);
    }

    /**
     * 调用卖座接口
     * @return JSONObject
     */
    public <T extends BaseTaobaoRequest> void sendMzRequestForSuccess(T req, MzApiEnum mzApiEnum) {
        String apiName =mzApiEnum.getRequestInfo();
        String reqTime = DateUtil.format(DateUtil.date(), DatePattern.NORM_DATETIME_MS_PATTERN);
        log.info("麦座接口-{}请求报文：{}",apiName,req.getTextParams().toString());
        TaobaoResponse rsp;
        try {
            rsp = defaultTaobaoClient.execute(req);
        } catch (Exception e) {
            log.error("麦座接口-{}调用失败:",apiName,e);
            throw new ThirdApiException(ResultCodeEnum.MZ_REQUEST_ERROR,"请求麦座"+apiName+"接口异常");
        }
        log.info("麦座接口-{}返回报文：{}",apiName,rsp.getBody());
        //校验麦座返回报文是否能正常解析
        checkMzResponse(req, mzApiEnum,reqTime,rsp);
//        return JSONObject.parseObject(rsp.getBody()).getJSONObject(mzApiEnum.getResponseName()).getJSONObject(RESPONSE_PARAM_RESULT).get(RESPONSE_PARAM_STATUS);
    }

    /**
     * 校验麦座接口返回数据是否正常
     * @param req 请求
     * @param requestEnum 请求信息
     * @param reqTime 请求时间
     * @param rsp 返回数据
     */
    public void checkMzResponse(BaseTaobaoRequest req, MzApiEnum requestEnum, String reqTime, TaobaoResponse rsp){
        JSONObject resObj;
        boolean res = false;
        try{
            if(JSONObject.parseObject(rsp.getBody()).get(RESPONSE_ERROR)!=null){
                throw new ThirdApiException(ResultCodeEnum.MZ_REQUEST_ERROR,"麦座接口"+requestEnum.getRequestInfo()+"请求失败");
            }
            resObj = JSONObject.parseObject(rsp.getBody()).getJSONObject(requestEnum.getResponseName()).getJSONObject(RESPONSE_PARAM_RESULT);
            if (RESPONSE_PARAM_CODE_SUCCESS.equals(resObj.get(RESPONSE_PARAM_CODE)) && resObj.getBoolean(RESPONSE_PARAM_STATUS)) {
                res=true;
            }
        }catch (Exception e){
            log.error("麦座接口-{}解析报文失败：{}",requestEnum.getRequestInfo(),rsp.getBody());
            throw new ThirdApiException(ResultCodeEnum.MZ_REQUEST_ERROR,"麦座"+requestEnum.getRequestInfo()+"接口数据解析失败");
        }finally {
            thirdLogService.addThirdLog(ThirdApiLogTypeEnum.MZ.getLogType(),requestEnum.getRequestInfo(), REQUEST_METHOD_POST, requestEnum.getRequestName(),reqTime,req.getTextParams().toString(),rsp==null?null:rsp.getBody(),res);
        }
        if(!res){
            log.error("麦座接口-{}解析报文失败：{}",requestEnum.getRequestInfo(),rsp.getBody());
        }
    }
}
