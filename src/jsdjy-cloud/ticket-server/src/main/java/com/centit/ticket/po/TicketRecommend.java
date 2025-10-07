package com.centit.ticket.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-05-07
 **/
@Data
public class TicketRecommend implements Serializable {


    private String id;

    private String projectId;

    /**
     * 移动端排序号
     */
    private Integer sn;


}
