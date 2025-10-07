package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;

import lombok.Data;

/**
 * <p>商城轮播<p>
 *
 * @version : 1.0
 * @Author : su_yl
 * @Description : 实体类
 * @Date : 2021-04-19
 **/
@Data
public class ShoppingCarouselStore implements Serializable {


    private Integer id;

    /**
     * 主图
     */
    private String mainPhoto;

    /**
     * 1:开启 0:关闭
     */
    private String status;

    /**
     * 1:URL
     * 2:文创商品
     * 3:积分兑换商品
     * 4:艺术活动
     * 5:艺术课程
     * 6:院线电影
     */
    private String type;

    /**
     * url或者演出id
     */
    private String value;

    /**
     * 描述
     */
    private String desc;

    /**
     * 排序
     */
    private Integer sort;


}
