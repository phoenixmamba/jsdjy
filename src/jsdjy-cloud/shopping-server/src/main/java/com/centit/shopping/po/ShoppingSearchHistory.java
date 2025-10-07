package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-05-24
 **/
@Data
public class ShoppingSearchHistory implements Serializable {


    private Long id;

    private String userId;

    private String str;

    private Date addTime;


}
