package com.centit.ticket.utils;

/**
 * @author ：cui_jian
 * @version ：1.0
 * @date ：Created in 2021/1/25 16:51
 * @description ：科拓接口签名
 */
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;

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
                        && !(p.getValue() instanceof Map)
                        && !(p.getValue() instanceof Iterable))
                .forEach(p -> {
                    if (!p.getValue().equals("")) {
                        params.put(p.getKey(), p.getValue().toString());
                    }
                });
        //拼接appSecret
        String temp = Joiner.on("&").withKeyValueSeparator("=").join(params).concat("&").concat(appSecret);
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

    public static void main(String[] args){
        String appId = "10031";
//            String key = "9d682649d9f64faeb5e4477a8e27858e";
        String parkId = "1039";
        String serviceCode = "getParkingPaymentInfo";
        String ts = String.valueOf(System.currentTimeMillis());
        String reqId = ts;
        JSONObject reqJson = new JSONObject();
        reqJson.put("appId",appId);
        reqJson.put("parkId",parkId);
        reqJson.put("serviceCode",serviceCode);
        reqJson.put("ts",ts);
        System.out.println(ts);
        reqJson.put("reqId",ts);
        reqJson.put("plateNo","闽D01022");
//        reqJson.put("orderNo","000120210204093446258D01022");
//        reqJson.put("payableAmount",44);
//        reqJson.put("amount",1);
//        reqJson.put("payType",5);
//        reqJson.put("payMethod",4);
//        reqJson.put("freeMoney",0);
//        reqJson.put("freeTime",0);
        System.out.println(reqJson);
        String signValue = SignUtil.paramsSign(reqJson,"9d682649d9f64faeb5e4477a8e27858e");
        System.out.println(signValue);
    }
}
