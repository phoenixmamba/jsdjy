package com.centit.shopping.utils;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.common.enums.Const;
import com.centit.shopping.po.TInvoiceToken;
import com.google.gson.Gson;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.*;
import com.taobao.api.response.*;
import nuonuo.open.sdk.NNOpenSDK;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;


public class NNService {
    private static final Logger log = LoggerFactory.getLogger(NNService.class);

    /**
     * @描述: 获取诺诺平台accessToken
     *
     */
    public static String getAccessToken() {
        NNOpenSDK sdk = NNOpenSDK.getIntance();

        String reqtime = StringUtil.nowTimeMilesString();
        try {
            String reStr =sdk.getMerchantToken(Const.INVOICE_APPKEY,Const.INVOICE_APPSECRET);

            String rettime = StringUtil.nowTimeMilesString();
            CommonUtil.addThirdLog(0,Const.THIRDLOG_TYPE_NN,"获取诺诺平台accessToken","POST", "getMerchantToken",
                    reqtime,"",rettime,reStr);
            log.info(reStr);
            JSONObject resObj = JSONObject.parseObject(reStr);
            return resObj.getString("access_token");
        } catch (Exception e) {
            String rettime = StringUtil.nowTimeMilesString();
            CommonUtil.addThirdLog(1,Const.THIRDLOG_TYPE_NN,"获取诺诺平台accessToken","POST", "getMerchantToken",
                    reqtime,"",rettime,e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @描述: 提交开票
     *
     */
    public static String addInvoice(JSONObject reqObj) {
        NNOpenSDK sdk = NNOpenSDK.getIntance();

        String reqtime = StringUtil.nowTimeMilesString();
        String content ="";
        try {
            String taxnum = Const.INVOICE_TAXNUM; // 授权企业税号
            String appKey = Const.INVOICE_APPKEY;
            String appSecret = Const.INVOICE_APPSECRET;
            String method = "nuonuo.ElectronInvoice.requestBillingNew"; // API方法名

            String token = CommonUtil.getNNAccessToken(); // 访问令牌
//            String token = UUID.randomUUID().toString().replace("-", ""); // 测试环境使用SD89737190/SD7FC5BD3C9B41E1这一组APPKey和APPSecret时，token随便填
            content =reqObj.toJSONString();
            String url = Const.INVOICE_URL; // SDK请求地址
            String senid = UUID.randomUUID().toString().replace("-", ""); // 唯一标识，32位随机码，无需修改，保持默认即可
            String result = sdk.sendPostSyncRequest(url, senid, appKey, appSecret, token, taxnum, method, content);

            String rettime = StringUtil.nowTimeMilesString();
            CommonUtil.addThirdLog(0,Const.THIRDLOG_TYPE_NN,"向诺诺平台提交开票","POST", "https://sdk.nuonuo.com/open/v1/services",
                    reqtime,content,rettime,result);
            log.info(result);
            JSONObject resObj = JSONObject.parseObject(result);
            if(null !=resObj.get("code")&&"E0000".equals(resObj.get("code"))){
                return resObj.getJSONObject("result").getString("invoiceSerialNum");
            }else{
                return null;
            }

        } catch (Exception e) {
            String rettime = StringUtil.nowTimeMilesString();
            CommonUtil.addThirdLog(1,Const.THIRDLOG_TYPE_NN,"向诺诺平台提交开票","POST", "https://sdk.nuonuo.com/open/v1/services",
                    reqtime,content,rettime,e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @描述: 获取开票结果
     *
     */
    public static JSONObject queryResult(JSONObject reqObj) {
        NNOpenSDK sdk = NNOpenSDK.getIntance();

        String reqtime = StringUtil.nowTimeMilesString();
        String content ="";
        try {
            String taxnum = Const.INVOICE_TAXNUM; // 授权企业税号
            String appKey = Const.INVOICE_APPKEY;
            String appSecret = Const.INVOICE_APPSECRET;
            String method = "nuonuo.ElectronInvoice.queryInvoiceResult"; // API方法名

            String token = CommonUtil.getNNAccessToken(); // 访问令牌
            content =reqObj.toJSONString();
            String url = Const.INVOICE_URL; // SDK请求地址
            String senid = UUID.randomUUID().toString().replace("-", ""); // 唯一标识，32位随机码，无需修改，保持默认即可
            String result = sdk.sendPostSyncRequest(url, senid, appKey, appSecret, token, taxnum, method, content);

            String rettime = StringUtil.nowTimeMilesString();
            CommonUtil.addThirdLog(0,Const.THIRDLOG_TYPE_NN,"查询开票结果","POST", "https://sdk.nuonuo.com/open/v1/services",
                    reqtime,content,rettime,result);
            log.info(result);
            JSONObject resObj = JSONObject.parseObject(result);

            if(resObj.get("code").equals("E0000")){
                return resObj.getJSONArray("result").getJSONObject(0);

            }else{
                return null;
            }

        } catch (Exception e) {
            String rettime = StringUtil.nowTimeMilesString();
            CommonUtil.addThirdLog(1,Const.THIRDLOG_TYPE_NN,"查询开票结果","POST", "https://sdk.nuonuo.com/open/v1/services",
                    reqtime,content,rettime,e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @描述: 重新交付发票
     *
     */
    public static Boolean reSendInvoice(JSONObject reqObj) {
        NNOpenSDK sdk = NNOpenSDK.getIntance();

        String reqtime = StringUtil.nowTimeMilesString();
        String content ="";
        try {
            String taxnum = Const.INVOICE_TAXNUM; // 授权企业税号
            String appKey = Const.INVOICE_APPKEY;
            String appSecret = Const.INVOICE_APPSECRET;
            String method = "nuonuo.ElectronInvoice.deliveryInvoice"; // API方法名

            String token = CommonUtil.getNNAccessToken(); // 访问令牌
//            String token = UUID.randomUUID().toString().replace("-", ""); // 测试环境使用SD89737190/SD7FC5BD3C9B41E1这一组APPKey和APPSecret时，token随便填
            content =reqObj.toJSONString();
            String url = Const.INVOICE_URL; // SDK请求地址
            String senid = UUID.randomUUID().toString().replace("-", ""); // 唯一标识，32位随机码，无需修改，保持默认即可
            String result = sdk.sendPostSyncRequest(url, senid, appKey, appSecret, token, taxnum, method, content);

            String rettime = StringUtil.nowTimeMilesString();
            CommonUtil.addThirdLog(0,Const.THIRDLOG_TYPE_NN,"重新交付发票","POST", "https://sdk.nuonuo.com/open/v1/services",
                    reqtime,content,rettime,result);
            log.info(result);
            JSONObject resObj = JSONObject.parseObject(result);
            if(null !=resObj.get("code")&&"E0000".equals(resObj.get("code"))){
                return true;
            }else{
                return false;
            }

        } catch (Exception e) {
            String rettime = StringUtil.nowTimeMilesString();
            CommonUtil.addThirdLog(1,Const.THIRDLOG_TYPE_NN,"重新交付发票","POST", "https://sdk.nuonuo.com/open/v1/services",
                    reqtime,content,rettime,e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    public static void main(String[] args) {
        NNOpenSDK sdk = NNOpenSDK.getIntance();
        String accessToken =sdk.getMerchantToken("SD68601635","SD353A0058354B15");
        System.out.println(accessToken);
    }
}
