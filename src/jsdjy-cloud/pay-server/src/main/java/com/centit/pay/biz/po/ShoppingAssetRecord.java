package com.centit.pay.biz.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2022-01-13
 **/
@Data
public class ShoppingAssetRecord implements Serializable {


    private String id;

    private String userId;

    private String changeTime;

    private String changeReason;

    private Integer changeType;

    private Long changeValue;

    private Integer assetType;

    private String businessId;


}
