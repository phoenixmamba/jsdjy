package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-07-01
 **/
@Data
public class ShoppingSign implements Serializable {


    private String userId;

    private String dateStr;

    private String addTime;


}
