package com.centit.logstatistics.logstatisticsserver.webmgr.po;

import java.math.BigDecimal;
import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p> <p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 艺术课程 实体类
 * @Date : 2021-06-10
 **/
@Data
public class ShoppingArtclass implements Serializable {


    private String id;

    private String addTime;

    private String deleteStatus;

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

    private Integer appSort;

    private String classNotice;

    private String returnExplain;

    private String addUser;

    private Integer useBalanceSet;

    private Integer useMembershipSet;


}
