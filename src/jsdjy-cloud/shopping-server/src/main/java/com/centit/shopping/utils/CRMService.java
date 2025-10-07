package com.centit.shopping.utils;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.common.enums.CRMConst;
import com.centit.shopping.common.enums.Const;
import com.centit.shopping.po.*;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class CRMService {
    private static final Logger log = LoggerFactory.getLogger(CRMService.class);


    /**
     * 获取鉴权信息
     */
    public static String getAuthorization(String roleType) {
        CrmAuthorization crmAuthorization = new CrmAuthorization();
        crmAuthorization.setRoleType(roleType);
        crmAuthorization = CommonInit.staticCrmAuthorizationDao.queryDetail(crmAuthorization);
        if (crmAuthorization != null) {
            String invalidTime = crmAuthorization.getInvalidTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date dateInvalid = sdf.parse(invalidTime);
                Date dateNow = new Date();
                if (dateNow.getTime() >= dateInvalid.getTime()) {  //当前时间大于失效时间
                    //重新获取authorization
                    String authorization = getAuthorizationFromCRM(roleType);
                    //当前时间加上60分钟作为新的authorization的失效时间，crm默认失效时间为两个小时
                    crmAuthorization.setInvalidTime(StringUtil.nowTimePlusMinutes(60));
                    crmAuthorization.setAuthorization(authorization);
                    CommonInit.staticCrmAuthorizationDao.update(crmAuthorization);
                    return authorization;
                } else {
                    return crmAuthorization.getAuthorization();
                }
            } catch (Exception e) {
                return null;
            }
        } else {
            String authorization = getAuthorizationFromCRM(roleType);
            if (null != authorization) {
                crmAuthorization = new CrmAuthorization();
                crmAuthorization.setRoleType(roleType);
                crmAuthorization.setAuthorization(authorization);
                //当前时间加上60分钟作为新的authorization的失效时间，crm默认失效时间为两个小时
                crmAuthorization.setInvalidTime(StringUtil.nowTimePlusMinutes(60));
                CommonInit.staticCrmAuthorizationDao.insert(crmAuthorization);
            }
            return authorization;
        }
    }

    /**
     * 创建优惠券
     */
    public static JSONObject createCoupon(JSONObject reqObj){
        try {
            String authorization = getAuthorization(CRMConst.CREATECOUPON_ROLETYPE);
            if (null != authorization) {
                HttpPost post = new HttpPost(CRMConst.CREATECOUPON_URL);
                post.setHeader("Content-Type","application/json;charset=utf-8");
                post.addHeader("Authorization", authorization);
                reqObj.put("source",CRMConst.AUTHORIZATION_SYSTEMNAME);
                StringEntity postingString = new StringEntity(reqObj.toString(),"utf-8");
                post.setEntity(postingString);
                String reqtime = StringUtil.nowTimeString();
                try {
                String resStr =postSend(post);
                String rettime = StringUtil.nowTimeString();
                CommonUtil.addThirdLog(0,Const.THIRDLOG_TYPE_CRM,"创建优惠券","POST",CRMConst.CREATECOUPON_URL,
                        reqtime,reqObj.toString(),rettime,resStr);
                log.info(resStr);
                JSONObject resObj = JSONObject.parseObject(resStr);
                return resObj;
                } catch (Exception e) {
                    String rettime = StringUtil.nowTimeString();
                    CommonUtil.addThirdLog(1,Const.THIRDLOG_TYPE_CRM,"创建优惠券","POST",CRMConst.CREATECOUPON_URL,
                            reqtime,reqObj.toString(),rettime,e.getMessage());
                    e.printStackTrace();
                    return null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * 获取优惠券列表
     */
    public static JSONObject getCouponList() {
        try {
            String authorization = getAuthorization(CRMConst.COUPONLIST_ROLETYPE);
            if (null != authorization) {
                HttpGetWithEntity httpGetWithEntity = new HttpGetWithEntity(CRMConst.COUPONLIST_URL);
                httpGetWithEntity.setHeader("Authorization", authorization);
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.addTextBody("source", CRMConst.AUTHORIZATION_SYSTEMNAME);
                builder.addTextBody("pageNum", "1");
                builder.addTextBody("pageSize", "1000");
                HttpEntity multipart = builder.build();
                httpGetWithEntity.setEntity(multipart);
                String reqtime = StringUtil.nowTimeString();
                try {
                String resStr = sendGet(httpGetWithEntity, "utf-8");
                String rettime = StringUtil.nowTimeString();
                CommonUtil.addThirdLog(0,Const.THIRDLOG_TYPE_CRM,"获取优惠券列表","GET",CRMConst.COUPONLIST_URL,
                        reqtime,builder.toString(),rettime,resStr);
                log.info(resStr);
                JSONObject resObj = JSONObject.parseObject(resStr);
                if (resObj.get("result").equals("ok")) {
                    return resObj.getJSONObject("data");
                }
                } catch (Exception e) {
                    String rettime = StringUtil.nowTimeString();
                    CommonUtil.addThirdLog(1,Const.THIRDLOG_TYPE_CRM,"获取优惠券列表","GET",CRMConst.COUPONLIST_URL,
                            reqtime,builder.toString(),rettime,e.getMessage());
                    e.printStackTrace();
                    return null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * 删除优惠券
     */
    public static JSONObject delCoupon(String right_No) {
        String authorization = getAuthorization(CRMConst.DELETECOUPON_ROLETYPE);
        if(null !=authorization){
            HttpPost httpPost = new HttpPost(CRMConst.DELETECOUPON_URL);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addTextBody("source", CRMConst.AUTHORIZATION_SYSTEMNAME);
            builder.addTextBody("right_No", right_No);
            HttpEntity multipart = builder.build();
            httpPost.setEntity(multipart);
            httpPost.addHeader("Authorization", authorization);
            String reqtime = StringUtil.nowTimeString();
            try {

                String resStr = postSend( httpPost);
                String rettime = StringUtil.nowTimeString();
                CommonUtil.addThirdLog(0,Const.THIRDLOG_TYPE_CRM,"删除优惠券","POST",CRMConst.DELETECOUPON_URL,
                        reqtime,builder.toString(),rettime,resStr);
                log.info(resStr);
                JSONObject resObj = JSONObject.parseObject(resStr);
                return resObj;
            } catch (Exception e) {
                String rettime = StringUtil.nowTimeString();
                CommonUtil.addThirdLog(1,Const.THIRDLOG_TYPE_CRM,"删除优惠券","POST",CRMConst.DELETECOUPON_URL,
                        reqtime,builder.toString(),rettime,e.getMessage());
                return null;
            }
        }
        return null;
    }

    /**
     * 获取会员优惠券列表
     */
    public static JSONArray getUserCouponList(String userId,String regPhone,String flag) {
        try {
            String authorization = getAuthorization(CRMConst.MEMCPLIST_ROLETYPE);
            if (null != authorization) {
                HttpGetWithEntity httpGetWithEntity = new HttpGetWithEntity(CRMConst.MEMCPLIST_URL);
                httpGetWithEntity.setHeader("Authorization", authorization);
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.addTextBody("source", CRMConst.AUTHORIZATION_SYSTEMNAME);
                builder.addTextBody("regPhone", regPhone);
                if(null !=flag){
                    builder.addTextBody("flag", flag);
                }
                HttpEntity multipart = builder.build();
                httpGetWithEntity.setEntity(multipart);
                String reqtime = StringUtil.nowTimeString();
                try {
                String resStr = sendGet(httpGetWithEntity, "utf-8");
                String rettime = StringUtil.nowTimeString();
                CommonUtil.addThirdLog(0,Const.THIRDLOG_TYPE_CRM,"获取会员优惠券列表","GET",CRMConst.MEMCPLIST_URL,
                        reqtime,builder.toString(),rettime,resStr);
                log.info(resStr);
                JSONObject resObj = JSONObject.parseObject(resStr);
                List<String> lockCouponIds = CommonUtil.getUserLockCoupon(userId);
                if (resObj.get("result").equals("ok")) {
                    JSONArray dataArray = resObj.getJSONArray("data");
                    JSONArray resArray = new JSONArray();
                    for(int i=0;i<dataArray.size();i++){
                        JSONObject dataObj =dataArray.getJSONObject(i);
                        if(!lockCouponIds.contains(dataObj.get("id"))&&!dataObj.get("right_No").equals("RI201903000047")){
                            resArray.add(dataObj);
                        }
                    }
                    return resArray;
                }
                } catch (Exception e) {
                    String rettime = StringUtil.nowTimeString();
                    CommonUtil.addThirdLog(1,Const.THIRDLOG_TYPE_CRM,"获取会员优惠券列表","GET",CRMConst.MEMCPLIST_URL,
                            reqtime,builder.toString(),rettime,e.getMessage());
                    e.printStackTrace();
                    return null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * 获取用户优惠券详情
     */
    public static JSONObject getUserCouponDtl(String id) {
        try {
            if(!StringUtil.isNotNull(id)){
                return null;
            }
            String authorization = getAuthorization(CRMConst.COUPONDTL_ROLETYPE);
            if (null != authorization) {
                HttpGetWithEntity httpGetWithEntity = new HttpGetWithEntity(CRMConst.COUPONDTL_URL);
                httpGetWithEntity.setHeader("Authorization", authorization);
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.addTextBody("source", CRMConst.AUTHORIZATION_SYSTEMNAME);
                builder.addTextBody("id", id);

                HttpEntity multipart = builder.build();
                httpGetWithEntity.setEntity(multipart);
                String reqtime = StringUtil.nowTimeString();
                try {
                String resStr = sendGet(httpGetWithEntity, "utf-8");
                String rettime = StringUtil.nowTimeString();
                CommonUtil.addThirdLog(0,Const.THIRDLOG_TYPE_CRM,"获取用户优惠券详情","GET",CRMConst.COUPONDTL_URL,
                        reqtime,builder.toString(),rettime,resStr);
                log.info(resStr);
                JSONObject resObj = JSONObject.parseObject(resStr);
                if (resObj.get("result").equals("ok")) {

                    return resObj.getJSONObject("data");
                }
                } catch (Exception e) {
                    String rettime = StringUtil.nowTimeString();
                    CommonUtil.addThirdLog(1,Const.THIRDLOG_TYPE_CRM,"获取用户优惠券详情","GET",CRMConst.COUPONDTL_URL,
                            reqtime,builder.toString(),rettime,e.getMessage());
                    e.printStackTrace();
                    return null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * 发放优惠券
     */
    public static JSONObject grantCoupon(String regPhone,String right_No) {
        ShoppingCoupon shoppingCoupon = new ShoppingCoupon();
        shoppingCoupon.setRight_No(right_No);
        shoppingCoupon = CommonInit.staticShoppingCouponDao.queryDetail(shoppingCoupon);
        if(shoppingCoupon.getOffline()==1){   //优惠券仅支持线下手动发放
            return null;
        }

        String authorization = getAuthorization(CRMConst.GRANTCOUPON_ROLETYPE);
        if(null !=authorization){
            HttpPost httpPost = new HttpPost(CRMConst.GRANTCOUPON_URL);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addTextBody("source", CRMConst.AUTHORIZATION_SYSTEMNAME);
            builder.addTextBody("right_No", right_No);
            builder.addTextBody("regPhone", regPhone);
            HttpEntity multipart = builder.build();
            httpPost.setEntity(multipart);
            httpPost.addHeader("Authorization", authorization);
            String reqtime = StringUtil.nowTimeString();
            try {

                String resStr = postSend( httpPost);
                String rettime = StringUtil.nowTimeString();
                CommonUtil.addThirdLog(0,Const.THIRDLOG_TYPE_CRM,"发放优惠券","POST",CRMConst.GRANTCOUPON_URL,
                        reqtime,builder.toString(),rettime,resStr);
                log.info(resStr);
                JSONObject resObj = JSONObject.parseObject(resStr);
                if (resObj.get("result").equals("ok")) {
                    ShoppingCouponGrant shoppingCouponGrant = new ShoppingCouponGrant();
                    shoppingCouponGrant.setRegPhone(regPhone);
                    shoppingCouponGrant.setRightNo(right_No);
                    CommonInit.staticShoppingCouponGrantDao.insert(shoppingCouponGrant);

                    //是否需要生成核销二维码
                    if(StringUtil.isNotNull(shoppingCoupon.getWriteOffCount())){
                        ShoppingUser user = CommonUtil.getShoppingUserByMobile(regPhone);
                        //获取该该用户针对该优惠券已经生成的核销记录
                        HashMap<String, Object> reqMap = new HashMap<>();
                        reqMap.put("rightNo",right_No);
                        reqMap.put("userId",user.getId());
                        List<ShoppingWriteoffCoupon> couponList = CommonInit.staticShoppingWriteoffCouponDao.queryList(reqMap);
                        List<String> cIds = new ArrayList<>();
                        for(ShoppingWriteoffCoupon shoppingWriteoffCoupon:couponList){
                            cIds.add(shoppingWriteoffCoupon.getRightId());
                        }
                        //从CRM获取会员优惠券
                        JSONArray couponArray = CRMService.getUserCouponList(user.getId(),regPhone,"0");
                        if(null !=couponArray){
                            for (int j = 0; j < couponArray.size(); j++) {
                                JSONObject obj = (JSONObject) couponArray.get(j);
                                String c_right_No =  obj.get("right_No").toString();
                                if(c_right_No.equals(right_No)&&!cIds.contains(obj.get("id").toString())){
                                    String rightId = obj.get("id").toString();
                                    ShoppingWriteoffCoupon shoppingWriteoffCoupon = new ShoppingWriteoffCoupon();
                                    shoppingWriteoffCoupon.setRightNo(right_No);
                                    shoppingWriteoffCoupon.setRightId(rightId);
                                    shoppingWriteoffCoupon.setUserId(user.getId());
                                    shoppingWriteoffCoupon.setOffCode("CP_"+StringUtil.randomOffCode(6));
                                    shoppingWriteoffCoupon.setOffCount(1);
                                    shoppingWriteoffCoupon.setOffAccount(shoppingCoupon.getWriteOffCount());
                                    CommonInit.staticShoppingWriteoffCouponDao.insert(shoppingWriteoffCoupon);
                                }
                            }
                        }
                    }
                }
                return resObj;
            } catch (Exception e) {
                String rettime = StringUtil.nowTimeString();
                CommonUtil.addThirdLog(1,Const.THIRDLOG_TYPE_CRM,"发放优惠券","POST",CRMConst.GRANTCOUPON_URL,
                        reqtime,builder.toString(),rettime,e.getMessage());
                return null;
            }
        }
        return null;
    }

    /**
     * 手动发放优惠券
     */
    public static JSONObject directGrantCoupon(String regPhone,String right_No) {
        String authorization = getAuthorization(CRMConst.GRANTCOUPON_ROLETYPE);
        if(null !=authorization){
            HttpPost httpPost = new HttpPost(CRMConst.GRANTCOUPON_URL);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addTextBody("source", CRMConst.AUTHORIZATION_SYSTEMNAME);
            builder.addTextBody("right_No", right_No);
            builder.addTextBody("regPhone", regPhone);
            HttpEntity multipart = builder.build();
            httpPost.setEntity(multipart);
            httpPost.addHeader("Authorization", authorization);
            String reqtime = StringUtil.nowTimeString();
            try {

                String resStr = postSend( httpPost);
                String rettime = StringUtil.nowTimeString();
                CommonUtil.addThirdLog(0,Const.THIRDLOG_TYPE_CRM,"发放优惠券","POST",CRMConst.GRANTCOUPON_URL,
                        reqtime,builder.toString(),rettime,resStr);
                log.info(resStr);
                JSONObject resObj = JSONObject.parseObject(resStr);
                if (resObj.get("result").equals("ok")) {
                    ShoppingCouponGrant shoppingCouponGrant = new ShoppingCouponGrant();
                    shoppingCouponGrant.setRegPhone(regPhone);
                    shoppingCouponGrant.setRightNo(right_No);
                    CommonInit.staticShoppingCouponGrantDao.insert(shoppingCouponGrant);

                }
                return resObj;
            } catch (Exception e) {
                String rettime = StringUtil.nowTimeString();
                CommonUtil.addThirdLog(1,Const.THIRDLOG_TYPE_CRM,"发放优惠券","POST",CRMConst.GRANTCOUPON_URL,
                        reqtime,builder.toString(),rettime,e.getMessage());
                return null;
            }
        }
        return null;
    }


    public static String getAuthorizationFromCRM(String roleType) {
        HttpPost httpPost = new HttpPost(CRMConst.AUTHORIZATION_URL);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody("systemName", CRMConst.AUTHORIZATION_SYSTEMNAME);
        builder.addTextBody("roleType", roleType);
        HttpEntity multipart = builder.build();
        httpPost.setEntity(multipart);
        String reqtime = StringUtil.nowTimeString();
        try {
        String responseJson = postSend( httpPost);
        String rettime = StringUtil.nowTimeString();
        CommonUtil.addThirdLog(0,Const.THIRDLOG_TYPE_CRM,"获取鉴权信息:"+roleType,"POST",CRMConst.AUTHORIZATION_URL,
                reqtime,builder.toString(),rettime,responseJson);

            JSONObject obj = JSONObject.parseObject(responseJson);
            if (null != obj.get("Authorization")) {
                return obj.getString("Authorization");
            }
        } catch (Exception e) {
            String rettime = StringUtil.nowTimeString();
            CommonUtil.addThirdLog(1,Const.THIRDLOG_TYPE_CRM,"获取鉴权信息:"+roleType,"POST",CRMConst.AUTHORIZATION_URL,
                    reqtime,builder.toString(),rettime,e.getMessage());
            return null;
        }
        return null;
    }


    /***
     *
     * @Description post请求
     * @Date 14:35 2020/9/25
     * @Param [stringBuilder, params]
     * @return java.lang.String
     **/
    public static String postSend( HttpPost httpPost) {
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            CloseableHttpResponse response = client.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String result = EntityUtils.toString(entity, "UTF-8");
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public static String sendGet(HttpGetWithEntity httpGetWithEntity, String encoding) {

        try {
            //创建httpclient对象
            CloseableHttpClient client = HttpClients.createDefault();
            //执行请求操作，并拿到结果（同步阻塞）
            CloseableHttpResponse response = client.execute(httpGetWithEntity);
            //获取结果实体
            String body = "";
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                //按指定编码转换结果实体为String类型
                body = EntityUtils.toString(entity, encoding);
            }
            //释放链接
            response.close();
            return body;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 核销优惠券
     */
    public static JSONObject writeoffCoupon(String id){
        try {
            String authorization = getAuthorization(CRMConst.WRITEOFF_ROLETYPE);
            if (null != authorization) {
                HttpPost post = new HttpPost(CRMConst.WRITEOFF_URL);
                post.setHeader("Content-Type","application/json;charset=utf-8");
                post.addHeader("Authorization", authorization);
                JSONObject reqObj = new JSONObject();
                reqObj.put("source",CRMConst.AUTHORIZATION_SYSTEMNAME);
                List<String> ids = new ArrayList<>();
                ids.add(id);
                reqObj.put("ids",ids);
                StringEntity postingString = new StringEntity(reqObj.toString(),"utf-8");
                post.setEntity(postingString);
                String reqtime = StringUtil.nowTimeString();
                try {
                    String resStr =postSend(post);
                    String rettime = StringUtil.nowTimeString();
                    CommonUtil.addThirdLog(0, Const.THIRDLOG_TYPE_CRM,"核销优惠券","POST",CRMConst.WRITEOFF_URL,
                            reqtime,reqObj.toString(),rettime,resStr);
                    log.info(resStr);
                    JSONObject resObj = JSONObject.parseObject(resStr);
                    return resObj;
                } catch (Exception e) {
                    String rettime = StringUtil.nowTimeString();
                    CommonUtil.addThirdLog(1,Const.THIRDLOG_TYPE_CRM,"核销优惠券","POST",CRMConst.WRITEOFF_URL,
                            reqtime,reqObj.toString(),rettime,e.getMessage());
                    e.printStackTrace();
                    return null;
                }

            }
        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }
        return null;
    }

    public static void getGoodsCouppon(String goodsId,int goodsType,String userId){
        if(goodsType ==1||goodsType ==2){   //文创或积分商品

        }
    }

//    public static void main(String[] args) throws UnsupportedEncodingException, AlipayApiException, ApiException {
//
//        HttpPost httpPost = new HttpPost("http://crmtest.jsartcentre.org:81/crm/getToken");
//        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//        builder.addTextBody("systemName", "JSART_APP_PROGRAM");
//        builder.addTextBody("roleType", "ROLE_APP_COUPON_CREATE");
//        HttpEntity multipart = builder.build();
//        String responseJson = postFormData(multipart, httpPost);
//        System.out.println(responseJson);
//    }

    public static void main(String[] args) throws Exception {
        HttpPost httpPost = new HttpPost("http://crmtest.jsartcentre.org:81/crm/getToken");
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody("systemName", "JSART_APP_PROGRAM");
        builder.addTextBody("roleType", "ROLE_APP_COUPON_LIST");
        HttpEntity multipart = builder.build();
        httpPost.setEntity(multipart);
        String reqtime = StringUtil.nowTimeString();
        try {
            String responseJson = postSend( httpPost);
            String rettime = StringUtil.nowTimeString();

            JSONObject obj = JSONObject.parseObject(responseJson);
            System.out.println(obj);
        } catch (Exception e) {
            e.printStackTrace();

        }

    }
}
