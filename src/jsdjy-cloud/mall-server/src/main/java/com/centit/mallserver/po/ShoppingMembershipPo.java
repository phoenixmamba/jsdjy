package com.centit.mallserver.po;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-06-04
 **/
@Data
public class ShoppingMembershipPo implements Serializable {


    private BigDecimal normal;

    private BigDecimal silver;

    private BigDecimal gold;

    private BigDecimal diamond;


}
