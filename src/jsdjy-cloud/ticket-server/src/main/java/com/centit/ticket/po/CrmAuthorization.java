package com.centit.ticket.po;

import lombok.Data;

import java.io.Serializable;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-05-18
 **/
@Data
public class CrmAuthorization implements Serializable {


    private String roleType;

    private String authorization;

    private String updateTime;

    private String invalidTime;


}
