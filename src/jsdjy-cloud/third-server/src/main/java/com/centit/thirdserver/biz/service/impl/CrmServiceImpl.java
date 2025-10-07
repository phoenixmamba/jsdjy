package com.centit.thirdserver.biz.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.core.exp.ThirdApiException;
import com.centit.core.result.Result;
import com.centit.core.result.ResultCodeEnum;
import com.centit.thirdserver.biz.dao.CrmAuthorizationDao;
import com.centit.thirdserver.biz.enums.CrmApiEnum;
import com.centit.thirdserver.biz.enums.ThirdApiLogTypeEnum;
import com.centit.thirdserver.biz.po.CrmAuthorization;
import com.centit.thirdserver.biz.service.CrmService;
import com.centit.thirdserver.biz.service.ThirdLogService;
import com.centit.thirdserver.config.CrmConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2025/1/3 17:04
 **/
@Slf4j
@Service
public class CrmServiceImpl implements CrmService {
    @Resource
    private CrmConfig crmConfig;
    @Resource
    private CrmAuthorizationDao crmAuthorizationDao;
    @Resource
    private ThirdLogService thirdLogService;

    private final static String RESPONSE_PARAM_STATUS ="result";
    private final static String RESPONSE_PARAM_STATUS_SUCCESS ="ok";
    private final static String RESPONSE_PARAM_DATA="data";
    private final static String AUTHORIZATION_URL ="/crm/getToken";
    private final static String AUTHORIZATION_PREX ="获取鉴权信息";
    private final static String REQUESTMETHOD_POST="POST";


    @Override
    public Result<JSONObject> getCouponList() {
        String authorization = getAuthorization(CrmApiEnum.APP_COUPON_LIST.getRoleType());
        Map<String,Object> params =new HashMap<>(4);
        params.put("source", crmConfig.getSystemName());
        params.put("pageNum", "1");
        params.put("pageSize", "100");
        return Result.jsonObjectResult(getCRM(CrmApiEnum.APP_COUPON_LIST,authorization,params));
    }

    @Override
    public JSONObject getCouponDtl(String id) {
        String authorization = getAuthorization(CrmApiEnum.APP_MEMBER_COUPON_INFO.getRoleType());

        Map<String,Object> params =new HashMap<>(2);
        params.put("source", crmConfig.getSystemName());
        params.put("id", id);
        log.info("获取CRM优惠券详情请求报文：{}",params);
        JSONObject resObj = getCRM(CrmApiEnum.APP_MEMBER_COUPON_INFO,authorization,params);
        log.info("获取CRM优惠券详情返回报文：{}",resObj);
        try{
            return resObj.getJSONObject(RESPONSE_PARAM_DATA);
        }catch (Exception e){
            log.error("获取CRM优惠券详情数据失败:",e);
            throw new ThirdApiException(ResultCodeEnum.CRM_REQUEST_ERROR,"获取CRM优惠券详情失败");
        }
    }

    @Override
    public Result<JSONArray> getUserCouponList(String userId, String regPhone, String flag) {
        String authorization = getAuthorization(CrmApiEnum.APP_MEMBER_COUPON_LIST.getRoleType());

        Map<String,Object> params =new HashMap<>(4);
        params.put("source", crmConfig.getSystemName());
        params.put("regPhone", regPhone);
        if(StringUtils.isBlank(flag)){
            params.put("flag", flag);
        }
        log.info("获取会员优惠券列表请求报文：{}",params);
        JSONObject resObj = getCRM(CrmApiEnum.APP_MEMBER_COUPON_LIST,authorization,params);
        log.info("获取会员优惠券列表返回报文：{}",resObj);
        try{
            if(resObj.get(RESPONSE_PARAM_DATA)!=null){
                return Result.jsonArrayResult(resObj.getJSONArray(RESPONSE_PARAM_DATA));
            }
            return null;
        }catch (Exception e){
            log.error("从CRM获取会员优惠券列表失败:",e);
            throw new ThirdApiException(ResultCodeEnum.CRM_REQUEST_ERROR,"获取会员优惠券列表失败");
        }
    }

    @Override
    public void writeoffCoupon(String cid) {
        String authorization = getAuthorization(CrmApiEnum.APP_COUPON_WRITEOFF.getRoleType());
        Map<String,Object> params =new HashMap<>(4);
        params.put("source", crmConfig.getSystemName());
        List<String> ids = new ArrayList<>();
        ids.add(cid);
        params.put("ids",ids);
        log.info("核销CRM优惠券请求报文：{}",params);
        JSONObject resObj = getCRM(CrmApiEnum.APP_COUPON_WRITEOFF,authorization,params);
        log.info("核销CRM优惠券返回报文：{}",resObj);
        try{
            if(!resObj.get(RESPONSE_PARAM_STATUS).equals(RESPONSE_PARAM_STATUS_SUCCESS)){
                throw new ThirdApiException(ResultCodeEnum.CRM_REQUEST_ERROR,"核销CRM优惠券失败");
            }
        }catch (Exception e){
            log.error("核销CRM优惠券失败：",e);
            throw new ThirdApiException(ResultCodeEnum.CRM_REQUEST_ERROR,"核销CRM优惠券失败");
        }
    }

    public JSONObject getCRM(CrmApiEnum crmApiEnum, String authorization, Map<String,Object> params){
        String rsp;
        String reqTime = DateUtil.format(DateUtil.date(), DatePattern.NORM_DATETIME_MS_PATTERN);
        log.info("CRM接口-{}请求报文：{}",crmApiEnum.getApiName(), params);
        try {
            if(crmApiEnum.getReqMethod().equals(REQUESTMETHOD_POST)){
                rsp = HttpRequest.post(crmConfig.getHost()+crmApiEnum.getApiUrl()).
                        header("Authorization",authorization).form(params).execute().body();
            }else{
                rsp = HttpRequest.get(crmConfig.getHost()+crmApiEnum.getApiUrl()).
                        header("Authorization",authorization).form(params).execute().body();
            }
        } catch (Exception e) {
            log.error("CRM接口-{}调用失败:",crmApiEnum.getApiName(),e);
            throw new ThirdApiException(ResultCodeEnum.CRM_REQUEST_ERROR,crmApiEnum.getApiName()+"异常");
        }
        log.info("CRM接口-{}返回报文：{}",crmApiEnum.getApiName(), rsp);
        checkCrmResponse(params,crmApiEnum,reqTime,rsp);
        return JSONObject.parseObject(rsp);
    }

    /**
     * 根据roleType获取鉴权信息
     * @param roleType 类型
     * @return String 鉴权信息
     */
    public String getAuthorization(String roleType) {
        String authorization;
        CrmAuthorization crmAuthorization = crmAuthorizationDao.selectByPrimaryKey(roleType);
        //之前未获取过鉴权信息或者当前时间大于失效时间
        if(crmAuthorization==null||
                DateUtil.date().getTime() >= DateUtil.parseDateTime(crmAuthorization.getInvalidTime()).getTime()){
            //重新获取authorization
            authorization = getAuthorizationFromCrm(roleType);
            if(crmAuthorization==null){
                crmAuthorization=new CrmAuthorization();
            }
            crmAuthorization.setRoleType(roleType);
            //当前时间加上60分钟作为新的authorization的失效时间，crm默认失效时间为两个小时
            crmAuthorization.setInvalidTime(DateUtil.offsetMinute(new Date(),crmConfig.getAuthExpireMinutes()).toStringDefaultTimeZone());
            crmAuthorization.setAuthorization(authorization);
            //有则更新无则新增
            crmAuthorizationDao.insertOnDuplicateKey(crmAuthorization);
        }else{
            authorization =crmAuthorization.getAuthorization();
        }
        return authorization;
    }

    /**
     * 根据roleType从CRM获取鉴权信息
     * @param roleType 外部角色
     * @return String 鉴权信息
     */
    public String getAuthorizationFromCrm(String roleType) {
        Map<String,Object> params =new HashMap<>(4);
        params.put("systemName", crmConfig.getSystemName());
        params.put("roleType", roleType);
        String rsp;
        String reqTime = DateUtil.format(DateUtil.date(), DatePattern.NORM_DATETIME_MS_PATTERN);
        boolean res = false;
        log.info("CRM接口-获取鉴权信息请求报文：{}", params);
        try {
            rsp = HttpRequest.post(crmConfig.getHost()+AUTHORIZATION_URL).form(params).execute().body();
        } catch (Exception e) {
            log.error("CRM接口-获取鉴权信息调用失败:",e);
            throw new ThirdApiException(ResultCodeEnum.CRM_REQUEST_ERROR,"CRM鉴权失败");
        }
        log.info("CRM接口-获取鉴权信息返回报文：{}", rsp);
        try {
            JSONObject obj = JSONObject.parseObject(rsp);
            res=true;
            return obj.getString("Authorization");
        } catch (Exception e) {
            log.error("CRM接口-获取鉴权信息解析报文失败:",e);
            throw new ThirdApiException(ResultCodeEnum.CRM_REQUEST_ERROR,"CRM鉴权失败");
        }finally {
            thirdLogService.addThirdLog(ThirdApiLogTypeEnum.CRM.getLogType(),AUTHORIZATION_PREX+roleType,REQUESTMETHOD_POST,AUTHORIZATION_URL,reqTime,params.toString(),rsp,res);
        }
    }

    /**
     * 校验CRM接口返回数据是否正常
     * @param params 请求参数
     * @param requestEnum 请求信息
     * @param reqTime 请求时间
     * @param rsp 返回数据
     */
    public void checkCrmResponse(Map<String,Object> params, CrmApiEnum requestEnum, String reqTime, String rsp){
        JSONObject resObj;
        boolean res = false;
        try {
            resObj = JSONObject.parseObject(rsp);
            if (RESPONSE_PARAM_STATUS_SUCCESS.equals(resObj.get(RESPONSE_PARAM_STATUS))) {
                res=true;
            }
        }catch (Exception e){
            log.info("CRM接口-{}解析报文失败：{}",requestEnum.getApiName(),rsp);
            throw new ThirdApiException(ResultCodeEnum.CRM_REQUEST_ERROR.getCode(),"CRM接口数据解析失败");
        }finally {
            thirdLogService.addThirdLog(ThirdApiLogTypeEnum.CRM.getLogType(),requestEnum.getApiName(),requestEnum.getReqMethod(),requestEnum.getApiUrl(),reqTime,params.toString(),rsp,res);
        }
        if(!res){
            log.info("CRM接口-{}获取数据失败：{}",requestEnum.getApiName(),rsp);
            throw new ThirdApiException(ResultCodeEnum.CRM_REQUEST_ERROR.getCode(),"CRM接口获取数据失败");
        }
    }
}
