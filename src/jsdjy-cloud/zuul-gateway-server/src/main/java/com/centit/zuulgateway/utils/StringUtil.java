package com.centit.zuulgateway.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * <p>常用工具<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 常用工具
 * @Date : 2020-04-13
 **/
public class StringUtil {

    /**
     * 获取长度32位的随机字符串
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().substring(0, 32).replaceAll("-", "");
    }

    /**
     * 获取当前时间字符串
     */
    public static String getNowDateString() {
        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sf.format(new Date());
    }
}
