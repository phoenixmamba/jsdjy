package com.centit.ticket.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-04-08
 **/
@Data
public class TicketExchangePlaceWorktime implements Serializable {


    /**
     * 取票地点id，由于麦座没有返回，暂定用longitude+latitude
     */
    private String placeId;

    /**
     * 工作时间类型；1=固定时间，2=自定义时间
     */
    private Integer workDateType;

    /**
     * 自定义工作时间；当work_date_type=2时非空
     */
    private String workDateDesc;

    /**
     * 	具体一周中周几工作；例 Set = 1,2,3,4,5 : 周一、周二、周三、周四、周五工作；当work_date_type=1时非空
     */
    private String workDateSets;

    /**
     * 工作时间，开始时间 格式：HH:mm；当work_date_type=1时非空
     */
    private String workStartTime;

    /**
     * 工作时间，结束时间 格式：HH:mm；当work_date_type=1时非空
     */
    private String workEndTime;


}
