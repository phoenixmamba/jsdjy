package com.centit.shopping.utils;


import java.math.BigDecimal;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.DateFormat;
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

    public static String formatDate(String date, String pattern) {
        if (date == null) {
            return null;
        }
        DateFormat format = new SimpleDateFormat(pattern);
        try {
            return format.format(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(date));
        } catch (ParseException e) {
            return "";
        }
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

    /**
     * @Description 获取x位随机数字
     **/
    public static String randomNum(int x) {
        String codes = "0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < x; ++i) {// 随机生成4个字符
            int index = r.nextInt(codes.length());
            sb.append(codes.charAt(index));
        }
        return ""+sb;
    }
    /**
     * @Description 获取x位随机核销码
     **/
    public static String randomOffCode(int x) {
        String codes = "23456789ABCDEFGHIJKLMNOPQRSTUVWXYXZ";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < x; ++i) {// 随机生成i个字符
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

    public static String nowYearString() {
        SimpleDateFormat sf=new SimpleDateFormat("yyyy");
        return sf.format(new Date());
    }

    /**
     * 通过时间秒毫秒数判断参数时间与当前时间相差的天数

     * @return
     */
    public static int differentDaysByMillisecond(String dateStr)
    {
        Date date1 = new Date();
        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date2 = null;
        try {
            date2 = sf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int days = (int) ((date2.getTime() - date1.getTime()) / (1000*3600*24));
        return days;
    }

    /**
     * 通过时间秒毫秒数判断参数时间与当前时间相差的天数

     * @return
     */
    public static int differentDaysByMillisecond(String dateStr,SimpleDateFormat sf)
    {
        Date date1 = new Date();
        Date date2 = null;
        try {
            date2 = sf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int days = (int) ((date2.getTime() - date1.getTime()) / (1000*3600*24));
        return days;
    }

    /**
     * 通过时间秒毫秒数判断参数时间与当前时间相差的天数

     * @return
     */
    public static int differentMillisecond(String dateStr,SimpleDateFormat sf)
    {
        Date date1 = new Date();

        Date date2 = null;
        try {
            date2 = sf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int days = (int) ((date2.getTime() - date1.getTime()));
        return days;
    }

    /**
     * 通过时间秒毫秒数判断参数时间与当前时间大小

     * @return
     */
    public static int compareMillisecond(String dateStr,SimpleDateFormat sf)
    {
        Date date1 = new Date();

        Date date2 = null;
        try {
            date2 = sf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(date2.getTime()>date1.getTime()){
            return 1;
        }else if(date2.getTime()<date1.getTime()){
        return -1;
        }else{
            return 0;
        }

    }

    /**
     * 通过时间秒毫秒数判断参数时间与当前时间相差的小时

     * @return
     */
    public static int differentHoursByMillisecond(String timeStr)
    {
        Date date1 = new Date();
        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date2 = null;
        try {
            date2 = sf.parse(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int hours = (int) ((date1.getTime() - date2.getTime()) / (1000*3600));
        return hours;
    }

    /**
     * 通过时间秒毫秒数判断参数时间与当前时间相差的分钟

     * @return
     */
    public static int differentMinutesByMillisecond(String timeStr)
    {
        Date date1 = new Date();
        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date2 = null;
        try {
            date2 = sf.parse(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int minutes = (int) ((date1.getTime() - date2.getTime()) / (1000*60));
        return minutes;
    }

    /**
     * 判断参数时间与当前时间相差的毫秒

     * @return
     */
    public static int differentMilesByMillisecond(String timeStr)
    {
        Date date1 = new Date();
        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date2 = null;
        try {
            date2 = sf.parse(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int miles = (int) ((date1.getTime() - date2.getTime()) );
        return miles;
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

    /**
     * @Description 当前时间加几小时
     **/
    public static String nowTimePlusHours(int i) {
        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar nowTime = Calendar.getInstance();
        nowTime.add(Calendar.HOUR, i);
        return sf.format(nowTime.getTime());
    }

    /**
     * 时间加几天
     **/
    public static String addDay(String s, int n) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Calendar cd = Calendar.getInstance();
            cd.setTime(sdf.parse(s));
            cd.add(Calendar.DATE, n);//增加n天
            //cd.add(Calendar.MONTH, n);//增加一个月

            return sdf.format(cd.getTime());

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 时间加几个月
     **/
    public static String addMonth(String s, int n) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Calendar cd = Calendar.getInstance();
            cd.setTime(sdf.parse(s));
//            cd.add(Calendar.DATE, n);//增加n天
            cd.add(Calendar.MONTH, n);//增加一个月

            return sdf.format(cd.getTime());

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 判断参数时间间相差的毫秒

     * @return
     */
    public static int differentMilesBetween2Times(String timeStr1,String timeStr2)
    {
        Date date1 = null;
        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date2 = null;
        try {
            date1 = sf.parse(timeStr1);
            date2 = sf.parse(timeStr2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int miles = (int) ((date1.getTime() - date2.getTime()) );
        return miles;
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

    /*获取数组最大值和最小值*/
    public static Map<String,BigDecimal> getMaxMin(List<BigDecimal> a){
        //声明2个变量，取数组任意两个值进行赋值
        BigDecimal max=a.get(0);
        BigDecimal min=a.get(1);
        //遍历数组，分别与这两值比较，比当前最大值大时，把它赋值给最大值，比当前最小值小时，把它赋值给最小值。
        for (int i = 0; i < a.size(); i++) {
            if(null !=a.get(i)){
                if (a.get(i).compareTo(max)==1) {
                    max=a.get(i);
                }else if(a.get(i).compareTo(min)==-1){
                    min=a.get(i);
                }
            }

        }
        Map<String,BigDecimal> resMap = new HashMap<>();
        resMap.put("max",max);
        resMap.put("min",min);

        return resMap;
    }

    public static Integer getRandomNum(int min,int max){
        return new Random().nextInt(max-min)+min;
    }

//    /**
//     * 获取本机ip
//     **/
//    public static String getLocalIp() {
//        try {
//            InetAddress address = InetAddress.getLocalHost();//获取的是本地的IP地址
//            String hostAddress = address.getHostAddress();
//
//            return hostAddress;
//
//        } catch (Exception e) {
//            return null;
//        }
//    }

//    public static String getLocalIp() {
//        try {
//            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
//            while (allNetInterfaces.hasMoreElements()) {
//                System.out.println("getLocalIp==========================================================");
//                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces
//                        .nextElement();
//
//// 去除回环接口，子接口，未运行和接口
//                if (netInterface.isLoopback() || netInterface.isVirtual()
//                        || !netInterface.isUp()) {
//                    continue;
//                }
//
//                if (!netInterface.getDisplayName().contains("Intel")
//                        && !netInterface.getDisplayName().contains("Realtek")) {
//                    continue;
//                }
//                Enumeration<InetAddress> addresses = netInterface
//                        .getInetAddresses();
//                System.out.println("netInterface.getDisplayName()==========================================================");
//                System.out.println(netInterface.getDisplayName());
//                while (addresses.hasMoreElements()) {
//                    InetAddress ip = addresses.nextElement();
//                    if (ip != null) {
//// ipv4
//                        if (ip instanceof Inet4Address) {
//                            System.out.println("ipv4 = " + ip.getHostAddress());
//                            return ip.getHostAddress();
//                        }
//                    }
//                }
//                break;
//            }
//        } catch (SocketException e) {
//            System.err.println("Error when getting host ip address"
//                    + e.getMessage());
//        }
//        return null;
//    }

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

    public static void main(String[] args) {
        StringBuffer sb = new StringBuffer("13776407246,");
        for(int i=0;i<2000;i++){
            sb.append("13776407246,");
        }
        String ss = String.valueOf(sb);
        System.out.println(ss);
    }
    }