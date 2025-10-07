package com.centit.pay.biz.po;

import java.math.BigDecimal;
import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2022-03-08
 **/
@Data
public class ShoppingArtplan implements Serializable {


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

    /**
     * 积分使用限制 0不使用积分1定额积分2不限额积分
     */
    private Integer useIntegralSet;

    /**
     * 可使用积分额
     */
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
     * 是否允许取消 0:不允许；1:允许
     */
    private Integer cancelEnable;

    /**
     * 是否显示剩余数量 0：否；1：是
     */
    private Integer showLeftnum;

    /**
     * 移动端排序
     */
    private Integer appSort;

    /**
     * 活动须知
     */
    private String activityNotice;

    /**
     * 退改说明
     */
    private String returnExplain;

    private Integer leftnum;

    private String addUser;

    /**
     * 是否允许使用余额支付 0:不允许;1:可使用
     */
    private Integer useBalanceSet;

    /**
     * 是否支持会员权益 0:不支持;1:支持
     */
    private Integer useMembershipSet;

    /**
     * 核销账号
     */
    private String writeOffCount;

    /**
     * 商户手机号
     */
    private String phone;


}
