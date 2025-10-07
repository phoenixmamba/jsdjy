package com.centit.jobserver.po;

import cn.hutool.core.util.RandomUtil;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>商城首页推荐商品<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-04-26
 **/
@Data
public class HomeGoods implements Serializable {
    private String userId;

    private String goodsId;

    private String goodsName;

    private String photoId;

    private BigDecimal goodsPrice;

    private Integer orderCount;

    private Integer type;

    private int weight;

    private BigDecimal restPrice;

    private Integer integralValue;

    public void addWeight(int num){
        this.weight = getWeight()+num+ RandomUtil.randomInt(-5,5);
    }
}
