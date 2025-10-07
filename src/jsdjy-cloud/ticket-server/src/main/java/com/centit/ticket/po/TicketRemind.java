package com.centit.ticket.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-06-21
 **/
@Data
public class TicketRemind implements Serializable {


    private String projectId;

    private Date pushTime;


}
