package com.centit.jobserver.po;

import lombok.Data;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 
 * @Date : 2024/11/26 21:14
 **/
@Data
public class EarlywarningPo {
    private Long id;

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
    * 预警事件类型 1：单轮预警；2：每年预警
    */
    private Integer taskType;

    /**
    * 预警事件截止时间
    */
    private String deadline;

    /**
    * 下次预警事件日期
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", title=").append(title);
        sb.append(", mobiles=").append(mobiles);
        sb.append(", smsContent=").append(smsContent);
        sb.append(", taskType=").append(taskType);
        sb.append(", deadline=").append(deadline);
        sb.append(", nextExpDay=").append(nextExpDay);
        sb.append(", advanceDay=").append(advanceDay);
        sb.append(", warningType=").append(warningType);
        sb.append(", lastSendTime=").append(lastSendTime);
        sb.append(", taskSwitch=").append(taskSwitch);
        sb.append(", createTime=").append(createTime);
        sb.append(", isDelete=").append(isDelete);
        sb.append("]");
        return sb.toString();
    }
}