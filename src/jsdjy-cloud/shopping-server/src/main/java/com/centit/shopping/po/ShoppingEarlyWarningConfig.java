package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;

import lombok.Data;

/**
 * <p><p>
 *
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-05-31
 **/
@Data
public class ShoppingEarlyWarningConfig implements Serializable {


    /**
     * 主键
     */
    private String id;

    private String cpu;

    private String memory;

    private String disc;

    /**
     * 服务器地址
     */
    private String server;

    private String remark;


}
