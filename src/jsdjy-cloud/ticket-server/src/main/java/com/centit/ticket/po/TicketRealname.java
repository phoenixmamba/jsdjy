package com.centit.ticket.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-04-20
 **/
@Data
public class TicketRealname implements Serializable {


    private String id;

    private String userId;

    /**
     * 真实姓名
     */
    private String collectorName;

    /**
     * 证件类型；1=身份证
     */
    private Integer collectorCardType;

    /**
     * 证件号
     */
    private String collectorCardNo;


}
