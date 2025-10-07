//package com.centit.core.service.third;
//
//
//import cn.hutool.core.date.DatePattern;
//import cn.hutool.core.date.DateUtil;
//import com.alibaba.fastjson.JSONObject;
//import com.centit.core.exp.ThirdApiException;
//import com.centit.core.result.ResultCodeEnum;
//import com.centit.core.third.enums.MzApiEnum;
//import com.taobao.api.BaseTaobaoRequest;
//import com.taobao.api.DefaultTaobaoClient;
//import com.taobao.api.TaobaoResponse;
//import com.taobao.api.request.AlibabaDamaiMzUserAddressDetailRequest;
//import com.taobao.api.request.AlibabaDamaiMzUserAddressListRequest;
//import com.taobao.api.request.AlibabaDamaiMzUserAssetRuleGetRequest;
//import com.taobao.api.request.AlibabaDamaiMzUserAssetinfoGetRequest;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//
///**
// * @version : 1.0
// * @Author : cui_jian
// * @Description : 麦座接口调用服务类
// * @Date : 2024/11/25 11:18
// **/
//@Slf4j
//@Component
//public class MzService {
//    private final static String REQUEST_METHOD_POST ="POST";
//    private final static String RESPONSE_PARAM_CODE="code";
//    private final static String RESPONSE_PARAM_CODE_SUCCESS ="200";
//    private final static String RESPONSE_PARAM_STATUS ="success";
//    private final static String RESPONSE_PARAM_RESULT="result";
//    private final static String RESPONSE_PARAM_DATA="data";
//
//    @Resource
//    private ThirdLogService thirdLogService;
//    @Resource
//    private DefaultTaobaoClient defaultTaobaoClient;
//
//    /**
//     * 获取麦座限额信息
//     * @return JSONObject
//     */
//    public JSONObject getAssetRule() {
//        AlibabaDamaiMzUserAssetRuleGetRequest req = new AlibabaDamaiMzUserAssetRuleGetRequest();
//        return sendMzRequest(req,MzApiEnum.ASSET_RULE);
//    }
//
//    /**
//     * 获取会员账户资产信息
//     * @return JSONObject
//     */
//    public JSONObject getUserAccountInfo(String mzUserId) {
//        AlibabaDamaiMzUserAssetinfoGetRequest req = new AlibabaDamaiMzUserAssetinfoGetRequest();
//        req.setMzUserId(mzUserId);
//        return sendMzRequest(req,MzApiEnum.ASSET_INFO);
//    }
//
//    /**
//     * 获取会员收货地址详情
//     * @return JSONObject
//     */
//    public JSONObject getUserAddressDetail(String mzUserId,String addressId) {
//        AlibabaDamaiMzUserAddressDetailRequest req = new AlibabaDamaiMzUserAddressDetailRequest();
//        req.setMzUserId(mzUserId);
//        req.setAddressId(addressId);
//        return sendMzRequest(req,MzApiEnum.ADDRESS_DETAIL);
//    }
//
//    /**
//     * 获取会员收货地址列表
//     * @return JSONObject
//     */
//    public JSONObject getUserAddressList(String mzUserId, int pageSize, int page) {
//        AlibabaDamaiMzUserAddressListRequest req = new AlibabaDamaiMzUserAddressListRequest();
//        req.setMzUserId(mzUserId);
//        req.setPageSize((long) pageSize);
//        req.setPage((long) page);
//        return sendMzRequest(req,MzApiEnum.ADDRESS_LIST);
//    }
//
//    /**
//     * 调用卖座接口
//     * @return JSONObject
//     */
//    public <T extends BaseTaobaoRequest> JSONObject sendMzRequest(T req, MzApiEnum mzApiEnum) {
//        String apiName =mzApiEnum.getRequestInfo();
//        String reqTime = DateUtil.format(DateUtil.date(), DatePattern.NORM_DATETIME_MS_PATTERN);
//        log.info("麦座接口-{}请求报文：{}",apiName,req.getTextParams().toString());
//        TaobaoResponse rsp;
//        try {
//            rsp = defaultTaobaoClient.execute(req);
//        } catch (Exception e) {
//            log.error("麦座接口-{}调用失败:",apiName,e);
//            throw new ThirdApiException(ResultCodeEnum.MZ_REQUEST_ERROR,"请求麦座"+apiName+"接口异常");
//        }
//        log.info("麦座接口-{}返回报文：{}",apiName,rsp.getBody());
//        //校验麦座返回报文是否能正常解析
//        checkMzResponse(req, mzApiEnum,reqTime,rsp);
//        return JSONObject.parseObject(rsp.getBody()).getJSONObject(mzApiEnum.getResponseName()).getJSONObject(RESPONSE_PARAM_RESULT).getJSONObject(RESPONSE_PARAM_DATA);
//    }
//
//    /**
//     * 校验麦座接口返回数据是否正常
//     * @param req 请求
//     * @param requestEnum 请求信息
//     * @param reqTime 请求时间
//     * @param rsp 返回数据
//     */
//    public void checkMzResponse(BaseTaobaoRequest req, MzApiEnum requestEnum, String reqTime, TaobaoResponse rsp){
//        JSONObject resObj;
//        boolean res = false;
//        try{
//            resObj = JSONObject.parseObject(rsp.getBody()).getJSONObject(requestEnum.getResponseName()).getJSONObject(RESPONSE_PARAM_RESULT);
//            if (RESPONSE_PARAM_CODE_SUCCESS.equals(resObj.get(RESPONSE_PARAM_CODE)) && resObj.getBoolean(RESPONSE_PARAM_STATUS)&&resObj.getJSONObject(RESPONSE_PARAM_DATA)!=null) {
//                res=true;
//            }
//        }catch (Exception e){
//            log.error("麦座接口-{}解析报文失败：{}",requestEnum.getRequestInfo(),rsp.getBody());
//            throw new ThirdApiException(ResultCodeEnum.MZ_REQUEST_ERROR,"麦座"+requestEnum.getRequestInfo()+"接口数据解析失败");
//        }finally {
//            thirdLogService.addThirdLog(requestEnum.getRequestInfo(), REQUEST_METHOD_POST, requestEnum.getRequestName(),reqTime,req.getTextParams().toString(),rsp==null?null:rsp.getBody(),res);
//        }
//        if(!res){
//            log.error("麦座接口-{}解析报文失败：{}",requestEnum.getRequestInfo(),rsp.getBody());
//        }
//    }
//
//}
