package com.centit.shopping.po;

import java.util.ArrayList;
import java.util.Date;
import java.io.Serializable;

import com.centit.shopping.utils.CommonUtil;
import lombok.Data;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-03-06
 **/
@Data
public class ShoppingStorecart implements Serializable {


    private String id;

    private String addTime;

    private Boolean deleteStatus;

    private String cartSessionId;

    private BigDecimal totalPrice;

    /**
     * 累计积分
     */
    private Integer totalIntegral;

    private String storeId;

    private String storeName;

    public String getStoreName(){
        return CommonUtil.getStoreInfo(getStoreId()).getStoreName();
    }

    private String userId;

    private Integer scStatus;

    //购物车商品信息
    private List<HashMap<String, Object>> goodsList = new ArrayList<>();
}
