package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p>VIEW<p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-05-21
 **/
@Data
public class VSearch implements Serializable {


    private String id;

    private String name;

    private String photo;

    private String photourl;

    private String timestr;

    private Integer type;


}
