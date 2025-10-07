package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2025-07-01
 **/
@Data
public class CCorpinfo implements Serializable {


    private String id;

    private String name;

    private String tyshxydm;

    private String fax;

    private String phone;

    private Integer email;

    private String postcode;

    private String position;

    private String administrativeAreaCode;

    private String type;

    private String industry;

    private String isValid;

    private String creator;

    private String updator;

    private Date createDate;

    private Date updateDate;


}
