package com.centit.mallserver.po;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>商品评价<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-03-16
 **/
@Data
public class ShoppingEvaluatePo implements Serializable {


    private String id;

    private String addTime;

    private String evaluateInfo;

    private String goodsSpec;

    private String evaluateGoodsId;

    private String evaluateUserId;
    private String evaluateUserName;
    private String evaluateUserPhoto;

    private String ofId;

    private Integer descriptionEvaluate;

    private Integer serviceEvaluate;


}
