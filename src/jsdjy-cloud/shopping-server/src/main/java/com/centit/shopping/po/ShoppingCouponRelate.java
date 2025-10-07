package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-05-25
 **/
@Data
public class ShoppingCouponRelate implements Serializable {


    private String id;

    private String rightNo;

    /**
     * 1：关联到分类；2：关联到商品
     */
    private Integer linkType;

    /**
     * 1：文创；2：积分；3：艺术活动；4：艺术课程
     */
    private Integer goodsType;

    /**
     * 分类id/商品id
     */
    private String goodsId;

    private String goodsName;
}
