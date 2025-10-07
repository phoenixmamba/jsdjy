package com.centit.shopping.po;

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
public class ShoppingAccessory implements Serializable {


    private String id;

    private String addTime;

    private Boolean deleteStatus;

    private String ext;

    private Integer height;

    private String info;

    private String name;

    private String path;

    private Float size;

    private Integer width;

    private String albumId;

    private String userId;

    private String configId;

    private String findId;


}
