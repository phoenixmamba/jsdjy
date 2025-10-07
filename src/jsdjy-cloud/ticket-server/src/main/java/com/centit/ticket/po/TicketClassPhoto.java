package com.centit.ticket.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-05-11
 **/
@Data
public class TicketClassPhoto implements Serializable {


    private String classId;

    private String photoId;

    private Integer sn;


}
