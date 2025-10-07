package com.centit.admin.system.po;

import java.io.Serializable;

import com.centit.admin.util.CommonUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-03-03
 **/
@Data
public class FRoleinfo implements Serializable {


    private String roleCode;

    private String roleName;

    /**
     * F 为系统 固有的 G 全局的 P 公用的 D 部门的 I 为项目角色 W工作量角色
     */
    private String roleType;

    public String getRoleTypeText(){
        if(StringUtils.isNotBlank(getRoleType())){
            return CommonUtil.getCodeValue("RoleType",getRoleType());
        }else{
            return "";
        }
    }

    private String unitCode;

    private String isValid;

    private String roleDesc;

    private String updateDate;

    private String createDate;

    private String creator;

    private String updator;

    public String getLastModifyDate(){
        if(StringUtils.isNotBlank(getUpdateDate())){
            return getUpdateDate();
        }else{
            return "";
        }
    }
}
