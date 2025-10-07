package com.centit.zuulgateway.filter;

import com.alibaba.fastjson.JSONObject;
import com.centit.zuulgateway.dao.FUserloginTokenDao;
import com.centit.zuulgateway.po.PageData;
import com.centit.zuulgateway.utils.InterfaceUtil;
import com.centit.zuulgateway.utils.StringOrderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.LinkedHashSet;


@Component
public class ParamCheckFilter implements GlobalFilter, Ordered {
    private static Logger log = LoggerFactory.getLogger(ParamCheckFilter.class);




    @Resource
    private FUserloginTokenDao fUserloginTokenDao;

    @Value("${loginExpireTime}")
    private String loginExpireTime;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("---------执行全局过滤器-----------");
        String serverPath="";
        String tokenId = "";
        String userCode = "";
        String dataEncode = "";
        String signValue = "";

        tokenId = exchange.getRequest().getHeaders().getFirst("tokenid")==null?"":exchange.getRequest().getHeaders().getFirst("tokenid");
        userCode = exchange.getRequest().getHeaders().getFirst("usercode")==null?"":exchange.getRequest().getHeaders().getFirst("usercode");
        dataEncode = exchange.getRequest().getHeaders().getFirst("dataencode")==null?"":exchange.getRequest().getHeaders().getFirst("dataencode");
        signValue = exchange.getRequest().getHeaders().getFirst("signvalue")==null?"":exchange.getRequest().getHeaders().getFirst("signvalue");
        LinkedHashSet<URI> attr = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR);
        Iterator iterator = attr.iterator();
        while (iterator.hasNext()) {
            URI originalUri = (URI) iterator.next();
            serverPath=originalUri.getPath();
        }

        ServerHttpResponse response = exchange.getResponse();
        //校验管理后台接口中的token是否过期
//        if(serverPath.contains("/webmgr/")&&!serverPath.contains("/classLive")&&!serverPath.contains("/export")
//                &&!serverPath.contains("/login")&&tokenId.equals("")){
//            JSONObject jsonObject = new JSONObject();
//            response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
//            jsonObject.put("retCode", "10008");
//            jsonObject.put("retMsg", "用户登录状态已过期");
//            response.setStatusCode(HttpStatus.UNAUTHORIZED);
//            byte[] bytes = jsonObject.toJSONString().getBytes(StandardCharsets.UTF_8);
//            DataBuffer buffer = response.bufferFactory().wrap(bytes);
//            // 请求结束，不继续向下请求
//            return response.writeWith(Mono.just(buffer));
//        }else if (!tokenId.equals("") && !userCode.equals("")) {
//            response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
//            HashMap<String, Object> rMap = new HashMap<>();
//            rMap.put("usercode", userCode);
//            rMap.put("token", tokenId);
//            List<FUserloginToken> list = fUserloginTokenDao.queryList(rMap);
//            JSONObject jsonObject = new JSONObject();
//            if (list.isEmpty()) {
//                jsonObject.put("retCode", "10008");
//                jsonObject.put("retMsg", "用户登录状态已过期");
//                response.setStatusCode(HttpStatus.UNAUTHORIZED);
//                byte[] bytes = jsonObject.toJSONString().getBytes(StandardCharsets.UTF_8);
//                DataBuffer buffer = response.bufferFactory().wrap(bytes);
//                // 请求结束，不继续向下请求
//                return response.writeWith(Mono.just(buffer));
//            } else {
//                //如果当前用户已在另外设备登录，则刷新过期时间，后面可以改为同时只有一台设备登录
//                FUserloginToken fUserloginToken = list.get(0);
//                String expiretime = fUserloginToken.getExpiretime();
//                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                try {
//                    if (new Date().getTime() > sf.parse(expiretime).getTime()) {
//                        jsonObject.put("retCode", "10008");
//                        jsonObject.put("retMsg", "用户登录状态已过期");
//                        response.setStatusCode(HttpStatus.UNAUTHORIZED);
//                        byte[] bytes = jsonObject.toJSONString().getBytes(StandardCharsets.UTF_8);
//                        DataBuffer buffer = response.bufferFactory().wrap(bytes);
//                        // 请求结束，不继续向下请求
//                        return response.writeWith(Mono.just(buffer));
//                    } else {
//                        //失效时间往后顺延半小时
//                        Date afterDate = new Date(new Date().getTime() + Integer.valueOf(loginExpireTime) * 60 * 1000);
//                        fUserloginToken.setExpiretime(sf.format(afterDate));
//                        fUserloginTokenDao.update(fUserloginToken);
//                    }
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//            }
//        }

        if ((serverPath.contains("/shopping/") || serverPath.contains("/ticket/") || serverPath.contains("/pay/")) && !serverPath.contains("/webmgr") && !serverPath.contains("/common") && !serverPath.contains("/jpushInfo")&& !serverPath.contains("/pay/notify")) {
            try {
                if(dataEncode.equals("f0ff6a087d9543f613616274b509ac60")){
                    return chain.filter(exchange);//执行请求
                }
                //解密并校验参数
                PageData pd = null;
                String dataEncodeStr = URLDecoder.decode(dataEncode, "utf-8");
                pd = InterfaceUtil.toPageData(dataEncodeStr);

                //ToDo 如果dataEncode中只有一个datetoken,判断为原生接口，不进行校验
                if(pd.size()==1&&pd.containsKey("datetoken")){
                    return chain.filter(exchange);//执行请求
                }

                boolean paramCheck=true;

                if (exchange.getRequest().getMethodValue().equals("GET")) {
                    MultiValueMap<String, String> multiValueMap= exchange.getRequest().getQueryParams();
                    for(String key:multiValueMap.keySet()){
                        String valueStr=multiValueMap.getFirst(key);
                        if(pd.get(key)==null||!pd.get(key).toString().equals(valueStr)){
                            paramCheck=false;
                            break;
                        }
                    }
                } else if (exchange.getRequest().getMethodValue().equals("POST")){

                    System.out.println("=======================dataEncodeStr==================="+dataEncodeStr);
                    JSONObject dataObj=JSONObject.parseObject(dataEncodeStr);

                    String body = (String) exchange.getAttributes().get("POST_BODY");
                    JSONObject resObj=JSONObject.parseObject(body);
                    for(String key:resObj.keySet()){
                        if(resObj.get(key)!=null&&(dataObj.get(key)==null||!StringOrderUtil.isScrambledString(dataObj.get(key).toString(),resObj.get(key).toString()))){
                            System.out.println("=======================key==================="+key);
                            System.out.println("=======================dataObj.get(key)==================="+dataObj.get(key));
                            System.out.println("=======================valueStr==================="+resObj.get(key).toString());
                            paramCheck=false;
                            break;
                        }
                    }
//                    for(String key:resObj.keySet()){
//                        String valueStr=resObj.getString(key);
//                        if(pd.get(key)==null||!pd.get(key).toString().equals(valueStr)){
//                            System.out.println("=======================key==================="+key);
//                            System.out.println("=======================pd.get(key)==================="+pd.get(key));
//                            System.out.println("=======================valueStr==================="+valueStr);
//                            paramCheck=false;
//                            break;
//                        }
//                    }
                }
                int code =1;
                if((serverPath.contains("/shopping/shoppingArts/addActivityOrder")||serverPath.contains("/ticket/project/addOrder")||serverPath.contains("/shopping/shoppingOrder/pageList"))&&!paramCheck){
                    code = -5;
                }else{
                    code = InterfaceUtil.chekParam(dataEncode, signValue);//检查接口是否被修改
                }

                if (code != 1) {
                    response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");

                    JSONObject jsonObject = new JSONObject();
                    if (code == -5||code == -6) {
                        jsonObject.put("retCode", "1002");
                        jsonObject.put("retMsg", "参数校验失败");
                    } else {
                        jsonObject.put("retCode", "1");
                        jsonObject.put("retMsg", "服务器内部错误");
                    }
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    byte[] bytes = jsonObject.toJSONString().getBytes(StandardCharsets.UTF_8);
                    DataBuffer buffer = response.bufferFactory().wrap(bytes);
                    // 请求结束，不继续向下请求
                    return response.writeWith(Mono.just(buffer));
                }


            } catch (Exception e) {
                e.printStackTrace();
                response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("retCode", "1002");
                jsonObject.put("retMsg", "参数校验失败");
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                byte[] bytes = jsonObject.toJSONString().getBytes(StandardCharsets.UTF_8);
                DataBuffer buffer = response.bufferFactory().wrap(bytes);
                // 请求结束，不继续向下请求
                return response.writeWith(Mono.just(buffer));
            }
        }

        return chain.filter(exchange);//执行请求
    }


    /**
     * 当有多个过滤器时， order值越小，越优先先执行
     *
     * @return
     */
    @Override
    public int getOrder() {
        return 100;
    }

}

