package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-05-05
 **/
@Data
public class ShoppingWriteoffRecord implements Serializable {


    private String gcId;

    private String addTime;

    private String offAccount;


}
