package com.centit.logstatistics.logstatisticsserver.webmgr.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p> <p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description :  实体类
 * @Date : 2021-06-15
 **/
@Data
public class TicketProject implements Serializable {


    private String projectId;

    private String projectName;

    private Integer projectSeatType;

    private Integer projectSaleState;

    private String firstClassId;

    private String firstClassName;

    private String secondClassId;

    private String secondClassName;

    private String projectImgUrl;

    private String projectRound;

    private String projectIntroduce;

    private Integer sn;


}
