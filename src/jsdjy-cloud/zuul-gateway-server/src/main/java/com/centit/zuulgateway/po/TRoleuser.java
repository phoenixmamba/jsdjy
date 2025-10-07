package com.centit.zuulgateway.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p>移动端角色-用户关联<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2020-09-01
 **/
@Data
public class TRoleuser implements Serializable {


    /**
     * 移动端角色id
     */
    private String roleid;

    /**
     * 用户id
     */
    private String userid;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建用户USERID
     */
    private String createuserid;

    /**
     * 创建时间
     */
    private Date createtime;


}
