package com.centit.shopping.po;

import java.io.Serializable;
import lombok.Data;

/**
 * <p>快递公司<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-02-25
 **/
@Data
public class ShoppingExpressCompany implements Serializable {


    private String id;

    private String addTime;

    private Boolean deleteStatus;

    /**
     * 快递公司标记
     */
    private String companyMark;

    /**
     * 快递公司名称
     */
    private String companyName;

    private Integer companyStatus;

    private Integer companySequence;

    private String companyType;


}
