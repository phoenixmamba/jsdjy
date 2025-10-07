package com.centit.shopping.po;

import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-02-23
 **/
@Data
public class ShoppingTransport implements Serializable {


    private String id;

    private String addTime;

    private String deleteStatus;

    private Integer transEms;

    private String transEmsInfo;

    private Integer transExpress;

    private String transExpressInfo;

    private Integer transMail;

    private String transMailInfo;

    private String transName;

    private String storeId;

    private Integer transTime;

    private Integer transType;


}
