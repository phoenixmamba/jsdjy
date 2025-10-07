package com.centit.shopping.po;

import com.centit.shopping.utils.StringUtil;
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

    private Integer weight;

    private BigDecimal restPrice;

    private Integer integralValue;

    public void addWeight(int num){
        int currentNum = this.getWeight()==null?0:this.getWeight();
        this.weight = currentNum+num+ StringUtil.getRandomNum(-5,5);
    }
}
