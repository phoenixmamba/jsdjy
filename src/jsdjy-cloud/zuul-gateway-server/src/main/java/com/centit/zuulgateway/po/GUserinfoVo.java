package com.centit.zuulgateway.po;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>人员信息综合包装类<p>
 *
 * @version : 1.0
 * @Author : li_hao
 * @Description : 人员信息综合包装类
 * @Date : 2020-06-015
 **/
@Data
public class GUserinfoVo extends GUserCustomer implements Serializable {

    /**
     * 人员-角色关联信息
     */
    private List<GUserrole> userRoleList;



}
