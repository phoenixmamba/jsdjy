package com.centit.shopping.po;

import java.math.BigDecimal;
import java.util.Date;
import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-03-19
 **/
@Data
public class ShoppingArtactivity implements Serializable {


    private String id;

    private String addTime;

    /**
     * 0：未删除；1：已删除
     */
    private String deleteStatus;

    /**
     * 活动状态 0：未上线；1：以上线
     */
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

    /**
     * 报名总人数限制 0表示不限制
     */
    private Integer signupTotalLimit;

    /**
     * 单人报名次数限制 0表示不限制
     */
    private Integer signupPerLimit;

    /**
     * 是否允许取消 '0':不允许；'1':允许
     */
    private Integer cancelEnable;

    /**
     * 是否显示剩余数量 '0'：否；'1'：是
     */
    private Integer showLeftnum;

    private Integer leftnum;

    private Integer appSort;

    private String activityNotice;

    private String returnExplain;

    private String addUser;

    private Integer useBalanceSet;

    private Integer useMembershipSet;

    private String writeOffCount;

    private String phone;

    private List<ShoppingArtactivityPhoto> photos;

    private String activityInventoryDetail;
}
