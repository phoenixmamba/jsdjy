package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2022-06-28
 **/
@Data
public class TEarlywarning implements Serializable {


    private String id;

    /**
     * 预警事件标题
     */
    private String title;

    /**
     * 手机号，多个手机号用;分隔
     */
    private String mobiles;

    /**
     * 短信通知内容
     */
    private String smsContent;

    /**
     * 预警任务类型 1：单次；2：每年预警
     */
    private Integer taskType;

    /**
     * 预警短信截止时间
     */
    private String deadline;

    /**
     * 下次预警截止时间
     */
    private String nextExpDay;

    /**
     * 提前开始预警天数
     */
    private Integer advanceDay;

    /**
     * 预警短信发送类型 1：只发送一次；2：每天发送
     */
    private Integer warningType;

    /**
     * 最近一次发送短信的时间
     */
    private String lastSendTime;

    /**
     * 预警任务开关 on：打开;off：关闭
     */
    private String taskSwitch;

    private String createTime;

    /**
     * 0:未删除；1：已删除
     */
    private String isDelete;


}
