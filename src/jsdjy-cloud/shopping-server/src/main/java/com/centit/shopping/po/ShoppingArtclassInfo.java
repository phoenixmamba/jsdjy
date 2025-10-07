package com.centit.shopping.po;

import lombok.Data;

import java.io.Serializable;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-03-24
 **/
@Data
public class ShoppingArtclassInfo implements Serializable {


    private String id;

    private String classId;

    private String infoId;

    /**
     * 是否必填 '0'：否;'1'：是
     */
    private Integer required;


}
