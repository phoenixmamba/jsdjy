package com.centit.logstatistics.logstatisticsserver.webmgr.utils;

import java.security.MessageDigest;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

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
     * 获取当前时间字符串
     */
    public static String nowDateString() {
        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sf.format(new Date());
    }

    public static String getCurrentTime(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String str = sdf.format(date);
        return str;
    }

    public static String nowTimeString() {
        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sf.format(new Date());
    }

    public static Timestamp getSystemTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    public static void main(String[] args){
        int b=0;
        int a=1;
        b =a++;
        System.out.println(b);
        System.out.println(a);
    }
}