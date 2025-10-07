package com.centit.admin.system.po;

import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-03-03
 **/
@Data
public class FUserrole implements Serializable {


    private String userCode;

    private String roleCode;

    private String obtainDate;

    private String secedeDate;

    private String changeDesc;

    private String updateDate;

    private String createDate;

    private String creator;

    private String updator;

    private String loginName;

    private String userName;

    private String userPrimaryUnit;

    private String userPrimaryUnitText;

    private String topUnit;

    private String topUnitName;

}
