package com.centit.mallserver.po;

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
public class ShoppingSpecPropertyPo implements Serializable {

    private String propertyId;

    private String propertyValue;

    private String specId;

}
