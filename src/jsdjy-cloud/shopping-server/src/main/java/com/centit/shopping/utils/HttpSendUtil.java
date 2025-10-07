package com.centit.shopping.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.centit.shopping.common.enums.Const;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>HTTP 请求工具类<p>
 * @version 1.0
 * @author cui_jian
 * @date 2018年11月7日
 */
public class HttpSendUtil {
    private static PoolingHttpClientConnectionManager connMgr;
    private static RequestConfig requestConfig;
    private static final int MAX_TIMEOUT = 600000;

    static {
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", createSSLConnSocketFactory())
                .build();
        // 设置连接池
        connMgr = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        // 设置连接池大小
        connMgr.setMaxTotal(100);
        connMgr.setDefaultMaxPerRoute(connMgr.getMaxTotal());
        RequestConfig.Builder configBuilder = RequestConfig.custom();
        // 设置连接超时
        configBuilder.setConnectTimeout(MAX_TIMEOUT);
        // 设置读取超时
        configBuilder.setSocketTimeout(MAX_TIMEOUT);
        // 设置从连接池获取连接实例的超时
        configBuilder.setConnectionRequestTimeout(MAX_TIMEOUT);
        // 在提交请求之前 测试连接是否可用
        configBuilder.setStaleConnectionCheckEnabled(true);
        requestConfig = configBuilder.build();
    }

    /**
     * 发送 GET 请求（HTTP）
     *
     * @param url
     * @return
     */
    public static JSONObject doGet(String url) {
        CloseableHttpClient httpclient = HttpClients.custom().setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();
        HttpGet httpGet = new HttpGet(url);
        HttpResponse response = null;
        String out = null;
        JSONObject jsonObject = null;//接收结果
        try {
            response = httpclient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) { //请求出错
                System.out.println(EntityUtils.toString(response.getEntity(), "utf-8") + url); //打印错误信息
                return null;
            }
            out = EntityUtils.toString(response.getEntity(), "utf-8");
            jsonObject = JSONObject.parseObject(out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (httpGet != null) {
                httpGet.releaseConnection();
            }
        }
        return jsonObject;
    }

    /**
     * 发送 GET 请求（HTTP）
     *
     * @param url
     * @return
     */
    public static JSONArray doGetForArray(String url) {
        CloseableHttpClient httpclient = HttpClients.custom().setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();
        HttpGet httpGet = new HttpGet(url);
        HttpResponse response = null;
        String out = null;
        JSONArray jsonArray = new JSONArray();//接收结果
        try {
            response = httpclient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) { //请求出错
                System.out.println(EntityUtils.toString(response.getEntity(), "utf-8") + url); //打印错误信息
                return null;
            }
            out = EntityUtils.toString(response.getEntity(), "utf-8");
            jsonArray = JSONArray.parseArray(out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (httpGet != null) {
                httpGet.releaseConnection();
            }
        }
        return jsonArray;
    }

    public static String doGetForString(String url) {
        CloseableHttpClient httpclient = HttpClients.custom().setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();
        HttpGet httpGet = new HttpGet(url);
        HttpResponse response = null;
        String out = null;
//        JSONObject jsonObject = null;//接收结果
        try {
            response = httpclient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) { //请求出错
                System.out.println(EntityUtils.toString(response.getEntity(), "utf-8") + url); //打印错误信息
                return null;
            }
            out = EntityUtils.toString(response.getEntity(), "utf-8");
//            jsonObject = JSONObject.parseObject(out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (httpGet != null) {
                httpGet.releaseConnection();
            }
        }
        return out;
    }

    /**
     * 发送 POST 请求
     *
     * @param url API接口URL
     * @param params 参数map
     * @return
     */
    public static JSONObject doPost(String url, String params) {
        CloseableHttpClient httpclient = HttpClients.custom().setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;
        String out = null;
        JSONObject jsonObject = null;//接收结果
        try {
            httpPost.setConfig(requestConfig);
            StringEntity stringEntity = new StringEntity(params, "UTF-8");
            stringEntity.setContentEncoding("UTF-8");
            stringEntity.setContentType("application/json");
            httpPost.setEntity(stringEntity);
            httpPost.addHeader("version", "1.0.0");

            response = httpclient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                System.out.println(EntityUtils.toString(response.getEntity(), "utf-8") + "url: " +url + "params: " + params); //打印错误信息
                return null;
            }
            out = EntityUtils.toString(response.getEntity(), "utf-8");
            jsonObject = JSONObject.parseObject(out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpPost != null) {
                httpPost.releaseConnection();
            }
        }
        return jsonObject;
    }

    /**
     * 创建SSL安全连接
     *
     * @return
     */
    private static SSLConnectionSocketFactory createSSLConnSocketFactory() {
        SSLConnectionSocketFactory sslsf = null;
        try {
            SSLContext ctx = SSLContext.getInstance("SSL");
            X509TrustManager tm = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            ctx.init(null, new TrustManager[]{tm}, null);
            sslsf = new SSLConnectionSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return sslsf;
    }

    public static String post(JSONObject json, String url){
        String result = "";
        HttpPost post = new HttpPost(url);
        try{
            CloseableHttpClient httpClient = HttpClients.createDefault();

            post.setHeader("Content-Type","application/json;charset=utf-8");
            post.addHeader("Authorization", "Basic YWRtaW46");
            StringEntity postingString = new StringEntity(json.toString(),"utf-8");
            post.setEntity(postingString);
            HttpResponse response = httpClient.execute(post);

            InputStream in = response.getEntity().getContent();
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));
            StringBuilder strber= new StringBuilder();
            String line = null;
            while((line = br.readLine())!=null){
                strber.append(line+'\n');
            }
            br.close();
            in.close();
            result = strber.toString();
            if(response.getStatusLine().getStatusCode()!=HttpStatus.SC_OK){
                result = "服务器异常";
            }
        } catch (Exception e){
            System.out.println("请求异常");
            throw new RuntimeException(e);
        } finally{
            post.abort();
        }
        return result;
    }

    public static String post(String url, Map<String, String> maps) {
        // 第一步，创建HttpPost对象
        HttpPost httpPost = new HttpPost(url);
        // 设置HTTP POST请求参数必须用NameValuePair对象
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        if (params != null) {
            Set<String> keys = maps.keySet();
            for (String key : keys) {
                params.add(new BasicNameValuePair(key, maps.get(key)));
            }
        }

//        params.add(new BasicNameValuePair("action", "downloadAndroidApp"));
//        params.add(new BasicNameValuePair("packageId",
//                "89dcb664-50a7-4bf2-aeed-49c08af6a58a"));
//        params.add(new BasicNameValuePair("uuid", "test_ok1"));

        HttpResponse httpResponse = null;
        try {
            // 设置httpPost请求参数
            httpPost.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));
            httpResponse = HttpClientBuilder.create().build().execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                // 第三步，使用getEntity方法活得返回结果
                String result = EntityUtils.toString(httpResponse.getEntity());
                return result;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject doPost3(String url, Map<String, String> params) {
        String respStr = null;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;
        String out = null;
        JSONObject jsonObject = null;//接收结果
        try {
            httpPost.setConfig(requestConfig);
            //存储参数的BasicNameValuePair集合
            ArrayList list = new ArrayList();

            //获得参数的迭代器
            Iterator iterator = params.entrySet().iterator();

            //迭代参数
            while(iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                //BasicNameValuePair通常是用来封装post请求中的参数名称和值；
                list.add(new BasicNameValuePair((String) entry.getKey(), (String) entry.getValue()));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(list, "UTF-8"));

//            httpPost.setHeader("Authorization", token);
//            httpPost.setHeader("Authorization", "Bearer " + token);

            //发送post请求
            response = httpclient.execute(httpPost);
            //如果请求不为空并且响应code为200
            if (response != null && response.getStatusLine().getStatusCode() == 200) {
                //获取请求模型返回的实体
//                    HttpEntity responseEntity = response.getEntity();
                out = EntityUtils.toString(response.getEntity(), "utf-8");
                jsonObject = JSONObject.parseObject(out);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                httpclient.close();
                if (response != null) {
                    response.close();
                }
            } catch (IOException var22) {
                var22.printStackTrace();
            }
        }
        return jsonObject;
    }

    public static JSONObject sendMsg(String url,Map<String, String> params){
        String reqtime = StringUtil.nowTimeMilesString();
        try {
            JSONObject retJson = doPost3(url, params);
            String rettime = StringUtil.nowTimeMilesString();
            CommonUtil.addThirdLog(0, Const.THIRDLOG_TYPE_SMS,"发送短信","POST", url,
                    reqtime,params.toString(),rettime,retJson.toString());
            return retJson;
        } catch (Exception e) {
            String rettime = StringUtil.nowTimeMilesString();
            CommonUtil.addThirdLog(1,Const.THIRDLOG_TYPE_SMS,"发送短信","POST", url,
                    reqtime,params.toString().toString(),rettime,e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) throws IOException {
        JSONObject reqJson =new JSONObject();
        String serviceCode = "getHourlyFlowReport";
        reqJson.put("appId", "10665");
        reqJson.put("parkId", "7624");
        reqJson.put("serviceCode", serviceCode);
        String ts = String.valueOf(System.currentTimeMillis());
        //时间戳
        reqJson.put("ts", ts);
        //每次请求的唯一标识
        reqJson.put("reqId", ts);
        SimpleDateFormat sdf2= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -25);
        Date newTime = calendar.getTime();
        reqJson.put("startTime", sdf2.format(newTime));
        Date day=new Date();
        reqJson.put("endTime", sdf2.format(day));
        reqJson.put("pageIndex", 1);
        reqJson.put("pageSize", 50);
        String signValue = SignUtil.paramsSign(reqJson, "3e36665eb77142bcb370bc67247f10a4");
        reqJson.put("key", signValue);
        try {
            JSONObject res = HttpSendUtil.doPost("http://kp-open.keytop.cn/unite-api/api/wec/GetHourlyFlowReport", reqJson.toJSONString());
            System.out.println(res); //打印错误信息
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

    public static String sendGet(String url) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url ;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            return "OK";
        } catch (Exception e) {

            e.printStackTrace();
            return "NO";
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public static String doGetForTest(String url) {
        CloseableHttpClient httpclient = HttpClients.custom().setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();
        HttpGet httpGet = new HttpGet(url);
        HttpResponse response = null;
        String out = null;
        try {
            response = httpclient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) { //请求出错
                return "NO";
            }else{
                return "OK";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "NO";
        } finally {
            if (httpGet != null) {
                httpGet.releaseConnection();
            }
        }
    }

    public static String upload() throws IOException {

        String url = "D:\\city.txt";
        File file = new File(url);
        FileReader reader = new FileReader(file);
        int fileLength = (int)file.length();
        char[] chars = new char[fileLength];
        reader.read(chars);
        String area [] = String.valueOf(chars).split("]");
        List<Map<String,Object>> list = new ArrayList<>();
        Object object[] = null;
        String everyRecords [] = null;
        String str = "";
        int areaLength = area.length;
        int m = 0;
        for(int j = 0; j < areaLength;j++){
            str = (String)area[j];
            str = str.substring(2,str.length());
            everyRecords = str.split("'");
            if(everyRecords.length == 8){
                Map<String,Object> map = new HashMap<>();
                if(everyRecords[1].length() == 6){
                    map.put("num1",everyRecords[1]);
                    map.put("num2",everyRecords[3]);
                    map.put("num3",everyRecords[5].equals("1")?"0086":everyRecords[5]);
                    map.put("num4",everyRecords[7]);

                    String city = everyRecords[3];
                    if(city.length()>2&&city.endsWith("市")){
                        city =city.substring(0,city.length()-1);
                    }
                    System.out.println(city);

                    list.add(map);
                }else if(everyRecords[1].length() < 6){
                    //去除其他国家的记录
                    m++;
                }
                //添加中国区域代码
                if(m == 1){
                    map.put("num1","0086");
                    map.put("num2","中国");
                    map.put("num3","0");
                    map.put("num4","zhongguo");

                    list.add(map);
                }
            }
        }
        if(list != null){
//            System.out.println(list);
        }else{
            System.out.println("List is null");
        }
        return "NICE";
    }
}