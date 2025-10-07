package com.centit.mallserver.po;

import lombok.Data;

import java.io.Serializable;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-05-14
 **/
@Data
public class ShoppingFavoritePo implements Serializable {


    private String id;

    private String addTime;

    private Integer type;

    private String goodsId;

    private String userId;


}
