package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-09-30
 **/
@Data
public class ShoppingRechargeActivity implements Serializable {


    private Long id;

    /**
     * 活动标题
     */
    private String title;

    /**
     * 活动说明
     */
    private String remark;

    /**
     * 下限金额
     */
    private Integer floor;

    /**
     * 赠送金额
     */
    private Integer give;

    /**
     * 每人可参与次数
     */
    private Integer pernum;

    private String startTime;

    private String endTime;

    private String createTime;

    /**
     * '0':未上架；'1'：上架
     */
    private String isPub;

    /**
     * '0'：未删除；'1'：已删除
     */
    private String isDelete;


}
