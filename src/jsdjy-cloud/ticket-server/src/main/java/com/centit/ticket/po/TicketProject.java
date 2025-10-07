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
public class TicketProject implements Serializable {


    private String projectId;

    private String projectName;

    /**
     * 项目座位类型；1=有座自助选座，2=无座
     */
    private Integer projectSeatType;

    /**
     * 项目销售状态；2=销售中；只返回在售项目
     */
    private Integer projectSaleState;

    private String firstClassId;

    private String firstClassName;

    private String secondClassId;

    private String secondClassName;

    private String projectImgUrl;

    private String projectRound;

    private String projectIntroduce;


}
