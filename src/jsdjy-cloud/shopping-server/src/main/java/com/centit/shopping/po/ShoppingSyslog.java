package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-02-21
 **/
@Data
public class ShoppingSyslog implements Serializable {


    private Long id;

    private Date addTime;

    private Boolean deleteStatus;

    private String content;

    private String ip;

    private String title;

    private Integer type;

    private Long userId;


}
