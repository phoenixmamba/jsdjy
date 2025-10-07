package com.centit.logstatistics.logstatisticsserver.webmgr.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p> <p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description :  实体类
 * @Date : 2021-06-15
 **/
@Data
public class TicketEvent implements Serializable {


    private String eventId;

    private String projectId;

    private String eventName;

    private Integer eventSaleState;

    private Date eventStartTime;

    private Date eventEndTime;

    private String deliveryTypeList;

    private String realNameBuyLimitBoolean;

    private Integer realNameBuyLimitType;

    private Integer singleCardLimitNum;

    private String venueId;

    private String venueName;

    private String venueBasemapSvgUrl;

    private String promotionIdList;


}
