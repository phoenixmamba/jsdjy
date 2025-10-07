package com.centit.pay.utils;

/**
 * @author ：cui_jian
 * @version ：1.0
 * @date ：Created in 2021/1/25 16:51
 * @description ：科拓接口签名
 */
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.pay.biz.po.ParkOrder;
import com.google.common.base.Joiner;
import com.google.gson.Gson;
import org.jdom.JDOMException;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * 签名工具
 *
 * @author keytop
 * @date 2020/3/6
 */
public class SignUtil {
    /**
     * 参数签名
     * 示例：
     * 参数对象：{"amount":100,"orderNo":"闽C12345","payTime":"2020-03-06 10:57:22","freeDetail":{"code":"","money":100,"time":0,"type":0},"paySource":"85d15350778b11e9bbaa506b4b2f6421","outOrderNo":"T20200306124536001","parkId":"1000001","payableAmount":200,"reqId":"5be4e3e6d5704a7d91ccbd9731d970f5","payType":1006,"payMethod":6,"appId":"85d15350778b11e9bbaa506b4b2f6421","freeTime":0,"paymentExt":{"deviceNo":"123456"},"freeMoney":100,"ts":1583744086841}
     * url拼接：amount=100&freeDetail={"code":"","money":100,"time":0,"type":0}&freeMoney=100&freeTime=0&orderNo=闽C12345&outOrderNo=T20200306124536001&parkId=1000001&payMethod=6&paySource=85d15350778b11e9bbaa506b4b2f6421&payTime=2020-03-06 10:57:22&payType=1006&payableAmount=200&paymentExt={"deviceNo":"123456"}&reqId=5be4e3e6d5704a7d91ccbd9731d970f5&ts=1583744086841&EED96C219E83450A
     * 签名结果：EFCAE7AFD1BD6CC9A3826E03EFD0F543
     *
     * @param requestBody 参数对象
     * @param appSecret   秘钥
     * @return
     */
    public static String paramsSign(JSONObject requestBody, String appSecret) {
        TreeMap<String, String> params = new TreeMap<>();
        //过滤掉key，appId字段，空属性及Map或List等复杂对象
        requestBody.entrySet().stream().filter(
                p -> !"key".equals(p.getKey())
                        && !"appId".equals(p.getKey())
                        && p.getValue() != null
//                        && !(p.getValue() instanceof Map)
//                        && !(p.getValue() instanceof Iterable)
        )
                .forEach(p -> {
                    if (!p.getValue().equals("")) {
                        params.put(p.getKey(), p.getValue().toString());
                    }
                });
        //拼接appSecret
        String temp = Joiner.on("&").withKeyValueSeparator("=").join(params).concat("&").concat(appSecret);
        System.out.println("temp======================="+temp);
        return md5(temp).toUpperCase();
    }

    /**
     * 对文本执行 md5 摘要加密, 此算法与 mysql,JavaScript生成的md5摘要进行过一致性对比.
     *
     * @param plainText
     * @return 返回值中的字母为小写
     */
    private static String md5(String plainText) {
        if (null == plainText) {
            plainText = "";
        }
        String mD5Str = null;
        try {
            // JDK 支持以下6种消息摘要算法，不区分大小写
            // md5,sha(sha-1),md2,sha-256,sha-384,sha-512
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes(StandardCharsets.UTF_8));
            byte[] b = md.digest();
            int i;
            StringBuilder builder = new StringBuilder(32);
            for (byte value : b) {
                i = value;
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    builder.append("0");
                }
                builder.append(Integer.toHexString(i));
            }
            mD5Str = builder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return mD5Str;
    }

//    public static void main(String[] args){
//        String appId = "10685";
////            String key = "9d682649d9f64faeb5e4477a8e27858e";
//        String parkId = "1208";
//        String serviceCode = "payParkingFee";
//        String ts = String.valueOf(System.currentTimeMillis());
//        String reqId = "1632278928540";
//        JSONObject reqJson = new JSONObject();
//
//        reqJson.put("appId",appId);
//        reqJson.put("amount","100");
//        reqJson.put("freeMoney","500");
//        reqJson.put("freeTime","0");
//        reqJson.put("payTime","");
//        reqJson.put("isNoSense","0");
//        reqJson.put("orderNo","Bm7C1gQzNINIc9eqyEomTP");
//        reqJson.put("outOrderNo","PK20210918155846343");
//        reqJson.put("parkId","1208");
//        reqJson.put("payMethod","7");
//        reqJson.put("payType","5");
//        reqJson.put("payableAmount","600");
//        reqJson.put("reqId",ts);
//        reqJson.put("serviceCode","payParkingFee");
//        reqJson.put("ts",ts);
//
//        JSONArray freeDetail = new JSONArray();
//        JSONObject couponObj = new JSONObject(new  LinkedHashMap());
//        couponObj.put("money", "300");  //减免金额，单位：分
//        couponObj.put("time", "0");    //减免时间 单位：秒
//        couponObj.put("code", "137333");    //抵扣券id
//        couponObj.put("type", "1003");    //抵扣来源:1
//        couponObj.put("freeName", "积分抵扣");    //减免项目名称
//
//        freeDetail.add(couponObj);
//
//        JSONObject couponObj2 = new JSONObject(new  LinkedHashMap());
//        couponObj2.put("money", "200");  //减免金额，单位：分
//        couponObj2.put("time", "0");    //减免时间 单位：秒
//        couponObj2.put("code", "137333");    //抵扣券id
//        couponObj2.put("type", "1003");    //抵扣来源:1
//        couponObj2.put("freeName", "余额抵扣");    //减免项目名称
//        freeDetail.add(couponObj2);
//
////        List<Map<String,String>> list = new ArrayList<>();
////        Map<String,String> map = new HashMap<>();
////        map.put("money", "500");  //减免金额，单位：分
////        map.put("time", "0");    //减免时间 单位：秒
////        map.put("code", "137333");    //抵扣券id
////        map.put("type", "1003");    //抵扣来源:1
////        map.put("freeName", "积分抵扣");    //减免项目名称
////        list.add(map);
////        System.out.println(JSON.toJSONString(list));
//
//        reqJson.put("freeDetail",freeDetail);
//
//        System.out.println(reqJson);
//        String signValue = SignUtil.paramsSign(reqJson,"f82e3b547cae43b1b041a05d673f8fd0");
//        System.out.println(signValue);
//        reqJson.put("key", signValue);
//
//        Gson gson = new Gson();
//        String jsonInString = gson.toJson(freeDetail);
//        reqJson.put("freeDetail",jsonInString);
//        System.out.println("+++++++++++++++"+reqJson);
//
//        JSONObject res = HttpSendUtil.doPost("http://kp-open.keytop.cn/unite-api/api/wec/PayParkingFee", reqJson.toJSONString());
//        System.out.println("+++++++++++++++"+res);
//    }

    public static void main(String[] args){
        String xmlstring ="<xml><appid><![CDATA[wx18a5d8b28707a7aa]]></appid>\n" +
                "<bank_type><![CDATA[OTHERS]]></bank_type>\n" +
                "<cash_fee><![CDATA[25268]]></cash_fee>\n" +
                "<fee_type><![CDATA[CNY]]></fee_type>\n" +
                "<is_subscribe><![CDATA[N]]></is_subscribe>\n" +
                "<mch_id><![CDATA[1288220501]]></mch_id>\n" +
                "<nonce_str><![CDATA[1632364779]]></nonce_str>\n" +
                "<openid><![CDATA[oXVaa515pWA6IJWNeezPfphJ2L4Y]]></openid>\n" +
                "<out_trade_no><![CDATA[CUL20210923103935790]]></out_trade_no>\n" +
                "<result_code><![CDATA[SUCCESS]]></result_code>\n" +
                "<return_code><![CDATA[SUCCESS]]></return_code>\n" +
                "<sign><![CDATA[1B5E8477891EA2B1E9B7B90AB20F1A91]]></sign>\n" +
                "<time_end><![CDATA[20210923103952]]></time_end>\n" +
                "<total_fee>25268</total_fee>\n" +
                "<trade_type><![CDATA[APP]]></trade_type>\n" +
                "<transaction_id><![CDATA[4200001221202109239636760412]]></transaction_id>\n" +
                "</xml>";
        try {
            Map<String, String> paramMap = PayUtil.doXMLParse(xmlstring);
            SortedMap<Object, Object> packageParams = new TreeMap<Object, Object>();
            for(String key:paramMap.keySet()){
                packageParams.put(key, paramMap.get(key));
            }
            System.out.println("+++++++++++++++"+packageParams);
            String sign = PayUtil.getSign("UTF-8", packageParams, "jsdjyfpyjfxzdzzdyysfssseqktzhzjl");
            System.out.println("+++++++++++++++"+sign);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JDOMException e) {
            e.printStackTrace();
        }
    }
}
