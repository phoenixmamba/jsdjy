package com.centit.shopping.po;

import java.math.BigDecimal;
import java.util.Date;
import java.io.Serializable;

import com.centit.shopping.utils.CommonUtil;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-09-30
 **/
@Data
public class ShoppingRechargeActivityRecord implements Serializable {


    private String id;

    private String activityId;

    private String userid;

    private String username;

    public String getUsername(){
        ShoppingUser user = CommonUtil.getShoppingUserByUserId(getUserid());
        return user==null?"":user.getNickName();
    }

    private String rechargeTime;

    private BigDecimal rechargePrice;


}
