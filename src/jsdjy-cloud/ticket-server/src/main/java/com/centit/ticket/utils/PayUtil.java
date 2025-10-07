package com.centit.ticket.utils;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;


public class PayUtil {
    public final static String DATETIME_PATTERN = "yyyyMMddHHmmss";

    public final static String TIME_STAMP_PATTERN = "yyyyMMddHHmmssSSS";

    public final static String DATE_PATTERN = "yyyyMMdd";

    public final static String TIME_PATTERN = "HHmmss";

    public final static String STANDARD_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public final static String STANDARD_DATETIME_PATTERN_HM = "yyyy-MM-dd HH:mm";

    public final static String STANDARD_DATE_PATTERN = "yyyy-MM-dd";

    public final static String STANDARD_TIME_PATTERN = "HH:mm:ss";

    public final static String STANDARD_DATETIME_PATTERN_SOLIDUS = "yyyy/MM/dd HH:mm:ss";

    public final static String STANDARD_DATETIME_PATTERN_SOLIDUS_HM = "yyyy/MM/dd HH:mm";

    public final static String STANDARD_DATE_PATTERN_SOLIDUS = "yyyy/MM/dd";

    /**
     * @param head
     * @return
     * @描述:获取订单号
     * @作者: zhouChaoXi
     * @时间: 2018年6月3日
     */
    public static String getOrderNo(String head) {
        if (CommUtil.isNull(head)) {
            head = "";
        }
        return head + formatDate(new Date(), TIME_STAMP_PATTERN);
    }

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

    public static String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        DateFormat format = new SimpleDateFormat(STANDARD_DATETIME_PATTERN);
        return format.format(date);
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url   发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
      /*      conn.setRequestProperty("accept", "*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");*/
            conn.setRequestProperty("contentType", "utf-8");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "GBK"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    /**
     * @param request
     * @return
     * @描述:获取项目路径
     * @作者: zhouChaoXi
     * @时间: 2018年6月9日
     */
    public static String getURL(HttpServletRequest request) {
        String url = "//" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        if (request.getServerPort() == 80 || request.getServerPort() == 443) {
            url = "//" + request.getServerName() + request.getContextPath();
        }
        return url;
    }

    public static String getIpAddr(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
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

    /**
     * 是否签名正确,规则是:按参数名称a-z排序,遇到空值的参数不参加签名
     *
     * @return true:正确
     * @date 2018年4月30日
     */
    public static boolean isTenpaySign(String characterEncoding,
                                       SortedMap<Object, Object> packageParams, String API_KEY) {
        StringBuffer sb = new StringBuffer();
        Set<Entry<Object, Object>> es = packageParams.entrySet();
        Iterator<Entry<Object, Object>> it = es.iterator();
        while (it.hasNext()) {
            Entry<Object, Object> entry = it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            if (!"sign".equals(k) && null != v && !"".equals(v)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + API_KEY);
        // 算出摘要
        String mysign = MD5.md5(sb.toString()).toLowerCase();
        String tenpaySign = ((String) packageParams.get("sign")).toLowerCase();
        return tenpaySign.equals(mysign);
    }

    /**
     * @return
     * @描述: 获取微信时间戳
     * @作者: zhouChaoXi
     * @时间: 2018年6月3日
     */
    public static String getWxTimeStamp() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }

    /**
     * @return
     * @描述: 获取微信时间戳
     * @作者: zhouChaoXi
     * @时间: 2018年6月3日
     */
    public static String getWxTimeStamp(String payTime) {
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            long time = dateformat.parse(payTime).getTime() / 1000;
            return String.valueOf(time);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

    }


    /**
     * 参数加密
     *
     * @param characterEncoding 编码格式
     * @param packageParams     请求参数
     * @param API_KEY
     * @return
     * @author zhouchaoxi
     * @date 2018年4月30日
     */
    public static String getSign(String characterEncoding, SortedMap<Object, Object> packageParams, String API_KEY) {
        StringBuffer sb = new StringBuffer();
        Set<Entry<Object, Object>> es = packageParams.entrySet();
        Iterator<Entry<Object, Object>> it = es.iterator();
        while (it.hasNext()) {
            Entry<Object, Object> entry = it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            if (null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + API_KEY);
        System.out.println(sb.toString());
        return MD5Util.md5(sb.toString()).toUpperCase();
    }


    /**
     * 将请求参数转换为xml格式的string
     *
     * @param parameters
     * @return
     * @author zhouchaoxi
     * @date 2018年4月30日
     */
    public static String getRequestXml(SortedMap<Object, Object> parameters) {
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        Set<Entry<Object, Object>> es = parameters.entrySet();
        Iterator<Entry<Object, Object>> it = es.iterator();
        while (it.hasNext()) {
            Entry<Object, Object> entry = it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            if ("attach".equalsIgnoreCase(k) || "body".equalsIgnoreCase(k)
                    || "sign".equalsIgnoreCase(k)) {
                sb.append("<" + k + ">" + "<![CDATA[" + v + "]]></" + k + ">");
            } else {
                sb.append("<" + k + ">" + v + "</" + k + ">");
            }
        }
        sb.append("</xml>");
        return sb.toString();
    }

    /**
     * 取出一个指定长度大小的随机正整数
     *
     * @param length 设定所取出随机数的长度。length小于11
     * @return
     * @author zhouchaoxi
     * @date 2018年4月30日
     */
    public static int buildRandom(int length) {
        int num = 1;
        double random = Math.random();
        if (random < 0.1) {
            random = random + 0.1;
        }
        for (int i = 0; i < length; i++) {
            num = num * 10;
        }
        return (int) ((random * num));
    }

    /**
     * 发送post请求
     *
     * @param urlStr
     * @param data
     * @return 返回结果
     * @author zhouchaoxi
     * @date 2018年4月30日
     */
    public static String postData(String urlStr, String data) {
        return postData(urlStr, data, null);
    }

    /**
     * 发送post请求
     *
     * @param urlStr
     * @param data
     * @return 返回结果
     * @author zhouchaoxi
     * @date 2018年4月30日
     */
    public static String postData(String urlStr, String data, String contentType) {
        int CONNECT_TIMEOUT = 5000;
        String DEFAULT_ENCODING = "UTF-8";
        BufferedReader reader = null;
        try {
            URL url = new URL(urlStr);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setReadTimeout(CONNECT_TIMEOUT);
            if (contentType != null) {
                conn.setRequestProperty("content-type", contentType);
            }
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), DEFAULT_ENCODING);
            if (data == null) {
                data = "";
            }
            writer.write(data);
            writer.flush();
            writer.close();
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), DEFAULT_ENCODING));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\r\n");
            }
            return sb.toString();
        } catch (IOException e) {
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
            }
        }
        return null;
    }

    /**
     * 解析xml,返回第一级元素键值对。
     * 如果第一级元素有子节点，则此节点的值是子节点的xml数据。
     *
     * @param strxml
     * @return
     * @throws IOException
     * @throws JDOMException
     * @author zhouchaoxi
     * @date 2018年4月30日
     */
    public static Map<String, String> doXMLParse(String strxml) throws IOException, JDOMException {
        strxml = strxml.replaceFirst("encoding=\".*\"", "encoding=\"UTF-8\"");
        if (null == strxml || "".equals(strxml)) {
            return null;
        }
        Map<String, String> m = new HashMap<String, String>();
        InputStream in = new ByteArrayInputStream(strxml.getBytes(StandardCharsets.UTF_8));
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(in);
        Element root = doc.getRootElement();
        List list = root.getChildren();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            Element e = (Element) it.next();
            String k = e.getName();
            String v = "";
            List children = e.getChildren();
            if (children.isEmpty()) {
                v = e.getTextNormalize();
            } else {
                v = getChildrenText(children);
            }
            m.put(k, v);
        }
        // 关闭流
        in.close();
        return m;
    }

    /**
     * 将map按字典排序转换为string拼接
     *
     * @param params
     * @param encode
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String createSign(Map<String, String> params, boolean encode) throws UnsupportedEncodingException {
        Set<String> keysSet = params.keySet();
        Object[] keys = keysSet.toArray();
        Arrays.sort(keys);
        StringBuffer temp = new StringBuffer();
        boolean first = true;
        for (Object key : keys) {
            if (key == null || CommUtil.isNull(params.get(key))) // 参数为空不参与签名
            {
                continue;
            }
            if (first) {
                first = false;
            } else {
                temp.append("&");
            }
            temp.append(key).append("=");
            Object value = params.get(key);
            String valueStr = "";
            if (null != value) {
                valueStr = value.toString();
            }
            if (encode) {
                temp.append(URLEncoder.encode(valueStr, "UTF-8"));
            } else {
                temp.append(valueStr);
            }
        }
        return temp.toString();
    }

    /**
     * 获取子结点的xml
     *
     * @param children
     * @return
     * @author zhouchaoxi
     * @date 2018年4月30日
     */
    @SuppressWarnings("rawtypes")
    public static String getChildrenText(List children) {
        StringBuffer sb = new StringBuffer();
        if (!children.isEmpty()) {
            Iterator it = children.iterator();
            while (it.hasNext()) {
                Element e = (Element) it.next();
                String name = e.getName();
                String value = e.getTextNormalize();
                List list = e.getChildren();
                sb.append("<" + name + ">");
                if (!list.isEmpty()) {
                    sb.append(getChildrenText(list));
                }
                sb.append(value);
                sb.append("</" + name + ">");
            }
        }

        return sb.toString();
    }

    /**
     * 给微信返回信息，通知接收情况
     *
     * @param msg
     * @param response
     * @throws IOException
     */
    public static void sendToCFT(String msg, HttpServletResponse response) {
        PrintWriter out = null;
        try {
            String strHtml = msg;
            out = response.getWriter();
            out.println(strHtml);

        } catch (Exception e) {

        } finally {
            out.flush();
            out.close();
        }
    }

    /**
     * 微信金额处理
     *
     * @return
     * @author: zhouchaoxi
     * @date: 2018-3-15
     */
    public static String getMoney(BigDecimal money) {
        return money.multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP).toString();
    }

    public static Map<String, String> getRequestParams(HttpServletRequest request) {
        Enumeration enu = request.getParameterNames();
        HashMap<String, String> map = new HashMap<>();
        while (enu.hasMoreElements()) {
            String key = (String) enu.nextElement();
            String value = request.getParameter(key);
            map.put(key, value);
        }
        return map;
    }

}
