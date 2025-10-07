package com.centit.shopping.po;

import lombok.Data;

import java.io.Serializable;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-04-08
 **/
@Data
public class TicketVenue implements Serializable {


    /**
     * 场馆id
     */
    private String venueId;

    /**
     * 场馆名称
     */
    private String venueName;

    /**
     * 场馆简介；格式：富文本
     */
    private String venueIntroduce;

    /**
     * 场馆详细地址
     */
    private String address;

    /**
     * 经度，高德地图坐标
     */
    private String longitude;

    /**
     * 纬度，高德地图坐标
     */
    private String latitude;

    /**
     * 省代码；国标6位，中国民政部行政区划代码
     */
    private Integer provinceCode;

    /**
     * 省名称
     */
    private String provinceName;

    /**
     * 	市代码；国标6位，中国民政部行政区划代码
     */
    private Integer cityCode;

    /**
     * 市名称
     */
    private String cityName;

    /**
     * 是否为大场馆，1=是，0=否；若为大场馆，项目座位购买类型为自助选座时，调用获取座位列表接口时入参“看台id”为必填，需按看台分批获取座位；避免单次获取商品数量过大；此值为场馆基础属性，一般不会改变；
     */
    private String bigVenueBoolean;


}
