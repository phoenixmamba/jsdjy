package com.centit.zuulgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

//import javax.servlet.MultipartConfigElement;

//@EnableZuulProxy
@EnableEurekaClient
@Configuration
@EnableFeignClients
@EnableHystrix
@SpringBootApplication
public class ZuulGatewayServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZuulGatewayServerApplication.class, args);
    }

    /**
     * 解决跨域
     * @return
     */
    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // 允许cookies跨域
        config.addAllowedOrigin("*");// 允许向该服务器提交请求的URI，*表示全部允许。
        config.addAllowedHeader("*");// 允许访问的头信息,*表示全部
        config.setMaxAge(18000L);// 预检请求的缓存时间（秒），即在这个时间段里，对于相同的跨域请求不会再预检了
        config.addAllowedMethod("*");// 允许提交请求的方法，*表示全部允许，也可以单独设置GET、PUT等
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

//    @Bean
//    public MultipartConfigElement multipartConfigElement() {
//        MultipartConfigFactory factory = new MultipartConfigFactory();
//        // 单个数据大小
//        factory.setMaxFileSize(DataSize.ofMegabytes(500)); // KB,MB
//        // 总上传数据大小
//        factory.setMaxRequestSize(DataSize.ofMegabytes(500));
//        return factory.createMultipartConfig();
//    }

//    @Bean
//    public ServletWebServerFactory webServerFactory() {
//        TomcatServletWebServerFactory fa = new TomcatServletWebServerFactory();
//        fa.addConnectorCustomizers((TomcatConnectorCustomizer) connector -> connector.setProperty("relaxedQueryChars", "[]{}&"));
//        return fa;
//    }

//    /**
//     * 启动自定义过滤器
//     *
//     * @return
//     */
//    @Bean
//    public DeviceAccessFilter deviceAccessFilter() {
//        return new DeviceAccessFilter();
//    }

}
