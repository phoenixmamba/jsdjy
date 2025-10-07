package com.centit.ticket.po;

import java.io.Serializable;
import lombok.Data;

/**
 * <p>订单操作日志<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-02-25
 **/
@Data
public class ShoppingOrderLog implements Serializable {


    private String id;

    private String addTime;

    private Boolean deleteStatus;

    private String logInfo;

    private String stateInfo;

    private String logUserId;

    private String ofId;

    private String beforeInfo;

    private String afterInfo;

    private String logUserType;

    public String getLogUserName(){
        return "管理员";
    }
}
