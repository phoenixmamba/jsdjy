package com.centit.zuulgateway.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p>移动端角色信息<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2020-09-01
 **/
@Data
public class TRoleinfo implements Serializable {


    private String id;

    private String rolename;

    /**
     * 是否启用Y：启用N：禁用
     */
    private String isenabled;

    private String roledesc;

    /**
     * 删除标志Y 未删除,N 删除
     */
    private String isdeleted;

    /**
     * 创建用户UDERID
     */
    private String createuserid;

    /**
     * 创建时间
     */
    private Date createtime;

    /**
     * 最近一次更新用户USERID
     */
    private String updateuserid;

    /**
     * 最近一次更新时间
     */
    private Date updatetime;


}
