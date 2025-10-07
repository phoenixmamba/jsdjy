package com.centit.scan.common.enums;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 *
 * @author:cui_jian
 */
@Component
public class Const {

    //订单状态


    public static String STORE_ID = "0";//TODO 	系统默认官方商家ID

    @PostConstruct
    public void init(){

    }

}
