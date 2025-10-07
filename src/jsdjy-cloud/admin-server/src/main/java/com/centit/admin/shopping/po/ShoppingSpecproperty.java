package com.centit.admin.shopping.po;

import lombok.Data;

import java.io.Serializable;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-02-22
 **/
@Data
public class ShoppingSpecproperty implements Serializable {


    private String id;

    private String addTime;

    private Boolean deleteStatus;

    private Integer sequence;

    private String value;

    private String specId;

    private String specimageId;

    private boolean hasChosen=false;
}
