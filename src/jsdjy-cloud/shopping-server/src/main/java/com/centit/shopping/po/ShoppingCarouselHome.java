package com.centit.shopping.po;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>首页轮播<p>
 *
 * @version : 1.0
 * @Author : su_yl
 * @Description : 实体类
 * @Date : 2021-04-19
 **/
@Data
public class ShoppingCarouselHome implements Serializable {


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
     * 1:URL 2:演出
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
