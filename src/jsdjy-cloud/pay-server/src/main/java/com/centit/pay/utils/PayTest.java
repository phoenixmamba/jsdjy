//package com.centit.pay.utils;
//
//import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
//import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
//import com.wechat.pay.contrib.apache.httpclient.util.RsaCryptoUtil;
//import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.utils.URIBuilder;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.util.EntityUtils;
//import org.springframework.util.Base64Utils;
//
//import javax.crypto.BadPaddingException;
//import javax.crypto.Cipher;
//import javax.crypto.IllegalBlockSizeException;
//import javax.crypto.NoSuchPaddingException;
//import javax.crypto.spec.GCMParameterSpec;
//import javax.crypto.spec.SecretKeySpec;
//import java.io.FileInputStream;
//import java.nio.charset.StandardCharsets;
//import java.security.*;
//
///**
// * @author ：cui_jian
// * @version ：1.0
// * @date ：Created in 2021/1/8 10:45
// * @description ：
// */
//public class PayTest {
//
//
//
//
//
////    public static void main(String[] args) throws Exception {
////        HttpPost httpPost = new HttpPost("https://api.mch.weixin.qq.com/v3/pay/transactions/out-trade-no/1217752501201407033233368018/close");
////        httpPost.addHeader("Accept", "application/json");
////        httpPost.addHeader("Content-type","application/json; charset=utf-8");
////
////        ByteArrayOutputStream bos = new ByteArrayOutputStream();
////        ObjectMapper objectMapper = new ObjectMapper();
////
////        ObjectNode rootNode = objectMapper.createObjectNode();
////        rootNode.put("mchid","1607523004");
////
////        objectMapper.writeValue(bos, rootNode);
////
////        PrivateKey merchantPrivateKey = PemUtil.loadPrivateKey(
////                new ByteArrayInputStream("4d328f68771a209dc255c06b9d85c7c3".getBytes("utf-8")));
////        WechatPayHttpClientBuilder builder = WechatPayHttpClientBuilder.create()
////                .withMerchant("1607523004", merchantSerialNumber, merchantPrivateKey)
////                .withWechatPay(wechatPayCertificates);
////// ... 接下来，你仍然可以通过builder设置各种参数，来配置你的HttpClient
////
////// 通过WechatPayHttpClientBuilder构造的HttpClient，会自动的处理签名和验签
////        CloseableHttpClient httpClient = builder.build();
////
////        httpPost.setEntity(new StringEntity(bos.toString("UTF-8"), "UTF-8"));
////        CloseableHttpResponse response = httpClient.execute(httpPost);
////
////        String bodyAsString = EntityUtils.toString(response.getEntity());
////        System.out.println(bodyAsString);
////    }
//
//    public static void main(String[] args) throws Exception {
//        URIBuilder uriBuilder = new URIBuilder("https://api.mch.weixin.qq.com/v3/certificates");
//        HttpGet httpGet = new HttpGet(uriBuilder.build());
//        httpGet.addHeader("Accept", "application/json");
//
//        PrivateKey merchantPrivateKey = PemUtil.loadPrivateKey(
//                new FileInputStream("D:\\MyWork/apiclient_key.pem"));
//        WechatPayHttpClientBuilder builder = WechatPayHttpClientBuilder.create()
//                .withMerchant("1607523004", "35D8DB4191B21AF5A0B15E755854F5F10C054007", merchantPrivateKey)
//                .withValidator(response -> true);
//// ... 接下来，你仍然可以通过builder设置各种参数，来配置你的HttpClient
//
//// 通过WechatPayHttpClientBuilder构造的HttpClient，会自动的处理签名和验签
//        CloseableHttpClient httpClient = builder.build();
//
//
//        CloseableHttpResponse response = httpClient.execute(httpGet);
//
//        String bodyAsString = EntityUtils.toString(response.getEntity());
//
//        String ciphertext = RsaCryptoUtil.decryptOAEP(, merchantPrivateKey);
//        System.out.println(ciphertext);
//    }
//
//    public String decryptResponseBody(String apiV3Key,String associatedData, String nonce, String ciphertext) {
//        try {
//            Cipher cipher = null;
//            try {
//                cipher = Cipher.getInstance("AES/GCM/NoPadding");
//            } catch (NoSuchAlgorithmException e) {
//                e.printStackTrace();
//            } catch (NoSuchPaddingException e) {
//                e.printStackTrace();
//            }
//
//            SecretKeySpec key = new SecretKeySpec(apiV3Key.getBytes(StandardCharsets.UTF_8), "AES");
//            GCMParameterSpec spec = new GCMParameterSpec(128, nonce.getBytes(StandardCharsets.UTF_8));
//
//            try {
//                cipher.init(Cipher.DECRYPT_MODE, key, spec);
//            } catch (InvalidKeyException e) {
//                e.printStackTrace();
//            } catch (InvalidAlgorithmParameterException e) {
//                e.printStackTrace();
//            }
//            cipher.updateAAD(associatedData.getBytes(StandardCharsets.UTF_8));
//
//            byte[] bytes;
//            try {
//                bytes = cipher.doFinal(Base64Utils.decodeFromString(ciphertext));
//            } catch (GeneralSecurityException | IllegalBlockSizeException | BadPaddingException e) {
//                throw new IllegalArgumentException(e);
//            }
//            return new String(bytes, StandardCharsets.UTF_8);
//        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
//            throw new IllegalStateException(e);
//        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
//            throw new IllegalArgumentException(e);
//        }
//    }
//}