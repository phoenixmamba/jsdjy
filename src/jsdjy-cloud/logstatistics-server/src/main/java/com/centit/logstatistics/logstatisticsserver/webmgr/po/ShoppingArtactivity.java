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
public class ShoppingArtactivity implements Serializable {


    private String id;

    private String addTime;

    private String deleteStatus;

    private Integer activityStatus;

    private String activityName;

    private String mainPhotoId;

    private String signupStarttime;

    private String signupEndtime;

    private String activityTime;

    private String activityLocation;

    private String activityDetails;

    private BigDecimal originPrice;

    private BigDecimal currentPrice;

    private Integer useIntegralSet;

    private Integer useIntegralValue;

    private Integer signupTotalLimit;

    private Integer signupPerLimit;

    private Integer cancelEnable;

    private Integer showLeftnum;

    private Integer appSort;

    private String activityNotice;

    private String returnExplain;

    private Integer leftnum;

    private String addUser;

    private Integer useBalanceSet;

    private Integer useMembershipSet;


}
