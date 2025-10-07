package com.centit.shopping.po;

import java.io.Serializable;
import java.util.List;

import com.centit.shopping.utils.CommonUtil;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-02-22
 **/
@Data
public class ShoppingGoodsspecification implements Serializable {


    private String id;

    private String addTime;

    private Boolean deleteStatus;

    private String name;

    private Integer sequence;

    private String type;

    private String storeId;

    private List<ShoppingGoodsspecproperty> propertys;

//    public List<ShoppingGoodsspecproperty> getPropertys(){
//        return CommonUtil.getPropertys(getId());
//    }

    private boolean hasChosen=false;
}
