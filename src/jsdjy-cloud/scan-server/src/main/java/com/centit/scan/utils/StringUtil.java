package com.centit.scan.utils;


import com.alibaba.fastjson.JSONObject;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.dingtalk.api.response.OapiMessageCorpconversationAsyncsendV2Response;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author ：cui_jian
 * @version ：1.0
 * @date ：Created in 2020/4/15 10:21
 * @description ：字符串工具类
 */
public class StringUtil {

    /**
     * @Description 获取32位随机字符串
     **/
    public static String UUID(){
        return UUID.randomUUID().toString().substring(0, 32).replaceAll("-", "");
    }

    private static Random r = new Random();

    protected static MessageDigest messagedigest = null;

    public final static String TIME_STAMP_PATTERN = "yyyyMMddHHmmssSSS";

    /**
     * @param date
     * @param pattern
     * @return
     * @描述: 格式化时间
     * @作者: zhouChaoXi
     * @时间: 2018年6月3日
     */
    public static String formatDate(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        DateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    /**
     * @Description MD5加密
     **/
    public static String md5(String x) {
        try {
            byte[] ret = MessageDigest.getInstance("MD5").digest(x.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : ret) {
                int c = b;
                if (c<0){
                    c = 256 + c;
                }
                String str = Integer.toHexString(c);
                if (str.length()==1){
                    str = "0" + str;
                }
                sb.append(str);
            }
            return sb.toString();
        } catch (Exception e) {
            return x;
        }
    }

    /**
     * @Description 获取x位随机验证码
     **/
    public static String randomCode(int x) {
        String codes = "23456789abcdefghijkmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYXZ";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < x; ++i) {// 随机生成4个字符
            int index = r.nextInt(codes.length());
            sb.append(codes.charAt(index));
        }
        return ""+sb;
    }

    public final static String getMD5(String s) {
        char hexDigits[] = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            byte strTemp[] = s.getBytes();
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(strTemp);
            byte b[] = md.digest();

            int len = b.length;
            char str[] = new char[len*2];
            int k=0;
            for(int i=0; i<len; i++){
                str[k++] = hexDigits[b[i] >>> 4 & 0xf];
                str[k++] = hexDigits[b[i] & 0xf];
            }
            return new String(str);

        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static String nowTimeString() {
        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sf.format(new Date());
    }

    public static String nowDateString() {
        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
        return sf.format(new Date());
    }

    public static String getCurrentTime(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String str = sdf.format(date);
        return str;
    }

    /**
     * @Description 当前时间加几分钟
     **/
    public static String nowTimePlusMinutes(int i) {
        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar nowTime = Calendar.getInstance();
        nowTime.add(Calendar.MINUTE, i);
        return sf.format(nowTime.getTime());
    }


    public static Timestamp getSystemTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    /**
     * @Description 获取x位随机数
     **/
    public static String randomNumber(int x) {
        String codes = "1234567890";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < x; ++i) {// 随机生成i个字符
            int index = r.nextInt(codes.length());
            sb.append(codes.charAt(index));
        }
        return ""+sb;
    }

    public static boolean isNotNull(Object obj) {
        return (obj != null) && !"".equals(obj.toString()) && !"null".equals(obj);
    }

    public static String getTimeNo(String head) {
        if (CommUtil.isNull(head)) {
            head = "";
        }
        return head + formatDate(new Date(), TIME_STAMP_PATTERN);
    }

    public static int null2Int(Object s) {
        int v = 0;
        if (s != null) {
            try {
                v = Integer.parseInt(s.toString());
            } catch (Exception localException) {
            }
        }
        return v;
    }

    public static float null2Float(Object s) {
        float v = 0.0F;
        if (s != null) {
            try {
                v = Float.parseFloat(s.toString());
            } catch (Exception localException) {
            }
        }
        return v;
    }

    public static Double null2Double(Object s) {
        Double v = 0.0D;
        if (s != null) {
            try {
                v = Double.parseDouble(null2String(s));
            } catch (Exception localException) {
            }
        }
        return v;
    }

    public static boolean null2Boolean(Object s) {
        boolean v = false;
        if (s != null) {
            try {
                v = Boolean.parseBoolean(s.toString());
            } catch (Exception localException) {
            }
        }
        return v;
    }

    public static String null2String(Object s) {
        return s == null ? "" : s.toString().trim();
    }

    public static Long null2Long(Object s) {
        Long v = Long.valueOf(-1L);
        if (isNotNull(s)) {
            try {
                v = Long.valueOf(Long.parseLong(s.toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return v;
    }

    public static String getToken() throws Exception{
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/gettoken");
        OapiGettokenRequest request = new OapiGettokenRequest();
        request.setAppkey("dinghtyw5czd5xgmwumn");
        request.setAppsecret("mI4ctCXByByAlLhn_z8sjH-UaiUTnEycMoccLNK-zANFkyd70cD2ZRGq9nk0aNsJ");
        request.setHttpMethod("GET");
        OapiGettokenResponse response = client.execute(request);
        System.out.println(response.getBody());
        JSONObject obj = JSONObject.parseObject(response.getBody());
        return obj.getString("access_token");

    }

    public static void main(String[] args) throws Exception {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2");
        OapiMessageCorpconversationAsyncsendV2Request request = new OapiMessageCorpconversationAsyncsendV2Request();
        request.setAgentId(1399512727L);
        request.setUseridList("0460090334762982");
        request.setToAllUser(false);

        OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
        msg.setMsgtype("text");
        msg.setText(new OapiMessageCorpconversationAsyncsendV2Request.Text());
        msg.getText().setContent("测试1");
        request.setMsg(msg);

        OapiMessageCorpconversationAsyncsendV2Response rsp = client.execute(request, getToken());
        System.out.println(rsp.getBody());

        }
    }