package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p>兑换码创建批次<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2022-07-18
 **/
@Data
public class TicketRedeemBatch implements Serializable {


    private String id;

    /**
     * 创建说明
     */
    private String remarks;

    /**
     * 兑换码前缀
     */
    private String codePrefix;

    /**
     * 兑换码数量
     */
    private Integer codeCount;

    private String createUser;

    private String createTime;

    /**
     * '0':未删除;'1':已删除
     */
    private String isDelete;


}
