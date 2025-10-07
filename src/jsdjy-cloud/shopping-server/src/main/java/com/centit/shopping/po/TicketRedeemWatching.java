package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p>兑换项目观看人信息表<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2022-07-18
 **/
@Data
public class TicketRedeemWatching implements Serializable {


    private Long id;

    /**
     * 优惠码
     */
    private String code;

    /**
     * 观看人姓名
     */
    private String watchingUser;

    /**
     * 观看人手机号
     */
    private String watchingMobile;

    /**
     * 观看人身份证号
     */
    private String watchingCard;


}
