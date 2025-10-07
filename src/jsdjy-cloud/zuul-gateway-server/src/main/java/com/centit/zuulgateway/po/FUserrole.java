package com.centit.zuulgateway.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p>用户角色<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2020-09-16
 **/
@Data
public class FUserrole implements Serializable {


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
