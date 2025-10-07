package com.centit.admin.shopping.po;

import lombok.Data;

import java.io.Serializable;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-02-22
 **/
@Data
public class ShoppingGoodsclass implements Serializable {

    private String id;

    private String addTime;

    private Boolean deleteStatus;

    private String className;

    private Boolean display;

    private Integer level;

    private Boolean recommend;

    private Integer sequence;

    private String goodstypeId;

    private String goodstypeName;

    private String parentId;

    private String seoDescription;

    private String seoKeywords;

    private String iconSys;

    private Integer iconType;

    private String iconAccId;

    private String storeId;

    private String place;

    private String sellingpointgroupId;

    private Boolean idleRecommend;

    private Boolean isEnd;
}
