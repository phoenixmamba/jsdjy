package com.centit.admin.system.po;

import com.centit.admin.util.CommonUtil;
import lombok.Data;

import java.io.Serializable;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-03-03
 **/
@Data
public class FUnitinfo implements Serializable {


    private String unitCode;

    private String parentUnit;

    private String patentUnitName;

    public String getpatentUnitName(){
        if(null !=getParentUnit()&&!"".equals(getParentUnit())){
            if(null != CommonUtil.getUnitinfo(getParentUnit())){
                return CommonUtil.getUnitinfo(getParentUnit()).getUnitName();
            }
        }
        return "";
    }

    /**
     * 发布任务/ 邮电规划/组队/接收任务
     */
    private String unitType;

    /**
     * T:生效 F:无效
     */
    private String isValid;

    /**
     * 用户第三方系统管理
     */
    private String unitTag;

    /**
     * 部门名称
     */
    private String unitName;

    private String englishName;

    /**
     * 组织机构代码：
     */
    private String depNo;

    private String unitDesc;

    private Integer addrbookId;

    /**
     * 部门简称
     */
    private String unitShortName;

    private String unitWord;

    private Integer unitGrade;

    /**
     * 在父级部门中的排序号
     */
    private Integer unitOrder;

    private String updateDate;

    private String createDate;

    private String extjsoninfo;

    private String creator;

    private String updator;

    /**
     * 部门层级路径
     */
    private String unitPath;

    private String unitManager;

    /**
     * 组织机构类型，o单位；ou部门
     */
    private String type;

    /**
     * 单位/部门负责人编号
     */
    private String leader;

    /**
     * 单位/部门负责人名称
     */
    private String leadername;

    /**
     * 单位/部门分管领导编号
     */
    private String fgld;

    /**
     * 单位/部门分管领导名称
     */
    private String fgldname;

    /**
     * 单位/部门分管单位编号
     */
    private String fgdw;

    /**
     * 单位/部门分管单位名称
     */
    private String fgdwname;

    /**
     * 是否显示到通讯录，0不显示，1显示
     */
    private String showaddbook;

    /**
     * 第三方标识
     */
    private String corpid;

    /**
     * 邮编
     */
    private String postCode;

    /**
     * 电话
     */
    private String phoneNumber;

    /**
     * 传真
     */
    private String faxNumber;

    /**
     * 地址
     */
    private String address;


    private String state;

    private String lastModifyDate;

}
