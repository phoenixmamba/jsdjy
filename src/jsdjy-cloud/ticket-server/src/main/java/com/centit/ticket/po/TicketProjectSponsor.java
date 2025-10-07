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
public class TicketProjectSponsor implements Serializable {


    private String projectId;

    /**
     * 主办方名称
     */
    private String sponsorName;

    /**
     * 主办方id
     */
    private String sponsorId;


}
