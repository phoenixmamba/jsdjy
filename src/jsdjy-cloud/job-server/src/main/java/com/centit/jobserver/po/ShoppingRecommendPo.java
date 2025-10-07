package com.centit.jobserver.po;

import lombok.Data;

import java.io.Serializable;

/**
 * <p><p>
 *
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-04-26
 **/
@Data
public class ShoppingRecommendPo implements Serializable {


    private String id;

    private String goodsId;

    private Integer goodsType;

    private Integer sn;

    private String goodsName;

    private String photoId;


}
