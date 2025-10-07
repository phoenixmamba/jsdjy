package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-08-02
 **/
@Data
public class TInterfacecheckMsgLog implements Serializable {


    private String id;

    /**
     * 1：麦座；2：CRM；3：速停车
     */
    private Integer type;

    private String sendTime;


}
