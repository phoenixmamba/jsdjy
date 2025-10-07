//package com.centit.core.service.third;
//
//import cn.hutool.core.date.DatePattern;
//import cn.hutool.core.date.DateUtil;
//import cn.hutool.http.HttpUtil;
//import com.alibaba.fastjson.JSONObject;
//import com.centit.core.config.ParkConfig;
//import com.centit.core.exp.ThirdApiException;
//import com.centit.core.result.ResultCodeEnum;
//import com.centit.core.third.enums.ParkApiEnum;
//import com.google.common.base.Joiner;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.Resource;
//import java.nio.charset.StandardCharsets;
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//import java.util.Map;
//import java.util.TreeMap;
//
///**
// * @version : 1.0
// * @Author : cui_jian
// * @Description :
// * @Date : 2024/11/26 10:17
// **/
//@Slf4j
//@Service
//public class ParkService {
//
//    private final static String REQUEST_METHOD_POST ="POST";
//    private final static String RESPONSE_PARAM_STATUS ="resCode";
//    private final static String RESPONSE_PARAM_STATUS_SUCCESS ="0";
//    private final static String RESPONSE_PARAM_DATA="data";
//
//    private final static int REPORT_OFFSET_HOURS=-25;
//
//    @Resource
//    private ParkConfig parkConfig;
//    @Resource
//    private ThirdLogService thirdLogService;
//
//    /**
//     * 检查速停车接口是否正常
//     * @param plateNo 车牌号
//     */
//    public void checkApi(String plateNo) {
//        JSONObject reqJson =buildDefaultParams(ParkApiEnum.GET_PARKING_PAYMENT_INFO.getServiceCode());
//        reqJson.put("plateNo",plateNo);
//        //签名
//        String signValue = paramsSign(reqJson, parkConfig.getAppSecret());
//        reqJson.put("key", signValue);
//        String apiName=ParkApiEnum.GET_PARKING_PAYMENT_INFO.getApiName();
//        log.info("速停车接口-{}请求报文：{}",apiName,reqJson);
//        try {
//            HttpUtil.createPost(parkConfig.getHost()+ParkApiEnum.GET_PARKING_PAYMENT_INFO.getApiUrl()).header("version",parkConfig.getVersion()).body(reqJson.toString()).execute().body();
//        } catch (Exception e) {
//            log.error("速停车接口-{}调用失败:",apiName,e);
//            throw new ThirdApiException(ResultCodeEnum.PARK_REQUEST_ERROR,"速停车接口"+apiName+"请求失败");
//        }
//    }
//
//    /**
//     * 账单查询/费用查询
//     * @param plateNo 车牌号
//     * @return JSONObject
//     */
//    public JSONObject getParkingPaymentInfo(String plateNo) {
//        JSONObject reqJson =buildDefaultParams(ParkApiEnum.GET_PARKING_PAYMENT_INFO.getServiceCode());
//        reqJson.put("plateNo",plateNo);
//        return sendRequest(reqJson,ParkApiEnum.GET_PARKING_PAYMENT_INFO);
//    }
//
//    /**
//     * 获取车流量数据
//     * @return JSONObject
//     */
//    public JSONObject getParkingReport() {
//        JSONObject reqJson =buildDefaultParams(ParkApiEnum.GET_PARKING_REPORT_INFO.getServiceCode());
//        reqJson.put("startTime", DateUtil.formatDateTime(DateUtil.offsetHour(DateUtil.date(),REPORT_OFFSET_HOURS)));
//        reqJson.put("endTime", DateUtil.now());
//        reqJson.put("pageIndex", 1);
//        reqJson.put("pageSize", 50);
//
//        return sendRequest(reqJson,ParkApiEnum.GET_PARKING_REPORT_INFO);
//    }
//
//    public JSONObject sendRequest(JSONObject reqJson,ParkApiEnum apiEnum){
//        //签名
//        reqJson.put("key", paramsSign(reqJson, parkConfig.getAppSecret()));
//        String reqTime = DateUtil.format(DateUtil.date(), DatePattern.NORM_DATETIME_MS_PATTERN);
//        String rsp;
//        String apiName=apiEnum.getApiName();
//        log.info("速停车接口-{}请求报文：{}",apiName,reqJson);
//        try {
//            rsp = HttpUtil.createPost(parkConfig.getHost()+apiEnum.getApiUrl()).header("version",parkConfig.getVersion()).body(reqJson.toString()).execute().body();
//        } catch (Exception e) {
//            log.error("速停车接口-{}调用失败:",apiName,e);
//            throw new ThirdApiException(ResultCodeEnum.PARK_REQUEST_ERROR,"速停车接口"+apiName+"请求失败");
//        }
//        log.info("速停车接口-{}返回报文：{}",apiName,rsp);
//        checkParkResponse(reqJson,apiEnum,reqTime,rsp);
//        return JSONObject.parseObject(rsp).getJSONObject(RESPONSE_PARAM_DATA);
//    }
//
//    /**
//     * 速停车接口默认请求参数
//     * @param serviceCode 接口业务代码
//     * @return JSONObject
//     */
//    public JSONObject buildDefaultParams(String serviceCode){
//        JSONObject reqJson = new JSONObject();
//        reqJson.put("appId", parkConfig.getAppId());
//        reqJson.put("parkId", parkConfig.getParkId());
//        reqJson.put("serviceCode", serviceCode);
//        String ts = String.valueOf(DateUtil.current());
//        //时间戳
//        reqJson.put("ts", ts);
//        //每次请求的唯一标识
//        reqJson.put("reqId", ts);
//        return reqJson;
//    }
//
//    /**
//     * (该方法由速停车接口文档提供，建议直接使用该方法)
//     * 参数签名
//     * 示例：
//     * 参数对象：{"amount":100,"orderNo":"闽C12345","payTime":"2020-03-06 10:57:22","freeDetail":{"code":"","money":100,"time":0,"type":0},"paySource":"85d15350778b11e9bbaa506b4b2f6421","outOrderNo":"T20200306124536001","parkId":"1000001","payableAmount":200,"reqId":"5be4e3e6d5704a7d91ccbd9731d970f5","payType":1006,"payMethod":6,"appId":"85d15350778b11e9bbaa506b4b2f6421","freeTime":0,"paymentExt":{"deviceNo":"123456"},"freeMoney":100,"ts":1583744086841}
//     * url拼接：amount=100&freeDetail={"code":"","money":100,"time":0,"type":0}&freeMoney=100&freeTime=0&orderNo=闽C12345&outOrderNo=T20200306124536001&parkId=1000001&payMethod=6&paySource=85d15350778b11e9bbaa506b4b2f6421&payTime=2020-03-06 10:57:22&payType=1006&payableAmount=200&paymentExt={"deviceNo":"123456"}&reqId=5be4e3e6d5704a7d91ccbd9731d970f5&ts=1583744086841&EED96C219E83450A
//     * 签名结果：EFCAE7AFD1BD6CC9A3826E03EFD0F543
//     *
//     * @param requestBody 参数对象
//     * @param appSecret   秘钥
//     * @return String
//     */
//    public static String paramsSign(JSONObject requestBody, String appSecret) {
//        TreeMap<String, String> params = new TreeMap<>();
//        //过滤掉key，appId字段，空属性及Map或List等复杂对象
//        requestBody.entrySet().stream().filter(
//                        p -> !"key".equals(p.getKey())
//                                && !"appId".equals(p.getKey())
//                                && p.getValue() != null
//                                && !(p.getValue() instanceof Map)
//                                && !(p.getValue() instanceof Iterable))
//                .forEach(p -> {
//                    if (!"".equals(p.getValue())) {
//                        params.put(p.getKey(), p.getValue().toString());
//                    }
//                });
//        //拼接appSecret
//        String temp = Joiner.on("&").withKeyValueSeparator("=").join(params).concat("&").concat(appSecret);
//
//        return md5(temp).toUpperCase();
//    }
//
//    /**
//     * (该方法由速停车接口文档提供，建议直接使用该方法)
//     * 对文本执行 md5 摘要加密, 此算法与 mysql,JavaScript生成的md5摘要进行过一致性对比.
//     *
//     * @param plainText 加密文本
//     * @return 返回值中的字母为小写
//     */
//    private static String md5(String plainText) {
//        if (null == plainText) {
//            plainText = "";
//        }
//        String mD5Str = null;
//        try {
//            // JDK 支持以下6种消息摘要算法，不区分大小写
//            // md5,sha(sha-1),md2,sha-256,sha-384,sha-512
//            MessageDigest md = MessageDigest.getInstance("MD5");
//            md.update(plainText.getBytes(StandardCharsets.UTF_8));
//            byte[] b = md.digest();
//            int i;
//            StringBuilder builder = new StringBuilder(32);
//            for (byte value : b) {
//                i = value;
//                if (i < 0) {
//                    i += 256;
//                }
//                if (i < 16) {
//                    builder.append("0");
//                }
//                builder.append(Integer.toHexString(i));
//            }
//            mD5Str = builder.toString();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        return mD5Str;
//    }
//
//    public void checkParkResponse(JSONObject params, ParkApiEnum requestEnum, String reqTime, String rsp){
//        JSONObject resObj;
//        boolean res = false;
//        try{
//            resObj = JSONObject.parseObject(rsp);
//            if (RESPONSE_PARAM_STATUS_SUCCESS.equals(resObj.get(RESPONSE_PARAM_STATUS))&&resObj.getJSONObject(RESPONSE_PARAM_DATA)!=null) {
//                res=true;
//            }
//        }catch (Exception e){
//            log.error("速停车接口-{}解析报文失败：{}",requestEnum.getApiName(),rsp);
//            throw new ThirdApiException(ResultCodeEnum.PARK_REQUEST_ERROR,"速停车"+requestEnum.getApiName()+"接口数据异常");
//        }finally {
//            thirdLogService.addThirdLog(requestEnum.getApiName(), REQUEST_METHOD_POST,requestEnum.getApiUrl(),reqTime,params.toString(),rsp,res);
//        }
//        if(!res){
//            log.error("速停车接口-{}解析报文失败：{}",requestEnum.getApiName(),rsp);
//            throw new ThirdApiException(ResultCodeEnum.PARK_REQUEST_ERROR,"速停车"+requestEnum.getApiName()+"接口数据异常");
//        }
//    }
//}
