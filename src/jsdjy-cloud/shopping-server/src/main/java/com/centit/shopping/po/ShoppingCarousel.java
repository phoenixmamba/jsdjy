package com.centit.shopping.po;

import java.io.Serializable;

import lombok.Data;

/**
 * <p><p>
 *
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-04-19
 **/
@Data
public class ShoppingCarousel implements Serializable {


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
     * 轮播类型 1:首页 2:商城
     */
    private String type;

    /**
     * 轮播分类
     * 1:URL
     * 2:演出
     * 3:文创商品
     * 4:积分兑换商品
     * 5:艺术活动
     * 6:艺术课程
     * 7:院线电影
     */
    private String category;

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
