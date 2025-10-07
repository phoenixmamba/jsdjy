package com.centit.shopping.po;

import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-03-01
 **/
@Data
public class ShoppingGoodstype implements Serializable {


    private String id;

    private String addTime;

    private Boolean deleteStatus;

    private String name;

    private Integer sequence;

    private String storeId;


}
