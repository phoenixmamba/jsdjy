package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-10-12
 **/
@Data
public class ShoppingPayLimit implements Serializable {


    private int pointPay;

    private int balancePay;

    private int balanceRecharge;


}
