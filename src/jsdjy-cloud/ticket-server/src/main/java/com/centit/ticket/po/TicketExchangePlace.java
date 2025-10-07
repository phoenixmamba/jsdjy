package com.centit.ticket.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-04-08
 **/
@Data
public class TicketExchangePlace implements Serializable {


    private String eventId;

    /**
     * 取票地点id，由于麦座没有返回，暂定用longitude+latitude
     */
    private String placeId;

    /**
     * 取票点详细地址
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
     * 取票点类型，1=人工服务，2=自助取票机；可既是1又是2
     */
    private String placeTypeList;


}
