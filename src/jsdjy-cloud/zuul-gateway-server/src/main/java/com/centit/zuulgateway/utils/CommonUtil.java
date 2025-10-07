package com.centit.zuulgateway.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>通用工具类<p>
 * @version 1.0
 * @author cui_jian
 * @date 2018年3月22日
 */
public class CommonUtil {
    private static final Log log = LogFactory.getLog(CommonUtil.class);


    /**
     * 获取客户端请求的当前网络ip
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request){
        String ipAddress = request.getHeader("X-Real-IP");//先从nginx自定义配置获取
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("x-forwarded-for");
        }
        if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }

        if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if("127.0.0.1".equals(ipAddress) || "0:0:0:0:0:0:0:1".equals(ipAddress)){
                //根据网卡取本机配置的IP
                InetAddress inet=null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
//                    e.printStackTrace();
                    log.error(e);
                }
                if(inet != null){
                    ipAddress= inet.getHostAddress();
                }
            }
        }
        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if(ipAddress!=null && ipAddress.length()>15){ //"***.***.***.***".length() = 15
            if(ipAddress.indexOf(",")>0){
                ipAddress = ipAddress.substring(0,ipAddress.indexOf(","));
            }
        }
        return ipAddress;
    }

    /**
     * Java生成doc的特殊字符处理
     * <对应&lt;
     * >对应&gt;
     * &对应&amp;
     * word中的“<”和“>”在word编码中实际上为：“&lt;”和“&gt;”需要替换掉，否则word报错打不开
     * 例如：办公厅<关于对真抓实干的通知>的工作，在编码中为：办公厅&lt;关于对真抓实干的通知&gt;的工作
     * @return
     */
    public static String replaceWordStr(String str){
        if(StringUtils.isNotBlank(str)){
//			str = str.replace("<", "&lt;").replace(">", "&gt;").replace("&", "&amp;");
            str = str.replace("<", "＜").replace(">", "＞").replace("&", "＆");
        }
        return str;
    }

    /**
     * 将字符串中所有的非标准字符（双字节字符）替换成两个标准字符（**，或其他的也可以）。这样就可以直接例用length方法获得字符串的字节长度了
     * @param str
     * @return
     */
    public static  int getWordCountRegex(String str){
        str = str.replaceAll("[^\\x00-\\xff]", "**");
        int length = str.length();
        return length;
    }

    /**
     * 判断字符串是否为数字
     * @param str
     * @return
     */
    public static boolean isNumeric(String str)	{
        for (int i = 0; i < str.length(); i++){
            if (!Character.isDigit(str.charAt(i))){
                return false;
            }
        }
        return true;
    }

    /**
     * 数字转中文(支持最大长度13位)
     * @param str
     * @return
     */
    public static String numToChinese(String str) {
        String[] s1 = { "零", "一", "二", "三", "四", "五", "六", "七", "八", "九" };
        String[] s2 = { "十", "百", "千", "万", "十", "百", "千", "亿", "十", "百", "千" };
        String result = "";
        int n = str.length();
        for (int i = 0; i < n; i++) {
            int num = str.charAt(i) - '0';
            if (i != n - 1 && num != 0) {
                result += s1[num] + s2[n - 2 - i];
            } else {
                result += s1[num];
            }
        }
        while(result.endsWith("零") && result.length()>1){
            result = result.substring(0, result.length()-1);
        }
        while(result.startsWith("一") && result.length()>1 && "十".equals(result.substring(1,2))){
            result = result.substring(1, result.length());
        }
        return result;
    }

    /**
     * 将时间转换为时间戳
     * @param datetime yyyy-MM-dd HH:mm:ss
     * @return 时间戳
     */
    public static String dateToStamp(String datetime){
        String res = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = simpleDateFormat.parse(datetime);
            long ts = date.getTime();
            res = String.valueOf(ts);
        } catch (ParseException e) {
//			e.printStackTrace();
            log.error(e);
        }
        return res;
    }

    /**
     * 将时间戳转换为时间
     * @param timestamp 时间戳
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String stampToDate(String timestamp){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(timestamp);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }


    /**
     * Date格式的时间增加、减少 N/天/小时/分钟/秒/毫秒
     * @pattern 传入日期格式的字符串格式，例如：yyyy-MM-dd HH:mm:ss；yyyy-MM-dd HH:mm，yyyy-MM-dd
     * @param dateStr Date格式的字符串，例如：2018-07-11 23:50:00
     * @param time  需要增加/减少的时间转换为毫秒，例如：增加30分钟为30*60*1000L；减少30分钟为-30*60*1000L；
     * @return
     */
    public static String dateStrAddSub(String pattern, String dateStr, Long time){
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = sdf.parse(dateStr, pos);

        Date dateRet = new Date(strtodate .getTime() + time);  //增加/减少N毫秒后的时间
        return sdf.format(dateRet);
    }

    /**
     * 对比两个list,返回两个list中：合并后集合、合并去重后的集合、相同的集合、不同的集合、list1中不在list2中的集合、list2不在list1中的集合
     * @param list1  集合1
     * @param list2  集合2
     * @param cmpType 比较类型返回：a：合并后集合；b：合并去重后的集合；c：相同的集合；d：不同的集合；e：list1中不在list2中的集合；f：list2不在list1中的集合；
     * @return List 返回处理后的集合
     * 例如：
     * list1  ：[1, 2, 3, 3, 4, 5, 6]
     * list2  ：[3, 4, 4, 7, 8]
     * a：合并后集合，listAll：[1, 2, 3, 3, 4, 5, 6, 3, 4, 4, 7, 8]
     * b：合并去重后的集合；[1, 2, 3, 4, 5, 6, 7, 8]
     * c：相同的集合；[3, 4]
     * d：不同的集合；[1, 2, 5, 6, 7, 8]
     * e：list1中不在list2中的集合；[1, 2, 5, 6]
     * f：list2不在list1中的集合；[7, 8]
     */
    public static List<String> compareList(List<String> list1, List<String> list2, String cmpType){
        List<String> retList = new ArrayList<String>();
        List<String> listAll = new ArrayList<String>();

        listAll.addAll(list1);
        listAll.addAll(list2);

        if("a".equals(cmpType)){
            //合并后的集合
            retList = listAll;
        }
        if("b".equals(cmpType)){
            //合并去重后的集合
            retList = listAll.stream().distinct().collect(Collectors.toList());
        }
        if("c".equals(cmpType) || "d".equals(cmpType) || "e".equals(cmpType) || "f".equals(cmpType)){
            //相同的集合
            List<String> listSameTemp = new ArrayList<String>();
            list1.stream().forEach(a -> {
                if(list2.contains(a))
                    listSameTemp.add(a);
            });
            retList = listSameTemp.stream().distinct().collect(Collectors.toList());

            //不同的集合
            if("d".equals(cmpType)){
                List<String> listTemp = new ArrayList<>(listAll);
                listTemp.removeAll(retList);
                retList = listTemp;
            }
            //list1中不在list2中的集合
            if("e".equals(cmpType)){
                List<String> listTemp = new ArrayList<>(list1);
                listTemp.removeAll(retList);
                retList = listTemp;
            }
            //list2中不在list1中的集合
            if("f".equals(cmpType)){
                List<String> listTemp = new ArrayList<>(list2);
                listTemp.removeAll(retList);
                retList = listTemp;
            }
        }
        return retList;
    }

    /**
     * 生成6位随机数
     */
    public static int randomSexNum(){
        int num = (int)((Math.random()*9+1)*100000);
        return num;
    }

    /**
     * 获取指定字符串出现的次数
     *
     * @param srcText 源字符串
     * @param findText 要查找的字符串
     * @return
     */
    public static int appearNumber(String srcText, String findText) {
        int count = 0;
        Pattern p = Pattern.compile(findText);
        Matcher m = p.matcher(srcText);
        while (m.find()) {
            count++;
        }
        return count;
    }

    /**
     * (休眠)倒计时
     * @param seconds 倒计时的秒数
     * @param content 倒计时备注（例如：执行获取部门失败，等待60秒后继续获取！）
     */
    public static void countDown(int seconds, String content) {
        System.out.println(content + "|||||||||" + "倒计时" + seconds + "秒,倒计时开始:");
//		System.err.println(content + "|||||||||" + "倒计时" + seconds + "秒,倒计时开始:");
        int i = seconds;
        while (i > 0) {
//			System.err.println(i);
            System.out.println(content + "|||||||||" + "倒计时第：" + i + "秒");
//			System.err.println(content + "|||||||||" + "倒计时第：" + i + "秒");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
//			e.printStackTrace();
                log.error(e);
                Thread.currentThread().interrupt();
            }
            i--;
        }
//		System.err.println(i);
        System.out.println(content + "|||||||||" + "倒计时第：" + i + "秒");
//		System.err.println(content + "|||||||||" + "倒计时第：" + i + "秒");
        System.out.println(content + "|||||||||" + "倒计时结束");
//		System.err.println(content + "|||||||||" + "倒计时结束");
    }





}
