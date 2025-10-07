package com.centit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


/**
 * @author cui_jian
 */
@SpringBootApplication
@EnableFeignClients
public class AdminServerApplication {

    public static void main(String[] args) {

        SpringApplication.run(AdminServerApplication.class, args);
    }

}
