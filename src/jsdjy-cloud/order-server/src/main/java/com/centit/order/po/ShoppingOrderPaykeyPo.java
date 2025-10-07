package com.centit.order.po;

import lombok.Data;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/12/30 16:47
 **/
@Data
public class ShoppingOrderPaykeyPo {
    private String ofId;

    private String accountPointPayKey;

    private String accountMoneyPayKey;
}
