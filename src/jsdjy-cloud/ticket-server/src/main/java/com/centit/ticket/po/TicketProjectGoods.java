package com.centit.ticket.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-04-29
 **/
@Data
public class TicketProjectGoods implements Serializable {


    private String id;

    private String projectId;

    private String goodsId;

    private Integer goodsType;

    private Integer sn;


}
