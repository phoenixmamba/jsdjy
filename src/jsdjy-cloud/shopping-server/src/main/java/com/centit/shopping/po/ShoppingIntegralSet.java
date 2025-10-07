package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-09-28
 **/
@Data
public class ShoppingIntegralSet implements Serializable {


    private String id;

    private String name;

    private Integer integral;

    private Integer dailyMax;


}
