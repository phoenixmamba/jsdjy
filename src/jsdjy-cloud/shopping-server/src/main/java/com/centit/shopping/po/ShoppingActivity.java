package com.centit.shopping.po;

import java.util.ArrayList;
import java.util.Date;
import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-05-27
 **/
@Data
public class ShoppingActivity implements Serializable {


    private String id;

    private String addTime;

    private String deleteStatus;

    private String acBeginTime;

    private String acContent;

    private String acEndTime;

    private Integer acSequence;

    private Integer acStatus;

    private String acTitle;

    private String acAccId;

    /**
     * 0：无首页弹窗；1：有首页弹窗
     */
    private Integer acHome;

    private String acHomePhoto;

    private boolean userStatus = false;

    private List<ShoppingCoupon> coupons = new ArrayList<>();

}
