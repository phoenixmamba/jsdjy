package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2022-03-10
 **/
@Data
public class ShoppingArtplanPush implements Serializable {


    private String id;

    /**
     * 活动id
     */
    private String activityId;

    private String pushTitle;

    /**
     * 推送内容
     */
    private String pushContent;

    /**
     * 推送类型 1：立即推送；2：定时推送
     */
    private Integer pushType;

    /**
     * 推送时间，只有定时推送时必须
     */
    private String pushTime;

    /**
     * 推送范围 1：所有购买用户；2：指定用户
     */
    private Integer pushRange;

    /**
     * 推送手机号，多个以分号分隔，只有推送范围为指定用户时必须
     */
    private String pushMobiles;

    /**
     * 推送状态 1：未推送；2：推送完成；0：取消推送；-1：推送失败
     */
    private Integer pushStatus;

    /**
     * 实际推送时间
     */
    private String doneTime;

    /**
     * 推送任务添加时间
     */
    private String addTime;

    /**
     * 推送任务创建用户id
     */
    private String addUser;


}
