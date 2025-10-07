package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p>活动和报名信息关联表<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-12-14
 **/
@Data
public class ShoppingArtplanInfo implements Serializable {


    private Long id;

    private Long activityId;

    private Long infoId;

    /**
     * 是否必填 '0'：否;'1'：是
     */
    private Integer required;


}
