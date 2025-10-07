package com.centit.shopping.po;

import java.io.Serializable;
import lombok.Data;

/**
 * <p>商品评价照片<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-03-16
 **/
@Data
public class ShoppingEvaluatePhoto implements Serializable {


    private String id;

    private String addTime;

    private Boolean deleteStatus;

    private String photo;

    private Integer sort;

    private String evaluateId;


}
