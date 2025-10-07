package com.centit.zuulgateway.utils;


import com.centit.zuulgateway.po.PageData;
import com.centit.zuulgateway.utils.gson.GsonUtil;
import net.sf.json.JSONObject;

import java.io.*;
import java.net.URLDecoder;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InterfaceUtil {

    public static int serverversion = 4;
    private static long lastClickTime;

    /**
     * 判空
     *
     * @author hzh 2015-06-07 20:40
     */
    public static boolean isEmpty(Object value) {
        if (value == null) {
            return true;
        }
        return "".equals(value.toString()) || "null".equals(value.toString());
    }

    /**
     * 浮点数显示格式(默认保留小数点后面2位)
     */
    public static Double doubleFormat2Double(Double value) {
        return Double.parseDouble(doubleFormat(value, "#0.00"));
    }

    /**
     * 浮点数显示格式(默认保留小数点后面2位)
     */
    public static String doubleFormat(Double value, String format) {
        return new DecimalFormat(format).format(value);
    }

    /**
     * 接口加密
     */
    public static String interfaceMD5(String value) {
        return MD5.md5((MD5.md5(value) + "2200820a3e35ed74648e775cf3164e9d"));
    }

    /*
     * 检查接口传参是否正常 return -5接口加密错误 -6接口版本过低-3服务器内部错误
     */
    public static int chekParam(String value, String key) {
        int code = 1;
        if ("7bangkeji".equals(key)) {
            return code;
        }
        try {
            PageData pd = null;
            if (InterfaceUtil.interfaceMD5(value).equals(key)) {// 参数是否被修改
//                String dataStr = URLDecoder.decode(value, "utf-8");
                pd = InterfaceUtil.toPageData(URLDecoder.decode(value, "utf-8"));
                if (!chekTime(pd.get("datetoken").toString())) {
                    code = -5;
                }
            } else {
                code = -6;
            }
        } catch (Exception e) {
            e.printStackTrace();
            code = -6;
        }

        return code;
    }

    public static boolean chekTime(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.HOUR_OF_DAY, -1);
            if (c.getTimeInMillis() >= dateFormat.parse(date).getTime()) {
                return false;
            }
            c = Calendar.getInstance();
            c.add(Calendar.HOUR_OF_DAY, 1);
            if (c.getTimeInMillis() <= dateFormat.parse(date).getTime()) {
                return false;
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        return true;
    }

    /**
     * 用户密码加密
     */
    public static String passwordMD5(String value) {
        return MD5.md5(MD5.md5(value + "hello")
                + "b81e6e98db8da25e20268227ebc50775");
    }

    /**
     * 验证码加密
     */
    public static String smsMD5(String value) {
        return MD5.md5(MD5.md5(value + "sms")
                + "5fds56a6fd8as35fd3as8fd5asf1das");
    }

    /**
     * 将json格式的字符串解析成Map对象 <li>
     * json格式：{"name":"shopping","retries":"3fff","testname"
     * :"ddd","testretries":"fffffffff"}
     */
    public static HashMap<String, Object> toHashMap(Object object) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        // 将json字符串转换成jsonObject
        JSONObject jsonObject = JSONObject.fromObject(object);
        Iterator it = jsonObject.keys();
        // 遍历jsonObject数据，添加到Map对象
        while (it.hasNext()) {
            String key = String.valueOf(it.next());
            Object value = jsonObject.get(key);
            map.put(key, value);
        }
        return map;
    }

    /**
     * 将json格式的字符串解析成Map对象 <li>
     * json格式：{"name":"shopping","retries":"3fff","testname"
     * :"ddd","testretries":"fffffffff"}
     */
    public static PageData toPageData(String object) {
        return GsonUtil.jsonToPageData(object);
    }

    /**
     * 修改密码验证加密
     */
    public static String updPwdMD5(String username, String password) {
        return MD5.md5(MD5.md5(username + "hello lol" + MD5.md5(password))
                + "hello android");
    }

    /*
     * 判断是否为整数
     *
     * @param str 传入的字符串
     *
     * @return 是整数返回true,否则返回false
     */
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    /**
     * 获得系统的最新时间
     *
     * @return
     */
    public static String getNewTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(System.currentTimeMillis());
    }

    /**
     * 获得指定毫秒数时间
     *
     * @return
     */
    public static String getTimeByLong(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(time);
    }

    // 得到byte[]
    public static byte[] File2byte(String filePath) {
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    // 得到File
    public static void byte2File(byte[] buf, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            if (!dir.exists() && dir.isDirectory()) {
                dir.mkdirs();
            }
            file = new File(filePath + File.separator + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(buf);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public synchronized static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * @param strTime 时间毫秒数
     * @param format  需要返回的时间格式
     */
    public static String long2Date(String strTime, String format) {
        return new SimpleDateFormat(format).format(new Date(Long
                .parseLong(strTime)));
    }

    /**
     * 判断是否是数字[0-9]
     *
     * @param content 需要判断的内容
     */
    public static boolean isNumber(String content) {
        Pattern p = Pattern.compile("[0-9]*");
        Matcher m = p.matcher(content);
        return m.matches();
    }

    /**
     * 判断是否是字母[a-zA-Z]
     *
     * @param content 需要判断的内容
     */
    public static boolean isChar(String content) {
        Pattern p = Pattern.compile("[a-zA-Z]");
        Matcher m = p.matcher(content);
        return m.matches();
    }

    /**
     * 判断是否是中文[\u4e00-\u9fa5]
     *
     * @param content 需要判断的内容
     */
    public static boolean isChinese(String content) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(content);
        return m.matches();
    }

    /**
     * 保留前五位显示数字的数值进位
     */
    public static String[] numberCarry(long number) {
        String[] result = new String[2];
        if (number / 100000000 > 0) {
            result[0] = (number / 10000) + "";
            result[1] = "万人";
        } else if (number / 10000000 > 0) {
            result[0] = (number / 1000) + "";
            result[1] = "千人";
        } else if (number / 1000000 > 0) {
            result[0] = (number / 100) + "";
            result[1] = "百人";
        } else if (number / 100000 > 0) {
            result[0] = (number / 10) + "";
            result[1] = "十人";
        } else {
            result[0] = number + "";
            result[1] = "人";
        }

        return result;
    }

    /**
     * 随机数五位小数点
     */
    public static double nextDouble(final double min, final double max)
            throws Exception {
        DecimalFormat df = new DecimalFormat("######0.00000");
        if (max < min) {
            throw new Exception("min < max");
        }
        if (min == max) {
            return min;
        }
        double s = min + ((max - min) * new Random().nextDouble());
        return Double.parseDouble(df.format(s));
    }

    /**
     * 随机数2位小数点
     *
     * @param min
     * @param max
     * @return
     * @throws Exception
     */
    public static double nextDoubleTwo(final double min, final double max)
            throws Exception {
        DecimalFormat df = new DecimalFormat("######0.00");
        if (max < min) {
            throw new Exception("min < max");
        }
        if (min == max) {
            return min;
        }
        double s = min + ((max - min) * new Random().nextDouble());
        return Double.parseDouble(df.format(s));
    }

    /**
     * list打包
     *
     * @param lists
     * @return
     * @author zhouchaoxi
     * @date 2018年4月24日
     */
    public static String listToString(List<Object> lists) {
        StringBuffer buffer = new StringBuffer();
        for (Object string : lists) {
            if (isEmpty(buffer)) {
                buffer.append("'" + string + "'");
            } else {
                buffer.append(",").append("'" + string + "'");
            }
        }
        return buffer.toString();
    }

    public static void main(String[] args){
        System.out.println(interfaceMD5("%7B%22datetoken%22%3A%222021-12-20+10%3A19%3A45%22%2C%22userId%22%3A%22137280%22%7D"));
    }
}
