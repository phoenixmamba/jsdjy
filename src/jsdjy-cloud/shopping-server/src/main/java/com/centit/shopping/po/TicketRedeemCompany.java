package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p>发卡单位<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2022-07-18
 **/
@Data
public class TicketRedeemCompany implements Serializable {


    private String id;

    /**
     * 单位名称
     */
    private String name;

    /**
     * 备注
     */
    private String remarks;

    /**
     * '0':未删除;'1':已删除
     */
    private String isDelete;

    private String createUser;

    private String createTime;


}
