package com.centit.logstatistics.logstatisticsserver.webmgr.po;

import java.math.BigDecimal;
import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p> <p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description :  实体类
 * @Date : 2021-06-10
 **/
@Data
public class TOnOndemand implements Serializable {


    private String id;

    private String title;

    private String content;

    private String classid;

    private String ondemanduserid;

    private String coverfilepath;

    private String createtime;

    private String isproject;

    private String isread;

    private String isdel;

    private String updatetime;

    private String isaudit;

    private String istop;

    private String isfree;

    private String classname;

    private BigDecimal originPrice;

    private BigDecimal currentPrice;

    private Integer useIntegralSet;

    private Integer useIntegralValue;

    private Integer useBalanceSet;

    private Integer useMembershipSet;

    private String istranscord;

    private String isshelves;

    private String classid1;

    private String classname1;

    private String rtmpstreamname;

    private String pushurl;

    private String rtmpurl;

    private String starttime;

    private String expirationtime;

    private String islive;


}
