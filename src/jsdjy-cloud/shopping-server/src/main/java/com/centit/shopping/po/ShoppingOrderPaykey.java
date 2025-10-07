package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-03-25
 **/
@Data
public class ShoppingOrderPaykey implements Serializable {


    private String ofId;

    private String accountPointPayKey;

    private String accountMoneyPayKey;


}
