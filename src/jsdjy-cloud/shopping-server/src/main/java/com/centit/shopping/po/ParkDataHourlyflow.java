package com.centit.shopping.po;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>车场每小时的车流数<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2023-10-12
 **/
@Data
public class ParkDataHourlyflow implements Serializable {


    private String statTime;

    /**
     * 车流量
     */
    private Integer totalTraffic;

    /**
     * 进场数
     */
    private Integer inNum;

    /**
     * 出场数
     */
    private Integer outNum;

    /**
     * 异常放行数
     */
    private Integer abnormalNum;


}
