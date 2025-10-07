package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-03-19
 **/
@Data
public class ShoppingArtactivityInfo implements Serializable {


    private String id;

    private String activityId;

    private String infoId;

    /**
     * 是否必填 '0'：否;'1'：是
     */
    private Integer required;


}
