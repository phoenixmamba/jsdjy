package com.centit.shopping.po;

import java.math.BigDecimal;
import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-04-21
 **/
@Data
public class TOnOndemand implements Serializable {


    /**
     * 点播专题主键id
     */
    private String id;

    /**
     * 标题
     */
    private String title;

    /**
     * 简介
     */
    private String content;

    /**
     * 类型id
     */
    private String classid;

    /**
     * 创建人id
     */
    private String ondemanduserid;

    /**
     * 创建人名称
     */
    private String ondemandusername;

    /**
     * 封面url
     */
    private String coverfilepath;

    /**
     * 创建时间
     */
    private String createtime;

    /**
     * 是否专题 0否 1是
     */
    private String isproject;

    /**
     * 已读未读状态 0未读 1已读
     */
    private String isread;

    /**
     * 删除标识 0未删除 1已删除
     */
    private String isdel;

    /**
     * 修改时间
     */
    private String updatetime;

    /**
     * 审核状态 0未审核 1已审核
     */
    private String isaudit;

    /**
     * 是否置顶 0否 1是
     */
    private String istop;

    /**
     * 是否免费 0是 1否
     */
    private String isfree;

    /**
     * 类型名称
     */
    private String classname;

    /**
     * 原价
     */
    private BigDecimal originPrice;

    /**
     * 现价
     */
    private BigDecimal currentPrice;

    /**
     * 是否支持积分抵扣 0:不支持;1:限额积分抵扣；2：不限额积分抵扣
     */
    private Integer useIntegralSet;

    /**
     *  限额积分抵扣值
     */
    private Integer useIntegralValue;

    /**
     *  是否支持余额支付 0:不支持;1:支持
     */
    private Integer useBalanceSet;

    /**
     * 是否支持会员权益 0:不支持;1:支持
     */
    private Integer useMembershipSet;

    /**
     * 转码状态 0未成功 1成功
     */
    private String istranscord;

    /**
     * 0 下架 1上架
     */
    private String isshelves;

    private String classid1;

    private String classname1;


}
