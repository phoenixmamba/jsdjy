package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p>活动绑定兑换码记录<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2022-07-19
 **/
@Data
public class TicketRedeemActivityBind implements Serializable {


    private String id;

    /**
     * 所属活动ID
     */
    private String activityId;

    /**
     * 兑换码数量
     */
    private Integer amount;

    /**
     * 起始兑换码
     */
    private String startCode;

    private String createTime;

    private String createUser;

    private String isDelete;
}
