package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p>兑换码活动与项目关联表<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2022-07-19
 **/
@Data
public class TicketRedeemActivityProject implements Serializable {


    private String id;

    private String activityId;

    private String projectId;

    private String eventId;


}
