package com.centit.ticket.po;

import java.util.ArrayList;
import java.util.Date;
import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-04-19
 **/
@Data
public class TicketAreacode implements Serializable {


    private String code;

    private String name;

    private String parentCode;

    private List<TicketAreacode> childList = new ArrayList<>();

}
