package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-07-29
 **/
@Data
public class ShoppingArtclassLiveset implements Serializable {


    private Integer id;

    private String classId;

    private String userId;

    private String addTime;

    private String endTime;

    private String deleteStatus;


}
