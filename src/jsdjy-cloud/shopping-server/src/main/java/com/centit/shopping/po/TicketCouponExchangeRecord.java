package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2022-07-04
 **/
@Data
public class TicketCouponExchangeRecord implements Serializable {


    private Long id;

    /**
     * APP优惠码活动ID
     */
    private String actId;

    /**
     * 麦座优惠码
     */
    private String promotionId;

    /**
     * APP生成的兑换码
     */
    private String couponCode;

    /**
     * APP优惠码的兑换密码
     */
    private String couponPwd;

    private String createTime;

    private String exchangeUser;

    private String exchangeMobile;

    private String exchangeTime;


}
