package com.centit.zuulgateway.po;

import java.util.Date;
import java.io.Serializable;

import lombok.Data;

/**
 * <p><p>
 *
 * @version : 1.0
 * @Author : li_hao
 * @Description : 实体类
 * @Date : 2020-05-22
 **/
@Data
public class GUserrole implements Serializable {


    private String userCode;

    private String roleCode;

    private Date obtainDate;

    private Date secedeDate;

    private String changeDesc;

    private Date updateDate;

    private Date createDate;

    private String creator;

    private String updator;


}
