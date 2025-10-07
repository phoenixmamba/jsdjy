package com.centit.pay.biz.po;

import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-03-09
 **/
@Data
public class ShoppingOrderLog implements Serializable {


    private String id;

    private String addTime;

    private Boolean deleteStatus;

    private String logInfo;

    private String stateInfo;

    private String logUserId;

    private String ofId;

    private String beforeInfo;

    private String afterInfo;

    /**
     * 用户类型 0：移动端；1：管理后台；2：核销端
     */
    private String logUserType;


}
