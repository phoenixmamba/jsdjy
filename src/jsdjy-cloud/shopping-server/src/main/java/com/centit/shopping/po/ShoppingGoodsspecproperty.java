package com.centit.shopping.po;

import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-02-22
 **/
@Data
public class ShoppingGoodsspecproperty implements Serializable {


    private String id;

    private String addTime;

    private Boolean deleteStatus;

    private Integer sequence;

    private String value;

    private String specId;

    private String specimageId;

    private boolean hasChosen=false;
}
