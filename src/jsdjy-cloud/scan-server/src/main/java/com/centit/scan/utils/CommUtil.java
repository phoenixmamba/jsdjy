package com.centit.scan.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;


public class CommUtil {

    public final static String IMGEXT = "bmp,dib,jfif,gif,jpe,jpeg,jpg,png,tif,tiff,ico";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat sdfLongTime = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    /**
     * TODO  判断是否为整数
     *
     * @作者： zhouchaoxi
     * @日期：2020/1/15
     */
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }





    /**
     * TODO  获取拼接的参数
     *
     * @作者： zhouchaoxi
     * @日期：2018/9/8
     */
    public static HashMap<String, Object> strToMap(String str) {
        HashMap<String, Object> map = new HashMap<>();
        String[] strList = str.split("&");
        for (String temp : strList) {
            String[] t = temp.split("=");
            map.put(t[0], t[1]);
        }
        System.out.println(map.toString());
        return map;
    }

    /**
     * TODO  获取当前日期格式化
     *
     * @作者： zhouchaoxi
     * @日期：2018/10/29
     */
    public static String getDay() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(new Date());
    }

    /**
     * TODO  日期加上指定天数 返回date
     *
     * @作者： zhouchaoxi
     * @日期：2018/8/28
     */
    public static Date addDate(Date date, long day) {
        long time = date.getTime(); // 得到指定日期的毫秒数
        day = day * 24 * 60 * 60 * 1000; // 要加上的天数转换成毫秒数
        time += day; // 相加得到新的毫秒数
        return new Date(time); // 将毫秒数转换成日期
    }

    /**
     * TODO 当前时间加上指定天数
     *
     * @作者： zhouchaoxi
     * @日期：2018/8/28
     */
    public static Date addDay(long day) {
        long time = new Date().getTime(); // 得到指定日期的毫秒数
        day = day * 24 * 60 * 60 * 1000; // 要加上的天数转换成毫秒数
        time += day; // 相加得到新的毫秒数
        return new Date(time); // 将毫秒数转换成日期
    }

    /**
     * TODO 当前时间加上指定天数
     *
     * @作者： zhouchaoxi
     * @日期：2018/8/28
     */
    public static String addDate(long day) {
        long time = new Date().getTime(); // 得到指定日期的毫秒数
        day = day * 24 * 60 * 60 * 1000; // 要加上的天数转换成毫秒数
        time += day; // 相加得到新的毫秒数
        return sdfTime.format(new Date(time)); // 将毫秒数转换成日期
    }

    /**
     * DecimalFormat转换最简便
     */
    public static double formatDouble(double f) {
        return new BigDecimal(f).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static boolean createFolder(String folderPath) {
        boolean ret = true;
        try {
            File myFilePath = new File(folderPath);
            if (!myFilePath.exists() && !myFilePath.isDirectory()) {
                ret = myFilePath.mkdirs();
                if (!ret) {
                    System.out.println("创建文件夹出错");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            ret = false;
        }
        return ret;
    }

    /**
     * @param o
     * @return
     * @描述: 空值判断
     * @作者: zhouChaoXi
     * @时间: 2018年6月8日
     */
    public static boolean isNull(Object o) {
        if (o == null || o.equals("") || o == "" || o == "null" || o.equals("null") || o.equals("undefined")) {
            return true;
        }
        if (o != null) {
            return o.toString().replaceAll("\\s*", "").length() == 0;
        }
        return true;
    }

    public static boolean isNotNull(Object o) {
        return o != null && !o.equals("") && o != "" && o != "null" && !o.equals("null") && !o.equals("undefined") && o.toString().replaceAll("\\s*", "").length() != 0;
    }

    public static double null2Double(Object s) {
        double v = 0.0D;
        if (s != null) {
            try {
                v = Double.parseDouble(null2String(s));
            } catch (Exception localException) {
            }
        }
        return v;
    }

    /**
     * 获取服务器访问路径
     *  * @Author: zhouchaoxi
     *  * @Date: Created in 2018/6/24 18:45
     *  * @Modified By: 
     *  
     */
    public static String getRequestUrl(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }
    public static String getRequestDomain(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
    }
    public static String getServerName(HttpServletRequest request) {
//        return  "https://" + request.getServerName();
        return  "http://192.168.131.104:8062/";
    }

    /**
     * 去掉String的空格
     *  * @Author: zhouchaoxi
     *  * @Date: Created in 2018/6/24 18:42
     *  * @Modified By: 
     *  
     */
    public static String null2String(Object s) {
        return s == null ? "" : s.toString().trim();
    }

    /**
     * 获取服务器名称
     *  * @Author: zhouchaoxi
     *  * @Date: Created in 2018/6/24 18:42
     *  * @Modified By: 
     *  
     */
    public static String generic_domain(HttpServletRequest request) {
        String system_domain = "localhost";
        String serverName = request.getServerName();
        if (isIp(serverName)) {
            system_domain = serverName;
        } else {
            system_domain = serverName.substring(serverName.indexOf(".") + 1);
        }
        return system_domain;
    }

    /**
     * 是否为IP
     *  * @Author: zhouchaoxi
     *  * @Date: Created in 2018/6/24 18:41
     *  * @Modified By: 
     *  
     */
    public static boolean isIp(String IP) {
        boolean b = false;
        IP = trimSpaces(IP);
        if (IP.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
            String[] s = IP.split("\\.");
            if ((Integer.parseInt(s[0]) < 255) && (Integer.parseInt(s[1]) < 255) && (Integer.parseInt(s[2]) < 255) && (Integer.parseInt(s[3]) < 255)) {
                b = true;
            }
        }
        return b;
    }

    /**
     * 去掉IP地址的空格
     *  * @Author: zhouchaoxi
     *  * @Date: Created in 2018/6/24 18:40
     *  * @Modified By: 
     *  
     */
    public static String trimSpaces(String IP) {
        while (IP.startsWith(" ")) {
            IP = IP.substring(1).trim();
        }
        while (IP.endsWith(" ")) {
            IP = IP.substring(0, IP.length() - 1).trim();
        }
        return IP;
    }

    /**
     * 获取项目路径
     *  * @Author: zhouchaoxi
     *  * @Date: Created in 2018/6/24 18:39
     *  * @Modified By: 
     *  
     */
    public static String getURL(HttpServletRequest request) {
        String contextPath = request.getContextPath().equals("/") ? "" : request.getContextPath();
        String url = "http://" + request.getServerName();
        if (null2Int(Integer.valueOf(request.getServerPort())) != 80) {
            url = url + ":" + request.getServerPort() + contextPath;
        } else {
            url = url + contextPath;
        }
        return url;
    }

    public static boolean null2Boolean(Object s) {
        boolean v = false;
        if (isNotNull(s)) {
            try {
                v = Boolean.parseBoolean(s.toString());
            } catch (Exception localException) {
            }
        }
        return v;
    }

    /**
     * 转为int
     *  * @Author: zhouchaoxi
     *  * @Date: Created in 2018/6/24 18:37
     *  * @Modified By: 
     *  
     */
    public static int null2Int(Object s) {
        int v = 0;
        if (isNotNull(s)) {
            try {
                v = Double.valueOf(s.toString()).intValue();
            } catch (Exception localException) {
            }
        }
        return v;
    }

    /**
     * 转为float
     *  * @Author: zhouchaoxi
     *  * @Date: Created in 2018/6/24 18:37
     *  * @Modified By: 
     *  
     */
    public static float null2Float(Object s) {
        float v = 0.0F;
        if (isNotNull(s)) {
            try {
                v = Float.parseFloat(s.toString());
            } catch (Exception localException) {
            }
        }
        return v;
    }

    public static Long null2Long(Object s) {
        long v = 0;
        if (isNotNull(s)) {
            try {
                v = Long.parseLong(s.toString());
            } catch (Exception localException) {
            }
        }
        return v;
    }

    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if ((ip == null) || (ip.length() == 0) || ("unknown".equalsIgnoreCase(ip))) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if ((ip == null) || (ip.length() == 0) || ("unknown".equalsIgnoreCase(ip))) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if ((ip == null) || (ip.length() == 0) || ("unknown".equalsIgnoreCase(ip))) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public static final String randomInt(int length) {
        if (length < 1) {
            return null;
        }
        Random randGen = new Random();
        char[] numbersAndLetters = "0123456789".toCharArray();
        char[] randBuffer = new char[length];
        for (int i = 0; i < randBuffer.length; i++) {
            randBuffer[i] = numbersAndLetters[randGen.nextInt(10)];
        }
        return new String(randBuffer);
    }

    public static final String randomString(int length) {
        if (length < 1) {
            return null;
        }
        Random randGen = new Random();
        char[] numbersAndLetters = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        char[] randBuffer = new char[length];
        for (int i = 0; i < randBuffer.length; i++) {
            randBuffer[i] = numbersAndLetters[randGen.nextInt(numbersAndLetters.length)];
        }
        return new String(randBuffer);
    }

    /**
     * 第一位转小写
     *  * @Author: zhouchaoxi
     *  * @Date: Created in 2018/6/24 18:35
     *  * @Modified By: 
     *  
     */
    public static String first2low(String str) {
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    /**
     * 第一位转大写
     *  * @Author: zhouchaoxi
     *  * @Date: Created in 2018/6/24 18:35
     *  * @Modified By: 
     *  
     */
    public static String first2upper(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * 字符串转数组
     *  * @Author: zhouchaoxi
     *  * @Date: Created in 2018/6/24 18:35
     *  * @Modified By: 
     *  
     */
    public static List<String> str2list(String s) throws IOException {
        List list = new ArrayList();
        if ((s != null) && (!s.equals(""))) {
            StringReader fr = new StringReader(s);
            BufferedReader br = new BufferedReader(fr);
            String aline = "";
            while ((aline = br.readLine()) != null) {
                list.add(aline);
            }
        }
        return list;
    }

    public static Date formatDate(String s) {
        Date d = null;
        try {
            d = dateFormat.parse(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return d;
    }


    public static Date formatDate(String s, String format) {
        Date d = null;
        try {
            SimpleDateFormat dFormat = new SimpleDateFormat(format);
            d = dFormat.parse(s);
        } catch (Exception localException) {
        }
        return d;
    }

    public static String formatTime(String format, Object v) {
        if (v == null) {
            return null;
        }
        if (v.equals("")) {
            return "";
        }
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(v);
    }

    /**
     * 验证码获取
     *  * @Author: zhouchaoxi
     *  * @Date: Created in 2018/6/24 18:31
     *  * @Modified By: 
     *  
     */
    public static int getNumber() {
        int max = 999999;
        int min = 100000;
        Random random = new Random();
        int s = random.nextInt(max) % (max - min + 1) + min;
        return s;
    }

    /**
     * 获取订单号
     *  * @Author: zhouchaoxi
     *  * @Date: Created in 2018/6/24 18:31
     *  * @Modified By: 
     *  
     */
    public static String getOrder() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return df.format(new Date());
    }

    /**
     * TODO   获取订单号
     *
     * @作者: zhouChaoXi
     * @时间: 2018/11/13 0:32
     */
    public static String getOrder(String head) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return head + df.format(new Date());
    }

    /**
     * 格式化秒级日期
     *  * @Author: zhouchaoxi
     *  * @Date: Created in 2018/6/24 18:32
     *  * @Modified By: 
     *  
     */
    public static String formatLongDate(Object v) {
        if ((v == null) || (v.equals(""))) {
            return "";
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(v);
    }

    /**
     * 格式化秒级日期
     *  * @Author: zhouchaoxi
     *  * @Date: Created in 2018/6/24 18:32
     *  * @Modified By: 
     *  
     */
    public static String formatLongDate(Date v) {
        if ((v == null) || (v.equals(""))) {
            return "";
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(v);
    }

    /**
     * 格式化秒级日期
     *  * @Author: zhouchaoxi
     *  * @Date: Created in 2018/6/24 18:32
     *  * @Modified By: 
     *  
     */
    public static String formatLongDate() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(new Date());
    }

    /**
     * TODO   字符串转时间
     *
     * @作者: zhouChaoXi
     * @时间: 2018/11/13
     */
    public static Date parseLongDate(String date) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return df.parse(date);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * TODO   格式化天级日期
     *
     * @作者: zhouChaoXi
     * @时间: 2018/11/13 0:34
     */
    public static String formatShortDate(Object v) {
        if (v == null) {
            return null;
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(v);
    }

    /**
     * TODO   地址解密
     *
     * @作者: zhouChaoXi
     * @时间: 2018/11/13 0:34
     */
    public static String decode(String s) {
        String ret = s;
        try {
            ret = URLDecoder.decode(s.trim(), "UTF-8");
        } catch (Exception localException) {
        }
        return ret;
    }

    /**
     * TODO   地址加密
     *
     * @作者: zhouChaoXi
     * @时间: 2018/11/13 0:34
     */
    public static String encode(String s) {
        String ret = s;
        try {
            ret = URLEncoder.encode(s.trim(), "UTF-8");
        } catch (Exception localException) {
        }
        return ret;
    }

    public static String convert(String str, String coding) {
        String newStr = "";
        if (str != null) {
            try {
                newStr = new String(str.getBytes(StandardCharsets.ISO_8859_1), coding);
            } catch (Exception e) {
                return newStr;
            }
        }
        return newStr;
    }

    /**
     * TODO  获取服务器中该项目的真实路径
     *
     * @作者： zhouchaoxi
     * @日期：2018/10/22
     */
    public static String getRealPath(HttpServletRequest request) {
        return request.getSession().getServletContext().getRealPath("/");
    }


    public static boolean isImg(String extend) {
        boolean ret = false;
        List<String> list = new ArrayList<String>();
        list.add("jpg");
        list.add("jpeg");
        list.add("bmp");
        list.add("gif");
        list.add("png");
        list.add("tif");
        for (String s : list) {
            if (s.equals(extend)) {
                ret = true;
            }
        }
        return ret;
    }


    public static int getMonthSpace(String date1, String date2) throws ParseException {

        int result = 0;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();

        c1.setTime(sdf.parse(date1));
        c2.setTime(sdf.parse(date2));

        result = c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH);

        return result == 0 ? 1 : Math.abs(result);

    }

    /**
     * @描述: TODO    获取异常信息
     * @作者: zhouChaoXi
     * @时间: 2018/8/28 0:38
     */
    public static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        try {
            throwable.printStackTrace(pw);
            return sw.toString();
        } finally {
            pw.close();
        }
    }

    public static double subtract(Object a, Object b) {
        double ret = 0.0D;
        BigDecimal e = new BigDecimal(null2Double(a));
        BigDecimal f = new BigDecimal(null2Double(b));
        ret = e.subtract(f).doubleValue();
        DecimalFormat df = new DecimalFormat("0.00");
        return Double.valueOf(df.format(ret)).doubleValue();
    }

    public static double add(Object a, Object b) {
        double ret = 0.0D;
        BigDecimal e = new BigDecimal(null2Double(a));
        BigDecimal f = new BigDecimal(null2Double(b));
        ret = e.add(f).doubleValue();
        DecimalFormat df = new DecimalFormat("0.00");
        return Double.valueOf(df.format(ret)).doubleValue();
    }

    public static double mul(Object a, Object b) {
        BigDecimal e = new BigDecimal(null2Double(a));
        BigDecimal f = new BigDecimal(null2Double(b));
        double ret = e.multiply(f).doubleValue();
        DecimalFormat df = new DecimalFormat("0.00");
        return Double.valueOf(df.format(ret)).doubleValue();
    }

    public static double formatMoney(Object money) {
        DecimalFormat df = new DecimalFormat("0.00");
        return Double.valueOf(df.format(money)).doubleValue();
    }

    /**
     * 随机字符串生成,用来做线下核销校验的
     *
     * @param length
     * @return
     */
    public static String getRandomString(int length) {
        // 判断参数是否为空
        if (length == 0) {
            length = 8;
        }
        char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();
        StringBuilder result = new StringBuilder();
        Random random = new Random();
        for (int x = 0; x < length; ++x) {
            result.append(chars[random.nextInt(chars.length)]);
        }
        return result.toString();
    }

    /**
     * 日期时间，GMT 格式字符串 (RFC 1123)
     *
     * @return
     */
    public static String getRfc1123Time() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(calendar.getTime());
    }

    /**
     * hmac-sha1 算法
     *
     * @param data 要加密的数据
     * @param key  秘钥，就是又拍云的操作员密码
     * @return
     * @throws SignatureException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static byte[] hashHmac(String data, String key) throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
        final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
        Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
        mac.init(signingKey);
        return mac.doFinal(data.getBytes());
    }

    /**
     * TODO   list打包成sql in 查询
     *
     * @作者: zhouChaoXi
     * @时间: 2018/11/14 22:50
     */
    public static <T> String listToString(List<T> lists) {
        StringBuffer buffer = new StringBuffer();
        for (Object string : lists) {
            if (CommUtil.isNull(buffer)) {
                buffer.append("'" + string + "'");
            } else {
                buffer.append(",").append("'" + string + "'");
            }
        }
        return buffer.toString();
    }

    /**
     * TODO   list打包成sql in 查询
     *
     * @作者: zhouChaoXi
     * @时间: 2018/11/14 22:50
     */
    public static <T> String listToLongString(List<T> lists) {
        StringBuffer buffer = new StringBuffer();
        for (Object string : lists) {
            if (isNotNull(string)) {
                if (CommUtil.isNull(buffer)) {
                    buffer.append(CommUtil.null2Long(string));
                } else {
                    buffer.append(",").append(CommUtil.null2Long(string));
                }
            }
        }
        return buffer.toString();
    }

    /**
     * 随机红包算法,类似红包分配,max = max - peopleNum*0.01,保证不会一次分完所有钱
     *
     * @param balance 剩余的钱
     * @param num     剩余的数量
     * @return
     */
    public static BigDecimal getRandomRedPackage(BigDecimal balance, int num) {
        // remainSize 剩余的红包数量
        // remainMoney 剩余的钱
        if (num == 1) {
            return balance;
        }
        Random r = new Random();
        BigDecimal random = BigDecimal.valueOf(r.nextDouble());
        BigDecimal min = BigDecimal.valueOf(0.01); //
        // 最大值不大于红包平均数的两倍
        BigDecimal max = balance.divide(BigDecimal.valueOf(num),2,BigDecimal.ROUND_HALF_EVEN).multiply(BigDecimal.valueOf(2));
        BigDecimal money = max.multiply(random);
        // 如果money <= min ,直接返回min
        if (money.compareTo(min) == 0 && money.compareTo(min) < 0) {
            money = min;
        }
        // 直接截取小数点前两位
        return money.setScale(2, BigDecimal.ROUND_DOWN);
    }

    /**
     * 把文件转为byte流
     *
     * @param file 文件
     * @return
     */
    public static byte[] fileToByte(File file) {
        ByteArrayOutputStream out = null;
        try {
            FileInputStream in = new FileInputStream(file);
            out = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int i = 0;
            while ((i = in.read(b)) != -1) {

                out.write(b, 0, b.length);
            }
            out.close();
            in.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        byte[] s = out.toByteArray();
        return s;
    }
}
