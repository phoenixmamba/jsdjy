package com.centit.zuulgateway.po;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>终端设备信息<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2020-05-27
 **/
@Data
public class TDmEquipment implements Serializable {


    private String equipmentid;

    /**
     * 设备名称
     */
    private String equipmentname;

    /**
     * 设备标识号
     */
    private String identcode;

    /**
     * 设备类型：2:IOS；3:ANDROID
     */
    private String devicetype;

    /**
     * 适用设备版本
     */
    private String osversion;

    /**
     * 设备描述
     */
    private String equipmentdesc;

    /**
     * 部门ID
     */
    private String deptid;

    /**
     * 是否遗失T：是 ;F：否
     */
    private String isloss;

    /**
     * 是否可用T：可用;F：不可用
     */
    private String isenabled;

    /**
     * 删除标志位T：删除;F：未删除
     */
    private String isdeleted;

    /**
     * 采购人ID
     */
    private String purchaseuserid;

    /**
     * 采购人名称
     */
    private String purchasename;

    /**
     * 采购时间
     */
    private String purchasedate;

    /**
     * 设备安装验证码
     */
    private String equverifcode;

    /**
     * 创建人ID
     */
    private String createuserid;

    /**
     * 创建时间
     */
    private String createtime;

    private String updateuserid;

    private String updatetime;

    /**
     * 所属人ID
     */
    private String belongto;



}
