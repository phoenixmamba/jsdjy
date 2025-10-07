package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p>发票抬头<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-11-24
 **/
@Data
public class TInvoiceHeader implements Serializable {


    private String id;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 抬头类型 '0':企业单位;'1':个人/非企业单位
     */
    private String headType;

    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 购方税号
     */
    private String taxNum;

    private String companyAddress;

    private String companyTel;

    private String companyBank;

    private String companyAccount;

    private String createTime;

    private String updateTime;

    /**
     * 是否为默认抬头 'Y'：是；'N'：否
     */
    private String isDefault;


}
