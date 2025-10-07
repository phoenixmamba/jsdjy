package com.centit.shopping.po;

import java.io.Serializable;

import lombok.Data;

/**
 * <p><p>
 *
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-04-26
 **/
@Data
public class ShoppingRecommend implements Serializable {


    private String id;

    private String goodsId;

    private Integer goodsType;

    private Integer sn;

    private String goodsName;

    private String photoId;


}
