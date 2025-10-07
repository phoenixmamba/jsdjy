package com.centit.pay.utils;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.ParseException;
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

    private static SimpleDateFormat sf_default=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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

    public static boolean isNotNull(Object obj) {
        return (obj != null) && !"".equals(obj.toString()) && !"null".equals(obj);
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

    public static String nowTimeMilesString() {
        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return sf.format(new Date());
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

    public static String timePlusMinutes(String timeStr,int i) {
        try {
            Date strDate = sf_default.parse(timeStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(strDate);
            calendar.add(Calendar.MINUTE, i);
            return sf_default.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String nowTimePlusMinutes(int i,SimpleDateFormat sf) {
        Calendar nowTime = Calendar.getInstance();
        nowTime.add(Calendar.MINUTE, i);
        return sf.format(nowTime.getTime());
    }

    public static String timePlusMinutes(String timeStr,int i,SimpleDateFormat sf) {
        try {
            Date strDate = sf_default.parse(timeStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(strDate);
            calendar.add(Calendar.MINUTE, i);
            return sf.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
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


    /**
     * 获取编码字符集
     *
     * @param request
     * @param response
     * @return String
     */
    public static String getCharacterEncoding(HttpServletRequest request,
                                              HttpServletResponse response) {

        if (null == request || null == response) {
            return "gbk";
        }

        String enc = request.getCharacterEncoding();
        if (null == enc || "".equals(enc)) {
            enc = response.getCharacterEncoding();
        }

        if (null == enc || "".equals(enc)) {
            enc = "gbk";
        }

        return enc;
    }

    /**
     * 获取本机ip
     **/
    public static String getLocalIp() {
        try{
            Enumeration allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()) {
                System.err.println("getLocalIp()======================================");
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                Enumeration addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    System.err.println("hasMoreElements()======================================");
                    InetAddress ip = (InetAddress) addresses.nextElement();
                    if (ip != null && ip.isSiteLocalAddress() && ip instanceof Inet4Address) {
                        String ipAddress = ip.getHostAddress();
                        if ("192.168.122.1".equals(ipAddress)) {
                            continue;
                        }
                        return ipAddress;
                    }
                }
            }
        }catch (SocketException e) {
            return null;
        }

        return "127.0.0.1";
    }

    public static void main(String[] args) throws ParseException {
//        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String startTime = "2021-10-11 14:03:27";
//
//        System.out.println(sf.parse(startTime).getTime());
//        String uuid = UUID.randomUUID().toString();	//获取UUID并转化为String对象
//        uuid = uuid.replace("-", "");				//因为UUID本身为32位只是生成时多了“-”，所以将它们去点就可
//        System.out.println(uuid);
        SimpleDateFormat sf=new SimpleDateFormat("yyyyMMddHHmmss");
        System.out.println(StringUtil.timePlusMinutes("2024-06-13 22:24:58",2));

    }
    }