package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-04-20
 **/
@Data
public class ParkPlate implements Serializable {


    private String id;

    private String userId;

    /**
     * 车牌号
     */
    private String plateNo;

    /**
     * 是否是默认车牌 '0'：否；'1'：是
     */
    private String defaultPlateBoolean;


}
