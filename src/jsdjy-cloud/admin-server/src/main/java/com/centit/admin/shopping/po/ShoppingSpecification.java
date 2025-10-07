package com.centit.admin.shopping.po;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-02-22
 **/
@Data
public class ShoppingSpecification implements Serializable {


    private String id;

    private String addTime;

    private Boolean deleteStatus;

    private String name;

    private Integer sequence;

    private String type;

    private String storeId;

    private List<ShoppingSpecproperty> propertys;

//    public List<ShoppingGoodsspecproperty> getPropertys(){
//        return CommonUtil.getPropertys(getId());
//    }

    private boolean hasChosen=false;
}
