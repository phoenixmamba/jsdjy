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
public class TicketClass implements Serializable {


    private String classId;

    private String className;

    private String parentClassId;

    private String isShow;

    private String photoId;

    private Integer sn;
}
