package com.centit.shopping.po;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;

/**
 * <p><p>
 * @version : 1.0
 * @Author : cui_jian
 * @Description : 实体类
 * @Date : 2021-05-28
 **/
@Data
public class ShoppingActivityUsertime implements Serializable {


    private String id;

    private String acId;

    private String userId;

    private String lastTime;


}
