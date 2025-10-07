package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p>兑换码详细表<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2022-07-18
 **/
@Data
public class TicketRedeemCode implements Serializable {


    /**
     * 兑换码
     */
    private String code;

    /**
     * 兑换码前缀
     */
    private String prefix;

    /**
     * 编号
     */
    private Integer number;

    /**
     * 密码
     */
    private String pwd;

    /**
     * '0'：未删除;'1：已删除'
     */
    private String isDelete;

    /**
     * 所属创建批次ID
     */
    private String batchId;

    /**
     * 创建时间
     */
    private String createTime;

    private String companyId;
    private String companyName;

    /**
     * 所属发码活动ID
     */
    private String activityId;
    private String activityName;

    private String bindId;

    /**
     * 兑换项目ID
     */
    private String projectId;
    private String projectName;
    private String projectImgUrl;

    private String eventId;
    private String eventName;
    private String eventStartTime;
    private String venueName;
    /**
     * 兑换时间
     */
    private String exchangeTime;

    /**
     * 兑换用户ID
     */
    private String exchangeUser;

    /**
     * 兑换手机号
     */
    private String exchangeMobile;

    /**
     * 核销时间
     */
    private String writeoffTime;

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
