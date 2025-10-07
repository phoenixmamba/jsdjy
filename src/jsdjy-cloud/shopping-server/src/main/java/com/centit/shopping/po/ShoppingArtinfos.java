package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-03-19
 **/
@Data
public class ShoppingArtinfos implements Serializable {


    private String id;

    private String inforName;

    private Integer sequence;

    private Integer infoType;

    private String required;
}
