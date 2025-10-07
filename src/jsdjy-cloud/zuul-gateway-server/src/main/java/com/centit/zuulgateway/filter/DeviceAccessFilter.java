package com.centit.zuulgateway.filter;

import com.alibaba.fastjson.JSONObject;
import com.centit.zuulgateway.dao.GUserinfoDao;
import com.centit.zuulgateway.po.GUserinfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;


@Component
public class DeviceAccessFilter implements GlobalFilter, Ordered {
    private static Logger log = LoggerFactory.getLogger(DeviceAccessFilter.class);




    @Resource
    private GUserinfoDao gUserinfoDao;

    @Value("${retCode998}")
    private String retCode998;
    @Value("${retMsg998}")
    private String retMsg998;
    @Value("${retCode999}")
    private String retCode999;
    @Value("${retMsg999}")
    private String retMsg999;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("---------执行全局过滤器-----------");
        boolean flag = true;
        String retCode = "";
        String retMsg = "";
        // 请求头或者请求参数中获取token
        String userinfo = exchange.getRequest().getHeaders().getFirst("userinfo");
        if(StringUtils.isNotBlank(userinfo)){
            try{
                JSONObject userJson = JSONObject.parseObject(userinfo);
                String regCellPhone = userJson.getString("regCellPhone");
                if (StringUtils.isNotBlank(regCellPhone)) {
                    //检测设备是否遗失，若遗失/停用，则拦截，并返回数据
                    try {
                        GUserinfo info=new GUserinfo();
                        info.setRegCellPhone(regCellPhone);
                        GUserinfo gUserinfo=gUserinfoDao.queryDetail(info);
                        if(gUserinfo.getIsValid().equals("F")){
                            flag = false;
                            retCode = retCode999;
                            retMsg = "此用户已停用";
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        if(!flag){
            ServerHttpResponse response = exchange.getResponse();
            response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
            // 401 用户没有访问权限
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("retCode", retCode);
            jsonObject.put("retMsg", retMsg);
            //3.3作JSON转换
            byte[] bytes = jsonObject.toJSONString().getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            // 请求结束，不继续向下请求
            return response.writeWith(Mono.just(buffer));
        }else {
            return chain.filter(exchange);//执行请求
        }


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

