package com.centit.pay.utils;

import com.alibaba.fastjson.JSONObject;
import com.centit.pay.biz.po.CrmAuthorization;
import com.centit.pay.common.enums.CRMConst;
import com.centit.pay.common.enums.Const;
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
     * 获取会员优惠券详情
     */
    public static JSONObject getCouponDtl(String id) {
        try {
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
                CommonUtil.addThirdLog(0,Const.THIRDLOG_TYPE_CRM,"获取会员优惠券列表","GET",CRMConst.MEMCPLIST_URL,
                        reqtime,builder.toString(),rettime,resStr);
                log.info(resStr);
                JSONObject resObj = JSONObject.parseObject(resStr);
                if (resObj.get("result").equals("ok")) {
                    return resObj.getJSONObject("data");
                }
                } catch (Exception e) {
                    String rettime = StringUtil.nowTimeString();
                    CommonUtil.addThirdLog(1,Const.THIRDLOG_TYPE_CRM,"获取会员优惠券列表","GET",CRMConst.MEMCPLIST_URL,
                            reqtime,builder.toString(),rettime,e.toString());
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
                            reqtime,reqObj.toString(),rettime,e.toString());
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

    public static String getAuthorizationFromCRM(String roleType) {
        HttpPost httpPost = new HttpPost(CRMConst.AUTHORIZATION_URL);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody("systemName", CRMConst.AUTHORIZATION_SYSTEMNAME);
        builder.addTextBody("roleType", roleType);
        HttpEntity multipart = builder.build();
        httpPost.setEntity(multipart);
        String responseJson = postSend( httpPost);
        try {
            JSONObject obj = JSONObject.parseObject(responseJson);
            if (null != obj.get("Authorization")) {
                return obj.getString("Authorization");
            }
        } catch (Exception e) {
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
        List<Integer> list = new ArrayList<>();
        for(int i=1;i<=50;i++){
            list.add(i);
        }
        System.out.println(list.subList(40,50));
    }
}
