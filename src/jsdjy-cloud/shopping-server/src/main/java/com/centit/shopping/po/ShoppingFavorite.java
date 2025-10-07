package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-05-14
 **/
@Data
public class ShoppingFavorite implements Serializable {


    private String id;

    private String addTime;

    private String deleteStatus;

    private Integer type;

    private String goodsId;

    private String userId;


}
