package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p>兑换码活动<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2022-07-19
 **/
@Data
public class TicketRedeemActivity implements Serializable {


    private String id;

    /**
     * 所属单位ID
     */
    private String companyId;

    private String companyName;

    /**
     * 标题
     */
    private String title;

    /**
     * 说明
     */
    private String remarks;

    /**
     * '0':未发布;'1':已发布
     */
    private String pubStatus;

    /**
     * '0':未删除;'1':已删除
     */
    private String isDelete;

    /**
     * 兑换开始时间
     */
    private String startTime;

    /**
     * 兑换截止时间
     */
    private String endTime;

    private String createTime;

    private String createUser;

    private String updateTime;

    private String updateUser;

    private String isBindall;
}
