package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2022-07-19
 **/
@Data
public class TicketRedeemEvent implements Serializable {


    /**
     * 麦座场次id
     */
    private String eventId;

    /**
     * 麦座项目id
     */
    private String projectId;

    /**
     * 麦座场次名称
     */
    private String eventName;

    /**
     * 场次销售状态；2=销售中；仅返回销售中的数据
     */
    private Integer eventSaleState;

    /**
     * 场次演出开始时间，精确到分，格式：yyyy-MM-dd HH:mm
     */
    private String eventStartTime;

    /**
     * 场次演出结束时间，精确到分，格式：yyyy-MM-dd HH:mm
     */
    private String eventEndTime;

    /**
     * 取票方式list；2=身份证自助换票（换纸质票），3=取票码自助换票（换纸质票），4=二维码自助换票（换纸质票），6=二维码电子票（无纸质票），7=快递；一个场次可同时支持多种取票方式，但下单只能选择一种
     */
    private String deliveryTypeList;

    /**
     * 是否需要实名制购买；1=是，0=否
     */
    private String realNameBuyLimitBoolean;

    /**
     * 实名制购买类型；1=一单一证，2=一票一证
     */
    private Integer realNameBuyLimitType;

    /**
     * 单个证件累计限购数量；当前场次单个证件（一单一证或一票一证持票人）；0=不限购
     */
    private Integer singleCardLimitNum;

    /**
     * 场次所在场馆id；一个项目下多场次允许有多个场馆；
     */
    private String venueId;

    /**
     * 场次所在场馆名称
     */
    private String venueName;

    /**
     * 场馆座位底图url；项目为选座项目时非空，渲染选座时使用
     */
    private String venueBasemapSvgUrl;

    /**
     * 场次绑定的优惠id列表；只支持票品优惠返回
     */
    private String promotionIdList;

    /**
     * 开始销售时间
     */
    private String saleStartTime;

    /**
     * 截止销售时间
     */
    private String saleEndTime;

    private String isDelete;

    private Integer inventory;

}
