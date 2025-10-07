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
 * @Date : 2021-03-24
 **/
@Data
public class ShoppingArtclass implements Serializable {


    private String id;

    private String addTime;

    /**
     * 0：未删除；1：已删除
     */
    private String deleteStatus;

    /**
     * 活动状态 0：未上线；1：以上线
     */
    private Integer classStatus;

    private String className;

    private Integer classNum;

    private String classRemark;

    private String mainPhotoId;

    private String details;

    private BigDecimal originPrice;

    private BigDecimal currentPrice;

    private Integer useIntegralSet;

    private Integer useIntegralValue;

    /**
     * 移动端排序
     */
    private Integer appSort;

    /**
     * 活动须知
     */
    private String classNotice;

    /**
     * 退改说明
     */
    private String returnExplain;

    private String addUser;

    private Integer useBalanceSet;

    private Integer useMembershipSet;

    private String isOnline;

    private List<ShoppingArtclassPhoto> photos;
}
