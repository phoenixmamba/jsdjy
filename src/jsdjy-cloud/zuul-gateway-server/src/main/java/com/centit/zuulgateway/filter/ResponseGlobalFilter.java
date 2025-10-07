package com.centit.zuulgateway.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.centit.zuulgateway.dao.TLmOperlogDao;
import com.centit.zuulgateway.dao.TStLoginDao;
import com.centit.zuulgateway.feign.LogstatisticsServerFeignClient;
import com.centit.zuulgateway.po.TLmOperlog;
import com.centit.zuulgateway.po.TStLogin;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR;

/**
 * @Author JCccc
 * @Description 拦截返回数据, 修改返回数据
 * @Date 2021/8/16 19:22
 */

@Component
public class ResponseGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(ResponseGlobalFilter.class);

    @Resource
    private LogstatisticsServerFeignClient logstatisticsServerFeignClient;

    @Resource
    private TStLoginDao tStLoginDao;
    @Resource
    private TLmOperlogDao tLmOperlogDao;

    @Value("${operlogSwitchOn}")
    private boolean operlogSwitchOn;

    @Override
    public int getOrder() {
        // -1 is response write filter, must be called before that
        return -2;
    }

    private static Joiner joiner = Joiner.on("");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponse originalResponse = exchange.getResponse();
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();
        ServerHttpResponseDecorator response = new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (getStatusCode().equals(HttpStatus.OK) && body instanceof Flux) {
                    // 获取ContentType，判断是否返回JSON格式数据
                    String originalResponseContentType = exchange.getAttribute(ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR);
                    if (StringUtils.isNotBlank(originalResponseContentType) && originalResponseContentType.contains("application/json")) {
                        Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                        //（返回数据内如果字符串过大，默认会切割）解决返回体分段传输
                        return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                            List<String> list = Lists.newArrayList();
//                            dataBuffers.forEach(dataBuffer -> {
//
//                                try {
//                                    byte[] content = new byte[dataBuffer.readableByteCount()];
//                                    dataBuffer.read(content);
//                                    DataBufferUtils.release(dataBuffer);
//                                    list.add(new String(content, "utf-8"));
//                                } catch (Exception e) {
//                                    log.info("加载Response字节流异常，失败原因：{}", Throwables.getStackTraceAsString(e));
//                                }
//                            });
//                            String responseData = joiner.join(list);

                            //DefaultDataBufferFactory join 乱码的问题解决
                            DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
                            DataBuffer join = dataBufferFactory.join(dataBuffers);
                            // probably should reuse buffers
                            byte[] content = new byte[join.readableByteCount()];
                            join.read(content);
                            //释放掉内存
                            DataBufferUtils.release(join);
                            String responseData = new String(content, StandardCharsets.UTF_8);
                            System.out.println("======="+responseData);
                            JSONObject logJson = new JSONObject();
                            logJson.put("retinfo", responseData);

                            String serverPath="";
                            String userinfo = exchange.getRequest().getHeaders().getFirst("userinfo")==null?"":exchange.getRequest().getHeaders().getFirst("userinfo");
                            LinkedHashSet<URI> attr = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR);
                            Iterator iterator = attr.iterator();
                            if (iterator.hasNext()) {
                                URI originalUri = (URI) iterator.next();
                                serverPath=originalUri.getPath();
                            }
                            if(org.apache.commons.lang.StringUtils.isNotBlank(userinfo)){
                                try{
                                    JSONObject js = JSONObject.parseObject(userinfo);
                                    for (String key : js.keySet()) {
                                        if (key.toLowerCase().equals("userid") || key.equals("username") || key.equals("deptcode")
                                                || key.equals("identcode") || key.equals("versionid") || key.equals("devicetype")
                                                || key.equals("osversion") || key.equals("logtype") || key.equals("brand")) {
                                            logJson.put(key, js.get(key));
                                        }
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                            }

                            logJson.put("logip", exchange.getRequest().getRemoteAddress().getHostString());
                            logJson.put("reqmethod", exchange.getRequest().getMethodValue());
                            logJson.put("serverpath", serverPath);

                            if (exchange.getRequest().getMethodValue().equals("GET")) {
                                MultiValueMap<String, String> multiValueMap= exchange.getRequest().getQueryParams();
                                StringBuilder params = new StringBuilder("?");
                                for(String key:multiValueMap.keySet()){
                                    String valueStr=multiValueMap.getFirst(key);
                                    params.append(key);
                                    params.append("=");
                                    params.append(valueStr);
                                    params.append("&");
                                }
                                if (params.length() > 0) {
                                    params.delete(params.length() - 1, params.length());
                                }
                                logJson.put("reqinfo", params);
                            } else if (exchange.getRequest().getMethodValue().equals("POST")){
                                String reqinfo = (String) exchange.getAttributes().get("POST_BODY");
                                logJson.put("reqinfo", reqinfo);
                            }

                            String[] attrs = serverPath.split("/");
                            if (attrs.length >= 3) {
                                logJson.put("servername", attrs[1]);
                                logJson.put("serverclass", attrs[2]);
                                logJson.put("servermethod", attrs[3]);
                            }
                            TLmOperlog tLmOperlog = JSON.parseObject(logJson.toJSONString(), TLmOperlog.class);
//                            tLmOperlog.setId(UUID.randomUUID().toString().substring(0, 32).replaceAll("-", ""));

                            if (null != tLmOperlog.getLogtype()) {
                                if (tLmOperlog.getLogtype().equals("1")) {
                                    tLmOperlog.setLoginfo("移动端");
                                    //TODO 替换成移动端必调的banner接口
                                    if (tLmOperlog.getServerclass().equals("home")) {
                                        TStLogin tStLogin = new TStLogin();
                                        tStLogin.setUserid(tLmOperlog.getUserid());
                                        tStLogin.setTerminalType(tLmOperlog.getDevicetype());
                                        tStLogin.setTerminalNumber(tLmOperlog.getIdentcode());
                                        tStLogin.setTerminalSystemVersion(tLmOperlog.getOsversion());
                                        tStLogin.setAppVersion(tLmOperlog.getVersionid());

                                        String terminalBrand = logJson.get("brand") == null ? "" : logJson.getString("brand").toLowerCase();
                                        String terminalStr = "";
                                        tStLogin.setTerminalBrand(terminalBrand);

                                        if (terminalBrand.contains("oppo")) {
                                            terminalStr = "OPPO";
                                        } else if (terminalBrand.contains("vivo")) {
                                            terminalStr = "vivo";
                                        } else if (terminalBrand.contains("iphone")) {
                                            terminalStr = "苹果";
                                        } else if (terminalBrand.contains("huawei")) {
                                            terminalStr = "华为";
                                        } else if (terminalBrand.contains("honor")) {
                                            terminalStr = "荣耀";
                                        } else if (terminalBrand.contains("xiaomi")) {
                                            terminalStr = "小米";
                                        } else if (terminalBrand.contains("redmi")) {
                                            terminalStr = "红米";
                                        } else if (terminalBrand.contains("meizu")) {
                                            terminalStr = "魅族";
                                        } else if (terminalBrand.contains("samsung")) {
                                            terminalStr = "三星";
                                        } else if (terminalBrand.contains("coolpad")) {
                                            terminalStr = "酷派";
                                        } else if (terminalBrand.contains("zte")) {
                                            terminalStr = "中兴";
                                        } else if (terminalBrand.contains("letv")) {
                                            terminalStr = "乐视";
                                        } else if (terminalBrand.contains("lenovo")) {
                                            terminalStr = "联想";
                                        } else if (terminalBrand.contains("sony")) {
                                            terminalStr = "索尼";
                                        } else if (terminalBrand.contains("lg")) {
                                            terminalStr = "LG";
                                        } else {
                                            terminalStr = "其他";
                                        }

                                        tStLogin.setTerminalBrandvalue(terminalStr);
                                        if(operlogSwitchOn)
                                            tStLoginDao.insert(tStLogin);
                                    }
                                } else if (tLmOperlog.getLogtype().equals("2")) {
                                    tLmOperlog.setLoginfo("管理平台");
                                }
                            }

                            try {
                                if(operlogSwitchOn)
                                    tLmOperlogDao.insert(tLmOperlog);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            byte[] uppedContent = new String(responseData.getBytes(), Charset.forName("UTF-8")).getBytes();
                            originalResponse.getHeaders().setContentLength(uppedContent.length);
                            return bufferFactory.wrap(uppedContent);
                        }));
                    }
                }
                return super.writeWith(body);
            }

            @Override
            public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
                return writeWith(Flux.from(body).flatMapSequential(p -> p));
            }
        };
        return chain.filter(exchange.mutate().response(response).build());
    }


}
