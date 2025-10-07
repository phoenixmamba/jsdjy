package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2022-01-06
 **/
@Data
public class TicketCouponBind implements Serializable {


    private String id;

    private String phone;

    private String codePromotionId;

    private String msg;

    private String addTime;


}
